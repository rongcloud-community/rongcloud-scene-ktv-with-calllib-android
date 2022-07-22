package cn.rongcloud.ktvwithcalllib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.basis.utils.Logger;

import cn.rongcloud.rtc.api.AudioDualMonoMode;
import cn.rongcloud.rtc.api.RCRTCAudioMixer;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.RCRTCRoomConfig;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.api.stream.RCRTCLiveInfo;
import cn.rongcloud.rtc.base.RCRTCLiveRole;
import cn.rongcloud.rtc.base.RCRTCRoomType;
import cn.rongcloud.rtc.base.RTCErrorCode;

/**
 * @author gyn
 * @date 2022/7/19
 */
public class TestActivity extends AppCompatActivity {

    public static void launch(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    boolean isOrigin = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RCRTCEngine.getInstance().init(getApplication(), RCRTCConfig.Builder.create()
                .enableStereo(true)
                .build());
        RCRTCRoomConfig roomConfig = RCRTCRoomConfig.Builder.create()
                // 根据实际场景，选择音视频直播：LIVE_AUDIO_VIDEO 或音频直播：LIVE_AUDIO
                .setRoomType(RCRTCRoomType.LIVE_AUDIO_VIDEO)
                .setLiveRole(RCRTCLiveRole.BROADCASTER)
                .build();

        RCRTCEngine.getInstance().joinRoom("123", roomConfig, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(RCRTCRoom data) {
                Logger.d("=========== join room success");
                RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(null);
                data.getLocalUser().publishDefaultLiveStreams(new IRCRTCResultDataCallback<RCRTCLiveInfo>() {
                    @Override
                    public void onSuccess(RCRTCLiveInfo liveInfo) {
                        Logger.d("=========== publishDefaultLiveStreams success");
                        RCRTCAudioMixer.getInstance().startMix("/storage/emulated/0/Android/data/cn.rongcloud.ktvwithcalllib/files/Music/02BD0DCD2F1C46", RCRTCAudioMixer.Mode.NONE, true, 1);
                    }

                    @Override
                    public void onFailed(RTCErrorCode code) {
                    }
                });

            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {

            }
        });

        findViewById(R.id.btn_call).setOnClickListener(view -> {
            isOrigin = !isOrigin;
            RCRTCAudioMixer.getInstance().setAudioDualMonoMode(isOrigin ? AudioDualMonoMode.AUDIO_DUAL_MONO_STEREO : AudioDualMonoMode.AUDIO_DUAL_MONO_R);
            ((Button) view).setText(isOrigin ? "立体声" : "右声道");
        });
    }

    @Override
    public void finish() {
        RCRTCEngine.getInstance().leaveRoom(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                TestActivity.super.finish();
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                TestActivity.super.finish();
            }
        });
    }
}
