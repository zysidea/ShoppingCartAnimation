package com.example.zys.shoppingcartanimation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
public class GoodsDetailView extends LinearLayout {

    private int[] mShopBagCoordinateOnScreen;
    private Animation mAnimation;
    private AnimatorSet mAnimatorSet;

    private float mOriginalTranslationX;
    private float mOriginalTranslationY;

    public GoodsDetailView(Context context) {
        super(context);
        initEnterAnimation();
    }

    public GoodsDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEnterAnimation();

    }

    private void initEnterAnimation() {
        mAnimatorSet = new AnimatorSet();

        //进入时候的动画
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_in);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                GoodsDetailView.this.setBackgroundColor(0x70000000);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mOriginalTranslationX = getChildAt(0).getTranslationX();
                mOriginalTranslationY = getChildAt(0).getTranslationY();
                initExitAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void initExitAnimation() {

        //缩放动画,缩小到原来的0.05
        final ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 0.05f);
        scaleAnimator.setDuration(500);
        scaleAnimator.setInterpolator(new LinearInterpolator());
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                getChildAt(0).setScaleX(value);
                getChildAt(0).setScaleY(value);
            }
        });

        //贝塞尔曲线动画
        int[] startCoordinateOnScreen = new int[2];
        this.getLocationOnScreen(startCoordinateOnScreen);
        //0.475是根据缩小的比例计算
        float startX = (float) (startCoordinateOnScreen[0] + getWidth() * 0.475);
        float startY = (float) (startCoordinateOnScreen[1] + getHeight() * 0.475);
        //减去startX是因为要相对于该View的坐标圆点去计算坐标
        //getWidth()*0.05/2的原因是为了在飞上去的中点两者中心一致
        float endX = (float) (mShopBagCoordinateOnScreen[0] - startX - getWidth() * 0.05 / 2);
        float endY = (float) (mShopBagCoordinateOnScreen[1] - startY - getHeight() * 0.033 / 2);


        Path path = new Path();
        path.moveTo(0, 0);
        path.quadTo(0, (0 + endY) / 2, endX, endY);

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final ValueAnimator bezierAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        bezierAnimator.setInterpolator(new AccelerateInterpolator());
        bezierAnimator.setDuration(800);
        bezierAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                float currentPos[] = new float[2];
                pathMeasure.getPosTan(value, currentPos, null);
                getChildAt(0).setTranslationX(currentPos[0]);
                getChildAt(0).setTranslationY(currentPos[1]);
            }
        });
        bezierAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                GoodsDetailView.this.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null) {
                    parent.removeView(GoodsDetailView.this);
                }
                reverse();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null) {
                    parent.removeView(GoodsDetailView.this);
                }
                reverse();

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimatorSet.playSequentially(scaleAnimator, bezierAnimator);

    }

    private void reverse() {
        getChildAt(0).setScaleX(1f);
        getChildAt(0).setScaleY(1f);
        getChildAt(0).setTranslationX(mOriginalTranslationX);
        getChildAt(0).setTranslationY(mOriginalTranslationY);
    }



    public void addWithAnimation(final ViewGroup parent) {
        if (getParent() == null) {
            parent.addView(GoodsDetailView.this);
        }
        getChildAt(0).startAnimation(mAnimation);
    }

    public void removeWithAnimation(boolean isStartAnimation) {
        if (isStartAnimation) {
            mAnimatorSet.start();
        } else {
            ViewGroup parent = (ViewGroup) getParent();
            if (parent != null) {
                parent.removeView(this);
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mAnimatorSet.isRunning()) {
            View child = getChildAt(0);
            if (child != null) {
                float y = ev.getY();
                int top = child.getTop();
                int bottom = child.getBottom();
                if (y <= top || y >= bottom) {
                    removeWithAnimation(false);
                    return true;
                }
            }
        }else {
            return true;
        }
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    public void setShopBagCoordinate(int[] shopBagCoordinateOnScreen) {
        mShopBagCoordinateOnScreen = shopBagCoordinateOnScreen;
    }
}
