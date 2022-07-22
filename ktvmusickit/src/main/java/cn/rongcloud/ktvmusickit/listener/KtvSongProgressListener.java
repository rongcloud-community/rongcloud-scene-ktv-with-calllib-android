package cn.rongcloud.ktvmusickit.listener;


/**
 * 歌曲播放进度回调
 */
public interface KtvSongProgressListener {
    /**
     * 设置当前播放进度，毫秒为单位
     *
     * @param currentPositionTime 毫秒进度时间
     */
    public void setCurrentTime(long currentPositionTime);

    /**
     * 设置暂停，播放开关
     *
     * @param isPause 是否暂停
     */
    public void setIsPause(boolean isPause);
}
