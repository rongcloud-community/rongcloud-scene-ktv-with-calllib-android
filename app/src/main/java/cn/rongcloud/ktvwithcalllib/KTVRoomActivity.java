package cn.rongcloud.ktvwithcalllib;

import static cn.rongcloud.ktvmusickit.model.RCSKTVScreenRole.KTV_SCREEN_ROLE_LEAD_SINGER;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.callback.SongInfoCallBack;
import cn.rongcloud.ktvmusickit.listener.KtvKitMixDialogListener;
import cn.rongcloud.ktvmusickit.listener.KtvSongListDialogListener;
import cn.rongcloud.ktvmusickit.listener.KtvSongScreenListener;
import cn.rongcloud.ktvmusickit.manager.LrcManager;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.model.KtvMusicKitConfig;
import cn.rongcloud.ktvmusickit.model.MixConfig;
import cn.rongcloud.ktvmusickit.model.RCSKTVScreenRole;
import cn.rongcloud.ktvmusickit.music.KtvMusicManager;
import cn.rongcloud.ktvmusickit.view.KtvMixDialog;
import cn.rongcloud.ktvmusickit.view.SongScreenView;
import cn.rongcloud.ktvwithcalllib.bean.Effect;
import cn.rongcloud.ktvwithcalllib.message.RCKTVRefreshMessage;
import cn.rongcloud.ktvwithcalllib.bean.User;
import cn.rongcloud.ktvwithcalllib.user.UserManager;
import cn.rongcloud.ktvwithcalllib.utils.MessageUtil;
import cn.rongcloud.rtc.api.AudioDualMonoMode;
import cn.rongcloud.rtc.api.RCRTCAudioMixer;
import cn.rongcloud.rtc.api.RCRTCAudioRouteManager;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.callback.IRCRTCAudioDataListener;
import cn.rongcloud.rtc.api.callback.IRCRTCAudioRouteListener;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.RCRTCAudioMixingStateChangeListener;
import cn.rongcloud.rtc.api.stream.RCRTCVideoView;
import cn.rongcloud.rtc.audioroute.RCAudioRouteType;
import cn.rongcloud.rtc.base.RCRTCAudioFrame;
import cn.rongcloud.rtc.base.RCRTCParamsType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import cn.rongcloud.voicebeautifier.RCRTCVoiceBeautifierEngine;
import cn.rongcloud.voicebeautifier.RCRTCVoiceBeautifierPreset;
import io.rong.calllib.IRongCallListener;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.ReceivedProfile;

/**
 * @author gyn
 * @date 2022/7/1
 * <p>
 * 目前的实现逻辑
 * 点歌后，双方同时本地播放歌曲，音乐仅本地播放不混音到发布的流中
 * 双方的播放/暂停/切歌/刷新列表等都通过发消息实现双方同步
 * 例如：角色A，角色B
 * A点歌，ktvmusickit会下载歌词歌曲，下载完成后添加到已点列表，同时会准备播放
 * A先发送 MESSAGE_MUSIC_PREPARE 消息，告知B准备播放了
 * B收到消息后，先检查本地是否存在音乐和歌词，不存在下载，歌曲歌词都存在后，给A发送 MESSAGE_MUSIC_START 消息， 同时开始播放
 * A收到消息后开始播放。
 * <p>
 * 本方式的弊端是双方音乐会有不同步，取决于消息的及时性
 */
public class KTVRoomActivity extends BaseActivity implements View.OnClickListener {
    private static final String IS_CALL = "IS_CALL";
    private static final String TARGET_ID = "TARGET_ID";
    private static final String TARGET_NAME = "TARGET_NAME";
    private static final String CALL_SESSION = "CALL_SESSION";

    // 以下是消息类型
    // K歌开始
    private static final String MESSAGE_MUSIC_START = "MESSAGE_MUSIC_START";
    // K歌暂停
    private static final String MESSAGE_MUSIC_PAUSE = "MESSAGE_MUSIC_PAUSE";
    // k歌恢复
    private static final String MESSAGE_MUSIC_RESUME = "MESSAGE_MUSIC_RESUME";
    // k歌结束，所有列表都播放完毕
    private static final String MESSAGE_MUSIC_END = "MESSAGE_MUSIC_END";
    // 下一首歌
    private static final String MESSAGE_MUSIC_NEXT = "MESSAGE_MUSIC_NEXT";
    // K歌准备，会通知对方准备，下载歌曲和歌词
    private static final String MESSAGE_MUSIC_PREPARE = "MESSAGE_MUSIC_PREPARE";
    // 刷新音乐列表
    private static final String MESSAGE_MUSIC_LIST = "MESSAGE_MUSIC_LIST";

    // 混响音效 https://doc.rongcloud.cn/live/Android/5.X/audio/effect
    private List<Effect> effectList = new ArrayList<Effect>() {
        {
            add(new Effect(RCRTCVoiceBeautifierPreset.NONE, "原声"));
            add(new Effect(RCRTCVoiceBeautifierPreset.FULL, "饱满"));
            add(new Effect(RCRTCVoiceBeautifierPreset.LOW, "低沉"));
            add(new Effect(RCRTCVoiceBeautifierPreset.HYPERACTIVITY, "高亢"));
            add(new Effect(RCRTCVoiceBeautifierPreset.FALSETTO, "假声"));
            add(new Effect(RCRTCVoiceBeautifierPreset.HULK, "绿巨人"));
            add(new Effect(RCRTCVoiceBeautifierPreset.BOY, "男孩"));
            add(new Effect(RCRTCVoiceBeautifierPreset.GIRL, "女孩"));
            add(new Effect(RCRTCVoiceBeautifierPreset.OLD_MAN, "老男人"));
            add(new Effect(RCRTCVoiceBeautifierPreset.VOCAL_CONCERT, "演唱会"));
            add(new Effect(RCRTCVoiceBeautifierPreset.KTV, "KTV"));
            add(new Effect(RCRTCVoiceBeautifierPreset.BOY_TO_MAN, "男青年"));
            add(new Effect(RCRTCVoiceBeautifierPreset.GIRL_TO_WOMAN, "女青年"));
        }
    };
    private List effectNames = new ArrayList();

    // 是否是主叫
    private boolean isCall = false;
    // 对方id
    private String targetId;
    // 对方名字
    private String targetName;
    // 通话Session信息
    private RongCallSession callSession;
    // 装载自己视频的ViewGroup
    private FrameLayout myContainer;
    // 对方视频视频的ViewGroup
    private FrameLayout otherContainer;
    // 拨打/接听的布局
    private ConstraintLayout clCall;
    // 拨打/接听提示信息
    private TextView tvDesc;
    // 拨打/接听布局中的挂断按钮
    private Button btnHangup;
    // 接通后的挂断按钮
    private Button btnHangup1;
    // 接听按钮
    private Button btnAccept;
    // 歌曲数量
    private TextView mTvMusicList;
    // 歌曲时长名称
    private TextView mTvMusicInfo;
    // 点歌按钮
    private TextView mTvChoiceMusic;
    // 歌词屏幕
    private SongScreenView mSsvScreen;
    //
    private ConstraintLayout clMusicList;
    // 歌曲下载提示信息
    private TextView tvTips;
    // 歌词管理
    private LrcManager lrcManager;
    // 正在播放的歌曲
    private ClickedMusic currentMusic;
    // 已点列表
    private List<ClickedMusic> clickedMusicList;
    // 歌曲总时长
    private long durationMillis;
    // 当前用户
    private User mUser;
    // 是否在播放
    private boolean isPlaying = false;
    // 声音路由类型
    private RCAudioRouteType routeType;

    // 拨打者调用
    public static void call(Context context, String targetId, String targetName) {
        Intent intent = new Intent(context, KTVRoomActivity.class);
        intent.putExtra(IS_CALL, true);
        intent.putExtra(TARGET_ID, targetId);
        intent.putExtra(TARGET_NAME, targetName);
        context.startActivity(intent);
    }

    // 接听者调用
    public static void response(Context context, RongCallSession callSession) {
        Intent intent = new Intent(context, KTVRoomActivity.class);
        intent.putExtra(IS_CALL, false);
        intent.putExtra(CALL_SESSION, callSession);
        context.startActivity(intent);
    }

    // 消息接收回调
    private final OnReceiveMessageWrapperListener onReceiveMessageWrapperListener = new OnReceiveMessageWrapperListener() {
        @Override
        public void onReceivedMessage(Message message, ReceivedProfile profile) {
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleMessage(message.getContent());
                }
            });
        }
    };
    // 混音回调
    private final RCRTCAudioMixingStateChangeListener audioMixingStateChangeListener = new RCRTCAudioMixingStateChangeListener() {
        @Override
        public void onStateChanged(RCRTCAudioMixer.MixingState state, RCRTCAudioMixer.MixingStateReason reason) {
            Logger.d("state:" + state + " reason:" + reason);
            isPlaying = state == RCRTCAudioMixer.MixingState.PLAY;
            // 刷新k歌屏幕上的播放状态
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSsvScreen.setPlay(isPlaying);
                }
            });
        }

        @Override
        public void onReportPlayingProgress(float progress) {
            // 目前暂停时也会一直回调该方法，所以判断一下状态
            if (!isPlaying) {
                return;
            }
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 设置歌词进度
                    lrcManager.setCurrentTime((long) (durationMillis * progress));
                    // 刷新进度时间
                    refreshMusicInfo(progress);
                    // 播放完成
                    if (progress == 1.0) {
                        // 清除歌词屏幕
                        clearLrc();
                        // 自动播放下一首
                        playNextMusic();
                    }
                }
            });
        }
    };

    @Override
    public int setLayoutId() {
        return R.layout.activity_ktv;
    }

    @Override
    public void init() {
        isCall = getIntent().getBooleanExtra(IS_CALL, false);
        targetId = getIntent().getStringExtra(TARGET_ID);
        targetName = getIntent().getStringExtra(TARGET_NAME);
        callSession = getIntent().getParcelableExtra(CALL_SESSION);
        initView();
        initLogic();
    }

    private void initView() {
        myContainer = findViewById(R.id.fl_my);
        otherContainer = findViewById(R.id.fl_other);

        clCall = findViewById(R.id.cl_call);
        tvDesc = findViewById(R.id.tv_desc);
        btnHangup = findViewById(R.id.btn_hangup);
        btnHangup1 = findViewById(R.id.btn_hangup1);
        btnAccept = findViewById(R.id.btn_accept);
        mTvMusicList = (TextView) findViewById(R.id.tv_music_list);
        mTvMusicInfo = (TextView) findViewById(R.id.tv_music_info);
        mTvChoiceMusic = (TextView) findViewById(R.id.tv_choice_music);
        mSsvScreen = (SongScreenView) findViewById(R.id.ssv_screen);
        clMusicList = findViewById(R.id.cl_music_list);
        tvTips = findViewById(R.id.tv_tips);
        btnHangup.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnHangup1.setOnClickListener(this);
        mTvChoiceMusic.setOnClickListener(this);
        clMusicList.setOnClickListener(this);
        mSsvScreen.setSongScreenListener(songScreenListener);
        lrcManager = mSsvScreen.getLrcManager();
        // 歌词屏幕根据不同角色控制了某些操作按钮的显示，可根据需求传入角色或者修改源码控制显示
        mSsvScreen.setKtvScreenRole(RCSKTVScreenRole.KTV_SCREEN_ROLE_LEAD_SINGER);
        // ktv 模块内部对是否是房主处理了一些ui展示和逻辑操作，这里默认双方都是房主，都能对音乐进行操作，如果有特殊需求可以修改ktvmusickit源码处理
        KtvMusicManager.getInstance().setHomeOwner(true);
        // 点歌列表里的控麦按钮是否显示
        KtvMusicManager.getInstance().setShowControlMic(false);
    }

    private void initLogic() {
        mUser = UserManager.getInstance().getUser();
        // 呼叫监听
        RongCallClient.getInstance().setVoIPCallListener(callListener);
        // 消息监听
        RongCoreClient.addOnReceiveMessageListener(onReceiveMessageWrapperListener);
        // 混音监听
        RCRTCAudioMixer.getInstance().setAudioMixingStateChangeListener(audioMixingStateChangeListener);

        // 拨打者需要调用拨打方法
        if (isCall) {
            List<String> userIds = new ArrayList<>();
            userIds.add(targetId);
            RongCallClient.getInstance().startCall(Conversation.ConversationType.PRIVATE, targetId, userIds, null, RongCallCommon.CallMediaType.VIDEO, UserManager.getInstance().getUser().getName());
            clCall.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.GONE);
            btnHangup.setVisibility(View.VISIBLE);
            tvDesc.setText(String.format("正在呼叫 %s", targetName));
        } else {
            clCall.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);
            btnHangup.setVisibility(View.VISIBLE);
            tvDesc.setText(String.format("%s 的来电", callSession.getExtra()));
            targetId = callSession.getTargetId();
            targetName = callSession.getExtra();
        }
        for (Effect effect : effectList) {
            effectNames.add(effect.getName());
        }
        // 默认伴唱，通过声道控制原/伴唱，demo现在的歌曲是双轨音源，左声道人声，右声道伴奏
        RCRTCAudioMixer.getInstance().setAudioDualMonoMode(AudioDualMonoMode.AUDIO_DUAL_MONO_R);
        // 配置RTC，开启立体声，不开的话某些机型会默认关掉，导致原伴唱切换失败
        RongCallClient.getInstance().setRTCConfig(RCRTCConfig.Builder.create().enableStereo(true));
        initRouteType();
    }


    private void initRouteType() {
        RCRTCAudioRouteManager.getInstance().setOnAudioRouteChangedListener(audioRouteListener);
        if (RCRTCAudioRouteManager.getInstance().hasHeadSet()) {
            routeType = RCAudioRouteType.HEADSET;
        } else if (RCRTCAudioRouteManager.getInstance().hasBluetoothA2dpConnected()) {
            routeType = RCAudioRouteType.HEADSET_BLUETOOTH;
        } else {
            routeType = RCAudioRouteType.SPEAKER_PHONE;
        }
    }

    // 是否可以开启耳返
    private boolean earsBackEnable() {
        return routeType == RCAudioRouteType.HEADSET || routeType == RCAudioRouteType.HEADSET_BLUETOOTH;
    }

    // 路由监听
    private IRCRTCAudioRouteListener audioRouteListener = new IRCRTCAudioRouteListener() {
        @Override
        public void onRouteChanged(RCAudioRouteType type) {
            routeType = type;
            checkEarsBack();
        }

        @Override
        public void onRouteSwitchFailed(RCAudioRouteType fromType, RCAudioRouteType toType) {

        }
    };

    // 如果未插耳机或未连接蓝牙，会关掉耳返
    private void checkEarsBack() {
        if (!earsBackEnable()) {
            if (RCRTCEngine.getInstance().getDefaultAudioStream() != null) {
                RCRTCEngine.getInstance().getDefaultAudioStream().enableEarMonitoring(false);
            }
            MixConfig mixConfig = KtvMusicManager.getInstance().getMixConfigLiveData().getValue();
            mixConfig.ear = false;
            KtvMusicManager.getInstance().getMixConfigLiveData().setValue(mixConfig);
        }
    }

    /**
     * 歌词屏幕上展示的提示信息
     *
     * @param tips
     */
    private void showTips(String tips) {
        if (TextUtils.isEmpty(tips)) {
            tvTips.setVisibility(View.GONE);
        } else {
            tvTips.setVisibility(View.VISIBLE);
            tvTips.setText(tips);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_hangup || id == R.id.btn_hangup1) {
            // 挂断
            RongCallClient.getInstance().hangUpCall();
        } else if (id == R.id.btn_accept) {
            // 接听
            RongCallClient.getInstance().acceptCall(callSession.getCallId());
        } else if (id == R.id.tv_choice_music) {
            // 打开点歌台，默认音乐列表
            KtvMusicKitConfig config = new KtvMusicKitConfig();
            config.setKtvType(KtvMusicManager.KTVType.none);
            config.setSelectType(KtvMusicManager.SelectType.MUSIC);
            config.setPlayingId(currentMusic == null ? null : currentMusic.getMusicId());
            KtvMusicManager.getInstance().showMusicListDialog(getSupportFragmentManager(), songListDialogListener, config);
        } else if (id == R.id.cl_music_list) {
            // 打开点歌台，默认已点列表
            KtvMusicKitConfig config = new KtvMusicKitConfig();
            config.setKtvType(KtvMusicManager.KTVType.none);
            config.setSelectType(KtvMusicManager.SelectType.CHOSEN);
            config.setPlayingId(currentMusic == null ? null : currentMusic.getMusicId());
            KtvMusicManager.getInstance().showMusicListDialog(getSupportFragmentManager(), songListDialogListener, config);
        }
    }

    /**
     * 通话监听
     */
    private IRongCallListener callListener = new IRongCallListener() {
        @Override
        public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
            // myContainer.addView(localVideo);
        }

        @Override
        public void onCallConnected(RongCallSession callSession, SurfaceView localVideo) {
            // 通话连接后
            KTVRoomActivity.this.callSession = callSession;
            // 自己的视频添加到自己的view容器中
            myContainer.addView(localVideo);
            // 隐藏呼叫/接听布局
            clCall.setVisibility(View.GONE);
            // true默认扬声器，false默认听筒
            RongCallClient.getInstance().setEnableSpeakerphone(true);
            // 初始化ktv模块
            KtvMusicManager.getInstance().init(callSession.getSessionId(), mUser.getUid(), mUser.getAvatar(), Constant.HOST);
            // 设置默认音量
            RCRTCEngine.getInstance().getDefaultAudioStream().adjustRecordingVolume(KtvMusicManager.getInstance().getMixConfigLiveData().getValue().vocalVolume);
            RCRTCEngine.getInstance().getDefaultAudioStream().enableEarMonitoring(KtvMusicManager.getInstance().getMixConfigLiveData().getValue().ear);
            RCRTCEngine.getInstance().getDefaultAudioStream().setAudioQuality(RCRTCParamsType.AudioQuality.SPEECH, RCRTCParamsType.AudioScenario.DEFAULT);
            Logger.d("=============== onCallConnected");
        }

        @Override
        public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
            // 断开连接
            deleteKtvSetting();
        }

        @Override
        public void onRemoteUserRinging(String userId) {

        }

        @Override
        public void onRemoteUserAccept(String userId, RongCallCommon.CallMediaType mediaType) {

        }

        @Override
        public void onRemoteUserJoined(String userId, RongCallCommon.CallMediaType mediaType, int userType, SurfaceView remoteVideo) {

        }

        @Override
        public void onRemoteUserInvited(String userId, RongCallCommon.CallMediaType mediaType) {

        }

        @Override
        public void onRemoteUserLeft(String userId, RongCallCommon.CallDisconnectedReason reason) {

        }

        @Override
        public void onMediaTypeChanged(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView video) {

        }

        @Override
        public void onError(RongCallCommon.CallErrorCode errorCode) {

        }

        @Override
        public void onRemoteCameraDisabled(String userId, boolean disabled) {

        }

        @Override
        public void onRemoteMicrophoneDisabled(String userId, boolean disabled) {

        }

        @Override
        public void onNetworkReceiveLost(String userId, int lossRate) {

        }

        @Override
        public void onNetworkSendLost(int lossRate, int delay) {

        }

        @Override
        public void onFirstRemoteVideoFrame(String userId, int height, int width) {

        }

        @Override
        public void onFirstRemoteAudioFrame(String userId) {

        }

        @Override
        public void onAudioLevelSend(String audioLevel) {

        }

        @Override
        public void onAudioLevelReceive(HashMap<String, String> audioLevel) {

        }

        @Override
        public void onRemoteUserPublishVideoStream(String userId, String streamId, String tag, SurfaceView surfaceView) {
            // 对方发布流后，把对方视频放到view容器中
            ((RCRTCVideoView) surfaceView).setMirror(true);
            otherContainer.addView(surfaceView);
        }

        @Override
        public void onRemoteUserUnpublishVideoStream(String userId, String streamId, String tag) {

        }

    };

    /**
     * ktv屏幕点击事件回调
     */
    private KtvSongScreenListener songScreenListener = new KtvSongScreenListener() {


        @Override
        public void showTuner() {
            // 显示调音台
            KtvMixDialog dialog = new KtvMixDialog(mixDialogListener, effectNames, KTV_SCREEN_ROLE_LEAD_SINGER);
            dialog.show(getSupportFragmentManager());
        }

        @Override
        public void nextSong() {
            // 点击下一首
            RCRTCAudioMixer.getInstance().stop();
            MessageUtil.sendRefreshMessage(targetId, MESSAGE_MUSIC_NEXT, "", new KtvDataCallBack<Boolean>() {
                @Override
                public void onResult(Boolean var1) {
                    playNextMusic();
                }
            });

        }

        @Override
        public boolean play(boolean isPlay) {
            // 播放/暂停
            if (clickedMusicList == null || clickedMusicList.size() == 0) {
                KToast.show("当前歌曲信息为空，请先点歌或点击下一首按钮~");
                return false;
            }

            // 播放/暂停,需要发消息通知对方
            MessageUtil.sendRefreshMessage(targetId, isPlay ? MESSAGE_MUSIC_RESUME : MESSAGE_MUSIC_PAUSE, "", new KtvDataCallBack<Boolean>() {
                @Override
                public void onResult(Boolean var1) {
                    // 消息发送成功后做操作
                    if (var1) {
                        if (isPlay) {
                            RCRTCAudioMixer.getInstance().resume();
                        } else {
                            RCRTCAudioMixer.getInstance().pause();
                        }
                    }
                }
            });

            return true;
        }

        @Override
        public boolean turnToOriginSong(boolean isOrigin) {
            // 原唱伴唱切换
            RCRTCAudioMixer.getInstance().setAudioDualMonoMode(isOrigin ? AudioDualMonoMode.AUDIO_DUAL_MONO_L : AudioDualMonoMode.AUDIO_DUAL_MONO_R);
            return true;
        }

    };

    /**
     * 点歌台弹框监听
     */
    private KtvSongListDialogListener songListDialogListener = new KtvSongListDialogListener() {

        @Override
        public void selectedMusic(List<ClickedMusic> list) {
            // 点击音乐列表的【点歌】按钮，会回调此处
            clickedMusicList = list;
            if (list != null && list.size() > 0) {
                // 当前没有播放的音乐，取列表第一个准备播放
                if (currentMusic == null) {
                    currentMusic = list.get(0);
                    preparePlay(currentMusic);
                }
            }
            // 刷新已点信息
            refreshMusicListInfo();
            // 通知对方刷新列表
            notifyRefreshMusicList();
        }

        @Override
        public void changeSelectedList() {
            // 当已点列表刷新时回调，置顶，删除等会触发
            reloadMusicList();
            notifyRefreshMusicList();
        }

        @Override
        public void controlMic(ClickedMusic clickedMusic) {
            // 内置控麦按钮点击
        }

        @Override
        public void disMissDialogClickedList(List<ClickedMusic> list) {
            // 弹框消失会回调
        }

        @Override
        public void applyForTopTheMusic(ClickedMusic clickedMusic) {
            // 非房主申请置顶回调
        }
    };

    // 通知对方刷新点歌列表
    private void notifyRefreshMusicList() {
        MessageUtil.sendRefreshMessage(targetId, MESSAGE_MUSIC_LIST, "", null);
    }

    /**
     * ktv调音台监听
     */
    private KtvKitMixDialogListener mixDialogListener = new KtvKitMixDialogListener() {
        @Override
        public void reverbChanged(int index) {
            RCRTCVoiceBeautifierEngine.getInstance().enable(true, new IRCRTCResultCallback() {
                @Override
                public void onFailed(RTCErrorCode errorCode) {
                    Logger.e("================reverb error" + errorCode);
                }

                @Override
                public void onSuccess() {
                    Logger.d("================reverb success" + index);
                    RCRTCVoiceBeautifierEngine.getInstance().setPreset(effectList.get(index).getVoiceBeautifierPreset());
                }
            });
        }

        @Override
        public void accompVolumeChanged(int progress) {
            // 伴奏音量改变
            RCRTCAudioMixer.getInstance().setVolume(progress);
        }

        @Override
        public void vocalVolumeChanged(int progress) {
            // 人声音量
            RCRTCEngine.getInstance().getDefaultAudioStream().adjustRecordingVolume(progress);
        }

        @Override
        public void toneStepperChanged(int value) {
            // 升降调，暂不支持
        }

        @Override
        public void earReturnChanged(boolean isEar) {
            // 耳返开启或关闭
            // 先检查是否有连接耳机才能打开耳返
            if (earsBackEnable()) {
                RCRTCEngine.getInstance().getDefaultAudioStream().enableEarMonitoring(isEar);
            } else {
                // 没连接耳机会自动关闭耳返
                checkEarsBack();
                KToast.show("耳返已关闭，请连接耳机");
            }
        }

        @Override
        public void pincherChanged(boolean isIntonation) {
            // 音准器，暂不支持
        }

        @Override
        public void allInfo(Map map) {
            // 弹框消失会回调所有数据，根据需求使用
        }
    };

    @Override
    protected void onDestroy() {
        RongCallClient.getInstance().setVoIPCallListener(null);
        RongCoreClient.removeOnReceiveMessageListener(onReceiveMessageWrapperListener);
        RCRTCAudioMixer.getInstance().setAudioMixingStateChangeListener(null);
        super.onDestroy();
    }

    //


    /**
     * 播放下一首歌，切歌时双方都会触发该方法
     */
    public void playNextMusic() {
        clearLrc();
        if (currentMusic != null) {
            // 谁点的歌谁负责调用删除接口
            if (!TextUtils.equals(currentMusic.getUserId(), mUser.getUid())) {
                return;
            }
            //播放下一首之前必须先删除当前这一首
            KtvMusicManager.getInstance().onDeleteMusic(currentMusic, new KtvDataCallBack<Boolean>() {
                public void onResult(Boolean isSuccess) {
                    //获取最新列表并播放下一首
                    obtainListAndPlay(isSuccess);
                }
            });
        } else {
            //获取最新列表并播放下一首
            obtainListAndPlay(true);
        }
    }

    private void clearLrc() {
        lrcManager.playEnd();
    }


    /**
     * 获取最新列表然后播放
     *
     * @param isDeleteSuccess 是否删除成功
     */
    public void obtainListAndPlay(boolean isDeleteSuccess) {
        //获取最新已点音乐列表页
        KtvMusicManager.getInstance().onSelectedMusicList(new KtvDataCallBack<List<ClickedMusic>>() {
            @Override
            public void onResult(List<ClickedMusic> list) {
                if (list == null) {
                    KToast.show("===获取最新已点列表异常~");
                    return;
                }
                clickedMusicList = list;
                refreshMusicListInfo();
                //表示有下一首可播
                if (list.size() != 0) {
                    if (isDeleteSuccess) {      //如果删除成功
                        //删除(切歌)成功,需要请求最新的歌曲列表
                        KToast.show("切歌成功~");
                        //说明删除切歌后，请求到的已点列表还有歌曲，则直接切到这首歌播放
                        currentMusic = list.get(0);
                    } else { //如果删除失败
                        //删除(切歌)失败,需要请求最新的歌曲列表
                        KToast.show("切歌失败~");
                        //切歌失败则前置歌曲还在列表中，继续播放下一首把
                        if (list.size() >= 2) {
                            currentMusic = list.get(1);
                        } else {
                            playEnd();
                            MessageUtil.sendRefreshMessage(targetId, MESSAGE_MUSIC_END, "", null);
                            return;
                        }
                    }
                    preparePlay(currentMusic);
                } else {
                    // 已点列表没有歌曲了代表播放完了
                    playEnd();
                    // 通知对方播完了
                    MessageUtil.sendRefreshMessage(targetId, MESSAGE_MUSIC_END, "", null);
                }
            }

        });
    }


    public void playEnd() {
        currentMusic = null;
        lrcManager.setLrcFile("");
        lrcManager.setCurrentTime(0);
        KToast.show("所有歌曲都已播放完毕~");
        refreshMusicInfo(0);
        notifyRefreshMusicList();
    }

    /**
     * 指定播放哪一首歌
     *
     * @param clickedSongBean
     */
    public void mediaPlay(ClickedMusic clickedSongBean) {
        showTips("");
        currentMusic = clickedSongBean;
        //播放前必然要设置歌词路径给管理者
        lrcManager.setLrcFile(clickedSongBean.getLrcPath());
        //设置角色来决定显示屏幕的哪个按钮
        mSsvScreen.setKtvScreenRole(KTV_SCREEN_ROLE_LEAD_SINGER);
        // 开始混音播放，RCRTCAudioMixer.Mode分三种模式，NONE:只播放不混音推流，MIX:播放并混音推流，REPLACE：播放并替换MIC采集的声音推流
        // playback:是否立即播放，loopCount：循环次数
        // 这里采用RCRTCAudioMixer.Mode.NONE是因为双方都在播音乐，不需要把音乐混音再推流，避免对方会有2个音乐重音
        // https://doc.rongcloud.cn/live/Android/5.X/audio/mix
        RCRTCAudioMixer.getInstance().startMix(clickedSongBean.getMusicPath(), RCRTCAudioMixer.Mode.NONE, true, 1);
        // 设置伴奏音量
        RCRTCAudioMixer.getInstance().setVolume(KtvMusicManager.getInstance().getMixConfigLiveData().getValue().styleVolume);
        // 获取歌曲总时长， 时长*progress = 当前播到的时间点
        durationMillis = RCRTCAudioMixer.getInstance().getDurationMillis();

    }

    // 刷新歌曲信息
    private void refreshMusicInfo(float progress) {
        if (currentMusic == null) {
            mTvMusicInfo.setText("暂无演唱");
        } else {
            int minute = (int) (durationMillis * (1 - progress) / 1000 / 60);
            int second = (int) (durationMillis * (1 - progress) / 1000 % 60);
            mTvMusicInfo.setText(String.format("%s:%s %s", minute < 10 ? "0" + minute : minute, second < 10 ? "0" + second : second, currentMusic.getMusicName()));
        }
    }

    /**
     * 准备播放
     * <p>
     * 点歌者A--prepare消息-->对方B，检查是否下载歌曲和歌词，没有下载，完成后--start消息-->点歌者A
     *
     * @param clickedMusic
     */
    private void preparePlay(ClickedMusic clickedMusic) {
        if (clickedMusic == null) {
            return;
        }
        // 自己准备播放，先发消息通知对方
        if (TextUtils.equals(clickedMusic.getUserId(), mUser.getUid())) {
            showTips("等待对方下载歌曲信息");
            MessageUtil.sendRefreshMessage(targetId, MESSAGE_MUSIC_PREPARE, GsonUtil.obj2Json(clickedMusic), null);
        } else {
            // 别人通知自己要准备播放歌曲了，这里检查1.歌曲是否下载；2.歌词是否下载
            // 如果已经下载则通知对方开始
            if (KtvMusicManager.getInstance().exists(clickedMusic.getMusicId(), false)
                    && KtvMusicManager.getInstance().exists(clickedMusic.getMusicId(), true)) {
                replayMusicStart(clickedMusic);
            } else {
                KtvMusicManager.getInstance().downloadMusic(clickedMusic.getMusicId(), new SongInfoCallBack<Boolean>() {
                    @Override
                    public void onDownProgress(float var1) {
                        UIKit.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showTips(String.format("正在下载歌曲 %d%s", (int) var1, "%"));
                            }
                        });
                    }

                    @Override
                    public void onResult(Boolean var1) {
                        showTips("");
                        if (var1) {
                            replayMusicStart(clickedMusic);
                        }
                    }
                });
            }
        }
    }

    // 回应对方下载完成了可以开始了
    private void replayMusicStart(ClickedMusic clickedMusic) {
        MessageUtil.sendRefreshMessage(targetId, MESSAGE_MUSIC_START, GsonUtil.obj2Json(clickedMusic), new KtvDataCallBack<Boolean>() {
            @Override
            public void onResult(Boolean var1) {
                if (var1) {
                    // 发消息成功后，本地开始播放
                    mediaPlay(clickedMusic);
                }
            }
        });
    }

    // 消息处理
    private void handleMessage(MessageContent messageContent) {
        if (messageContent instanceof RCKTVRefreshMessage) {
            String name = ((RCKTVRefreshMessage) messageContent).getName();
            Logger.d(Thread.currentThread() + "============== " + name);
            String content = ((RCKTVRefreshMessage) messageContent).getContent();
            switch (name) {
                case MESSAGE_MUSIC_PREPARE:
                    currentMusic = GsonUtil.json2Obj(content, ClickedMusic.class);
                    // 收到准备的消息准备播放
                    preparePlay(currentMusic);
                    // 刷新最新音乐列表
                    reloadMusicList();
                    break;
                case MESSAGE_MUSIC_START:
                    // 接收到开始的消息，开始播放
                    currentMusic = GsonUtil.json2Obj(content, ClickedMusic.class);
                    mediaPlay(currentMusic);
                    break;
                case MESSAGE_MUSIC_PAUSE:
                    // 暂停消息
                    RCRTCAudioMixer.getInstance().pause();
                    break;
                case MESSAGE_MUSIC_RESUME:
                    // 恢复播放消息
                    RCRTCAudioMixer.getInstance().resume();
                    break;
                case MESSAGE_MUSIC_LIST:
                    // 刷新列表消息
                    reloadMusicList();
                    KtvMusicManager.getInstance().refreshSelectedMusicList();
                    break;

                case MESSAGE_MUSIC_NEXT:
                    // 下一首消息
                    RCRTCAudioMixer.getInstance().stop();
                    playNextMusic();
                    break;
                case MESSAGE_MUSIC_END:
                    // 列表播完了
                    playEnd();
                    break;
            }
        }
    }

    /**
     * 刷新已点信息
     */
    private void refreshMusicListInfo() {
        mTvMusicList.setText(String.format("已点(%s)", clickedMusicList == null ? "0" : clickedMusicList.size()));
    }

    /**
     * 重新刷新已点列表数据
     */
    private void reloadMusicList() {
        KtvMusicManager.getInstance().onSelectedMusicList(new KtvDataCallBack<List<ClickedMusic>>() {
            @Override
            public void onResult(List<ClickedMusic> list) {
                clickedMusicList = list;
                refreshMusicListInfo();

                if (list.contains(currentMusic)) {

                } else {
                    // 已点列表里不包含当前歌曲了，说明被对方删除了，停止播放
                    RCRTCAudioMixer.getInstance().stop();
                    clearLrc();
                    obtainListAndPlay(true);
                }
            }
        });
    }

    /**
     * 删除服务端的此次点歌信息
     */
    private void deleteKtvSetting() {
        if (callSession == null) {
            finish();
            KtvMusicManager.getInstance().unInit();
            return;
        }
        OkApi.put(Constant.DEL_SETTING, OkParams.Builder().add("roomId", callSession.getSessionId()).build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.d("================" + result.getCode());
                finish();
                KtvMusicManager.getInstance().unInit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 点击返回键挂断
        RongCallClient.getInstance().hangUpCall();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ActionBar的返回键
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
