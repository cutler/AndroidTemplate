package com.cutler.template.util.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cutler.template.R;
import com.cutler.template.util.base.AppUtil;

import java.lang.ref.SoftReference;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by cuihu on 15/9/9.
 */
public class VideoPlayer extends LinearLayout {

    private static SoftReference<VideoPlayer> playingVideoPlayer;

    private VideoView mVideoView;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mPreviewLayout;
    private RelativeLayout mControllerLayout;
    private String url;
    private CheckBox mPlayPauseBtn;
    private SeekBar mSeekBar;
    private ImageView mPreviewIV;

    // 播放完成回调。
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    // 全屏
    private CheckBox mScaleScreenBtn;
    private OnFullScreenListener mFullScreenListener;

    // 时间显示
    private TextView mTimeTV;
    private StringBuilder mFormatBuilder = new StringBuilder();
    private Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    private StringBuilder timeStr = new StringBuilder();

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        initRootView();
        initVideoView();
        initLoadingLayout();
        initPreviewLayout();
        initControllerLayout();
    }

    private void initRootView() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_player, this);
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mControllerLayout.setVisibility(mControllerLayout.getVisibility() == View.GONE ?
                        View.VISIBLE : View.GONE);
            }
        });
        setClickable(false);
    }

    private void initVideoView() {
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                //视频缓冲完成。
                mLoadingLayout.setVisibility(View.GONE);
                mControllerLayout.setVisibility(View.VISIBLE);
                setClickable(true);
                // 当播放进度发生变化时，会回调里面的方法。
                // TODO 注意：此处有个问题，当断网时就不会回调此方法了，进而导致播放时间不更新，待解决。
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    int currentPosition, duration;
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        // 获得当前播放时间和当前视频的长度
                        currentPosition = mVideoView.getCurrentPosition();
                        duration = mVideoView.getDuration();
                        mSeekBar.setMax(mVideoView.getDuration());
                        // 设置进度条的主要进度，表示当前的播放时间
                        mSeekBar.setProgress(mVideoView.getCurrentPosition());
                        // 设置进度条的次要进度，表示视频的缓冲进度
                        mSeekBar.setSecondaryProgress(percent * duration / 100);
                        mTimeTV.setText(stringForTime(currentPosition) + "/" + stringForTime(duration));
                    }
                });
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mVideoView.seekTo(0);
                mSeekBar.setProgress(0);
                mPlayPauseBtn.setChecked(false);
                mTimeTV.setText(getTimeString(0, mp.getDuration()));
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getContext(), "播放出错，请稍后再试！", Toast.LENGTH_SHORT).show();
                reset(VideoPlayer.this);
                return true;
            }
        });
        // 防止Activity被切到后台再回来时，VideoView变黑。
        mVideoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                reset(VideoPlayer.this);
            }
        });
    }

    private void initLoadingLayout() {
        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
    }

    private void initPreviewLayout() {
        mPreviewLayout = (RelativeLayout) findViewById(R.id.preview_layout);
        mPreviewLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                play();
            }
        });
        mPreviewIV = (ImageView) findViewById(R.id.preview_IV);
    }

    private void initControllerLayout() {
        mControllerLayout = (RelativeLayout) findViewById(R.id.controller_layout);
        mPlayPauseBtn = (CheckBox) mControllerLayout.findViewById(R.id.playPauseBtn);
        mPlayPauseBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mVideoView.start();
                } else {
                    mVideoView.pause();
                }
            }
        });
        mSeekBar = (SeekBar) mControllerLayout.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo(progress);
                    mTimeTV.setText(getTimeString(progress, seekBar.getMax()));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mTimeTV = (TextView) mControllerLayout.findViewById(R.id.timeTV);
        mScaleScreenBtn = (CheckBox) mControllerLayout.findViewById(R.id.scaleScreenBtn);
        mScaleScreenBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int initWidth = Integer.MIN_VALUE, initHeight = Integer.MIN_VALUE;
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (getContext() instanceof Activity) {
                    int realWidth, realHeight;
                    Activity act = (Activity) getContext();
                    if (isChecked) {
                        // 记录VideoPlayer最初的尺寸信息
                        if(initWidth == Integer.MIN_VALUE || initHeight == Integer.MIN_VALUE){
                            initWidth = mVideoView.getWidth();
                            initHeight = mVideoView.getHeight();
                        }
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        realWidth = AppUtil.getScreenWidth(act);
                        realHeight = AppUtil.getScreenHeight(act);
                    } else {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        realWidth = initWidth;
                        realHeight = initHeight;
                    }
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
                    params.width = realWidth;
                    params.height = realHeight;
                    setLayoutParams(params);
                    if(mFullScreenListener != null){
                        mFullScreenListener.onChanged(isChecked);
                    }
                }
            }
        });
    }

    private void reset(VideoPlayer inst) {
        if (inst != null) {
            if (inst.mSeekBar.getSecondaryProgress() > 0) {
                inst.mVideoView.stopPlayback();
            }
            inst.mLoadingLayout.setVisibility(View.GONE);
            inst.mControllerLayout.setVisibility(View.GONE);
            inst.mPreviewLayout.setVisibility(View.VISIBLE);
        }
        playingVideoPlayer = null;
    }

    private void play() {
        if (hasUrl()) {
            stopCurrentVideoPlayer();
            mPreviewLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mVideoView.setVideoURI(Uri.parse(url));
            mVideoView.start();
            mPlayPauseBtn.setChecked(true);
        } else {
            Toast.makeText(getContext(), R.string.video_url_error, Toast.LENGTH_SHORT).show();
        }
    }

    // 停止正在播放的VideoPlayer控件，即保证同一时间内只有一个播放器在播放
    private void stopCurrentVideoPlayer() {
        if (playingVideoPlayer != null) {
            reset(playingVideoPlayer.get());
        }
        playingVideoPlayer = new SoftReference<VideoPlayer>(this);
    }

    // 检测Url是否正确
    private boolean hasUrl() {
        return !TextUtils.isEmpty(url);
    }

    // 格式化显示时间
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);

        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private String getTimeString(int progress, int max) {
        timeStr.setLength(0);
        timeStr.append(stringForTime(progress)).append("/").append(stringForTime(max));
        return timeStr.toString();
    }

    /**
     * 设置要播放的视频的url
     *
     * @param url
     */
    public void setUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getContext(), R.string.video_url_error, Toast.LENGTH_SHORT).show();
            return;
        }
        this.url = url;
    }

    /**
     * 获取用于展示视频首帧图像的ImageView对象
     *
     * @return
     */
    public ImageView getPreviewImageView() {
        return mPreviewIV;
    }

    /**
     * 当VideoPlayer在全屏和非全屏之间切换时，回调此接口中的方法。
     */
    public static interface OnFullScreenListener{
        public void onChanged(boolean isFullScrren);
    }

    public void setOnFullScreenListener(OnFullScreenListener listener){
        this.mFullScreenListener = listener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        this.mOnCompletionListener = listener;
    }
}
