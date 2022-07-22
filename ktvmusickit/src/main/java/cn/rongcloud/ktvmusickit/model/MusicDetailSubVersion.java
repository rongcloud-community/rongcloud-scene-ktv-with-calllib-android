package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

public class MusicDetailSubVersion {

    @SerializedName("versionName")
    private String versionName;
    @SerializedName("path")
    private String path;
    @SerializedName("wavePath")
    private String wavePath;
    @SerializedName("startTime")
    private Integer startTime;
    @SerializedName("endTime")
    private Integer endTime;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWavePath() {
        return wavePath;
    }

    public void setWavePath(String wavePath) {
        this.wavePath = wavePath;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }
}
