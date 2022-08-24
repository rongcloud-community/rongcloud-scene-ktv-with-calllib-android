package cn.rongcloud.ktvmusickit.songutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gyn
 * @date 2022/8/24
 * 解析酷狗格式歌词
 * [3228,650]编(3228,200)曲(3428,150)：Fsy小(3578,150)诺(3728,150)
 * [33486,3150]别(33486,350)待(33836,200)长(34036,200)亭(34236,250)晚(34486,650)望(35136,550)日(35686,250)落(35936,200)江(36136,250)寒(36386,250)
 */
public class KrcParse extends BaseParse {
    @Override
    public String getLineRegex() {
        return "\\[[1-9]\\d+,[1-9]\\d+\\]";
    }

    @Override
    public String getWordRegex() {
        return "\\([1-9]\\d+,[1-9]\\d+\\)";
    }

    /**
     * @param line 以此为例：[3228,650]编(3228,200)曲(3428,150)：Fsy小(3578,150)诺(3728,150)
     * @return 是否解析
     */
    @Override
    boolean parseLrcInfo(String line) {
        startTime = 0;
        lineLrc = null;
        lineWordTime = null;
        lineWordCount = null;
        // 匹配开头
        Matcher matcher = Pattern.compile(getLineRegex()).matcher(line);
        if (matcher.find()) {
            // 找到开始时间，如： [3228,650]
            String str = matcher.group();
            // 赋值开始时间 3228
            startTime = parseTime(str);
            // 去除开始时间，line = 编(3228,200)曲(3428,150)：Fsy小(3578,150)诺(3728,150)
            line = line.replace(str, "");
            // 匹配字的时间戳
            Matcher matcherWord = Pattern.compile(getWordRegex()).matcher(line);
            String wordTime = "";
            int oldIndex = 0;
            while (matcherWord.find()) {
                // 找到了 (3228,200)
                wordTime = matcherWord.group();
                if (lineWordTime == null) {
                    lineWordTime = new StringBuilder();
                }
                lineWordTime = lineWordTime.append(parseTime(wordTime)).append(SEPARATOR);
                int index = line.indexOf(wordTime);
                int length = index - oldIndex;
                if(length > 0){
                    if (lineWordCount == null) {
                        lineWordCount = new StringBuilder();
                    }
                    lineWordCount = lineWordCount.append(length).append(SEPARATOR);
                }
                oldIndex = index;
                line = line.replace(wordTime, "");
            }
            if (lineWordTime != null) {
                lineWordTime.append(parseEndTime(wordTime));
            }
            lineLrc = line;

            return true;
        }
        return false;
    }

    @Override
    public long parseTime(String time) {
        // 去除括号[3228,650]
        time = time.substring(1, time.length() - 1);
        // 3228,650
        String[] times = time.split(",");
        if (times.length > 0) {
            try {
                // 返回 3228
                return Long.parseLong(times[0]);
            } catch (NumberFormatException e) {

            }
        }
        return 0;
    }

    private long parseEndTime(String time) {
        // 去除括号[3228,650]
        time = time.substring(1, time.length() - 1);
        // 3228,650
        String[] times = time.split(",");
        if (times.length == 2) {
            try {
                // 返回 3228+650
                return Long.parseLong(times[0]) + Long.parseLong(times[1]);
            } catch (NumberFormatException e) {

            }
        }
        return 0;
    }
}
