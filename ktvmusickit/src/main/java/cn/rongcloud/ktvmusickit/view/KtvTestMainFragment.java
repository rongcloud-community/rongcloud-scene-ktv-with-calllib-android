//package cn.rongcloud.ktvmusickit.view;
//
//import android.content.res.AssetFileDescriptor;
//import android.media.MediaPlayer;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//
//import com.basis.ui.BaseFragment;
//import cn.rongcloud.ktvmusickit.R;
//
//import cn.rongcloud.ktvmusickit.model.ClickedMusic;
//import cn.rongcloud.ktvmusickit.model.ClickedSongBean;
//import cn.rongcloud.ktvmusickit.model.RCSKTVScreenRole;
//import cn.rongcloud.ktvmusickit.manager.LrcManager;
//import cn.rongcloud.ktvmusickit.listener.KtvKitMixDialogListener;
//import cn.rongcloud.ktvmusickit.listener.KtvSongListDialogListener;
//import cn.rongcloud.ktvmusickit.music.KtvMusicManager;
//import cn.rongcloud.ktvmusickit.view.lrc.LrcView;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import cn.rongcloud.ktvmusickit.KtvMusicKitConstants;
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.Observer;
//import io.reactivex.rxjava3.disposables.Disposable;
//
///**
// * 主房间fragment测试类
// */
//public class KtvTestMainFragment extends BaseFragment implements View.OnClickListener,
//        KtvKitMixDialogListener, KtvSongListDialogListener {
//    private LrcView mLrcView;
//
//    private Button btn1;
//
//    private Button btn2;
//
//    private Button btn3;
//
//    private ImageView playBtn;
//    private ImageView lrcMixImage;
//    private SeekBar mSeekBar;
//    private LrcManager mLrcManager;
//    private MediaPlayer mPlayer;
//
//
//    private boolean isPause = true;
//
//
//    @Override
//    public int setLayoutId() {
//        return R.layout.ktv_test_home;
//    }
//
//    @Override
//    public void init() {
//        lrcMixImage = getView().findViewById(R.id.lrc_mix_image);
//        playBtn = getView().findViewById(R.id.play_btn);
//        btn1 = getView().findViewById(R.id.btn1);
//        btn2 = getView().findViewById(R.id.btn2);
//        btn3 = getView().findViewById(R.id.btn3);
//        mLrcView = getView().findViewById(R.id.word_view);
//        mSeekBar = getView().findViewById(R.id.media_seekbar);
//        mSeekBar.setProgress(1);
//        btn1.setOnClickListener(this);
//        playBtn.setOnClickListener(this);
//        lrcMixImage.setOnClickListener(this);
//        //第一步要传递歌词view
//        mLrcManager = new LrcManager(mLrcView);
//
//        //添加并设置一个进度条，可以不设置
//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    setProgress(progress / 100.0f);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        //第二部初始化播放器，并选中要播的歌曲并将歌词文件传递给songScreenManager
//        initPlayer();
//        //第三步，模拟每隔200毫秒回调一次时间
//        setTimeCallback();
//    }
//
//    /**
//     * 初始化歌曲播放器
//     */
//    public void initPlayer() {
//        mPlayer = new MediaPlayer();
//        mPlayer.reset();
//        try {
//            AssetFileDescriptor fileDescriptor = getResources().getAssets().openFd("song.mp3");
//            //目前先读取本地assets固定的资源歌曲
//            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
//                    fileDescriptor.getStartOffset(), fileDescriptor.getLength());
////            mPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/song" +
////                    ".mp3");
//            mPlayer.prepare();
//            //第二步在适当的时间点位传递歌词路径
//            mLrcManager.setLrcFile("歌词文件路径");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 模拟开始和暂停播放，如果是暂停则播放，如果是播放则暂停
//     */
//    public void startSong() {
//        if (mPlayer == null) {
//            initPlayer();
//        }
//        if (!mPlayer.isPlaying()) {
//            mPlayer.start();
//            isPause = false;
//        } else {
//            mPlayer.pause();
//            isPause = true;
//        }
//        mLrcManager.setIsPause(isPause);
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn1) {
//            KtvSongPlatformDialog dialog
//                    = new KtvSongPlatformDialog(this, KtvMusicManager.SelectType.MUSIC,null,"");
//            dialog.show(getChildFragmentManager());
//        } else if (id == R.id.btn2) {
//
//        } else if (id == R.id.btn3) {
//        } else if (id == R.id.lrc_mix_image) {
//            KtvMixDialog dialog
//                    = new KtvMixDialog(this,null, RCSKTVScreenRole.KTV_SCREEN_ROLE_SINGER);
//            dialog.show(getChildFragmentManager());
//        } else if (id == R.id.play_btn) {
//            if (mLrcManager == null) {
//                mLrcManager = new LrcManager(mLrcView);
//            }
//            startSong();
//        }
//    }
//
//    /**
//     * 模拟时间回调
//     */
//    public void setTimeCallback() {
//        // 第一次事件前的延时，每次事件之间的时间，时间单位，interval返回的发布者的发布事件是总耗时长
//        // interval不继承线程调度关系和事件且默认观察者运行在子线程
//        Observable.interval(0, KtvMusicKitConstants.PERIOAD_TIME, TimeUnit.MILLISECONDS)
//                // 设置事件次数
//                .take(600000).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(Object outerValue) {
//                        if (mLrcManager != null && mPlayer != null) {
//                            setSeekBarProgress();
//                            mLrcManager.setCurrentTime(mPlayer.getCurrentPosition() + 600);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//
//    //下面的内容可以去除，sdk只要给定对应时间就能对应歌词
//
//    /**
//     * 用户主动设置播放进度
//     *
//     * @param progressRatio
//     */
//    public void setProgress(float progressRatio) {
//        if (mPlayer == null) {
//            initPlayer();
//        }
//        mPlayer.seekTo((int) (progressRatio * mPlayer.getDuration()));
//    }
//
//
//    /**
//     * 根据歌曲进度自动调整滚动条的进度
//     */
//    public void setSeekBarProgress() {
//        if (mSeekBar != null && mPlayer != null) {
//            mSeekBar.setProgress((int) (mPlayer.getCurrentPosition() / (float) mPlayer.getDuration() * 100));
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mPlayer != null) {
//            mPlayer.stop();
//            mPlayer.release();
//            mPlayer = null;
//        }
//    }
//
//    @Override
//    public void reverbChanged(int index) {
//    }
//
//    @Override
//    public void accompVolumeChanged(int progress) {
//    }
//
//    @Override
//    public void vocalVolumeChanged(int progress) {
//    }
//
//    @Override
//    public void toneStepperChanged(int value) {
//    }
//
//    @Override
//    public void earReturnChanged(boolean isEar) {
//    }
//
//    @Override
//    public void pincherChanged(boolean isIntonation) {
//    }
//
//    @Override
//    public void allInfo(Map map) {
//    }
//
//    @Override
//    public void chooseSongList(List list) {
//        //歌曲列表弹框收起，回调获取到了已点歌列表
//    }
//
//    @Override
//    public void disMissDialogClickedList(List<ClickedMusic> list) {
//
//    }
//}
//
