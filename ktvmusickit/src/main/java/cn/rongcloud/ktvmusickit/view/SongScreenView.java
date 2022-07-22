package cn.rongcloud.ktvmusickit.view;

import static cn.rongcloud.ktvmusickit.model.RCSKTVScreenRole.KTV_SCREEN_ROLE_LEAD_SINGER;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import cn.rongcloud.ktvmusickit.R;

import cn.rongcloud.ktvmusickit.model.RCSKTVScreenRole;
import cn.rongcloud.ktvmusickit.listener.KtvSongScreenListener;
import cn.rongcloud.ktvmusickit.manager.LrcManager;
import cn.rongcloud.ktvmusickit.view.lrc.LrcView;


/**
 * 整个屏幕自定义view
 */
public class SongScreenView extends ConstraintLayout implements View.OnClickListener {
    private Context mContext;

    private View mRootView;

    /**
     * 混音弹框组件
     */
    private ImageView mLrcMixImage;
    /**
     * 屏幕背景
     */
    private ImageView mScreenBgImage;

    /**
     * 歌词组件
     */
    private LrcView mLrcView;
    /**
     * 下一首
     */
    private ImageView mNextSongImage;
    /**
     * 开始，暂停
     */
    private ImageView mPlayImage;
    /**
     * 是否伴唱
     */
    private ImageView mOriginImage;

    /**
     * 按钮回调监听
     */
    private KtvSongScreenListener mKtvSongScreenListener;

    /**
     * 歌词歌曲互动动效管理器
     */
    private LrcManager mLrcManager;

    private boolean isPlay = false;

    private boolean isOrigin = false;

    public SongScreenView(@NonNull Context context) {
        this(context, null);
    }

    public SongScreenView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRootView = LayoutInflater.from(context).inflate(R.layout.song_screen_layout, this);
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        mLrcMixImage = mRootView.findViewById(R.id.lrc_mix_image);
        mScreenBgImage = mRootView.findViewById(R.id.screen_bg_image);
        mLrcView = mRootView.findViewById(R.id.lrc_view);
        mNextSongImage = mRootView.findViewById(R.id.next_song_image);
        mPlayImage = mRootView.findViewById(R.id.play_image);
        mOriginImage = mRootView.findViewById(R.id.accompany_image);
        mPlayImage.setSelected(false);
        mOriginImage.setSelected(false);
        mLrcMixImage.setOnClickListener(this);
        mNextSongImage.setOnClickListener(this);
        mPlayImage.setOnClickListener(this);
        mOriginImage.setOnClickListener(this);
        mLrcManager = new LrcManager(this);
    }

    /**
     * 设置监听器
     *
     * @param ktvSongScreenListener 监听器
     */
    public void setSongScreenListener(KtvSongScreenListener ktvSongScreenListener) {
        mKtvSongScreenListener = ktvSongScreenListener;
    }

    /**
     * 设置角色来决定哪个按钮是否展示
     *
     * @param rcsktvScreenRole
     */
    public void setKtvScreenRole(RCSKTVScreenRole rcsktvScreenRole) {
        if (mLrcMixImage == null || mNextSongImage == null || mPlayImage == null || mOriginImage == null) {
            return;
        }
        mNextSongImage.setOnClickListener(this);
        mPlayImage.setOnClickListener(this);
        mOriginImage.setOnClickListener(this);
        if (rcsktvScreenRole == KTV_SCREEN_ROLE_LEAD_SINGER) {
            mLrcMixImage.setVisibility(View.VISIBLE);
            mNextSongImage.setVisibility(View.VISIBLE);
            mPlayImage.setVisibility(View.VISIBLE);
            mOriginImage.setVisibility(View.VISIBLE);
        } else {
            mLrcMixImage.setVisibility(View.VISIBLE);
            mNextSongImage.setVisibility(View.INVISIBLE);
            mPlayImage.setVisibility(View.INVISIBLE);
            mOriginImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置屏幕背景
     */
    public void setScreenBgImage(int res) {
        if (mScreenBgImage != null) {
            mScreenBgImage.setBackgroundResource(res);
        }
    }


    /**
     * 点击回调
     *
     * @param v 被点击的view
     */
    @Override
    public void onClick(View v) {
        if (mKtvSongScreenListener == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.lrc_mix_image) {
            mKtvSongScreenListener.showTuner();
        } else if (id == R.id.next_song_image) {
            mKtvSongScreenListener.nextSong();
        } else if (id == R.id.play_image) {
            if (mKtvSongScreenListener.play(!isPlay)) {
                setPlay(!isPlay);
            }
        } else if (id == R.id.accompany_image) {
            if (mKtvSongScreenListener.turnToOriginSong(!isOrigin)) {
                //主动切换屏幕的原伴唱图标
                setOrigin(!isOrigin);
            }
        }
    }

    public LrcView getLrcView() {
        return mLrcView;
    }

    public LrcManager getLrcManager() {
        if (mLrcManager == null) {
            mLrcManager = new LrcManager(this);
        }
        return mLrcManager;
    }

    /**
     * 设置开始播放和暂停按钮的图标
     *
     * @param isPlay 是否要置为播放状态
     */
    public void setPlay(boolean isPlay) {
        this.isPlay = isPlay;
        mPlayImage.setSelected(isPlay);
    }


    /**
     * 设置是否伴唱的图标
     *
     * @param isOrigin 是否要置为伴唱状态
     */
    public void setOrigin(boolean isOrigin) {
        this.isOrigin = isOrigin;
        mOriginImage.setSelected(isOrigin);
    }
}
