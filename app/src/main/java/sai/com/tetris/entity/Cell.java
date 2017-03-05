package sai.com.tetris.entity;

import android.graphics.Point;

/**
 * Created by 147 on 2016/5/28.
 */
public class Cell {
    private Point point;
    private int ColorType;

    public Cell(Point p, int type) {
        this.point = p;
        this.ColorType = type;
    }

    public int getColorType() {
        return ColorType;
    }

    public Point getPoint() {
        return point;
    }

    public void setColorType(int colorType) {
        ColorType = colorType;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}

