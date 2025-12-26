package com.netherairtune.pixelart;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageRenderer {
    private List<BlockSnapshot> undoHistory;
    private final int maxDimension = 100;

    public ImageRenderer() {
        this.undoHistory = null;
    }

    public void renderImage(String imageUrl, ServerPlayerEntity player, int startX, int startY, int startZ, 
                           ServerWorld world, int maxHeight, String orientation, boolean eraseMode) {
        boolean debugMode = false;
        undoHistory = new ArrayList<>();
        BlockColorMapping colorMapper = new BlockColorMapping();
        DirectionOffsetCalculator offsetCalculator = new DirectionOffsetCalculator();

        try {
            URL url = new URL(imageUrl);
            BufferedImage image = null;
            InputStream inputStream = url.openStream();
            
            if (inputStream.available() > 0) {
                image = ImageIO.read(inputStream);
            } else {
                image = ImageIO.read(inputStream);
                if (image == null) {
                    player.sendMessage(Text.literal("Failed to read image from URL: " + imageUrl));
                    return;
                }
            }

            Raster raster = image.getData();
            Rectangle bounds = raster.getBounds();
            player.sendMessage(Text.literal("Image resolution: " + bounds.width + " x " + bounds.height));

            if (raster.getTransferType() != 0) {
                player.sendMessage(Text.literal("Image format not supported. Must be byte-based."));
                return;
            }

            boolean hasAlpha = false;
            int colorChannels = raster.getNumDataElements();
            if (colorChannels == 4) {
                hasAlpha = true;
            }

            double scaleFactor = 1.0;
            if (maxHeight > 0) {
                if (maxHeight >= maxDimension) {
                    maxHeight = maxDimension;
                }
            } else if (bounds.height > maxDimension) {
                maxHeight = maxDimension;
            } else {
                maxHeight = bounds.height;
            }

            if (bounds.height > maxHeight) {
                scaleFactor = (double) maxHeight / bounds.height;
            }

            if ((int) (bounds.width * scaleFactor) > maxDimension) {
                scaleFactor = (double) maxDimension / bounds.width;
            }

            int scaledHeight = (int) (bounds.height * scaleFactor);
            int scaledWidth = (int) (bounds.width * scaleFactor);

            int[] offsets = offsetCalculator.calculateOffsets(startX, startY, startZ, scaledWidth, scaledHeight, orientation);
            
            int xDirection = offsets[0];
            int xStart = offsets[1];
            int xXComponent = offsets[3];
            int xYComponent = offsets[4];
            int yDirection = offsets[5];
            int yStart = offsets[6];
            int yXComponent = offsets[8];
            int yYComponent = offsets[9];
            int zDirection = offsets[10];
            int zStart = offsets[11];
            int zXComponent = offsets[13];
            int zYComponent = offsets[14];

            int currentX = xStart;
            int currentY = yStart;
            int currentZ = zStart;

            for (int heightIndex = scaledHeight - 1; heightIndex >= 0; heightIndex--) {
                for (int widthIndex = 0; widthIndex < scaledWidth; widthIndex++) {
                    int sourceX = (int) (widthIndex / scaleFactor);
                    int sourceY = (int) (heightIndex / scaleFactor);
                    
                    Color color = new Color(image.getRGB(sourceX, sourceY), hasAlpha);
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    
                    BlockPos pos = new BlockPos(currentX, currentY, currentZ);
                    Block oldBlock = world.getBlockState(pos).getBlock();
                    undoHistory.add(new BlockSnapshot(currentX, currentY, currentZ, oldBlock));

                    Block newBlock = Blocks.AIR;
                    if (!eraseMode) {
                        newBlock = colorMapper.findClosestBlock(red, green, blue);
                    }

                    currentX += xDirection * xXComponent;
                    currentY += yDirection * yXComponent;
                    currentZ += zDirection * zXComponent;

                    int alpha = 255;
                    if (hasAlpha) {
                        alpha = color.getAlpha();
                        if (alpha < 10) {
                            continue;
                        }
                    }

                    world.setBlockState(pos, newBlock.getDefaultState());
                }

                if (xXComponent != 0) {
                    currentX = offsets[1];
                }
                if (yXComponent != 0) {
                    currentY = offsets[6];
                }
                if (zXComponent != 0) {
                    currentZ = offsets[11];
                }

                currentX += xDirection * xYComponent;
                currentY += yDirection * yYComponent;
                currentZ += zDirection * zYComponent;
            }

        } catch (IOException e) {
            player.sendMessage(Text.literal("Error loading image: " + e.getMessage()));
        }
    }

    public void undoLastRender(ServerPlayerEntity player, ServerWorld world) {
        if (undoHistory == null) {
            player.sendMessage(Text.literal("No render to undo."));
            return;
        }

        for (BlockSnapshot snapshot : undoHistory) {
            BlockPos pos = new BlockPos(snapshot.xPosition, snapshot.yPosition, snapshot.zPosition);
            world.setBlockState(pos, snapshot.blockType.getDefaultState());
        }

        undoHistory = null;
        player.sendMessage(Text.literal("Render undone."));
    }
}