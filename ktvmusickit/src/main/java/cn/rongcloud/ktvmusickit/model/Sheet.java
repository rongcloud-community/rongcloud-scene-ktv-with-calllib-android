package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;


public  class Sheet {
    @SerializedName("sheetId")
    private Integer sheetId;
    @SerializedName("sheetName")
    private String sheetName;

    public Integer getSheetId() {
        return sheetId;
    }

    public void setSheetId(Integer sheetId) {
        this.sheetId = sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
