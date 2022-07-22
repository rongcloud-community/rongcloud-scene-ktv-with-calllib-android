package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicListBean {
    @SerializedName("record")
    private List<Music> musicList;

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

}
