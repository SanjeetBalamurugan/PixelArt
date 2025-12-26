package com.netherairtune.pixelart;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockColorMapping {
    private final Map<Block, int[]> blockColorMap = new HashMap<>();

    public BlockColorMapping() {
        blockColorMap.put(Blocks.BRICKS, new int[]{146, 99, 86});
        blockColorMap.put(Blocks.CLAY, new int[]{158, 164, 176});
        blockColorMap.put(Blocks.COAL_BLOCK, new int[]{18, 18, 18});
        blockColorMap.put(Blocks.COAL_ORE, new int[]{115, 115, 115});
        blockColorMap.put(Blocks.COBBLESTONE, new int[]{122, 122, 122});
        blockColorMap.put(Blocks.DIAMOND_BLOCK, new int[]{97, 219, 213});
        blockColorMap.put(Blocks.DIAMOND_ORE, new int[]{129, 140, 143});
        blockColorMap.put(Blocks.DIRT, new int[]{134, 96, 67});
        blockColorMap.put(Blocks.EMERALD_BLOCK, new int[]{81, 217, 117});
        blockColorMap.put(Blocks.EMERALD_ORE, new int[]{109, 128, 116});
        blockColorMap.put(Blocks.FURNACE, new int[]{113, 113, 113});
        blockColorMap.put(Blocks.GOLD_BLOCK, new int[]{249, 236, 78});
        blockColorMap.put(Blocks.GOLD_ORE, new int[]{143, 139, 124});
        blockColorMap.put(Blocks.GRAVEL, new int[]{126, 124, 122});
        blockColorMap.put(Blocks.IRON_BLOCK, new int[]{219, 219, 219});
        blockColorMap.put(Blocks.IRON_ORE, new int[]{135, 130, 126});
        blockColorMap.put(Blocks.JACK_O_LANTERN, new int[]{185, 133, 28});
        blockColorMap.put(Blocks.LAPIS_BLOCK, new int[]{38, 67, 137});
        blockColorMap.put(Blocks.LAPIS_ORE, new int[]{102, 112, 134});
        blockColorMap.put(Blocks.MOSSY_COBBLESTONE, new int[]{103, 121, 103});
        blockColorMap.put(Blocks.NETHER_BRICKS, new int[]{44, 22, 26});
        blockColorMap.put(Blocks.NETHERRACK, new int[]{111, 54, 52});
        blockColorMap.put(Blocks.OBSIDIAN, new int[]{20, 18, 29});
        blockColorMap.put(Blocks.PACKED_ICE, new int[]{165, 194, 245});
        blockColorMap.put(Blocks.PUMPKIN, new int[]{142, 76, 12});
        blockColorMap.put(Blocks.REDSTONE_BLOCK, new int[]{171, 27, 9});
        blockColorMap.put(Blocks.REDSTONE_ORE, new int[]{132, 107, 107});
        blockColorMap.put(Blocks.SAND, new int[]{219, 211, 160});
        blockColorMap.put(Blocks.SANDSTONE, new int[]{219, 211, 160});
        blockColorMap.put(Blocks.SOUL_SAND, new int[]{84, 64, 51});
        blockColorMap.put(Blocks.SPONGE, new int[]{194, 195, 84});
        blockColorMap.put(Blocks.STONE, new int[]{125, 125, 125});
    }

    private int calculateColorDistance(Block block, int red, int green, int blue) {
        int[] blockColor = blockColorMap.get(block);
        int redDiff = blockColor[0] - red;
        int greenDiff = blockColor[1] - green;
        int blueDiff = blockColor[2] - blue;
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }

    public Block findClosestBlock(int red, int green, int blue) {
        Block closestBlock = null;
        int minDistance = 0;

        for (Block block : blockColorMap.keySet()) {
            if (closestBlock == null) {
                closestBlock = block;
                minDistance = calculateColorDistance(block, red, green, blue);
            } else {
                int distance = calculateColorDistance(block, red, green, blue);
                if (distance < minDistance) {
                    closestBlock = block;
                    minDistance = distance;
                }
            }
        }

        return closestBlock;
    }
}
