package cn.rongcloud.ktvwithcalllib.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import cn.rongcloud.ktvwithcalllib.Constant;

/**
 * @author gyn
 * @date 2022/7/11
 */
public class User implements Serializable {
    @SerializedName("userId")
    private String uid;
    @SerializedName("userName")
    private String name;
    @SerializedName("portrait")
    private String avatar;
    @SerializedName("imToken")
    private String token;
    private String authorization;
    private String phone;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return TextUtils.isEmpty(avatar) ?
                Constant.DEFAULT_PORTRAIT_ULR :
                avatar.startsWith("http")
                        ? avatar
                        : Constant.FILE_URL + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
