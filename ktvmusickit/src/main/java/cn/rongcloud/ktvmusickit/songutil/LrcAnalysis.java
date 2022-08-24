package cn.rongcloud.ktvmusickit.songutil;

import com.basis.utils.UIKit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * 歌词文件解析类
 */
public class LrcAnalysis {
    /**
     * 歌词集合
     */
    private List<String> mWords = new ArrayList();
    /**
     * 行时间集合
     */
    private List<Long> mTimeList = new ArrayList();
    /**
     * 逐字时间集合
     */
    private List<String> wordTimeList = new ArrayList();

    /**
     * 每行逐字块个数集合
     */
    private List<String> wordCountList = new ArrayList();

    private List<BaseParse> parseLrcList;

    private BaseParse mParseLrc;

    public LrcAnalysis() {
        parseLrcList = new ArrayList<>();
        parseLrcList.add(new KrcParse());
        parseLrcList.add(new HFParse());
        parseLrcList.add(new LrcParse());
    }

    /**
     * 添加自定义解析器
     *
     * @param parseLrc
     */
    public void addParse(BaseParse parseLrc) {
        if (parseLrc != null) {
            parseLrcList.add(parseLrc);
        }
    }

    //处理歌词⽂件
    public void readLRC(String path) {
        try {
            mParseLrc = null;
            mWords.clear();
            mTimeList.clear();
            wordTimeList.clear();
            wordCountList.clear();
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            //目前先默认读取assets资源文件的歌词
            // InputStream fileInputStream = UIKit.getContext().getResources().getAssets().open("2.krc");
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

    public List<String> getWords() {
        return mWords;
    }

    public List<Long> getTime() {
        return mTimeList;
    }

    public List<String> getWordTime() {
        return wordTimeList;
    }

    public List<String> getWordCountList() {
        return wordCountList;
    }


    /**
     * 获取每行头部时间
     *
     * @param string 每行内容
     */
    private void addTimeToList(String string) {
        // 确定使用哪种歌词解析
        if (mParseLrc == null) {
            for (BaseParse iParseLrc : parseLrcList) {
                if (iParseLrc.isMatch(string)) {
                    mParseLrc = iParseLrc;
                    break;
                }
            }
        }
        if (mParseLrc == null) {
            return;
        }
        boolean isParse = mParseLrc.parseLrcInfo(string);
        if (isParse) {
            mTimeList.add(mParseLrc.startTime);
            mWords.add(mParseLrc.lineLrc);
            if (mParseLrc.lineWordTime != null) {
                wordTimeList.add(mParseLrc.lineWordTime.toString());
            } else {
                wordTimeList.add("null");
            }
            if (mParseLrc.lineWordCount != null) {
                wordCountList.add(mParseLrc.lineWordCount.toString());
            } else {
                wordCountList.add("null");
            }
        }
    }
}
