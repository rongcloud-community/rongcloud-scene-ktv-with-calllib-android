package cn.rongcloud.ktvmusickit.listener;


import cn.rongcloud.ktvmusickit.model.ClickedMusic;

import java.util.List;

/**
 * 歌曲列表弹框回调
 */
public interface KtvSongListDialogListener {
    /**
     * 点歌后立刻获取最新点歌列表传递给房间
     * @param list 已点列表
     */
    void selectedMusic(List<ClickedMusic> list);

    /**
     * 删除第一首被播放的歌，告知房间
     */
    void changeSelectedList();

    /**
     * 点歌台点击控麦按钮通知房间
     *
     * @param clickedMusic 取消控麦用的clickedMusic模型
     */
    void controlMic(ClickedMusic clickedMusic);

    /**
     * 点歌台弹框消失时候获取最新已点列表并传递给房间
     *
     * @param list 已点列表
     */
    void disMissDialogClickedList(List<ClickedMusic> list);

    /**
     * 申请置顶通知房间
     *
     * @param clickedMusic
     */
    void applyForTopTheMusic(ClickedMusic clickedMusic);
}
