package org.zumh.android.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class Box implements Parcelable {
    private PointF mOrigin;
    private PointF mCurrent;
    private float mRotation;  // in degrees

    public Box(PointF origin) {
        this(origin, 0);
    }

    public Box(PointF origin, float rotation) {
        mOrigin = origin;
        mCurrent = origin;
        mRotation = rotation;
    }

    public Box(Parcel source) {
        mOrigin.readFromParcel(source);
        mCurrent.readFromParcel(source);
        mRotation = source.readFloat();
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrent() {

        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public float getRotation() {
        return mRotation;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mOrigin.writeToParcel(dest, flags);
        mCurrent.writeToParcel(dest, flags);
        dest.writeFloat(mRotation);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Box createFromParcel(Parcel source) {
            return new Box(source);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };
}
