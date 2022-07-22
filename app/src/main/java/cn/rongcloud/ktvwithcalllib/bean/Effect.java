package cn.rongcloud.ktvwithcalllib.bean;

import cn.rongcloud.voicebeautifier.RCRTCVoiceBeautifierPreset;

/**
 * @author gyn
 * @date 2022/7/14
 */
public class Effect {
    private RCRTCVoiceBeautifierPreset voiceBeautifierPreset;
    private String name;

    public Effect(RCRTCVoiceBeautifierPreset voiceBeautifierPreset, String name) {
        this.voiceBeautifierPreset = voiceBeautifierPreset;
        this.name = name;
    }

    public RCRTCVoiceBeautifierPreset getVoiceBeautifierPreset() {
        return voiceBeautifierPreset;
    }

    public void setVoiceBeautifierPreset(RCRTCVoiceBeautifierPreset voiceBeautifierPreset) {
        this.voiceBeautifierPreset = voiceBeautifierPreset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
