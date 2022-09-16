package com.ysp.playui.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ysp.playui.R;
import com.ysp.playui.listener.IControlTitle;
import com.ysp.videoplayer.bean.ClarityData;
import com.ysp.videoplayer.bean.PlayState;
import com.ysp.videoplayer.bean.PlayerState;
import com.ysp.videoplayer.controller.ControlWrapper;
import com.ysp.videoplayer.controller.IControlComponent;
import com.ysp.videoplayer.controller.IControlComponentClarity;
import com.ysp.videoplayer.util.PlayerUtils;

/**
 * 播放器顶部标题栏
 */
public class YspTitleControlView extends FrameLayout implements IControlComponent, IControlComponentClarity {

    private final String TAG = "TitleControlView";
    private final ConstraintLayout clTitleContainer;
    private final ImageView ivBack, ivShare, ivScreenProjection;
    private final TextView tvTitleName;
    private ControlWrapper mControlWrapper;
    private IControlTitle iControlTitle;
    private ClarityData mClarityData;
    private boolean isHide;

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.ysp_layout_title_view, this, true);
        clTitleContainer = findViewById(R.id.clTitleContainer);
        ivBack = findViewById(R.id.ivBack);
        ivShare = findViewById(R.id.ivShare);
        ivScreenProjection = findViewById(R.id.ivScreenProjection);
        tvTitleName = findViewById(R.id.tvTitleName);

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = PlayerUtils.scanForActivity(getContext());
                if (activity != null && mControlWrapper.isFullScreen()) {
                    mControlWrapper.toggleFullScreen(activity);
                } else if (activity != null) {
                    if (iControlTitle != null) {
                        iControlTitle.back();
                    }
                }
            }
        });
        ivShare.setOnClickListener(v -> {
            if (iControlTitle != null) {
                iControlTitle.share();
            }
        });
        ivScreenProjection.setOnClickListener(v -> {
            if (iControlTitle != null && mClarityData != null) {
                iControlTitle.screenProjection(mClarityData.defn);
            }
        });
    }

    public YspTitleControlView(@NonNull Context context) {
        super(context);
    }

    public YspTitleControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YspTitleControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        Log.d(TAG, "onVisibilityChanged isVisible = " + isVisible);
        if (isVisible) {
            setVisibility(VISIBLE);
            if (anim != null) {
                startAnimation(anim);
            }
        } else {
            setVisibility(GONE);
            if (anim != null) {
                startAnimation(anim);
            }
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        Log.d(TAG, "onPlayStateChanged playState = " + playState);
    }

    @Override
    public void onPlayerStateChanged(PlayerState playerState) {
        if (playerState == PlayerState.FULL_SCREEN) {
            ivBack.setVisibility(VISIBLE);
        } else if (playerState == PlayerState.NORMAL) {
            ivBack.setVisibility(isHide ? View.GONE : View.VISIBLE);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            Log.d(TAG, "onPlayerStateChanged cutoutHeight = " + cutoutHeight + ",orientation = " + orientation);
            ConstraintLayout.LayoutParams constraintLayout = (ConstraintLayout.LayoutParams) ivBack.getLayoutParams();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                constraintLayout.setMargins(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                constraintLayout.setMargins(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                constraintLayout.setMargins(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    public void setTitleName(String titleName) {
        tvTitleName.setText(titleName);
    }

    public void setIControlTitleListener(IControlTitle listener) {
        iControlTitle = listener;
    }

    @Override
    public void setClarityData(ClarityData[] clarityData) {
        Log.d(TAG, "setClarityData clarityData = " + clarityData);
        initClarityData(clarityData);
    }

    @Override
    public void onSwitchClaritySuccess() {

    }

    @Override
    public void onSwitchClarityError() {

    }

    private void initClarityData(ClarityData[] clarityData) {
        if (clarityData != null) {
            int len = clarityData.length;
            Log.d(TAG, "clarityData len = " + len);
            for (int i = 0; i < len; i++) {
                ClarityData clarityDataq = clarityData[i];
                if (clarityDataq.isSel) {
                    mClarityData = clarityDataq;
                }
                Log.d(TAG, "clarityDataq.isSel = "
                        + clarityDataq.isSel + ",clarityDataq.defn = " + clarityDataq.defn
                        + ",clarityDataq.desc = " + clarityDataq.desc + ",clarityDataq.defnrate = " + clarityDataq.defnrate);
            }
        }
    }

    public void isHideBack(boolean isHide) {
        this.isHide = isHide;
        ivBack.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    // 监听显示隐藏调用栈
//    @Override
//    public void setVisibility( int visibility) {
//        String desc = visibility  == View.VISIBLE?"visible":"gone";
//        PlayerLogUtil.e("YTCVYUAN", desc);
//
//    }
}
