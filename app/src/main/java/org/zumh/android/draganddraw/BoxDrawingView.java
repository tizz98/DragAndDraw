package org.zumh.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final String BOXES_KEY = "boxes";
    private static final String SAVED_STATE_KEY = "saved_state";

    private Box mCurrentBox;
    private ArrayList<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    private int mFirstPointerId = 0;
    private PointF mModPtrDown;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // fill the background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            float px = right - (float) Math.floor((right - left) / 2);
            float py = top - (float) Math.floor((top - bottom) / 2);

            canvas.save();
            canvas.rotate(box.getRotation(), px, py);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIdx = event.getActionIndex();
        int ptrId = event.getPointerId(actionIdx);

        PointF current = new PointF(event.getX(actionIdx), event.getY(actionIdx));
        String action = "";

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // reset drawing state
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN";
                mModPtrDown = current;
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBox != null && ptrId == mFirstPointerId) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP";
                if (ptrId == mFirstPointerId) {
                    mCurrentBox = null;
                } else {
                    // angle it!
                    float angle = (float) Math.toDegrees(Math.atan2(mModPtrDown.y - current.y, mModPtrDown.x - current.x));
                    mCurrentBox.setRotation(angle);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " w/ ptrid=" + ptrId + ", actionIdx=" + actionIdx + " at x=" + current.x + ", y=" + current.y);
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_STATE_KEY, super.onSaveInstanceState());
        bundle.putParcelableArrayList(BOXES_KEY, mBoxen);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable savedState = bundle.getParcelable(SAVED_STATE_KEY);

        mBoxen = bundle.getParcelableArrayList(BOXES_KEY);
        super.onRestoreInstanceState(savedState);
    }
}
