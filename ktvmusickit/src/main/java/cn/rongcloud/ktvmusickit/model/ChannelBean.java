package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

/**
 * 电台列表model
 */
public class ChannelBean {

    @SerializedName("groupId")
    private String groupId;
    @SerializedName("groupName")
    private String groupName;
    @SerializedName("coverUrl")
    private String coverUrl;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
