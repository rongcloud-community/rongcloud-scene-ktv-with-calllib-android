package cn.rongcloud.ktvwithcalllib.message;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 自定义消息，用来刷新通话过程中的状态同步
 * 可以定义多个name类型，区分不同消息，content可以根据name类型传不同数据
 */
@MessageTag(value = "RC:KTVRefreshMsg", flag = MessageTag.NONE)
public class RCKTVRefreshMessage extends MessageContent {

    private static final String TAG = "RCVoiceRoomRefreshMessage";

    private String name;
    private String content;

    public RCKTVRefreshMessage(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("name")) {
                name = jsonObj.getString("name");
            }
            if (jsonObj.has("content")) {
                content = jsonObj.getString("content");
            }
        } catch (JSONException e) {
            RLog.e(TAG, "JSONException " + e.getMessage());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.content);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.content = source.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RCKTVRefreshMessage() {
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(name)) {
                jsonObj.put("name", name);
            }
            if (!TextUtils.isEmpty(content)) {
                jsonObj.put("content", content);
            }
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            RLog.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "RCKTVRefreshMessage{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    protected RCKTVRefreshMessage(Parcel in) {
        this.name = in.readString();
        this.content = in.readString();
    }

    public static final Creator<RCKTVRefreshMessage> CREATOR = new Creator<RCKTVRefreshMessage>() {
        @Override
        public RCKTVRefreshMessage createFromParcel(Parcel source) {
            return new RCKTVRefreshMessage(source);
        }

        @Override
        public RCKTVRefreshMessage[] newArray(int size) {
            return new RCKTVRefreshMessage[size];
        }
    };
}
