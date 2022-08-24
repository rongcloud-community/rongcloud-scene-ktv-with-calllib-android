package cn.rongcloud.ktvmusickit.songutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gyn
 * @date 2022/8/24
 * 自定义解析器可以继承该类
 */
public abstract class BaseParse {
    public static final String SEPARATOR = "-";
    // 200我599爱649你698
    /**
     * 本行的开始时间戳
     * 如：200
     */
    long startTime;
    /**
     * 本行的歌词内容
     * 如：我爱你
     */
    String lineLrc;
    /**
     * 本行每个字的时间戳,用*分割的
     * 如：200*599*649*698
     */
    StringBuilder lineWordTime;
    /**
     * 2个时间戳中间的字数，用*分割
     * 如：1*1*1
     */
    StringBuilder lineWordCount;

    /**
     * @return 行开头的正则
     */
    abstract String getLineRegex();

    /**
     * @return 字开头的正则
     */
    abstract String getWordRegex();


    public boolean isMatch(String line) {
        Matcher matchers = Pattern.compile(getLineRegex()).matcher(line);
        return matchers.find();
    }

    abstract boolean parseLrcInfo(String line);

    /**
     * @return 解析时间戳
     */
    abstract long parseTime(String time);


}
