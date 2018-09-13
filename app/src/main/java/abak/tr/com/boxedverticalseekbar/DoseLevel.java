package abak.tr.com.boxedverticalseekbar;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DoseLevel extends View {

    private static final int MAX = 100;
    private static final int MIN = 0;

    /**
     * The min value of progress value.
     */
    private int mMin = MIN;

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private int mMax = MAX;

    /**
     * The increment/decrement value for each movement of progress.
     */
    private int mStep = 10;

    private String unit = "";

    private int drawnStep = 2;

    /**
     * The corner radius of the view.
     */
    private int mCornerRadius = 10;

    /**
     * Text size in SP.
     */
    private float mTextSize = 26;

    /**
     * Text bottom padding in pixel.
     */
    private int mtextBottomPadding = 20;

    private int mPoints;

    private boolean mEnabled = true;
    /**
     * Enable or disable text .
     */
    private boolean mtextEnabled = true;

    /**
     * Enable or disable image .
     */
    private boolean mImageEnabled = false;

    /**
     * mTouchDisabled touches will not move the slider
     * only swipe motion will activate it
     */
    private boolean mTouchDisabled = true;

    private float mProgressSweep = 0;
    private Paint mProgressPaint;
    private Paint mTextPaint;
    private Paint linePaint;
    private Paint levelPaint;
    private int scrWidth;
    private int scrHeight;
    private OnValuesChangeListener mOnValuesChangeListener;
    private int backgroundColor;
    private int mDefaultValue;
    private Rect dRect = new Rect();
    private boolean firstRun = true;
    private int unitDrawnWidth = 100;

    public DoseLevel(Context context) {
        super(context);
        init(context, null);
    }

    public DoseLevel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        System.out.println("INIT");
        float density = getResources().getDisplayMetrics().density;

        // Defaults, may need to link this into theme settings
        int progressColor = ContextCompat.getColor(context, R.color.color_progress);
        backgroundColor = ContextCompat.getColor(context, R.color.Green);

        int textColor = ContextCompat.getColor(context, R.color.color_text);
        mTextSize = (int) (mTextSize * density);
        mDefaultValue = mMax / 2;

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.Height, 0, 0);

            mPoints = a.getInteger(R.styleable.Height_points, mPoints);
            mMax = a.getInteger(R.styleable.Height_max, mMax);
            mMax = a.getInteger(R.styleable.Height_max, mMax);
            mStep = a.getInteger(R.styleable.Height_step, mStep);
            unit = a.getString(R.styleable.Height_unit);
            drawnStep = a.getInteger(R.styleable.Height_drawnStep, drawnStep);
            mDefaultValue = a.getInteger(R.styleable.Height_defaultValue, mDefaultValue);
            mtextBottomPadding = a.getInteger(R.styleable.Height_textBottomPadding, mtextBottomPadding);

            progressColor = a.getColor(R.styleable.Height_progressColor, progressColor);
            backgroundColor = a.getColor(R.styleable.Height_backgroundColor, backgroundColor);

            mTextSize = (int) a.getDimension(R.styleable.Height_textSize, mTextSize);
            textColor = a.getColor(R.styleable.Height_textColor, textColor);

            mEnabled = a.getBoolean(R.styleable.Height_enabled, mEnabled);
            mTouchDisabled = a.getBoolean(R.styleable.Height_touchDisabled, mTouchDisabled);
            mtextEnabled = a.getBoolean(R.styleable.Height_textEnabled, mtextEnabled);

            mPoints = mDefaultValue;

            a.recycle();
        }

        // range check
        mPoints = (mPoints > mMax) ? mMax : mPoints;
        mPoints = (mPoints < mMin) ? mMin : mPoints;

        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#dedede"));
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(10);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setTextSize(mTextSize);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        levelPaint = new Paint();
        levelPaint = linePaint;
        levelPaint.setTextAlign(Paint.Align.RIGHT);

        scrHeight = context.getResources().getDisplayMetrics().heightPixels;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        scrWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        scrHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        //sufficent spacce to write unit left side view then else 0
        if (scrWidth < (unitDrawnWidth * 2))
            unitDrawnWidth = 0;
        mProgressPaint.setStrokeWidth(scrWidth - unitDrawnWidth);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //whole view inside color
        Paint paint = new Paint();
        paint.setAlpha(255);
        canvas.translate(0, 0);
        Path mPath = new Path();
        mPath.addRoundRect(new RectF(0, 0, scrWidth, scrHeight), mCornerRadius, mCornerRadius, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        paint.setColor(backgroundColor);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, scrWidth, scrHeight, paint);

        //progress level inside view
        canvas.drawLine((canvas.getWidth() / 2) + (unitDrawnWidth / 2), canvas.getHeight(), (canvas.getWidth() / 2) + (unitDrawnWidth / 2), mProgressSweep, mProgressPaint);

        //left right and bottom line of view
        canvas.drawLine(unitDrawnWidth, 0, unitDrawnWidth, canvas.getHeight(), linePaint);//left line
        canvas.drawLine(canvas.getWidth()-5, 0, canvas.getWidth()-5, canvas.getHeight(), linePaint);//right line
        canvas.drawLine(unitDrawnWidth, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), linePaint);//bottom line


        //If image is disabled and text is enabled show text
        if (mtextEnabled) {
            String strPoint = String.valueOf(mPoints);
            drawText(canvas, mTextPaint, strPoint);
        }
        if (firstRun) {
            firstRun = false;
            setValue(mPoints);
        }

        Rect bounds = new Rect();
        String text;
        //draw label one greater aother smaller
        for (int i = 0 + drawnStep; i < mMax; i = i + drawnStep) {
            int tempy = getCanvasHeightForValue(i);
            text = "" + i + "" + unit;

            //level text
            levelPaint.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, unitDrawnWidth, tempy + (bounds.height() / 2), levelPaint);

            if (i % (drawnStep * 2) == 0) {
                canvas.drawLine(unitDrawnWidth, tempy, unitDrawnWidth + (canvas.getWidth() / 4), tempy, linePaint);
            } else {
                canvas.drawLine(unitDrawnWidth, tempy, unitDrawnWidth + (canvas.getWidth() / 6), tempy, linePaint);
            }
        }
    }

    public int getCanvasHeightForValue(int points) {
        points = points > mMax ? mMax : points;
        points = points < mMin ? mMin : points;
        points = mMax - points;
        double r = ((double) scrHeight / mMax) * points;
        return (int) r;
    }

    private void drawText(Canvas canvas, Paint paint, String text) {
        text += "" + unit;
        canvas.getClipBounds(dRect);
        int cWidth = dRect.width() + unitDrawnWidth;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), dRect);
        float x = cWidth / 2f - dRect.width() / 2f - dRect.left;
        canvas.drawText(text, x, canvas.getHeight() - mtextBottomPadding, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {

            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mOnValuesChangeListener != null)
                        mOnValuesChangeListener.onStartTrackingTouch(this);

                    if (!mTouchDisabled)
                        updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mOnValuesChangeListener != null)
                        mOnValuesChangeListener.onStopTrackingTouch(this);
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mOnValuesChangeListener != null)
                        mOnValuesChangeListener.onStopTrackingTouch(this);
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * Update the UI components on touch events.
     *
     * @param event MotionEvent
     */
    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        double mTouch = convertTouchEventPoint(event.getY());
        int progress = (int) Math.round(mTouch);
        updateProgress(progress);
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

    private void updateProgress(int progress) {
        mProgressSweep = progress;
        mPoints = progress;

        mPoints = (mPoints > scrHeight) ? scrHeight : mPoints;
        mPoints = (mPoints < 0) ? 0 : mPoints;
        mPoints = ((scrHeight - mPoints) * mMax) / scrHeight;
        mPoints = mPoints - (mPoints % mStep);

        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener
                    .onPointsChanged(this, mPoints);
        }

        invalidate();
    }

    public interface OnValuesChangeListener {
        /**
         * Notification that the point value has changed.
         *
         * @param boxedPoints The SwagPoints view whose value has changed
         * @param points      The current point value.
         */
        void onPointsChanged(DoseLevel boxedPoints, int points);

        void onStartTrackingTouch(DoseLevel boxedPoints);

        void onStopTrackingTouch(DoseLevel boxedPoints);
    }

    public void setValue(int points) {
        points = points > mMax ? mMax : points;
        points = points < mMin ? mMin : points;
        points = mMax - points;
        double r = ((double) scrHeight / mMax) * points;
        updateProgress((int) r);
    }

    public int getValue() {
        return mPoints;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        if (mMax <= mMin)
            throw new IllegalArgumentException("Max should not be less than zero");
        this.mMax = mMax;
    }


    public int getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(int mDefaultValue) {
        if (mDefaultValue > mMax)
            throw new IllegalArgumentException("Default value should not be bigger than max value.");
        this.mDefaultValue = mDefaultValue;

    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public void setOnBoxedPointsChangeListener(OnValuesChangeListener onValuesChangeListener) {
        mOnValuesChangeListener = onValuesChangeListener;
    }
}
