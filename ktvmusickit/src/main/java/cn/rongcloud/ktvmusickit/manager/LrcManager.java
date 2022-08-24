package cn.rongcloud.ktvmusickit.manager;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.basis.utils.KToast;
import com.basis.utils.Logger;

import cn.rongcloud.ktvmusickit.R;
import cn.rongcloud.ktvmusickit.listener.KtvSongProgressListener;
import cn.rongcloud.ktvmusickit.songutil.BaseParse;
import cn.rongcloud.ktvmusickit.songutil.LrcAnalysis;
import cn.rongcloud.ktvmusickit.view.SongScreenView;
import cn.rongcloud.ktvmusickit.view.lrc.LrcView;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌唱，歌词等内容的管理者
 */
public class LrcManager implements KtvSongProgressListener {
    private LinearLayout topCircleLayout;
    /**
     * 第1个预备圆点
     */
    private View lrcCircle1;

    /**
     * 第2个预备圆点
     */
    private View lrcCircle2;

    /**
     * 第3个预备圆点
     */
    private View lrcCircle3;

    /**
     * 第4个预备圆点
     */
    private View lrcCircle4;
    private LrcView mLrcView;
    private List<Long> mTimeList = new ArrayList<>();
    private List<String> wordTimeList = new ArrayList();
    private List<String> mWordsList = new ArrayList();

    /**
     * 每行逐字块个数集合
     */
    private List<String> wordCountList = new ArrayList();

    LrcAnalysis lrcHandler = new LrcAnalysis();


    final float TRANSLATE_TIME = 300;

    /**
     * 当前动画记录时间
     */
    private long nowTranslateTime;

    /**
     * 行下标
     */
    private int mLineIndex = 0;


    private ValueAnimator mCustomAnimator;
    long nowTime;
    private boolean isPause = false;
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                topCircleLayout.setVisibility(View.INVISIBLE);
            } else {
                if (topCircleLayout != null && topCircleLayout.getChildCount() == 4 && topCircleLayout.getVisibility() == View.VISIBLE) {
                    for (int i = 1; i <= 4; i++) {
                        if (i <= msg.what) {
                            topCircleLayout.getChildAt(i - 1).setVisibility(View.VISIBLE);
                        } else {
                            topCircleLayout.getChildAt(i - 1).setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
    };

    public LrcManager(SongScreenView rootView) {
        topCircleLayout = rootView.findViewById(R.id.top_circle_lay);
        lrcCircle1 = rootView.findViewById(R.id.lrc_circle1);
        lrcCircle2 = rootView.findViewById(R.id.lrc_circle2);
        lrcCircle3 = rootView.findViewById(R.id.lrc_circle3);
        lrcCircle4 = rootView.findViewById(R.id.lrc_circle4);
        mLrcView = rootView.findViewById(R.id.lrc_view);
    }

    public void setLrcFile(String path) {
        //解析歌词文件并获取三项集合
        lrcHandler.readLRC(path);
        mTimeList = lrcHandler.getTime();
        wordTimeList = lrcHandler.getWordTime();
        mWordsList = lrcHandler.getWords();
        wordCountList = lrcHandler.getWordCountList();
        Logger.e(mTimeList);
        Logger.e(wordTimeList);
        Logger.e(mWordsList);
        Logger.e(wordCountList);
        mLrcView.setLrcList(mWordsList);
        mLineIndex = 0;
        nowTranslateTime = 0;
        topCircleLayout.setVisibility(View.VISIBLE);
        lrcCircle1.setVisibility(View.INVISIBLE);
        lrcCircle2.setVisibility(View.INVISIBLE);
        lrcCircle3.setVisibility(View.INVISIBLE);
        lrcCircle4.setVisibility(View.INVISIBLE);
    }


    /**
     * 时间回调，进行歌词UI动画
     *
     * @param currentPositionTime 毫秒进度时间
     */
    @Override
    public void setCurrentTime(long currentPositionTime) {
        if (mLrcView == null) {
            return;
        }
        if (mTimeList == null || mTimeList.size() == 0 || mWordsList == null || mWordsList.size() == 0) {
            refreshLine(0);
            return;
        }
//        setCircle(currentPositionTime);
        stopAnim();
        if (isPause) {
            return;
        }
        nowTime = currentPositionTime;
        allLrcUI(nowTime);

        //下面的动画是为了精确10毫秒时间而做的模式时间点位动画，为了更加流畅
        mCustomAnimator = ValueAnimator.ofInt(0, 190);
        mCustomAnimator.setDuration(190);
        mCustomAnimator.setInterpolator(new LinearInterpolator());
        mCustomAnimator.addUpdateListener(new ValueAnimator
                .AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int s = (int) animation.getAnimatedValue();
                allLrcUI(nowTime + s);
            }
        });
        try {
            mCustomAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
//                        observable = Observable.interval(0, 10, TimeUnit.MILLISECONDS)
//                                // 设置事件次数
//                                .take(20);
//                        observable.subscribe(new Observer() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//                                System.out.println("=====onSubscribe");
//                            }
//
//                            @Override
//                            public void onNext(Object value) {
//                                System.out.println(nowTime + "=====内围事件===" + value);
//                                allLrcUI(nowTime + (long) value * 10);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                System.out.println("=====onError");
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                System.out.println("=====onComplete");
//                            }
//                        });
    }

    /**
     * 这个是测试用的mediaplayer开关判断
     *
     * @param isPause 是否暂停
     */
    @Override
    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }


    /**
     * 此方法管理所有歌词UI
     *
     * @param nowTime 当前时间毫秒数
     */
    public void allLrcUI(long nowTime) {
        if (nowTranslateTime < nowTime) {
            nowTranslateTime = nowTime;
        } else {
            return;
        }
        if (mTimeList == null || mTimeList.size() == 0) {
            return;
        }
        if (nowTime < mTimeList.get(0)) {
            mLrcView.setClipRectRight(0);
            //这里表明是开头第一行
            refreshLine(0);
            return;
        }
        for (int i = 0; i < mTimeList.size(); i++) {
            if (mTimeList.get(i) - TRANSLATE_TIME < nowTime && nowTime < mTimeList.get(i)) {
                float timeDifference = mTimeList.get(i) - mTimeList.get(i - 1);
                float ratio;
                if (timeDifference < TRANSLATE_TIME) {
                    ratio = (timeDifference - (mTimeList.get(i) - nowTime)) / timeDifference;
                } else {
                    ratio = (TRANSLATE_TIME - (mTimeList.get(i) - nowTime)) / TRANSLATE_TIME;
                }

                if (ratio > 0 && ratio <= 1) {
                    if (i - 1 != mLineIndex) {
                        refreshLine(i - 1);
                    }
                    //这是位移放大缩小动画
                    lrcChange(ratio);
                }
                return;
            }
        }

        for (int i = 0; i < mTimeList.size(); i++) {
            if (mTimeList.get(i) < nowTime && (i == mTimeList.size() - 1 || nowTime < mTimeList.get(i + 1))) {
                //歌词置行
                refreshLine(i);

                //逐字动画
                refreshWordAnim(nowTime);
                return;
            }
        }
    }

    /**
     * 歌词行处理
     *
     * @param lineIndex 行下标也是对应数据下标
     */
    public void refreshLine(int lineIndex) {
        if (mLineIndex < lineIndex) {
            mLineIndex = lineIndex;
        }
        //这个设置可能会被动画的setRefreshLine覆盖掉导致无效

        mLrcView.setIndex(mLineIndex);
        //每行的从头重绘不要有画布移动，让最终字后的动画进行画布移动
        mLrcView.setTranslateY(0);
        mLrcView.setCurrentTextSize(LrcView.BIG_SIZE, true);
        mLrcView.setSoonTextSize(LrcView.SMALL_SIZE);
        mLrcView.postInvalidate();
    }

    public void refreshWordAnim(long time) {
        if (mLineIndex >= wordTimeList.size() || mLineIndex > wordCountList.size()) {
            return;
        }
        //获取当前行所有尖括号时间
        String wordTimeString = wordTimeList.get(mLineIndex);
        //获取当前行每对尖括号时间之间的字数
        String wordCountString = wordCountList.get(mLineIndex);
        if (wordTimeString != null && !wordTimeString.equals("null") && wordCountString != null && !wordCountString.equals("null")) {
            //当前行每个尖括号时间放入数组
            String[] wordTimeArray = wordTimeString.split(BaseParse.SEPARATOR);
            //当前行每对尖括号时间之间的词个数放入数组
            String[] wordCountArray = wordCountString.split(BaseParse.SEPARATOR);
            if (wordTimeArray.length == 0 || wordCountArray.length == 0 || wordTimeArray.length != wordCountArray.length + 1) {
                return;
            }
            int preCount = 0;
            boolean isInvalidate = false;
            for (int i = 0; i < wordTimeArray.length - 1; i++) {
                if (i != 0) {
                    preCount = preCount + Integer.parseInt(wordCountArray[i - 1]);
                }
                float preTime = Long.parseLong(wordTimeArray[i]);
                float nextTime = Long.parseLong(wordTimeArray[i + 1]);
                float ratio = 1;
                if (preTime < time && time < nextTime) {
                    ratio = (time - preTime) / (nextTime - preTime);
                    float distance = mLrcView.getSingleWidth();
                    mLrcView.setClipRectRight((int) (preCount * distance + Integer.parseInt(wordCountArray[i]) * distance * ratio));
                    mLrcView.postInvalidate();
                    isInvalidate = true;
                    break;
                }
            }
            if (!isInvalidate) {
                //如果没有逐字时间，则整行覆盖唱过的颜色
                mLrcView.setClipRectRight(10000);
                mLrcView.postInvalidate();
            }
        } else {
            //如果没有逐字时间，则整行覆盖唱过的颜色
            mLrcView.setClipRectRight(10000);
            mLrcView.postInvalidate();
        }
    }

    /**
     * 歌词位移，放大，缩小
     */
    public void lrcChange(final float ratio) {
        //歌词位移
        mLrcView.setTranslateY((int) (ratio * -mLrcView.getDY()));

        //歌词缩小
        mLrcView.setCurrentTextSize((int) (mLrcView.BIG_SIZE - ratio * (mLrcView.BIG_SIZE
                - mLrcView.SMALL_SIZE)), false);
        //歌词放大
        mLrcView.setSoonTextSize((int) (ratio * (mLrcView.BIG_SIZE
                - mLrcView.SMALL_SIZE) + LrcView.SMALL_SIZE));
        //刷新UI
        mLrcView.postInvalidate();
    }

    public void stopAnim() {
        if (mCustomAnimator != null && mCustomAnimator.isRunning()) {
            try {
                mCustomAnimator.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放完毕处理
     */
    public void playEnd() {
        setLrcFile("");
        setCurrentTime(0);
        if (mLrcView != null) {
            mLrcView.setClipRectRight(10000);
            mLrcView.postInvalidate();
        }
    }

    /**
     * 设置顶部四个点
     *
     * @param currentPositionTime 进度时间
     */
    public void setCircle(long currentPositionTime) {
        if (topCircleLayout.getVisibility() != View.VISIBLE || mLrcView == null || mTimeList == null || mTimeList.size() == 0 || mWordsList == null || mWordsList.size() == 0 ||
                mTimeList.get(0) < 4000) {
            return;
        }
        long firstTime = mTimeList.get(0);
        if (currentPositionTime < firstTime) {
            if (firstTime - 4000 <= currentPositionTime && currentPositionTime < firstTime - 3000) {
                handler.sendEmptyMessage(4);
            } else if (firstTime - 3000 <= currentPositionTime && currentPositionTime < firstTime - 2000) {
                handler.sendEmptyMessage(3);
            } else if (firstTime - 2000 <= currentPositionTime && currentPositionTime < firstTime - 1000) {
                handler.sendEmptyMessage(2);
            } else if (firstTime - 1000 <= currentPositionTime && currentPositionTime < firstTime) {
                handler.sendEmptyMessage(1);
            }
        } else {
            handler.sendEmptyMessage(0);
        }
    }
}

