package com.netherairtune.pixelart;

import net.minecraft.block.Block;

public class BlockSnapshot {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public Block blockType;

    public BlockSnapshot(int x, int y, int z, Block block) {
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.blockType = block;
    }
}
