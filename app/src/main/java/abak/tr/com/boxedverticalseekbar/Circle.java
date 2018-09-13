package abak.tr.com.boxedverticalseekbar;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Circle extends View {
    private int scrWidth;
    private int scrHeight;
    private Paint mProgressPaint;
    private Paint linePaint;
    private Paint arcPaint;
    private int progressStep = 0;

    public Circle(Context context) {
        super(context);
        init(context, null);
    }

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        // Defaults, may need to link this into theme settings
        int progressColor = ContextCompat.getColor(context, R.color.color_progress);

        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.Height, 0, 0);
            progressColor = typedArray.getColor(R.styleable.Height_progressColor, progressColor);
        }

        mProgressPaint = new Paint();
        mProgressPaint.setStrokeWidth(10);
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#dedede"));
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        arcPaint = new Paint();
        arcPaint.setColor(progressColor);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        linePaint.setStrokeWidth(10);
        float viewPadding = (linePaint.getStrokeWidth() * 2);
        int radius = ((int) (scrWidth < scrHeight ? scrWidth - viewPadding : scrHeight - viewPadding)) / 2;
        radius = radius - (int) (viewPadding / 2);

        //draw progress steps
//        Path path = new Path();
//        path.addCircle((scrWidth / 2),
//                (scrHeight / 2), radius,
//                Path.Direction.CW);
        final RectF oval = new RectF();
        oval.set((scrWidth / 2) - radius,
                (scrHeight / 2) - radius,
                (scrWidth / 2) + radius,
                (scrHeight / 2) + radius);
        switch (progressStep) {

            case 1:
                canvas.drawArc(oval, -90, 90, true, arcPaint);
                break;
            case 2:
                canvas.drawArc(oval, -90, 180, true, arcPaint);
                break;

            case 3:
                canvas.drawArc(oval, -90, 270, true, arcPaint);
                break;

            case 4:
                canvas.drawArc(oval, -90, 360, true, arcPaint);
                break;

            default:
                canvas.drawArc(oval, 0, 0, true, arcPaint);
                break;

        }
//        canvas.drawArc(oval, -90, 90, true, arcPaint);
        //-90-90 -->1
        //-90-180 -->2
        //-90-270 -->3
        //-90-360 -->4

        //circle
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, (radius), linePaint);

        //horizontal diameter
        linePaint.setStrokeWidth(5);
        canvas.drawLine(viewPadding, scrHeight / 2, radius * 2, scrHeight / 2, linePaint);

        //vertical diameter
        canvas.drawLine(scrWidth / 2, viewPadding, scrWidth / 2, scrHeight - viewPadding, linePaint);


    }

    public void setProgressStep(int progressStep) {
        this.progressStep = progressStep;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        scrWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        scrHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_UP:
                setPressed(false);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        double mTouchX = convertTouchEventPoint(event.getX());
        double mTouchY = convertTouchEventPoint(event.getY());
        updateProgress((float) Math.round(mTouchX), (float) Math.round(mTouchY));
    }

    private double convertTouchEventPoint(float yPos) {
        float wReturn;

        if (yPos > (scrHeight * 2)) {
            wReturn = scrHeight * 2;
            return wReturn;
        } else if (yPos < 0) {
            wReturn = 0;
        } else {
            wReturn = yPos;
        }

        return wReturn;
    }

    private void updateProgress(float mTouchX, float mTouchY) {
        //calculate which part need to be colored (1/2,1/3,1/4,4/4)
        if (mTouchY < (scrHeight / 2)) {
            progressStep = mTouchX < (scrWidth / 2) ? 4 : 1;
        } else {
            progressStep = (mTouchX < (scrWidth / 2)) ? 3 : 2;
        }
        invalidate();
    }
}