package cn.rongcloud.ktvmusickit.listener;

import java.util.Map;

/**
 * 混响音效弹框回调监听
 */
public interface KtvKitMixDialogListener {
    /**
     * 混响类型
     *
     * @param index 混响类型列表下标
     */
    public void reverbChanged(int index);

    /**
     * 伴奏音量
     *
     * @param progress 伴奏音量
     */
    public void accompVolumeChanged(int progress);

    /**
     * 人声音量
     *
     * @param progress 人声音量
     */
    public void vocalVolumeChanged(int progress);


    /**
     * 升降调
     *
     * @param value 升降调
     */
    public void toneStepperChanged(int value);

    /**
     * 是否开启耳返
     *
     * @param isEar 是否
     */
    public void earReturnChanged(boolean isEar);


    /**
     * 是否开启音准器
     *
     * @param isIntonation 是否
     */
    public void pincherChanged(boolean isIntonation);

    /**
     * 返回全部信息
     *
     * @param map
     */
    public void allInfo(Map<String, Object> map);
}
