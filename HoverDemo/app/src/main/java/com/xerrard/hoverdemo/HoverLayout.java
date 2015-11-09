package com.xerrard.hoverdemo;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2015/11/3.
 */
public class HoverLayout extends FrameLayout {
    private int mDefaultTouchSlop;
    private static int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
    private static final float DEFAULT_SPEED = 1.0f;
    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private Rect mChildRect;

    public HoverLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
        fetchAttribute(context, attrs, defStyle);
    }

    public HoverLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HoverLayout(Context context) {
        super(context);
        initialize();
    }

    private void fetchAttribute(Context context, AttributeSet attrs, int defStyle) {
    }

    private void initialize() {
        mDefaultTouchSlop = ViewConfiguration.get(getContext())
                .getScaledTouchSlop(); //获取滑动的最小距离
        mChildRect = new Rect();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false /* no force left gravity */);
    }

    void layoutChildren(int left, int top, int right, int bottom,
                        boolean forceLeftGravity) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                                lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        if (!forceLeftGravity) {
                            childLeft = parentRight - width - lp.rightMargin;
                            break;
                        }
                    case Gravity.LEFT:
                    default:
                        childLeft = parentLeft + lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = parentTop + lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                                lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }

                //child.layout(childLeft, childTop, childLeft + width, childTop + height);
                mChildRect.set(childLeft, childTop, childLeft + width, childTop
                        + height);
                mChildRect.offset(mOffsetX, mOffsetY);
                child.layout(mChildRect.left, mChildRect.top, mChildRect.right,
                        mChildRect.bottom);
            }
        }
    }

    protected int clamp(int src, int limit) {
        if (src > limit) {
            return limit;
        } else if (src < -limit) {
            return -limit;
        }
        return src;
    }

    public void moveToHalf() {
        move(0, getHeight() / 2, true);
    }

    public void move(int deltaX, int deltaY, boolean animation) {
        deltaX = (int) Math.round(deltaX * DEFAULT_SPEED);
        deltaY = (int) Math.round(deltaY * DEFAULT_SPEED);
        moveWithoutSpeed(deltaX, deltaY, animation);
    }

    public void moveWithoutSpeed(int deltaX, int deltaY, boolean animation) {
        int hLimit = getWidth();
        int vLimit = getHeight();
        int newX = clamp(mOffsetX + deltaX, hLimit);
        int newY = clamp(mOffsetY + deltaY, vLimit);
        if (!animation) {
            setOffset(newX, newY);
        } else {
            Point start = new Point(mOffsetX, mOffsetY);
            Point end = new Point(newX, newY);
            /*带有线性插值器（针对x/y坐标）的属性(Point)动画*/
            ValueAnimator anim = ValueAnimator.ofObject(
                    new TypeEvaluator<Point>() {
                        @Override
                        public Point evaluate(float fraction, Point startValue,
                                              Point endValue) {
                            return new Point(Math.round(startValue.x
                                    + (endValue.x - startValue.x) * fraction),
                                    Math.round(startValue.y
                                            + (endValue.y - startValue.y)
                                            * fraction));
                        }
                    }, start, end);
            anim.setDuration(250);
            /*监听整个动画过程，每播放一帧动画，onAnimationUpdate就会调用一次*/
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    /*获得动画播放过程中的Point当前值*/
                    Point offset = (Point) animation.getAnimatedValue();
                    setOffset(offset.x, offset.y);//根据当前Point值去requestLayout
                }
            });
            anim.start();
        }
    }

    public void setOffsetX(int offset) {
        mOffsetX = offset;
        requestLayout();
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public void setOffsetY(int offset) {
        mOffsetY = offset;
        requestLayout();
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public void setOffset(int x, int y) {
        mOffsetX = x;
        mOffsetY = y;
        requestLayout();
    }

    public void goHome(boolean animation) {
        moveWithoutSpeed(-mOffsetX, -mOffsetY, animation);
    }

}
