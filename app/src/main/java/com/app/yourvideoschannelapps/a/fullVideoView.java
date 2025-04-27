package com.app.yourvideoschannelapps.a;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public  class fullVideoView extends VideoView {

    public fullVideoView(Context context) {
        super(context);
    }

    public fullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public fullVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public fullVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //这里可以看到是将其设为0，不走其他的逻辑。可以去看源码原生逻辑，是还有判断的。
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}

