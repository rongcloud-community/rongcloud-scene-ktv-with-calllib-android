package cn.rongcloud.ktvmusickit.music;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.api.callback.FileIOCallBack;
import com.basis.ui.UIStack;
import com.basis.utils.KToast;
import com.basis.utils.ListUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.loading.LoadTag;

import cn.rongcloud.ktvmusickit.callback.DataCallback;
import cn.rongcloud.ktvmusickit.model.ChannelBean;
import cn.rongcloud.ktvmusickit.model.ChannelSheetBean;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.callback.SongInfoCallBack;
import cn.rongcloud.ktvmusickit.listener.KtvSongListDialogListener;
import cn.rongcloud.ktvmusickit.listener.KtvSongNetworkInterfaceListener;
import cn.rongcloud.ktvmusickit.model.KtvMusicKitConfig;
import cn.rongcloud.ktvmusickit.model.MixConfig;
import cn.rongcloud.ktvmusickit.model.Music;
import cn.rongcloud.ktvmusickit.model.MusicDetailBean;
import cn.rongcloud.ktvmusickit.model.MusicDetailSubVersion;
import cn.rongcloud.ktvmusickit.view.KtvSongPlatformDialog;


import java.io.File;
import java.util.List;


/**
 * Created by lijie on 2021/11/25
 * KTV音乐网络接口管理
 */
public class KtvMusicManager implements KtvSongNetworkInterfaceListener {
    private static final String TAG = KtvMusicManager.class.getSimpleName();
    private static KtvMusicManager instance;

    // 音乐和歌词的本地存储路径
    private String musicPath;
    private LoadTag loadTag = null;
    private KtvMusicKitConfig ktvMusicKitConfig = new KtvMusicKitConfig();

    private String roomId;
    private String userId;
    private String userPortrait;

    /**
     * 混响弹框数据
     */
    private MutableLiveData<MixConfig> mixConfigLiveData = new MutableLiveData<MixConfig>(new MixConfig());

    /**
     * 是否是房主
     */
    private boolean isHomeOwner = true;

    /**
     * 是否使用控麦
     */
    private boolean isShowControlMic = true;

    /**
     * 总弹框
     */
    KtvSongPlatformDialog ktvSongPlatformDialog;

    public enum SelectType {
        MUSIC, //music 列表
        CHOSEN// 已点列表
    }

    public enum KTVType {
        none(-1),
        solo(1), //独唱
        chorus(0);// 合唱
        private final int value;

        KTVType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public MutableLiveData<MixConfig> getMixConfigLiveData() {
        if (mixConfigLiveData == null) {
            mixConfigLiveData = new MutableLiveData(new MixConfig());
        }
        return mixConfigLiveData;
    }

    private KtvMusicManager() {
        // 这里存到了cache缓存目录下，可根据自己需求存到其他地方
        musicPath = UIKit.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    public static KtvMusicManager getInstance() {
        if (instance == null) {
            instance = new KtvMusicManager();
        }
        return instance;
    }

    public void showMusicListDialog(FragmentManager fragmentManager, KtvSongListDialogListener ktvSongListDialogListener, KtvMusicKitConfig ktvMusicKitConfig) {
        this.ktvMusicKitConfig = ktvMusicKitConfig;
        ktvSongPlatformDialog = new KtvSongPlatformDialog(ktvSongListDialogListener, ktvMusicKitConfig.getSelectType(), this, ktvMusicKitConfig.getPlayingId());
        ktvSongPlatformDialog.show(fragmentManager);
    }


    /**
     * 加载电台tag分类
     *
     * @param dataCallback 数据回传
     */
    @Override
    public void onLoadMusicCategory(DataCallback<List<ChannelSheetBean>> dataCallback) {
        KtvMusicApi.channel(new IResultBack<List<ChannelBean>>() {
            @Override
            public void onResult(List<ChannelBean> channelBeans) {
                if (dataCallback != null) {
                    String groupId;
                    if (channelBeans == null || channelBeans.size() == 0) {
                        dataCallback.onResult(null);
                        KToast.show("电台列表获取失败~");
                        groupId = "";
                    } else {
                        groupId = channelBeans.get(0).getGroupId();
                    }
                    KtvMusicApi.channelSheet(groupId, 0, 1, 100, new IResultBack<List<ChannelSheetBean>>() {
                        @Override
                        public void onResult(List<ChannelSheetBean> channelSheetList) {
                            if (dataCallback != null) {
                                if (channelSheetList == null || channelSheetList.size() == 0 || channelSheetList.get(0).getRecord() == null) {
                                    dataCallback.onResult(null);
                                } else {
                                    dataCallback.onResult(channelSheetList);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取默认第一页音乐列表
     *
     * @param category
     * @param dataCallback 数据回传
     */
    @Override
    public void onLoadMusicListByCategory(String category, DataCallback<List<Music>> dataCallback) {
        onLoadMusicListByCategory(1, category, dataCallback);
    }

    /**
     * 获取指定页音乐列表
     *
     * @param page         页数
     * @param category     音乐组
     * @param dataCallback 回调
     */
    @Override
    public void onLoadMusicListByCategory(int page, String category, DataCallback<List<Music>> dataCallback) {
        KtvMusicApi.loadMusicList(category, 0, page, 10, new IResultBack<List<Music>>() {
            @Override
            public void onResult(List<Music> musicList) {
                if (dataCallback != null) {
                    if (musicList == null || musicList.size() == 0) {
                        dataCallback.onResult(null);
                    } else {
                        dataCallback.onResult(musicList);
                    }
                }
            }
        });
    }

    /**
     * 搜索音乐
     *
     * @param dataCallback 数据回传
     */
    @Override
    public void onSearchMusic(String keywords, DataCallback<List<Music>> dataCallback) {
        KtvMusicApi.searchMusic(keywords, 0, 1, 100, new IResultBack<List<Music>>() {
            @Override
            public void onResult(List<Music> musicList) {
                if (dataCallback != null) {
                    if (musicList == null || musicList.size() == 0) {
                        dataCallback.onResult(null);
                    } else {
                        dataCallback.onResult(musicList);
                    }
                }
            }
        });
    }

    /**
     * 根据音乐的id获取音乐的资源大小和歌曲歌词下载地址
     *
     * @param dataCallback 回调
     */
    @Override
    public void onLoadMusicInfo(String musicId, DataCallback<MusicDetailBean> dataCallback) {
        if (musicId == null || musicId.equals(null) || musicId.equals("null")) {
            KToast.show("要下载的歌曲id不能为空~");
            return;
        }

        KtvMusicApi.loadDetail(musicId, new IResultBack<MusicDetailBean>() {
            @Override
            public void onResult(MusicDetailBean musicDetailBean) {
                if (musicDetailBean == null) {
                    dataCallback.onResult(null);
                } else {
                    dataCallback.onResult(musicDetailBean);
                }
            }
        });
    }

    /**
     * 下载音乐到本地
     *
     * @param musicDetailBean
     * @param dataCallback
     */
    @Override
    public void onDownloadMusic(MusicDetailBean musicDetailBean, SongInfoCallBack<Boolean> dataCallback, boolean isClickMusic) {
        // 这里以音乐id命名，用户需根据自己业务需求下载音乐，这里就是简单的下载一下
        String name = musicDetailBean.getMusicId();

        // 如果本地已经下载过，就不下载了
        if (exists(name, false)) {
            if (isClickMusic) {
                musicLoadFinished(musicDetailBean, dataCallback);
            } else {
                dataCallback.onResult(true);
            }
            return;
        }
        KToast.show("开始尝试下载~");
        String musicResourcesUrl = "";
        List<MusicDetailSubVersion> list = musicDetailBean.getSubVersions();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVersionName().startsWith("左右声道")) {
                    musicResourcesUrl = list.get(i).getPath();
                }
            }
        }
        if (musicResourcesUrl.equals("")) {
            musicResourcesUrl = musicDetailBean.getFileUrl();
        }

        //这是开始下载歌曲的方法
        OkApi.download(musicResourcesUrl, null, new FileIOCallBack(musicPath, name) {
            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
                SongInfoCallBack songInfoCallBack = (SongInfoCallBack) dataCallback;
                songInfoCallBack.onDownProgress(progress * 100);
            }

            @Override
            public void onResult(File result) {
                super.onResult(result);
                //到这时候已经下载完了
                if (isClickMusic) {
                    musicLoadFinished(musicDetailBean, dataCallback);
                } else {
                    dataCallback.onResult(true);
                }
            }
        });
    }

    @Override
    public void onDownloadLrc(MusicDetailBean musicDetailBean) {
        if (musicDetailBean.getMusicId() == null || musicDetailBean.getMusicId().equals("") || TextUtils.isEmpty(musicDetailBean.getLyricUrl()) || musicDetailBean.getDynamicLyricUrl().equals("")) {
            return;
        }
        // 以歌名+lyric为存储歌词文件名
        String name = musicDetailBean.getMusicId() + "lyric";

        // 如果本地已经下载过，就不下载了
        if (exists(musicDetailBean.getMusicId(), true)) {
            return;
        }
        //这是开始下载歌词的方法
        OkApi.download(musicDetailBean.getLyricUrl(), null, new FileIOCallBack(musicPath, name) {
            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
            }

            @Override
            public void onResult(File result) {
                super.onResult(result);
            }
        });
    }


    /**
     * 音乐文件下载完之后点这首歌，用户可以根据需求看是否需要这一步
     *
     * @param dataCallback
     */
    private void musicLoadFinished(MusicDetailBean musicDetailBean, SongInfoCallBack<Boolean> dataCallback) {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(userId)) {
            KToast.show("请先调用init方法传递数据~");
            return;
        }

        ClickedMusic musicBean = new ClickedMusic();
        musicBean.setArtistName(musicDetailBean.getAuthorName());
        musicBean.setMusicId(musicDetailBean.getMusicId());
        musicBean.setMusicName(musicDetailBean.getMusicName());
        musicBean.setRoomId(roomId);
        musicBean.setSolo(ktvMusicKitConfig.getKtvType().getValue());
        musicBean.setSongId("");
        musicBean.setUserId(userId);
        musicBean.setUserPortrait(userPortrait);
        //点歌网络请求
        KtvMusicApi.addMusic(musicBean, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (dataCallback != null) {
                    //true表示点歌成功，false表示点歌失败
                    dataCallback.onResult(aBoolean);
                }
            }
        });
    }


    /**
     * 封装好的整个下载流程，供外部调用
     *
     * @param musicId          音乐id
     * @param songInfoCallBack 回调
     */
    public void downloadMusic(String musicId, SongInfoCallBack<Boolean> songInfoCallBack) {
        onLoadMusicInfo(musicId, new DataCallback<MusicDetailBean>() {
            @Override
            public void onResult(MusicDetailBean musicDetailBean) {
                if (musicDetailBean != null && musicDetailBean.getFileSize() > 0 && musicDetailBean.getFileUrl() != null) {
                    onDownloadLrc(musicDetailBean);
                    onDownloadMusic(musicDetailBean, songInfoCallBack, false);
                } else {
                    KToast.show("获取资源路径信息失败~");
                }
            }
        });
    }

    /**
     * 已点的音乐列表页
     *
     * @param dataCallback 数据回传
     */
    @Override
    public void onSelectedMusicList(KtvDataCallBack<List<ClickedMusic>> dataCallback) {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("请先调用init方法传递roomId~");
            return;
        }
        KtvMusicApi.loadClickedMusics(roomId, new IResultBack<List<ClickedMusic>>() {
            @Override
            public void onResult(List<ClickedMusic> myMusics) {
                if (dataCallback != null) {
                    dataCallback.onResult(myMusics);
                }
            }
        });
    }


    /**
     * 置顶音乐
     *
     * @param clickedSongBean
     * @param dataCallback
     */
    @Override
    public void onTopMusic(ClickedMusic clickedSongBean, DataCallback<Boolean> dataCallback) {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("请先调用init方法传递roomId~");
            return;
        }

        KtvMusicApi.moveMusic(clickedSongBean, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                KToast.show("置顶音乐" + (aBoolean ? "成功" : "失败"));
                dataCallback.onResult(aBoolean);
            }
        });
    }


    /**
     * 控麦置顶
     *
     * @param dataCallback
     */
    @Override
    public void onTopMicControl(String userName, DataCallback<Boolean> dataCallback) {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("请先调用init方法传递roomId~");
            return;
        }

        KtvMusicApi.controlMic(roomId, userId, userName, userPortrait, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                KToast.show("控麦置顶" + (aBoolean ? "成功" : "失败"));
                dataCallback.onResult(aBoolean);
            }
        });
    }

    /**
     * 删除音乐
     *
     * @param clickedSongBean
     * @param ktvDataCallBack
     */
    @Override
    public void onDeleteMusic(ClickedMusic clickedSongBean, KtvDataCallBack<Boolean> ktvDataCallBack) {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("请先调用setRoomId方法传递roomId~");
            return;
        }


        KtvMusicApi.deleteMusic(clickedSongBean, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                KToast.show("删除音乐" + (aBoolean ? "成功" : "失败"));
                ktvDataCallBack.onResult(aBoolean);
            }
        });
    }

    /**
     * 转换
     *
     * @param record
     * @return
     */
    public String convertToMusic(Music record) {
        if (ListUtil.isNotEmpty(record.getArtist())) {
            return record.getArtist().get(0).getName();
        } else if (ListUtil.isNotEmpty(record.getAuthor())) {
            return record.getAuthor().get(0).getName();
        } else if (ListUtil.isNotEmpty(record.getComposer())) {
            return record.getComposer().get(0).getName();
        }
        return "~";
    }

    /**
     * 获取音乐和歌词的本地存储路径
     *
     * @return
     */
    public String getMusicPath() {
        return musicPath;
    }


    /**
     * 判断资源是否存在
     *
     * @param musicId
     * @param isLrc   是否是歌词
     * @return 是否存在
     */
    public boolean exists(String musicId, boolean isLrc) {
        File file = new File(musicPath, musicId + (isLrc ? "lyric" : ""));
        return file.exists();
    }


    public void showLoading(String msg) {
        Activity activity = UIStack.getInstance().getTopActivity();
        if (activity != null) {
            loadTag = new LoadTag(activity, msg);
            loadTag.show();
        }
    }

    public void hideLoading() {
        if (loadTag != null) {
            loadTag.dismiss();
        }
    }

    public void init(String roomId, String userId, String userPortrait, String host) {
        this.roomId = roomId;
        this.userId = userId;
        this.userPortrait = userPortrait;
        KtvMusicApi.setHost(host);
    }


    public String getUserId() {
        return userId;
    }


    /**
     * 设置是否房主
     *
     * @param homeOwner
     */
    public void setHomeOwner(boolean homeOwner) {
        isHomeOwner = homeOwner;
    }

    /**
     * 判断是否是房主
     *
     * @return
     */
    public boolean isHomeOwner() {
        return isHomeOwner;
    }

    /**
     * 设置是否显示使用控麦
     *
     * @return
     */
    public boolean isShowControlMic() {
        return isShowControlMic;
    }

    public void setShowControlMic(boolean showControlMic) {
        isShowControlMic = showControlMic;
    }

    public KtvSongPlatformDialog getKtvSongPlatformDialog() {
        return ktvSongPlatformDialog;
    }

    /**
     * 直接刷新已点列表
     */
    public void refreshSelectedMusicList() {
        if (ktvSongPlatformDialog != null) {
            ktvSongPlatformDialog.refreshClickList(null);
        }
    }

    public void setKtvSongPlatformDialog(KtvSongPlatformDialog ktvSongPlatformDialog) {
        this.ktvSongPlatformDialog = ktvSongPlatformDialog;
    }

    public void unInit() {
        instance = null;
    }
}
