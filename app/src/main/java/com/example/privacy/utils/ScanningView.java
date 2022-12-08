package com.example.privacy.utils;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.privacy.R;

import java.util.Timer;
import java.util.TimerTask;

public class ScanningView extends FrameLayout {

    private static final String TAG = "ScanningView";

    /**
     * 指针
     */
    private ImageView ivNeedle;

    /**
     * 波纹
     */
    private ImageView ivRipple;

    /**
     * 中间文字
     */
    private TextView tvTitle;

    /**
     * 装波纹的容器
     */
    private FrameLayout fl_move_circle;


    private Context context;

    public ScanningView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ScanningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ivRipple.setVisibility(VISIBLE);
                    startOutCircleAnim();
                    break;
                case 2:
                    addMoveCircle();
                    break;
            }
        }
    };


    /**
     * 设置标题
     * @param txt
     */
    public void setTitle(String txt){
        tvTitle.setText(txt);
    }


    private void initView(){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.rotate_view,null);
        ivNeedle = v.findViewById(R.id.iv_btn);
        ivRipple = v.findViewById(R.id.iv_out_circle);
        tvTitle = v.findViewById(R.id.tv_title);
        fl_move_circle = v.findViewById(R.id.fl_move_circle);
        addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        startOutCircleAnim();
    }


    /**
     * 发散波纹
     */
    private void addMoveCircle() {
        final ImageView imageView = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(dip2px(getContext(), 100), dip2px(getContext(), 100));
        lp.gravity = Gravity.CENTER;
        imageView.setLayoutParams(lp);
        imageView.setImageResource(R.mipmap.outcircle);
        fl_move_circle.addView(imageView);
        ObjectAnimator outCircleAnimX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 5f);
        ObjectAnimator outCircleAnimY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 5f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(imageView, "alpha", 0.6f, 0);
        outCircleAnimX.setDuration(5000);
        outCircleAnimY.setDuration(5000);
        alphaAnim.setDuration(5000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(outCircleAnimX, outCircleAnimY, alphaAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //移除掉刚才添加的波纹
                fl_move_circle.removeView(imageView);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    /**
     * 开始循环的放大缩小波纹
     */
    private void startOutCircleAnim() {
        ObjectAnimator outCircleAlpha = ObjectAnimator.ofFloat(ivRipple, "alpha", 0.2f, 0.6f);
        outCircleAlpha.setDuration(1000);
        ObjectAnimator outCircleAnimX = ObjectAnimator.ofFloat(ivRipple, "scaleX", 1f, 1.18f, 1f);
        ObjectAnimator outCircleAnimY = ObjectAnimator.ofFloat(ivRipple, "scaleY", 1f, 1.18f, 1f);
        outCircleAnimX.setDuration(2000);
        outCircleAnimY.setDuration(2000);
        outCircleAnimX.setRepeatCount(ValueAnimator.INFINITE);
        outCircleAnimY.setRepeatCount(ValueAnimator.INFINITE);
        outCircleAnimX.setInterpolator(new LinearInterpolator());
        outCircleAnimY.setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(outCircleAnimX, outCircleAnimY, outCircleAlpha);
        animatorSet.start();
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 指针转动
     */
    private void pressStart() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(ivNeedle, "rotation", 0f, 360f);
        scaleYIn.setDuration(1800);
        scaleYIn.setInterpolator(new LinearInterpolator());
        scaleYIn.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.play(scaleYIn);
        animatorSet.start();
    }

    /**
     * 模拟开始
     */
    public void onceClick(){
        //取消掉循环的波纹
        ivRipple.setVisibility(GONE);
        pressStart();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        },0,1800);
    }
}