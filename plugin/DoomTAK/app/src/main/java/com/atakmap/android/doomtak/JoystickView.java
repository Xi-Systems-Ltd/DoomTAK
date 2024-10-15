package com.atakmap.android.doomtak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private float baseRadius;
    private float thumbRadius;
    private PointF baseCenter;
    private PointF thumbPosition;
    private Paint basePaint;
    private Paint thumbPaint;
    private JoystickListener joystickListener;

    private final Handler updateHandler = new Handler();
    private boolean isMoving = false;

    public JoystickView(Context context) {
        super(context);
        init();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        basePaint = new Paint();
        basePaint.setColor(0x80444444);
        basePaint.setStyle(Paint.Style.FILL);

        thumbPaint = new Paint();
        thumbPaint.setColor(0x80888888);
        thumbPaint.setStyle(Paint.Style.FILL);

        baseCenter = new PointF();
        thumbPosition = new PointF();

        // Default values.
        baseRadius = 300;
        thumbRadius = 100;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        baseCenter.set(w / 2f, h / 2f);
        thumbPosition.set(baseCenter.x, baseCenter.y);
        baseRadius = Math.min(w, h) / 3f;
        thumbRadius = baseRadius / 3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(baseCenter.x, baseCenter.y, baseRadius, basePaint);
        canvas.drawCircle(thumbPosition.x, thumbPosition.y, thumbRadius, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (joystickListener != null) {
                    joystickListener.onJoystickDown();
                }
            case MotionEvent.ACTION_MOVE:
                isMoving = true;
                updateThumbPosition(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
                if (joystickListener != null) {
                    joystickListener.onJoystickUp();
                }
                isMoving = false;
                thumbPosition.set(baseCenter.x, baseCenter.y);
                notifyJoystickListener(0, 0); // Reset notification.
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void updateThumbPosition(float touchX, float touchY) {
        float dx = touchX - baseCenter.x;
        float dy = touchY - baseCenter.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < baseRadius) {
            thumbPosition.set(touchX, touchY);
        } else {
            float ratio = baseRadius / distance;
            thumbPosition.set(baseCenter.x + dx * ratio, baseCenter.y + dy * ratio);
        }

        notifyJoystickListener((thumbPosition.x - baseCenter.x) / baseRadius,
                (thumbPosition.y - baseCenter.y) / baseRadius);

        invalidate();
    }

    private void notifyJoystickListener(float xPercent, float yPercent) {
        if (joystickListener != null) {
            joystickListener.onJoystickMoved(xPercent, yPercent);
        }
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMoving) {
                notifyJoystickListener((thumbPosition.x - baseCenter.x) / baseRadius,
                        (thumbPosition.y - baseCenter.y) / baseRadius);
            }
            updateHandler.postDelayed(this, 100); // Update every 100 ms.
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateHandler.post(updateRunnable); // Start the update loop.
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateHandler.removeCallbacks(updateRunnable); // Stop the update loop.
    }

    public void setJoystickListener(JoystickListener listener) {
        this.joystickListener = listener;
    }

    public interface JoystickListener {
        void onJoystickDown();
        void onJoystickUp();
        void onJoystickMoved(float xPercent, float yPercent);
    }
}