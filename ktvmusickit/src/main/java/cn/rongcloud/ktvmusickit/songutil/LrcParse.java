package cn.rongcloud.ktvmusickit.songutil;

/**
 * @author gyn
 * @date 2022/8/24
 * <p>
 * [00:00.00]到这里就好 - 蒋坤炜
 * [00:03.08]词：秦园园
 * [00:03.86]曲：何子豪
 */
public class LrcParse extends HFParse {
    @Override
    public String getLineRegex() {
        return "\\[\\d{2}:\\d{2}.\\d{2,}\\]";
    }
}
