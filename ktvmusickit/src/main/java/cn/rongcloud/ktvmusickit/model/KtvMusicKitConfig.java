package cn.rongcloud.ktvmusickit.model;

import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

public class KtvMusicKitConfig {
    /**
     * 独唱还是合唱
     */
    private KtvMusicManager.KTVType ktvType= KtvMusicManager.KTVType.solo;

    /**
     * 直接跳到普通列表还是已点列表
     */
    private KtvMusicManager.SelectType selectType;
    /**
     * 当前正在播放的这首歌的id
     */
    private String playingId;


    public KtvMusicKitConfig(KtvMusicManager.KTVType ktvType,
                             KtvMusicManager.SelectType selectType,
                             String playingId) {
        this.ktvType = ktvType;
        this.selectType = selectType;
        this.playingId = playingId;
    }

    public KtvMusicKitConfig() {
    }

    public KtvMusicManager.KTVType getKtvType() {
        return ktvType;
    }

    public void setKtvType(KtvMusicManager.KTVType ktvType) {
        this.ktvType = ktvType;
    }


    public KtvMusicManager.SelectType getSelectType() {
        return selectType;
    }

    public void setSelectType(KtvMusicManager.SelectType selectType) {
        this.selectType = selectType;
    }

    public String getPlayingId() {
        return playingId;
    }

    public void setPlayingId(String playingId) {
        this.playingId = playingId;
    }

}
