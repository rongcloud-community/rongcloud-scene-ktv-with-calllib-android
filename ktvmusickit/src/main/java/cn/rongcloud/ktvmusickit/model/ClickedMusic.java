package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

public class ClickedMusic {
    @SerializedName("songId")
    private String songId = "";
    @SerializedName("musicId")
    private String musicId = "";
    @SerializedName("musicName")
    private String musicName = "";
    @SerializedName("artistName")
    private String artistName = "";
    @SerializedName("userId")
    private String userId = "";
    @SerializedName("userPortrait")
    private String userPortrait = "";
    @SerializedName("solo")
    private Integer solo = KtvMusicManager.KTVType.solo.getValue();
    @SerializedName("roomId")
    private String roomId = "";

    private String musicPath;
    private String lrcPath;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public Integer getSolo() {
        return solo;
    }

    public void setSolo(Integer solo) {
        this.solo = solo;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickedMusic that = (ClickedMusic) o;
        return Objects.equals(songId, that.songId) && Objects.equals(musicId, that.musicId) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(songId, musicId);
    }
}
