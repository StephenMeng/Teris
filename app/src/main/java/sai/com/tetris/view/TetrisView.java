package sai.com.tetris.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import sai.com.tetris.R;
import sai.com.tetris.entity.Cell;
import sai.com.tetris.entity.Shape;
import sai.com.tetris.entity.Shapes.Block;
import sai.com.tetris.entity.Shapes.ShapeFactory;

/**
 * Created by 147 on 2016/5/26.
 */
public class TetrisView extends View {
    private static final int DEFAULT_STARTX = 5;
    private static final long DEFAULT_DELAYMILLS = 800;
    private final String TAG = "TetrisView";
    private final int MAX_HORIZONTAL_LINE = 10;
    private final int MAX_VERTICAL_LINE = 14;
    private final int NUM_OF_BLOCK = 6;
    private final int TOUCHAREA = 18;
    private final int BLOCK_COLOR_NUM = 6;

    private ArrayList<Cell> result;
    private ArrayList<Cell> shapePoints;
    private double ratioOfScreen = 8 / 5;
    private float ratioOfBlock = 100 * 1.0f / 100;
    private long delayMills = 800;
    private float mLineHeight;
    private Paint mLinePaint, mTextPaint;
    private String[] shapes = new String[]{"BShape", "IShape", "LShape", "TShape", "Z1Shape", "Z2Shape"};
    private Bitmap[] blockColors = new Bitmap[BLOCK_COLOR_NUM];
    private int currentShapeIndex = 2;
    private Shape currentShape;
    private int verticalNum = 0;
    private int horizontalNum = DEFAULT_STARTX;
    private Point start, end;
    private int screenX;
    private int screenY;
    private long score = 0;
    private int deleteLineNum;
    private int totalDeleteLineNum;
    private int rank;

    private boolean isGameOver = false;

    public TetrisView(Context context) {
        this(context, null);
    }

    public TetrisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if ((float) height >= width * ratioOfScreen) {
            height = (int) (width * ratioOfScreen);
        } else {
            width = (int) (height / ratioOfScreen);
        }
        screenX = width / 4;
        screenY = height / 8;
        setMeasuredDimension(width, height);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mLineHeight = (w / 2) / (MAX_HORIZONTAL_LINE);
        int scaledWidth = (int) (mLineHeight * ratioOfBlock);

        for (int i = 0; i < BLOCK_COLOR_NUM; i++) {
            blockColors[i] = Bitmap.createScaledBitmap(blockColors[i], scaledWidth, scaledWidth, false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                start.x = (int) event.getX();
                start.y = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                end.x = (int) event.getX();
                end.y = (int) event.getY();
                int resultX = start.x - end.x;
                int resultY = start.y - end.y;

                if (Math.abs(resultX) > TOUCHAREA) {
                    if (resultX > 0 && resultX > Math.abs(resultY) && !isReachLeftBlock(shapePoints) && !isReachedLeft(shapePoints)) {
                        --horizontalNum;
                        Log.i(TAG, "-->horizontalnum " + horizontalNum);
                        updateShapePoints();
                    }
                    if (resultX < 0 && Math.abs(resultX) > Math.abs(resultY) && !isReachRightBlock(shapePoints) && !isReachedRight(shapePoints)) {
                        ++horizontalNum;
                        Log.i(TAG, "-->horizontalnum " + horizontalNum);
                        updateShapePoints();
                    }
                }
                if (Math.abs(resultX) <= TOUCHAREA && Math.abs(resultY) <= TOUCHAREA && isSpaceEnough()) {
                    currentShape.change();
                    updateShapePoints();
                }
                if (Math.abs(resultY) > TOUCHAREA) {
                    if (resultY > 0 && resultY > Math.abs(resultX)) {

                    }
                    if (resultY < 0 && Math.abs(resultY) > Math.abs(resultX)) {
                        delayMills = 200;
                        myHandler.sendMessageDelayed(Message.obtain(), delayMills);
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();

        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawShape(canvas);
        drawResult(canvas);
        drawScore(canvas);
    }

    private void drawScore(Canvas canvas) {
        canvas.drawText(score + "分", (MAX_HORIZONTAL_LINE + 1) * mLineHeight, mLineHeight, mTextPaint);
        canvas.drawText(totalDeleteLineNum + "行", (MAX_HORIZONTAL_LINE + 1) * mLineHeight, 2 * mLineHeight, mTextPaint);
    }

    private void drawResult(Canvas canvas) {
        for (Cell p : result) {
            canvas.drawBitmap(blockColors[p.getColorType()],
                    screenX + mLineHeight * p.getPoint().x + (1 - ratioOfBlock) * mLineHeight / 2,
                    screenY + mLineHeight * p.getPoint().y + (1 - ratioOfBlock) * mLineHeight / 2, null);
        }
    }

    private void drawShape(Canvas canvas) {
        int[][] data = currentShape.getShape().getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == 1) {

                    canvas.drawBitmap(blockColors[currentShapeIndex],
                            screenX + mLineHeight * (i + horizontalNum) + (1 - ratioOfBlock) * mLineHeight / 2,
                            screenY + mLineHeight * (j + verticalNum) + (1 - ratioOfBlock) * mLineHeight / 2, null);
                }
            }
        }
    }

    private void drawBorder(Canvas canvas) {
        for (int i = 0; i <= MAX_HORIZONTAL_LINE; i++) {
            canvas.drawLine(screenX + i * mLineHeight, screenY, screenX + i * mLineHeight, screenY + mLineHeight * MAX_VERTICAL_LINE, mLinePaint);
        }
        for (int i = 0; i <= MAX_VERTICAL_LINE; i++) {
            canvas.drawLine(screenX, screenY + i * mLineHeight, screenX + mLineHeight * MAX_HORIZONTAL_LINE, screenY + i * mLineHeight, mLinePaint);
        }
    }

    public void start() {
        update();
    }

    private void update() {
        updateShapePoints();
        if (isReachedBottom(shapePoints) || isReachedBottomBlock(shapePoints)) {
            reShape();
            checkResult();
            TetrisView.this.invalidate();
            verticalNum++;
            myHandler.sleep(delayMills);
        } else {
            TetrisView.this.invalidate();
            verticalNum++;
            myHandler.sleep(delayMills);
        }
    }

    private void updateShapePoints() {
        shapePoints = getPointsLocation(currentShape.getShape());
    }

    public boolean isReachedBottomBlock(ArrayList<Cell> shape) {
        for (Cell bPoint : shape) {
            int bx = bPoint.getPoint().x;
            int by = bPoint.getPoint().y;
            for (Cell rPoint : result) {
                int rx = rPoint.getPoint().x;
                int ry = rPoint.getPoint().y;
                if (bx == rx && by == ry - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isReachLeftBlock(ArrayList<Cell> shape) {
        for (Cell bPoint : shape) {
            int bx = bPoint.getPoint().x;
            int by = bPoint.getPoint().y;
            for (Cell rPoint : result) {
                int rx = rPoint.getPoint().x;
                int ry = rPoint.getPoint().y;
                if (by == ry && bx == rx + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isReachRightBlock(ArrayList<Cell> shape) {
        for (Cell bPoint : shape) {
            int bx = bPoint.getPoint().x;
            int by = bPoint.getPoint().y;
            for (Cell rPoint : result) {
                int rx = rPoint.getPoint().x;
                int ry = rPoint.getPoint().y;
                if (by == ry && bx == rx - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isReachedBottom(ArrayList<Cell> shape) {
        return getBottemPosition(shape) >= MAX_VERTICAL_LINE - 1;
    }

    private boolean isReachedRight(ArrayList<Cell> shape) {
        return getRightPosition(shape) >= MAX_HORIZONTAL_LINE - 1;

    }

    private boolean isReachedLeft(ArrayList<Cell> shape) {

        return getLeftPosition(shape) <= 0;
    }

    private boolean isChangedReachedBottom(ArrayList<Cell> shape) {
        return getBottemPosition(shape) >= MAX_VERTICAL_LINE;
    }

    private boolean isChangedReachedRight(ArrayList<Cell> shape) {
        return getRightPosition(shape) >= MAX_HORIZONTAL_LINE;

    }

    private boolean isChangedReachedLeft(ArrayList<Cell> shape) {

        return getLeftPosition(shape) < 0;
    }

    public boolean isChangedReachedBlock(ArrayList<Cell> shape) {
        for (Cell bPoint : shape) {
            int bx = bPoint.getPoint().x;
            int by = bPoint.getPoint().y;
            for (Cell rPoint : result) {
                int rx = rPoint.getPoint().x;
                int ry = rPoint.getPoint().y;
                if (by == ry && bx == rx) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isSpaceEnough() {
        ArrayList<Cell> changedBlock = getPointsLocation(currentShape.getChangedBlock());
        if (isChangedReachedBottom(changedBlock) || isChangedReachedLeft(changedBlock) || isChangedReachedRight(changedBlock) || isChangedReachedBlock(changedBlock)) {
            return false;
        }
        return true;
    }

    private int checkResult() {
        int count = 0;
        for (int i = MAX_VERTICAL_LINE; i > -1; i--) {
            int rowBlockCount = 0;
            for (int j = 0; j < result.size(); j++) {
                if (result.get(j).getPoint().y == i) {
                    rowBlockCount++;
                }
            }
            if (rowBlockCount >= MAX_HORIZONTAL_LINE - 5) {
                count++;
                for (int j = 0; j < result.size(); j++) {
                    Cell p = result.get(j);
                    if (p.getPoint().y == i) {
                        result.remove(j);
                        j--;
                    } else if (p.getPoint().y < i) {
                        p.getPoint().set(p.getPoint().x, ++p.getPoint().y);
//                        result.remove(j);
//                        result.add(p);
                        result.set(j, p);
                    }
                }
            }
        }
        totalDeleteLineNum += count;
        long deleteScore = 0;
        switch (count) {
            case 1:
                deleteScore = 100;
                break;
            case 2:
                deleteScore = 300;
                break;
            case 3:
                deleteScore = 600;
                break;
            case 4:
                deleteScore = 1200;
                break;
        }
        score += deleteScore;
        invalidate();
        return count;
    }

    private void reShape() {
        int[][] data = currentShape.getShape().getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == 1) {
                    Point p = new Point(i + horizontalNum, j + verticalNum);
                    Cell cell = new Cell(p, currentShapeIndex);
//                    if (!result.contains(point)) {
                    result.add(cell);
//                    }
                }
            }
        }
        delayMills = DEFAULT_DELAYMILLS;
        horizontalNum = DEFAULT_STARTX;
        verticalNum = 0;
        currentShape = getNewShape();
    }

    public int getLeftPosition(ArrayList<Cell> shape) {
        int position = MAX_HORIZONTAL_LINE;
        for (Cell point : shape) {
            position = Math.min(position, point.getPoint().x);
        }
        Log.i(TAG, "-->getLeftPosition" + position);
        return position;
    }

    public int getRightPosition(ArrayList<Cell> shape) {
        int position = 0;
        for (Cell point : shape) {
            position = Math.max(position, point.getPoint().x);
        }
        return position;
    }

    public int getBottemPosition(ArrayList<Cell> shape) {
        int position = 0;
        for (Cell point : shape) {
            position = Math.max(position, point.getPoint().y);

        }
        return position;
    }

    public ArrayList<Cell> getPointsLocation(Block block) {
        ArrayList<Cell> points = new ArrayList<>();
        int[][] data = block.getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == 1) {
                    Point point = new Point(i + horizontalNum, j + verticalNum);
                    Cell cell = new Cell(point, currentShapeIndex);
                    points.add(cell);
                }
            }
        }
        return points;
    }

    private MyHandler myHandler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            TetrisView.this.update();
        }

        public void sleep(long mills) {
            this.removeMessages(0);
            sendMessageDelayed(this.obtainMessage(0), mills);
        }

    }

    public Shape getNewShape() {
        currentShapeIndex = (int) (Math.random() * (NUM_OF_BLOCK));
        Shape newShape = null;
        newShape = ShapeFactory.produce(shapes[currentShapeIndex]);
        if (newShape == null) {
            Log.i(TAG, "-->newShape is null");
        }
        return newShape;
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.parseColor("#0066cc"));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor("#0066cc"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        start = new Point();
        end = new Point();
        setBackgroundResource(R.drawable.bg);

        currentShape = getNewShape();
        Log.i(TAG, "-->" + currentShape.getClass().getName());
        result = new ArrayList<>();
        setupImages();
//        setBackgroundColor(Color.WHITE);
        updateShapePoints();

    }

    private void setupImages() {

        blockColors[0] = createBitmap(R.drawable.byellow);
        blockColors[1] = createBitmap(R.drawable.bblue);
        blockColors[2] = createBitmap(R.drawable.bgreen);
        blockColors[3] = createBitmap(R.drawable.borange);
        blockColors[4] = createBitmap(R.drawable.bpink);
        blockColors[5] = createBitmap(R.drawable.bread);
//        blockColors[6] = createBitmap(R.drawable.bsblue);
//        blockColors[7] = createBitmap(R.drawable.bsred);
    }

    public Bitmap createBitmap(int id) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(getResources(), id);
        } catch (OutOfMemoryError e) {
            while (bitmap == null) {
                System.gc();
                System.runFinalization();
                bitmap = BitmapFactory.decodeResource(getResources(), id);
            }
        }
        return bitmap;
    }
}
