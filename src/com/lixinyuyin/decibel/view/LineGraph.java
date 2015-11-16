package com.lixinyuyin.decibel.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.decibel.R;


public class LineGraph extends View {

    private final static int offset = 50;
    private final static float radius = 5.0f;

    private int mLineNums = 5; // 4 <= lineNums <= 6; default 5;
    private int yScale = 1; // default 1;

    private int mTextSize;
    private int mTextColor;

    private List<Float> xAxisList;
    private List<Float> yAxisList;
    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineGraph(Context context) {
        super(context);
    }

    public LineGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LineGraph);
        mTextSize =
                attributes.getDimensionPixelSize(R.styleable.LineGraph_text_size, getResources()
                        .getDimensionPixelSize(R.dimen.linegraph_text_size));
        mTextColor =
                attributes.getColor(R.styleable.LineGraph_text_color,
                        getResources().getColor(R.color.linegraph_text_color));
        yAxisList = new ArrayList<Float>();
        xAxisList = new ArrayList<Float>();

        attributes.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculate_xAxis();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Align.CENTER);
        int bottomBase = getHeight() - offset; // careful
        mLinePaint.setStrokeWidth(4.0f);
        mLinePaint.setColor(getResources().getColor(R.color.black));
        canvas.drawLine(getLeft() + offset, bottomBase, getRight() - offset, bottomBase, mLinePaint);
        canvas.drawLine(getRight() - offset, bottomBase, getRight() - offset - 20, bottomBase - 10,
                mLinePaint);
        canvas.drawLine(getRight() - offset, bottomBase, getRight() - offset - 20, bottomBase + 10,
                mLinePaint);
        mLinePaint.setStrokeWidth(3.0f);
        for (int i = 0; i < yAxisList.size(); i++) {
            float x = xAxisList.get(i);
            float y = yAxisList.get(i);
            y = y * yScale;
            if (i == yAxisList.size() - 1) {
                mLinePaint.setColor(getResources().getColor(R.color.blue));
                mTextPaint.setColor(mTextColor);
            } else {
                mLinePaint.setColor(getResources().getColor(R.color.gray));
                mTextPaint.setColor(getResources().getColor(R.color.gray));
            }
            canvas.drawLine(x, bottomBase, x, bottomBase - y, mLinePaint);
            canvas.drawCircle(x, bottomBase - y, radius, mLinePaint);
            canvas.drawText(String.format("%1$.2fdB", yAxisList.get(i)), x, bottomBase - y - 3
                    * radius, mTextPaint);
        }

    }

    public void setLineNums(int nums) {
        if (nums < 3) nums = 3;
        if (nums > 6) nums = 6;
        mLineNums = nums;
        invalidate();
    }

    public void setYscale(int scale) {
        yScale = scale;
        invalidate();
    }

    private void calculate_xAxis() {
        xAxisList.clear();
        for (int i = 0; i < mLineNums; i++) {
            float temp = 2 * offset + i * (float) (getWidth() - 4 * offset) / (mLineNums - 1);
            xAxisList.add(temp);
        }
    }

    public void addLine(float height) {
        if (null != yAxisList) {
            if (yAxisList.size() >= mLineNums) {
                yAxisList.remove(0); // remove first;
            }
            yAxisList.add(height);
            // if (mLineNums < mMaxLineNum) mLineNums++;
            // calculate_xAxis();
            invalidate();
        }
    }
}
