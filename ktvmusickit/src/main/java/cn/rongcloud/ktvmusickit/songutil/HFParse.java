package cn.rongcloud.ktvmusickit.songutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gyn
 * @date 2022/8/24
 * <p>
 * [00:01.98]{W}歌手：诺儿
 * [00:23.60]{W}<00:23.60>当<00:23.83>我<00:24.10>一<00:24.39>个<00:24.66>人<00:25.03>走<00:25.33>在<00:25.57>
 * [00:25.76]{W}<00:25.76>黑<00:26.05>夜<00:26.35>的<00:26.67>雨<00:27.01>中<00:27.44>
 */
public class HFParse extends BaseParse {
    @Override
    public String getLineRegex() {
        return "\\[\\d{2}:\\d{2}.\\d{2,}\\]\\{W\\}";
    }

    @Override
    public String getWordRegex() {
        return "\\<\\d{2}:\\d{2}.\\d{2,}\\>";
    }

    /**
     * @param line 以此为例：[00:25.76]{W}<00:25.76>黑<00:26.05>夜<00:26.35>的<00:26.67>雨<00:27.01>中<00:27.44>
     * @return
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
            // 找到开始时间，如： [00:25.76]
            String str = matcher.group();
            // 赋值开始时间 25*1000+76*10
            startTime = parseTime(str);
            // 去除开始时间，line = <00:25.76>黑<00:26.05>夜<00:26.35>的<00:26.67>雨<00:27.01>中<00:27.44>
            line = line.replace(str, "");
            // 匹配字的时间戳
            Matcher matcherWord = Pattern.compile(getWordRegex()).matcher(line);
            String wordTime = "";
            int oldIndex = 0;
            while (matcherWord.find()) {
                // 找到了 <00:25.76>
                wordTime = matcherWord.group();
                if (lineWordTime == null) {
                    lineWordTime = new StringBuilder();
                }
                lineWordTime = lineWordTime.append(parseTime(wordTime)).append(SEPARATOR);
                int index = line.indexOf(wordTime);
                int length = index - oldIndex;
                if (length > 0) {
                    if (lineWordCount == null) {
                        lineWordCount = new StringBuilder();
                    }
                    lineWordCount = lineWordCount.append(length).append(SEPARATOR);
                }
                oldIndex = index;
                line = line.replace(wordTime, "");
            }
            lineLrc = line;

            return true;
        }
        return false;
    }

    @Override
    public long parseTime(String time) {
        // [00:25.76]{W}
        Matcher matcher = Pattern.compile("\\d{2}:\\d{2}.\\d{2,}").matcher(time);
        if (matcher.find()) {
            time = matcher.group();
            time = time.replace(".", ":");
            String[] times = time.split(":");
            if (times.length == 3) {
                // 分离出分、秒并转换为整型
                int minute = Integer.parseInt(times[0]);
                int second = Integer.parseInt(times[1]);
                int millisecond = Integer.parseInt(times[2].substring(0, 2));
                // 计算上⼀⾏与下⼀⾏的时间转换为毫秒数
                return (minute * 60 + second) * 1000 + millisecond * 10;
            }
        }
        return 0;
    }
}
