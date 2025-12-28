package com.netherairtune.pixelart;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelArtMod implements ModInitializer {
    public static final String MOD_ID = "pixelart";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private ImageRenderer renderer;

    @Override
    public void onInitialize() {
        LOGGER.info("PixelArt mod initialized!");
        renderer = new ImageRenderer();
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("pixelart")
                .then(CommandManager.literal("render")
                    .then(CommandManager.argument("url", StringArgumentType.greedyString())
                        .executes(context -> executeRender(context, -1, false, null))))
                .then(CommandManager.literal("render")
                    .then(CommandManager.argument("maxheight", IntegerArgumentType.integer(1, 100))
                        .then(CommandManager.argument("url", StringArgumentType.greedyString())
                            .executes(context -> executeRender(context, 
                                IntegerArgumentType.getInteger(context, "maxheight"), false, null)))))
                .then(CommandManager.literal("render")
                    .then(CommandManager.argument("orientation", StringArgumentType.word())
                        .then(CommandManager.argument("url", StringArgumentType.greedyString())
                            .executes(context -> executeRender(context, -1, false, 
                                StringArgumentType.getString(context, "orientation"))))))
                .then(CommandManager.literal("render")
                    .then(CommandManager.argument("maxheight", IntegerArgumentType.integer(1, 100))
                        .then(CommandManager.argument("orientation", StringArgumentType.word())
                            .then(CommandManager.argument("url", StringArgumentType.greedyString())
                                .executes(context -> executeRender(context, 
                                    IntegerArgumentType.getInteger(context, "maxheight"), false,
                                    StringArgumentType.getString(context, "orientation")))))))
                .then(CommandManager.literal("erase")
                    .then(CommandManager.argument("url", StringArgumentType.greedyString())
                        .executes(context -> executeRender(context, -1, true, null))))
                .then(CommandManager.literal("erase")
                    .then(CommandManager.argument("maxheight", IntegerArgumentType.integer(1, 100))
                        .then(CommandManager.argument("url", StringArgumentType.greedyString())
                            .executes(context -> executeRender(context, 
                                IntegerArgumentType.getInteger(context, "maxheight"), true, null)))))
                .then(CommandManager.literal("erase")
                    .then(CommandManager.argument("orientation", StringArgumentType.word())
                        .then(CommandManager.argument("url", StringArgumentType.greedyString())
                            .executes(context -> executeRender(context, -1, true,
                                StringArgumentType.getString(context, "orientation"))))))
                .then(CommandManager.literal("erase")
                    .then(CommandManager.argument("maxheight", IntegerArgumentType.integer(1, 100))
                        .then(CommandManager.argument("orientation", StringArgumentType.word())
                            .then(CommandManager.argument("url", StringArgumentType.greedyString())
                                .executes(context -> executeRender(context,
                                    IntegerArgumentType.getInteger(context, "maxheight"), true,
                                    StringArgumentType.getString(context, "orientation")))))))
                .then(CommandManager.literal("undo")
                    .executes(this::executeUndo))
            );
        });
    }

    private int executeRender(CommandContext<ServerCommandSource> context, int maxHeight, boolean erase, String orientation) {
        ServerCommandSource source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        BlockPos pos = player.getBlockPos();
        
        String direction;
        
        if (orientation != null) {
            if (orientation.equalsIgnoreCase("horizontal")) {
                float yaw = player.getYaw();
                
                if (yaw < -180.0f) {
                    yaw += 360.0f;
                }
                if (yaw > 180.0f) {
                    yaw -= 360.0f;
                }

                if (yaw < 45.0f && yaw > -45.0f) {
                    direction = "+z";
                } else if (yaw < 135.0f && yaw > 44.0f) {
                    direction = "-x";
                } else if (yaw > -135.0f && yaw < -44.0f) {
                    direction = "+x";
                } else {
                    direction = "-z";
                }
            } else if (orientation.equalsIgnoreCase("vertical")) {
                float yaw = player.getYaw();
                float pitch = player.getPitch();
                
                if (yaw < -180.0f) {
                    yaw += 360.0f;
                }
                if (yaw > 180.0f) {
                    yaw -= 360.0f;
                }

                if (yaw < 45.0f && yaw > -45.0f) {
                    direction = "+z";
                } else if (yaw < 135.0f && yaw > 44.0f) {
                    direction = "-x";
                } else if (yaw > -135.0f && yaw < -44.0f) {
                    direction = "+x";
                } else {
                    direction = "-z";
                }

                if (pitch > 45.0f) {
                    direction = "-y" + direction;
                }
                if (pitch < -45.0f) {
                    direction = "+y" + direction;
                }
            } else {
                source.sendError(Text.literal("Invalid orientation. Use 'vertical' or 'horizontal'."));
                return 0;
            }
        } else {
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            
            if (yaw < -180.0f) {
                yaw += 360.0f;
            }
            if (yaw > 180.0f) {
                yaw -= 360.0f;
            }

            if (yaw < 45.0f && yaw > -45.0f) {
                direction = "+z";
            } else if (yaw < 135.0f && yaw > 44.0f) {
                direction = "-x";
            } else if (yaw > -135.0f && yaw < -44.0f) {
                direction = "+x";
            } else {
                direction = "-z";
            }

            if (pitch > 45.0f) {
                direction = "-y" + direction;
            }
            if (pitch < -45.0f) {
                direction = "+y" + direction;
            }
        }

        String url = StringArgumentType.getString(context, "url");
        
        renderer.renderImage(url, player, pos.getX(), pos.getY(), pos.getZ(), world, maxHeight, direction, erase);
        
        return 1;
    }

    private int executeUndo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        
        renderer.undoLastRender(player, world);
        
        return 1;
    }
}