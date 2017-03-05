package sai.com.tetris.entity.Shapes;

import android.util.Log;

import sai.com.tetris.entity.BlockArray;
import sai.com.tetris.entity.Shape;

/**
 * Created by 147 on 2016/5/28.
 */
public class ShapeFactory {
    private static Shape shape = null;

    public static Shape produce(String className) {
        try {
            Log.i("ShapeFactory", "-->" + className);
            String cn = "sai.com.tetris.entity.Shapes." + className.trim();
            shape = (Shape) Class.forName(cn).newInstance();
        } catch (Exception e) {
            Log.i("ShapeFactory", "-->" + e.toString());
            e.printStackTrace();
        }
        return shape;
    }
}

class BShape extends Shape {
    @Override
    public void init() {
        blocks = new Block[]{
                new Block(BlockArray.B)};
    }

}

class IShape extends Shape {
    @Override
    public void init() {
        blocks = new Block[]{
                new Block(BlockArray.IA),
                new Block(BlockArray.IB)};
    }

}

class LShape extends Shape {
    @Override
    public void init() {
        blocks = new Block[]{
                new Block(BlockArray.LA),
                new Block(BlockArray.LB),
                new Block(BlockArray.LC),
                new Block(BlockArray.LD)};
    }
}

class TShape extends Shape {
    @Override
    public void init() {
        blocks = new Block[]{
                new Block(BlockArray.TA),
                new Block(BlockArray.TB),
                new Block(BlockArray.TC),
                new Block(BlockArray.TD)};
    }
}

class Z1Shape extends Shape {
    @Override
    public void init() {
        blocks = new Block[]{
                new Block(BlockArray.ZA),
                new Block(BlockArray.ZB)};
    }
}

class Z2Shape extends Shape {
    @Override
    public void init() {
        blocks = new Block[]{
                new Block(BlockArray.ZC),
                new Block(BlockArray.ZD)};
    }

}
