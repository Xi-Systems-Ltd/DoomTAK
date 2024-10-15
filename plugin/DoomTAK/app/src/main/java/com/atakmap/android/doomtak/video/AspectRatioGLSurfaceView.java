package com.atakmap.android.doomtak.video;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class AspectRatioGLSurfaceView extends GLSurfaceView {

    private float aspectRatio = 16f / 10f;

    public AspectRatioGLSurfaceView(Context context) {
        super(context);
    }

    public AspectRatioGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(float widthRatio, float heightRatio) {
        aspectRatio = widthRatio / heightRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Calculate height based on aspect ratio.
        int calculatedHeight = (int) (originalWidth / aspectRatio);
        if (calculatedHeight > originalHeight) {
            // If the calculated height is too tall, adjust the width.
            int calculatedWidth = (int) (originalHeight * aspectRatio);
            setMeasuredDimension(calculatedWidth, originalHeight);
        } else {
            // Otherwise, use the original width and calculated height.
            setMeasuredDimension(originalWidth, calculatedHeight);
        }
    }
}
