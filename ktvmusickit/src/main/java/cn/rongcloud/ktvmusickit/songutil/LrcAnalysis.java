package cn.rongcloud.ktvmusickit.songutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 歌词文件解析类
 */
public class LrcAnalysis {
    /**
     * 歌词集合
     */
    private List mWords = new ArrayList();
    /**
     * 行时间集合
     */
    private List<Integer> mTimeList = new ArrayList();
    /**
     * 逐字时间集合
     */
    private List<String> wordTimeList = new ArrayList();

    /**
     * 每行逐字块个数集合
     */
    private List<String> wordCountList = new ArrayList();
    /**
     * 逐字分隔符
     */
    public static final char SEPARATOR = '*';

    //处理歌词⽂件
    public void readLRC(String path) {
        try {
            mWords.clear();
            mTimeList.clear();
            wordTimeList.clear();
            wordCountList.clear();
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            //目前先默认读取assets资源文件的歌词
//            InputStream fileInputStream =  getResources().getAssets().open("song_lrc.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String s;
            int lineIndex = -1;
            while ((s = bufferedReader.readLine()) != null) {
                if (s.trim().equals("")) {
                    continue;
                }
                lineIndex++;
                addTimeToList(s);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mWords.add("没有歌曲歌词信息，快去添加吧~");
        } catch (IOException e) {
            e.printStackTrace();
            mWords.add("没有读取到歌词");
        }
    }

    public List getWords() {
        return mWords;
    }

    public List getTime() {
        return mTimeList;
    }

    public List getWordTime() {
        return wordTimeList;
    }

    public List<String> getWordCountList() {
        return wordCountList;
    }

    // 分离出时间
    public int timeHandler(String string) {
        int currentTime = 0;
        try {
            string = string.replace(".", ":");
            String timeData[] = string.split(":");
            // 分离出分、秒并转换为整型
            int minute = Integer.parseInt(timeData[0]);
            int second = Integer.parseInt(timeData[1]);
            int millisecond = Integer.parseInt(timeData[2].substring(0, 2));
            // 计算上⼀⾏与下⼀⾏的时间转换为毫秒数
            currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    /**
     * 获取每行头部时间
     *
     * @param string 每行内容
     */
    private void addTimeToList(String string) {
        Matcher matcher = Pattern.compile(
                "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,3})?\\]").matcher(string);
        if (matcher.find()) {
            String str = matcher.group();
            mTimeList.add(new LrcAnalysis().timeHandler(str.substring(1,
                    str.length() - 1)));
            mWords.add(matcherWord(string.replace(str, "")));
            addWordTime(string, mTimeList.size() - 1);
        }
    }

    /**
     * 获取所有尖括号时间
     *
     * @param string 每行的内容
     */
    private void addWordTime(String string, int lineIndex) {
        Matcher matcher = Pattern.compile(
                "\\<\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\>").matcher(string);
        wordTimeList.add(lineIndex, "null");
        wordCountList.add(lineIndex, "null");
        String tempTime = null;
        while (matcher.find()) {
            String str = matcher.group();
            if (tempTime != null && !tempTime.equals("")) {
                String[] tempStringArray = string.split(tempTime);
                if (tempStringArray != null && tempStringArray.length == 2 &&
                        tempStringArray[1].split(str) != null && tempStringArray[1].split(str).length > 0) {
                    int length = tempStringArray[1].split(str)[0].length();
                    String oldWordCountStr = "";
                    if (!wordCountList.get(lineIndex).equals("null")) {
                        oldWordCountStr = wordCountList.get(lineIndex) + SEPARATOR;
                    }
                    wordCountList.set(lineIndex, oldWordCountStr + length);
                }
            }
            tempTime = str;

            String oldWordTimeStr = "";
            if (wordTimeList.get(lineIndex).equals("null")) {
                oldWordTimeStr = "";
            } else {
                oldWordTimeStr = wordTimeList.get(lineIndex) + SEPARATOR;
            }
            wordTimeList.set(lineIndex, oldWordTimeStr + new LrcAnalysis().timeHandler(str.substring(1,
                    str.length() - 1)));
        }
    }

    public String matcherWord(String str) {
        Matcher matchers = Pattern.compile(
                "\\<\\d{1,2}:\\d{1,2}([\\.:]\\d{1,3})?\\>").matcher(str);
        while (matchers.find()) {
            String bracketsStr = matchers.group();
            str = str.replace(bracketsStr, "");
        }
        str = str.replace("{W}", "");
        return str;
    }
}
