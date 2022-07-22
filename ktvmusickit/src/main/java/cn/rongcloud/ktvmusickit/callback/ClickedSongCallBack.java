package cn.rongcloud.ktvmusickit.callback;

import cn.rongcloud.ktvmusickit.model.ClickedSongBean;

import java.util.List;


public interface ClickedSongCallBack<T> extends DataCallback<T> {
    void onClickedSongList(List<ClickedSongBean> list);
}