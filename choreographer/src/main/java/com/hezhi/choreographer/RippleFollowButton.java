package com.hezhi.choreographer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by yf11 on 2017/3/28.
 */

public class RippleFollowButton extends FrameLayout {
    public static final int DEFAULT_FOLLOWTEXTCOLOR = Color.WHITE;
    public static final int DEFAULT_UNFOLLOWTEXTCOLOR = Color.WHITE;
    public static final int DEFAULT_FOLLOWBACKGROUNDCOLOR = Color.parseColor("#45CB7F");
    public static final int DEFAULT_UNFOLLOWBACKGROUNDCOLOR = Color.parseColor("#E0E0E0");
    public static final String DEFAULT_FOLLOWTEXT= "+ 关注";
    public static final String DEFAULT_UNFOLLOWTEXT = "取消关注";

    private boolean isFollowed = false;//默认为false 还没有关注
    private boolean firstInit = true;

    private float centerX;
    private float centerY;
    private float radius;

    private TextView tvUnfollow;
    private TextView tvFollow;

    private Path path ;

    private OnFollowListener onFollowListener;

    private ValueAnimator animator;


    public RippleFollowButton(@NonNull Context context)
    {
        this(context,null);
    }

    public RippleFollowButton(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs,0);
    }

    public RippleFollowButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        tvUnfollow = new TextView(getContext());
        tvUnfollow.setGravity(Gravity.CENTER);
        tvUnfollow.setSingleLine();

        tvFollow = new TextView(getContext());
        tvFollow.setGravity(Gravity.CENTER);
        tvFollow.setSingleLine();

        addView(this.tvUnfollow);
        addView(this.tvFollow);

        path = new Path();


        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RippleFollowButton);

        if(a.hasValue(R.styleable.RippleFollowButton_followText)) {
            setFollowedText(a.getString(R.styleable.RippleFollowButton_followText));
        } else {
            setFollowedText(DEFAULT_FOLLOWTEXT);
        }

        if(a.hasValue(R.styleable.RippleFollowButton_textSize)) {
            int size = a.getDimensionPixelSize(R.styleable.RippleFollowButton_textSize,-1);
            setTextSize(Util.px2dip(context,size));
        } else {
            throw new UnsupportedOperationException("must specify text size with sp");
        }

        if(a.hasValue(R.styleable.RippleFollowButton_followTextColor)) {
            setFollowedTextColor(a.getColor(R.styleable.RippleFollowButton_followTextColor,DEFAULT_FOLLOWTEXTCOLOR));
        } else {
            setFollowedTextColor(DEFAULT_FOLLOWTEXTCOLOR);
        }

        if(a.hasValue(R.styleable.RippleFollowButton_followBackgroundColor)) {
            setfollowedBackgroundColor(a.getColor(R.styleable.RippleFollowButton_followBackgroundColor,DEFAULT_FOLLOWBACKGROUNDCOLOR));
        } else {
            setfollowedBackgroundColor(DEFAULT_FOLLOWBACKGROUNDCOLOR);
        }

        if(a.hasValue((R.styleable.RippleFollowButton_unfollowText))) {
            setUnfollowedText(a.getString(R.styleable.RippleFollowButton_unfollowText));
        } else {
            setUnfollowedText(DEFAULT_UNFOLLOWTEXT);
        }

        if(a.hasValue(R.styleable.RippleFollowButton_unfollowTextColor)) {
            setUnfollowedTextColor(a.getColor(R.styleable.RippleFollowButton_unfollowTextColor,DEFAULT_UNFOLLOWTEXTCOLOR));
        } else {
            setUnfollowedTextColor(DEFAULT_UNFOLLOWTEXTCOLOR);
        }

        if(a.hasValue(R.styleable.RippleFollowButton_unfollowBackgroundColor)) {
            setUnfollowedBackgroundColor(a.getColor(R.styleable.RippleFollowButton_unfollowBackgroundColor,DEFAULT_UNFOLLOWBACKGROUNDCOLOR));
        } else {
            setUnfollowedBackgroundColor(DEFAULT_UNFOLLOWBACKGROUNDCOLOR);
        }

        a.recycle();
    }

    private void setTextSize(float dimensionPixelSize) {
        this.tvFollow.setTextSize(dimensionPixelSize);
        this.tvUnfollow.setTextSize(dimensionPixelSize);
    }

    private boolean isValidClick(float x, float y) {
        if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight() && animator != null && !animator.isRunning()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(firstInit) {
            animator = ObjectAnimator.ofFloat(tvUnfollow,"empty",0F,(float) Math.hypot(getMeasuredWidth(),getMeasuredHeight()));
            animator.setDuration(5000L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radius = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(onFollowListener!= null){
                        if(isFollowed) {
                            onFollowListener.onFollow();
                        } else {
                            onFollowListener.onUnFollow();
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            firstInit = false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isValidClick(event.getX(), event.getY())) {
                    return false;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (!isValidClick(event.getX(), event.getY())) {
                    return false;
                }
                centerX = event.getX();
                centerY = event.getY();
                radius = 0;
                setFollow(!isFollowed,true);
                return true;
        }
        return false;
    }




    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if(needDrawChild(child)){
                return super.drawChild(canvas,child,drawingTime);
            }
            int i = canvas.save();
            path.reset();
            path.addCircle(centerX,centerY,radius, Path.Direction.CW);
            canvas.clipPath(path);
            boolean bool = super.drawChild(canvas,child,drawingTime);
            canvas.restoreToCount(i);
            return bool;
    }

    protected void setFollow(final boolean isFollowed, boolean needAnim) {
        this.isFollowed = isFollowed;
        if (isFollowed) {
            bringChildToFront(this.tvUnfollow);
        } else {
            bringChildToFront(this.tvFollow);
        }
        if(needAnim) {
            animator.start();
        }
    }

    private boolean needDrawChild(View child) {
        if(firstInit) {
            return  true;
        }
        if (isFollowed && child == tvFollow) {
            return true;
        } else if (!isFollowed && child == tvUnfollow) {
            return true;
        }
        return false;
    }

    public void setFollowedText(String s)
    {
        this.tvFollow.setText(s);
    }

    public void setFollowedTextColor(int i)
    {
        this.tvFollow.setTextColor(i);
    }

    public void setFollowedBackground(Drawable drawable)
    {
        this.tvFollow.setBackground(drawable);
    }

    public void setfollowedBackgroundColor(int color)
    {
        Drawable localDrawable = android.support.v4.content.ContextCompat.getDrawable(getContext(),R.drawable.bg_gray);
        localDrawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        setFollowedBackground(localDrawable);
    }

    public void setUnfollowedText(String s)
    {
        this.tvUnfollow.setText(s);
    }
    public void setUnfollowedTextColor(int i)
    {
        this.tvUnfollow.setTextColor(i);
    }

    public void setUnfollowedBackground(Drawable drawable)
    {
        this.tvUnfollow.setBackground(drawable);
    }

    public void setUnfollowedBackgroundColor(int color)
    {
        Drawable localDrawable = android.support.v4.content.ContextCompat.getDrawable(getContext(),R.drawable.bg_gray);
        localDrawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        setUnfollowedBackground(localDrawable);
    }

    public void setOnFollowListener(OnFollowListener onFollowListener) {
        this.onFollowListener = onFollowListener;
    }

    public interface OnFollowListener {
        void onFollow();
        void onUnFollow();
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
        if(tvFollow!=null || tvUnfollow != null) {
            removeAllViews();
        }
        if(isFollowed) {
            addView(tvFollow);
            addView(tvUnfollow);

        }else{
            addView(tvUnfollow);
            addView(tvFollow);
        }
        requestLayout();
        invalidate();
    }
}
