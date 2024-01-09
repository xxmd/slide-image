package io.github.xxmd;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

public class SlideImage extends View {
    private int progress = 50;
    private Paint indicatorPaint;
    private Bitmap leftBitmap;
    private Bitmap rightBitmap;
    private float indicatorRadius = 32;
    //    private Region touchRegion;
    private int canvasWidth;
    private Paint arrowPaint;
    private TypedArray typedArray;
    private int preWidthMeasureSpec;
    private int preHeightMeasureSpec;

    public SlideImage(Context context, Bitmap leftBitmap, Bitmap rightBitmap) {
        super(context);
        this.leftBitmap = leftBitmap;
        this.rightBitmap = rightBitmap;
        init();
    }

    public void setLeftBitmap(@DrawableRes int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        setLeftBitmap(bitmap);
    }

    public void setLeftBitmap(Bitmap leftBitmap) {
        this.leftBitmap = leftBitmap;
        requestLayout();
    }

    public void setRightBitmap(@DrawableRes int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        setRightBitmap(bitmap);
    }

    public void setRightBitmap(Bitmap rightBitmap) {
        this.rightBitmap = rightBitmap;
        requestLayout();
    }

    private void reMeasure() {
    }

    public SlideImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideImage);
        init();
    }

    private void init() {
        if (typedArray != null) {
            initByTypedArray();
        }
        initPaint();
    }

    private void initByTypedArray() {
        BitmapDrawable leftDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.SlideImage_leftBitmap);
        BitmapDrawable rightDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.SlideImage_rightBitmap);
        if (leftDrawable == null || rightDrawable == null) {
            Log.e(SlideImage.class.getName(), "leftBitmap and rightBitmap can not be null");
            return;
        }
        leftBitmap = leftDrawable.getBitmap();
        rightBitmap = rightDrawable.getBitmap();
    }

    private void initPaint() {
        indicatorPaint = new Paint();
        indicatorPaint.setColor(Color.WHITE);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setStrokeWidth(2);

        arrowPaint = new Paint(indicatorPaint);
        arrowPaint.setColor(Color.parseColor("#2d7df9"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (leftBitmap == null || rightBitmap == null) {
            Log.e(SlideImage.class.getName(), "leftBitmap and rightBitmap can not be null");
            return;
        }
        canvasWidth = canvas.getWidth();
        indicatorRadius = canvasWidth * 0.04f;
        drawLeftImage(canvas);
        drawRightImage(canvas);
        drawIndicator(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        // when move to both ends, auto sticker.
        if (x <= indicatorRadius * 2) {
            progress = 0;
        } else if (x >= canvasWidth - indicatorRadius * 2) {
            progress = 100;
        } else {
            progress = (int) (event.getX() * 1f / canvasWidth * 100);
        }
        System.out.println(progress);
        invalidate();
        return true;
    }

    private void drawLeftImage(Canvas canvas) {
        RectF dest = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(leftBitmap, null, dest, new Paint());
    }

    private void drawIndicator(Canvas canvas) {
        float x = getIndicatorMiddleX(canvas);
        canvas.drawLine(x, 0, x, canvas.getHeight(), indicatorPaint);

        // draw circle background
        int circleBgColor = ColorUtils.setAlphaComponent(indicatorPaint.getColor(), (int) (255 * 0.7f));
        Paint paint = new Paint(indicatorPaint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circleBgColor);
        canvas.drawCircle(x, canvas.getHeight() / 2, indicatorRadius, paint);

        // draw circle border
        canvas.drawCircle(x, canvas.getHeight() / 2, indicatorRadius, indicatorPaint);

        // draw left and right blue arrow
        drawArrow(canvas);
//        touchRegion = new Region((int) (x - indicatorRadius), 0, (int) (x + indicatorRadius), canvas.getHeight());
    }

    private void drawArrow(Canvas canvas) {
        canvas.save();
        canvas.translate(getIndicatorMiddleX(canvas), canvas.getHeight() / 2);
        int onePercentSix = (int) (-indicatorRadius * 1.0 / 3);

        Path leftArrow = new Path();
        leftArrow.moveTo(-onePercentSix, -onePercentSix);
        leftArrow.lineTo(-onePercentSix * 2, 0);
        leftArrow.lineTo(-onePercentSix, onePercentSix);

        Path rightArrow = new Path();
        rightArrow.moveTo(onePercentSix, -onePercentSix);
        rightArrow.lineTo(onePercentSix * 2, 0);
        rightArrow.lineTo(onePercentSix, onePercentSix);

        canvas.drawPath(leftArrow, arrowPaint);
        canvas.drawPath(rightArrow, arrowPaint);
        canvas.restore();
    }


    private void drawRightImage(Canvas canvas) {
        Rect src = new Rect((int) (rightBitmap.getWidth() * progress * 1.0f / 100), 0, rightBitmap.getWidth(), rightBitmap.getHeight());

        RectF dest = new RectF();
        dest.left = getIndicatorMiddleX(canvas);
        dest.top = 0;
        dest.right = canvas.getWidth();
        dest.bottom = canvas.getHeight();
        canvas.drawBitmap(rightBitmap, src, dest, new Paint());
    }

    private float getIndicatorMiddleX(Canvas canvas) {
        return canvas.getWidth() * progress * 1.0f / 100;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (leftBitmap == null || rightBitmap == null) {
            Log.e(SlideImage.class.getName(), "leftBitmap and rightBitmap can not be null");
            setMeasuredDimension(0, 0);
            preWidthMeasureSpec = widthMeasureSpec;
            preHeightMeasureSpec = heightMeasureSpec;
            return;
        }
        float ratio = leftBitmap.getWidth() * 1.0f / leftBitmap.getHeight();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, (int) (width / ratio));
            return;
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension((int) (height * ratio), height);
            return;
        }
    }
}
