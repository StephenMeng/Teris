package sai.com.tetris.entity;

import sai.com.tetris.entity.Shapes.Block;

/**
 * Created by 147 on 2016/5/26.
 */
public abstract class Shape {
    private int index = 0;
    protected Block[] blocks;

    public Shape() {
        init();
    }

    public abstract void init();

    public Block getShape() {
        return blocks[index];
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }

    public void change() {
        if (blocks.length > 1) {
            if (index == blocks.length - 1) {
                index = 0;
            } else {
                index++;
            }
        }
    }

    public Block getChangedBlock() {
        if (blocks.length > 1) {
            if (index != blocks.length - 1) {
                int tmp = index + 1;
                return blocks[tmp];
            }
        }
        return blocks[0];
    }
}

