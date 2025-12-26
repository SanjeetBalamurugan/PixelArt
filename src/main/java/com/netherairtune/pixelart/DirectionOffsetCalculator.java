package com.netherairtune.pixelart;

import java.util.HashMap;
import java.util.Map;

public class DirectionOffsetCalculator {
    private Map<String, int[]> orientationOffsets = null;

    public int[] calculateOffsets(int playerX, int playerY, int playerZ, int imageWidth, int imageHeight, String direction) {
        orientationOffsets = new HashMap<>();
        
        orientationOffsets.put("+x", new int[]{0, playerX + 1, playerX + 1, 0, 0, 1, playerY, playerY + imageHeight, 0, 1, 1, playerZ - imageWidth / 2, playerZ + imageWidth, 1, 0});
        orientationOffsets.put("-x", new int[]{0, playerX - 2, playerX - 1, 0, 0, 1, playerY, playerY + imageHeight, 0, 1, -1, playerZ + imageWidth / 2, playerZ, 1, 0});
        orientationOffsets.put("+z", new int[]{-1, playerX + imageWidth / 2, playerX, 1, 0, 1, playerY, playerY + imageHeight, 0, 1, 0, playerZ + 1, playerZ + 1, 0, 0});
        orientationOffsets.put("-z", new int[]{1, playerX - imageWidth / 2, playerX + imageWidth, 1, 0, 1, playerY, playerY + imageHeight, 0, 1, 0, playerZ - 2, playerZ - 2, 0, 0});
        orientationOffsets.put("+y+x", new int[]{-1, playerX, playerX + imageHeight, 0, 1, 0, playerY + 2, playerY, 0, 0, -1, playerZ + imageWidth / 2, playerZ, 1, 0});
        orientationOffsets.put("+y-x", new int[]{1, playerX, playerX + imageHeight, 0, 1, 0, playerY + 2, playerY, 0, 0, 1, playerZ - imageWidth / 2, playerZ + imageWidth, 1, 0});
        orientationOffsets.put("-y+x", new int[]{1, playerX, playerX, 0, 1, 0, playerY - 1, playerY, 0, 0, 1, playerZ - imageWidth / 2, playerZ + imageWidth, 1, 0});
        orientationOffsets.put("-y-x", new int[]{-1, playerX, playerX, 0, 1, 0, playerY - 1, playerY, 0, 0, -1, playerZ + imageWidth / 2, playerZ, 1, 0});
        orientationOffsets.put("+y+z", new int[]{-1, playerX, playerX + imageWidth / 2, 1, 0, 0, playerY + 2, playerY + 2, 0, 0, -1, playerZ, playerZ, 0, 1});
        orientationOffsets.put("+y-z", new int[]{1, playerX - imageWidth / 2, playerX + imageWidth, 1, 0, 0, playerY + 2, playerY + 2, 0, 0, 1, playerZ, playerZ + imageHeight, 0, 1});
        orientationOffsets.put("-y+z", new int[]{-1, playerX + imageWidth / 2, playerX, 1, 0, 0, playerY - 1, playerY, 0, 0, 1, playerZ, playerZ, 0, 1});
        orientationOffsets.put("-y-z", new int[]{1, playerX - imageWidth / 2, playerX, 1, 0, 0, playerY - 1, playerY, 0, 0, -1, playerZ, playerZ, 0, 1});
        
        return orientationOffsets.get(direction);
    }
}
