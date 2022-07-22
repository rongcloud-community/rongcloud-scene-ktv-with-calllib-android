package cn.rongcloud.ktvmusickit.listener;


import cn.rongcloud.ktvmusickit.callback.DataCallback;
import cn.rongcloud.ktvmusickit.callback.SongInfoCallBack;
import cn.rongcloud.ktvmusickit.model.ChannelSheetBean;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.model.ClickedSongBean;
import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.model.Music;
import cn.rongcloud.ktvmusickit.model.MusicDetailBean;

import java.util.List;


/**
 * 歌曲网络接口访问情况回调
 */
public interface KtvSongNetworkInterfaceListener {
    void onTopMusic(ClickedMusic clickedSongBean, DataCallback<Boolean> dataCallback);

    /**
     * 房间通知kit进行控麦置顶网络请求
     *
     * @param dataCallback
     */
    void onTopMicControl(String userName, DataCallback<Boolean> dataCallback);

    void onDeleteMusic(ClickedMusic clickedSongBean, KtvDataCallBack<Boolean> ktvDataCallBack);

    void showLoading(String msg);

    void hideLoading();

    void onDownloadLrc(MusicDetailBean musicDetailBean);

    /**
     * 已点音乐列表
     *
     * @param ktvDataCallBack
     */
    void onSelectedMusicList(KtvDataCallBack<List<ClickedMusic>> ktvDataCallBack);


    void onLoadMusicCategory(DataCallback<List<ChannelSheetBean>> dataCallback);

    void onLoadMusicListByCategory(String var1, DataCallback<List<Music>> dataCallback);

    void onLoadMusicListByCategory(int page, String var1, DataCallback<List<Music>> dataCallback);


    void onSearchMusic(String var1, DataCallback<List<Music>> dataCallback);


    /**
     * 获取音乐资源大小，歌词，歌曲信息
     *
     * @param musicId
     * @param var2
     */
    void onLoadMusicInfo(String musicId, DataCallback<MusicDetailBean> var2);

    /**
     * 真实下载音乐
     *
     * @param musicDetailBean  音乐资源bean，其中至少要包含musicId
     * @param songInfoCallBack
     * @param isClickMusic     下载完后是否点此歌
     */
    void onDownloadMusic(MusicDetailBean musicDetailBean, SongInfoCallBack<Boolean> songInfoCallBack, boolean isClickMusic);

}
