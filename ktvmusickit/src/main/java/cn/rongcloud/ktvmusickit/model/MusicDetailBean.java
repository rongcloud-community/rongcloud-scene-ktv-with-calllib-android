package cn.rongcloud.ktvmusickit.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicDetailBean {

    @SerializedName("fileSize")
    private Integer fileSize;
    @SerializedName("fileUrl")
    private String fileUrl;
    @SerializedName("waveUrl")
    private String waveUrl;
    @SerializedName("expires")
    private Long expires;
    @SerializedName("musicId")
    private String musicId;
    @SerializedName("dynamicLyricUrl")
    private String dynamicLyricUrl;
    @SerializedName("staticLyricUrl")
    private String staticLyricUrl;
    @SerializedName("subVersions")
    private List<MusicDetailSubVersion> subVersions;

    private String musicName;
    private String authorName;

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getWaveUrl() {
        return waveUrl;
    }

    public void setWaveUrl(String waveUrl) {
        this.waveUrl = waveUrl;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getDynamicLyricUrl() {
        return dynamicLyricUrl;
    }

    public void setDynamicLyricUrl(String dynamicLyricUrl) {
        this.dynamicLyricUrl = dynamicLyricUrl;
    }

    public String getStaticLyricUrl() {
        return staticLyricUrl;
    }

    public void setStaticLyricUrl(String staticLyricUrl) {
        this.staticLyricUrl = staticLyricUrl;
    }

    public List<MusicDetailSubVersion> getSubVersions() {
        return subVersions;
    }

    public void setSubVersions(List<MusicDetailSubVersion> subVersions) {
        this.subVersions = subVersions;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
