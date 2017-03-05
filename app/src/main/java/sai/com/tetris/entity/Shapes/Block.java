package sai.com.tetris.entity.Shapes;

/**
 * Created by 147 on 2016/5/26.
 */
public class Block {
    int[][] data;

    Block(int[][] data) {
        this.data = data;
    }

    public int[][] getData() {
        return data;
    }
}