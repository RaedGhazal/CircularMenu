package com.raedghazal.circularmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CircularMenu extends View {

    private Context context;
    private AttributeSet attrs;
    private ArrayList<Pair<Integer, String>> iconsTexts = new ArrayList<>();
    private int textSize = 45;
    private String centerText = "";
    private int centerTextSize = 70;
    private int defaultItemsColor = Color.parseColor("#E1288F");
    private int selectedItemColor = Color.parseColor("#8BC63E");
    private boolean isSelected = false;
    private int selectedPosition;
    private int textColor = Color.WHITE;
    private int strokeWidth = 40;

    private int itemCount = 1;
    private int innerRadius = 200;
    private int outerRadius = 500;
    private int strokeColor = Color.WHITE;
    private int innerCircleColor = Color.WHITE;
    private int centerTextColor = Color.BLACK;
    private ArrayList<Pair<Float, Float>> items = new ArrayList<>();

    private final static int TOTAL_DEGREE =360;
    private final static int START_DEGREE = -90;

    private Paint mPaint;
    private RectF mOvalRect = null;

    private int mSweepAngle;

    private ArrayList<Bitmap> mCenterIcon = new ArrayList<>();
    private Pair<Float, Float> centerPoint;

    private OnClick onClick = null;

    public CircularMenu(Context context) {
        this(context, null);
    }

    public CircularMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;

        manageAttributes();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSweepAngle = TOTAL_DEGREE / itemCount;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX(), y = event.getY();
            float def = Math.abs(items.get(0).first - x) + Math.abs(items.get(0).second - y);
            int itemPos = 0;
            for (int i = 1; i < items.size(); i++) {
                float current = Math.abs(items.get(i).first - x) + Math.abs(items.get(i).second - y);
                if (current < def) {
                    def = current;
                    itemPos = i;
                }
            }
            float defCenter = Math.abs(centerPoint.first - x) + Math.abs(centerPoint.second - y);
            {
                if (onClick != null)
                    onClick.onClick(itemPos);
            }
            if (defCenter > def)
                if (def < outerRadius / 2.0) {
                    setSelectedPosition(itemPos);
                    invalidate();
                }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        if (mOvalRect == null) {
            mOvalRect = new RectF(width / 2.0f - outerRadius, height / 2.0f - outerRadius, width / 2.0f + outerRadius, height / 2.0f + outerRadius);
        }

        for (int i = 0; i < itemCount; i++) {
            int startAngle = START_DEGREE + i * mSweepAngle;


            mPaint.setColor((isSelected && i == selectedPosition) ? selectedItemColor : defaultItemsColor);
            mPaint.setStyle(Paint.Style.FILL);
            RectF selectedItemOvalRect = new RectF((width / 2.0f - outerRadius) - 30, (height / 2.0f - outerRadius) - 30, width / 2.0f + outerRadius + 30, height / 2.0f + outerRadius + 30);

            canvas.drawArc((isSelected && i == selectedPosition) ? selectedItemOvalRect : mOvalRect
                    , startAngle, mSweepAngle, true, mPaint);

            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(strokeColor);
            mPaint.setStyle(Paint.Style.STROKE);

            canvas.drawArc((isSelected && i == selectedPosition) ? selectedItemOvalRect : mOvalRect,
                    startAngle, mSweepAngle, true, mPaint);

            int centerX = (int) ((outerRadius + innerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2.0f)));
            int centerY = (int) ((outerRadius + innerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2.0f)));
            items.add(new Pair<>(width / 2.0f + centerX, height / 2.0f + centerY));
            if (iconsTexts.size() > 0 && i < iconsTexts.size()) {
                canvas.drawBitmap(mCenterIcon.get(i), width / 2.0f + centerX - mCenterIcon.get(i).getWidth() / 2.0f,
                        height / 2.0f + centerY - mCenterIcon.get(i).getHeight(), null);
                String[] str = iconsTexts.get(i).second.split("\n");
                mPaint.setColor(textColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(textSize);
                for (int k = 0; k < str.length; k++)
                    canvas.drawText(
                            str[k], width / 2.0f + centerX - mCenterIcon.get(i).getWidth() / 2.0f - (str[k].length() / 2.0f * textSize / 5),
                            height / 2.0f + centerY + mCenterIcon.get(i).getHeight() - mCenterIcon.get(i).getHeight() / 2.2f + k * textSize, mPaint);

            }
        }

        mPaint.setColor(innerCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2.0f, height / 2.0f, innerRadius, mPaint);

        mPaint.setColor(centerTextColor);
        mPaint.setTextSize(centerTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        centerPoint = new Pair<>(width / 2.0f, height / 2.0f);
        String[] txt = centerText.split("\n");
        for (int i = 0; i < txt.length; i++) {
            canvas.drawText(txt[i], width / 2.0f, height / 2.0f + (i * centerTextSize), mPaint);
        }
        super.onDraw(canvas);
    }

    public void manageAttributes()
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularMenu);
        centerText = a.getString(R.styleable.CircularMenu_centerText);
        textSize = (int)a.getDimension(R.styleable.CircularMenu_textSize, textSize);
        itemCount = a.getInt(R.styleable.CircularMenu_itemCount, itemCount);
        innerRadius = (int)a.getDimension(R.styleable.CircularMenu_innerRadius, innerRadius);
        outerRadius = (int)a.getDimension(R.styleable.CircularMenu_outerRadius, outerRadius);
        strokeWidth = (int)a.getDimension(R.styleable.CircularMenu_strokeWidth, strokeWidth);
        strokeColor = a.getInt(R.styleable.CircularMenu_strokeColor, strokeColor);
        centerTextSize = (int)a.getDimension(R.styleable.CircularMenu_centerTextSize, centerTextSize);
        centerTextColor = a.getInt(R.styleable.CircularMenu_centerTextColor, centerTextColor);
        innerCircleColor = a.getInt(R.styleable.CircularMenu_innerCircleColor, innerCircleColor);
        selectedItemColor = a.getInt(R.styleable.CircularMenu_selectedItemColor, selectedItemColor);
        defaultItemsColor = a.getInt(R.styleable.CircularMenu_defaultItemsColor, defaultItemsColor);

        a.recycle();
    }
    public void setSelectedPosition(int selectedPosition) {
        if (isSelected && this.selectedPosition == selectedPosition) {
            this.selectedPosition = -1;
            isSelected = false;
        } else {
            this.selectedPosition = selectedPosition;
            isSelected = true;
        }
    }

    public void setIconsTexts(ArrayList<Pair<Integer, String>> iconsTexts) {
        this.iconsTexts = iconsTexts;
        for (int i = 0; i < iconsTexts.size(); i++) {
            mCenterIcon.add(BitmapFactory.decodeResource(getResources(), iconsTexts.get(i).first));
        }
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        invalidate();
    }

    public void setInnerRadius(int innerRadius) {
        this.innerRadius = dpToPixels(innerRadius);
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = dpToPixels(strokeWidth);
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
        invalidate();
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        invalidate();
    }

    public void setSelectedItemColor(int selectedItemColor) {
        this.selectedItemColor = selectedItemColor;
        invalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = dpToPixels(textSize);
        ;
        invalidate();
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        invalidate();
    }

    public void setDefaultItemsColor(int defaultItemsColor) {
        this.defaultItemsColor = defaultItemsColor;
        invalidate();
    }

    public void setCenterTextSize(int centerTextSize) {
        this.centerTextSize = dpToPixels(centerTextSize);
        ;
        invalidate();
    }

    public void setOuterRadius(int outerRadius) {
        this.outerRadius = dpToPixels(outerRadius);
        invalidate();
    }

    public ArrayList<Pair<Integer, String>> getIconsTexts() {
        return iconsTexts;
    }

    public int getSelectedItemColor() {
        return selectedItemColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public String getCenterText() {
        return centerText;
    }

    public int getDefaultItemsColor() {
        return defaultItemsColor;
    }

    public int getCenterTextSize() {
        return centerTextSize;
    }

    public int getOuterRadius() {
        return pxToDp(outerRadius);
    }

    public int getInnerRadius() {
        return pxToDp(innerRadius);
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public int getInnerCircleColor() {
        return innerCircleColor;
    }

    public int getCenterTextColor() {
        return centerTextColor;
    }

    public String getItemTextAt(int itemPosition) {
        return iconsTexts.get(itemPosition).second;
    }

    public void onClick(OnClick onClick) {
        this.onClick = onClick;
        invalidate();
    }

    public int dpToPixels(final float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public int pxToDp(final float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dps - 0.5f) / scale);
    }

    public interface OnClick {
        void onClick(int itemPosition);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSweepAngle = TOTAL_DEGREE / itemCount;
    }
}
/*
package com.raedghazal.circularmenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CircularMenu extends View {

    private Context context;
    private ArrayList<Pair<Integer, String>> iconsTexts = new ArrayList<>();
    private int textSize = 45;
    private String centerText = "";
    private int centerTextSize = 70;
    private int defaultItemsColor = Color.parseColor("#E1288F");
    private int selectedItemColor = Color.parseColor("#8BC63E");
    private boolean isSelected = false;
    private int selectedPosition;
    private int textColor = Color.WHITE;
    private int strokeWidth = 40;

    private int itemCount = 1;
    private int innerRadius = 200;
    private int outerRadius = 500;
    private int strokeColor = Color.WHITE;
    private int innerCircleColor = Color.WHITE;
    private int centerTextColor = Color.BLACK;
    private ArrayList<Pair<Float, Float>> items = new ArrayList<>();

    private final static int TOTAL_DEGREE = 360;
    private final static int START_DEGREE = -90;

    private Paint mPaint;
    private RectF mOvalRect = null;

    private int mSweepAngle;

    private ArrayList<Bitmap> mCenterIcon = new ArrayList<>();
    private Pair<Float, Float> centerPoint;

    private OnClick onClick=null;

    public CircularMenu(Context context) {
        this(context,null);
    }

    public CircularMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircularMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSweepAngle = TOTAL_DEGREE / itemCount;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX(), y = event.getY();
            float def = Math.abs(items.get(0).first - x) + Math.abs(items.get(0).second - y);
            int itemPos = 0;
            for (int i = 1; i < items.size(); i++) {
                float current = Math.abs(items.get(i).first - x) + Math.abs(items.get(i).second - y);
                if (current < def) {
                    def = current;
                    itemPos = i;
                }
            }
            float defCenter = Math.abs(centerPoint.first - x) + Math.abs(centerPoint.second - y);
            {
                if (onClick != null)
                    onClick.onClick(itemPos);
            }
            if (defCenter > def)
                if (def < outerRadius / 2.0) {
                    setSelectedPosition(itemPos);
                    invalidate();
                }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        if (mOvalRect == null) {
            mOvalRect = new RectF(width / 2.0f - outerRadius, height / 2.0f - outerRadius, width / 2.0f + outerRadius, height / 2.0f + outerRadius);
        }

        for (int i = 0; i < itemCount; i++) {
            int startAngle = START_DEGREE + i * mSweepAngle;


            mPaint.setColor((isSelected && i == selectedPosition) ? selectedItemColor : defaultItemsColor);
            mPaint.setStyle(Paint.Style.FILL);
            RectF selectedItemOvalRect = new RectF((width / 2.0f - outerRadius) - 30, (height / 2.0f - outerRadius) - 30, width / 2.0f + outerRadius + 30, height / 2.0f + outerRadius + 30);

            canvas.drawArc((isSelected && i == selectedPosition) ? selectedItemOvalRect : mOvalRect
                    , startAngle, mSweepAngle, true, mPaint);

            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(strokeColor);
            mPaint.setStyle(Paint.Style.STROKE);

            canvas.drawArc((isSelected && i == selectedPosition) ? selectedItemOvalRect : mOvalRect,
                    startAngle, mSweepAngle, true, mPaint);

            int centerX = (int) ((outerRadius + innerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2.0f)));
            int centerY = (int) ((outerRadius + innerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2.0f)));
            items.add(new Pair<>(width / 2.0f + centerX, height / 2.0f + centerY));
            if (iconsTexts.size() > 0 && i < iconsTexts.size()) {
                canvas.drawBitmap(mCenterIcon.get(i), width / 2.0f + centerX - mCenterIcon.get(i).getWidth() / 2.0f,
                        height / 2.0f + centerY - mCenterIcon.get(i).getHeight(), null);
                String[] str = iconsTexts.get(i).second.split("\n");
                mPaint.setColor(textColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(textSize);
                for (int k = 0; k < str.length; k++)
                    canvas.drawText(
                            str[k], width / 2.0f + centerX - mCenterIcon.get(i).getWidth() / 2.0f - (str[k].length() / 2.0f * textSize / 5),
                            height / 2.0f + centerY + mCenterIcon.get(i).getHeight() - mCenterIcon.get(i).getHeight() / 2.2f + k * textSize, mPaint);

            }
        }

        mPaint.setColor(innerCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2.0f, height / 2.0f, innerRadius, mPaint);

        mPaint.setColor(centerTextColor);
        mPaint.setTextSize(centerTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        centerPoint = new Pair<>(width / 2.0f, height / 2.0f);
        String[] txt = centerText.split("\n");
        for (int i = 0; i < txt.length; i++) {
            canvas.drawText(txt[i], width / 2.0f, height / 2.0f + (i * centerTextSize), mPaint);
        }
        super.onDraw(canvas);
    }

    public void setSelectedPosition(int selectedPosition) {
        if (isSelected && this.selectedPosition == selectedPosition) {
            this.selectedPosition = -1;
            isSelected = false;
        } else {
            this.selectedPosition = selectedPosition;
            isSelected = true;
        }
    }

    public void setIconsTexts(ArrayList<Pair<Integer, String>> iconsTexts) {
        this.iconsTexts = iconsTexts;
        for (int i = 0; i < iconsTexts.size(); i++) {
            mCenterIcon.add(BitmapFactory.decodeResource(getResources(), iconsTexts.get(i).first));
        }
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        invalidate();
    }

    public void setInnerRadius(int innerRadius) {
        this.innerRadius = dpToPixels(innerRadius);
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = dpToPixels(strokeWidth);
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
        invalidate();
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        invalidate();
    }

    public void setSelectedItemColor(int selectedItemColor) {
        this.selectedItemColor = selectedItemColor;
        invalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = dpToPixels(textSize);
        ;
        invalidate();
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        invalidate();
    }

    public void setDefaultItemsColor(int defaultItemsColor) {
        this.defaultItemsColor = defaultItemsColor;
        invalidate();
    }

    public void setCenterTextSize(int centerTextSize) {
        this.centerTextSize = dpToPixels(centerTextSize);
        ;
        invalidate();
    }

    public void setOuterRadius(int outerRadius) {
        this.outerRadius = dpToPixels(outerRadius);
        invalidate();
    }

    public ArrayList<Pair<Integer, String>> getIconsTexts() {
        return iconsTexts;
    }

    public int getSelectedItemColor() {
        return selectedItemColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public String getCenterText() {
        return centerText;
    }

    public int getDefaultItemsColor() {
        return defaultItemsColor;
    }

    public int getCenterTextSize() {
        return centerTextSize;
    }

    public int getOuterRadius() {
        return pxToDp(outerRadius);
    }

    public int getInnerRadius() {
        return pxToDp(innerRadius);
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public int getInnerCircleColor() {
        return innerCircleColor;
    }

    public int getCenterTextColor() {
        return centerTextColor;
    }

    public String getItemTextAt(int itemPosition) {
        return iconsTexts.get(itemPosition).second;
    }

    public void onClick(OnClick onClick) {
        this.onClick = onClick;
        invalidate();
    }

    public int dpToPixels(final float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public int pxToDp(final float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dps - 0.5f) / scale);
    }

    public interface OnClick {
        void onClick(int itemPosition);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSweepAngle = TOTAL_DEGREE / itemCount;
    }
}
*/
