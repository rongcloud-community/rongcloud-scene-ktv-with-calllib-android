package cn.rongcloud.ktvmusickit.callback;

/**
 * 下载进度和结果回调
 */
public interface SongInfoCallBack<T>{
    /**
     * 歌曲下载进度
     *
     * @param progress
     */
    void onDownProgress(float progress);

    /**
     * 下载结果
     *
     * @param result
     */
    void onResult(T result);
}