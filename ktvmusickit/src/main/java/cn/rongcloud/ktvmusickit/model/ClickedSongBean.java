package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClickedSongBean {
    @SerializedName("roomId")
    private String roomId;
    @SerializedName("seat")
    private Integer seat;
    @SerializedName("songTotal")
    private Integer songTotal;
    @SerializedName("userSongTotal")
    private Integer userSongTotal;
    @SerializedName("songPickAuth")
    private Integer songPickAuth;
    @SerializedName("songQueue")
    private List<ClickedMusic> clickedMusicList;
    @SerializedName("userId")
    private String userId;
    @SerializedName("createTime")
    private Long createTime;
    @SerializedName("updateTime")
    private Long updateTime;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getSeat() {
        return seat;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public Integer getSongTotal() {
        return songTotal;
    }

    public void setSongTotal(Integer songTotal) {
        this.songTotal = songTotal;
    }

    public Integer getUserSongTotal() {
        return userSongTotal;
    }

    public void setUserSongTotal(Integer userSongTotal) {
        this.userSongTotal = userSongTotal;
    }

    public Integer getSongPickAuth() {
        return songPickAuth;
    }

    public void setSongPickAuth(Integer songPickAuth) {
        this.songPickAuth = songPickAuth;
    }

    public List<ClickedMusic> getClickedMusicList() {
        return clickedMusicList;
    }

    public void setClickedMusicList(List<ClickedMusic> clickedMusicList) {
        this.clickedMusicList = clickedMusicList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }


}
