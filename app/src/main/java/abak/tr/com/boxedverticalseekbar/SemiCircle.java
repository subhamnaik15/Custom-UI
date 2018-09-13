package abak.tr.com.boxedverticalseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Map;


public class SemiCircle extends View {

    private float midx, midy;
    private Paint primaryCirclePaint, linePaint;
    private List<Map.Entry<String, Integer>> entries_dec;
    private float progressPrimaryStrokeWidth = 35;
    private int max = 25;
    private RectF oval;


    public SemiCircle(Context context) {
        super(context);
        init();
    }

    public SemiCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SemiCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        primaryCirclePaint = new Paint();
        primaryCirclePaint.setAntiAlias(true);
        primaryCirclePaint.setStrokeWidth(progressPrimaryStrokeWidth);
        primaryCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.parseColor("#54ffffff"));
        linePaint.setStrokeWidth(convertDpToPixel(2, getContext()));
        oval = new RectF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minWidth = (int) convertDpToPixel(160, getContext());
        int minHeight = (int) convertDpToPixel(160, getContext());

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(minWidth, widthSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If width is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make width equal to height
            width = heightSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(minHeight, heightSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If height is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make height equal to width
            height = widthSize;
        }

        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            width = minWidth;
            height = minHeight;
        }
        progressPrimaryStrokeWidth = height / 2;
        setMeasuredDimension(width, height / 2+10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        midx = canvas.getWidth() / 2;
        midy = canvas.getHeight()-10;
        int radius = (int) midx;
        primaryCirclePaint.setStrokeWidth(progressPrimaryStrokeWidth);


        oval.set(midx/2, midy - radius / 2, midx + radius / 2, midy + radius / 2);
        Log.e("SEMICIRCLE", "" + entries_dec);
        for (int i = 0; i < entries_dec.size(); i++) {
            Log.e("SEMICIRCLE", "" + entries_dec.get(i).getValue() + " - " + i);
            drawProcess(entries_dec.get(i).getValue(), canvas, Color.parseColor(entries_dec.get(i).getKey()));
        }
//        canvas.drawLine(midx, midy, midx, 0, linePaint);
    }

    private void drawProcess(float deg, Canvas canvas, int color) {
        primaryCirclePaint.setColor(color);
        float deg3 = Math.min(deg, max);
        canvas.drawArc(oval, (float) 180, ((deg3) * ((float) 180 / max)), false, primaryCirclePaint);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }

    public void setProgress(List<Map.Entry<String, Integer>> entries_dec) {
        //descending order entries_dec
        if (entries_dec.size() == 0)
            return;
        int deg = entries_dec.get(0).getValue();//as it is in descending order so first value will be max
        setMax(deg);
        this.entries_dec = entries_dec;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
    }


    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
