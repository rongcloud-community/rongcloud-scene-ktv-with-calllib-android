package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelSheetBean {
    @SerializedName("record")
    private List<Sheet> sheetList;

    public List<Sheet> getRecord() {
        return sheetList;
    }

    public void setRecord(List<Sheet> record) {
        this.sheetList = record;
    }
}
