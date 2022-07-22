package cn.rongcloud.ktvmusickit.listener;

/**
 * 屏幕的四项按钮点击回调
 */
public interface KtvSongScreenListener {
    /**
     * 混音弹框按钮
     */
    void showTuner();

    /**
     * 下一首
     */
    void nextSong();

    /**
     * 开始，暂停
     *
     * @param isPlay 是否播放
     * @return 修改图标状态是否成功标识
     */
    boolean play(boolean isPlay);

    /**
     * 是否伴唱
     *
     * @param isOrigin 是否伴唱
     * @return 修改图标状态是否成功标识
     */
    boolean turnToOriginSong(boolean isOrigin);
}
