package cn.rongcloud.ktvmusickit.view.lrc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.basis.utils.UiUtils;

import cn.rongcloud.ktvmusickit.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
/**
 * 自定义歌词view
 */
public class LrcView extends TextView {
    private List<String> mWordsList = new ArrayList();
    private Paint mLoseFocusPaint;
    private Paint mOnFocusePaint;
    private float mX = 0;
    private float mMiddleY = 0;
    private float mY = 0;
    private static final int DY = UiUtils.dp2px(28);
    public static final int BIG_SIZE = UiUtils.dp2px(16);
    public static final int SMALL_SIZE = UiUtils.dp2px(12);

    private int mCurrentTextSize = BIG_SIZE;
    private int mSoonTextSize = SMALL_SIZE;

    private int mIndex = 0;
    private float currentRowLyricWidth;
    private float singleWidth;
    private int clipRectRight;
    private int showLines = 3;

    /**
     * 当前行歌词默认颜色
     */
    private int mCurrentColor = Color.WHITE;
    /**
     * 正在唱的歌词颜色
     */
    private int mInProcessColor =
            getContext().getResources().getColor(R.color.ktv_in_process_lyc_color);
    /**
     * 即将唱的歌词颜色
     */
    private int mSoonColor = Color.WHITE;
    /**
     * 已唱过的歌词颜色
     */
    private int mCompleteColor = Color.WHITE;

    public LrcView(Context context) throws IOException {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyle)
            throws IOException {
        super(context, attrs, defStyle);
        init();
    }

    public void setShowLines(int showLines){
        this.showLines = showLines;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWordsList == null || mWordsList.size() == 0) {
            return;
        }
        canvas.translate(0, translateY);
        Paint p = mLoseFocusPaint;
        p.setTextSize(SMALL_SIZE);
        p.setTextAlign(Paint.Align.CENTER);
        Paint p2 = mOnFocusePaint;
        p2.setTextSize(mCurrentTextSize);
        p2.setTextAlign(Paint.Align.CENTER);
        //这是绘制当前唱的这行歌词
        canvas.drawText(mWordsList.get(mIndex), mX, mMiddleY, p2);
        verbatim(canvas, "", 1, p2);

        int alphaValue = 25;
        float tempY = mMiddleY;
        //此循环是绘制被唱完的那一行的歌词，根据屏幕view高度来看正常情况此循环只跑一次，
        //第一次tempY=mMiddleY-DY(mMiddleY固定为h * 0.3f，DY固定为50)，所以第一次tempY大概等于四十多
        //第二次必然tempY < 0
        for (int i = mIndex - 1; i >= 0; i--) {
            tempY -= DY;
            if (tempY < 0) {
                break;
            }
            //蓝色RGB
//            p.setColor(mCompleteColor);
            //i = mIndex - 1
//            canvas.drawText(mWordsList.get(i), mX, tempY, p);

            alphaValue += 25;
        }
        alphaValue = 25;
        tempY = mMiddleY;
        //此循环是绘制接下来将要唱到的歌词
        for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
            tempY += DY;
            if (i > mIndex + showLines) {
                break;
            }
            if (i == mIndex + 1 && translateY != 0) {
                p.setTextSize(mSoonTextSize);
            } else {
                p.setTextSize(SMALL_SIZE);
            }
            //紫色
            p.setColor(mSoonColor);
            canvas.drawText(mWordsList.get(i), mX, tempY, p);
            alphaValue += 25;
        }
    }


    public void verbatim(Canvas canvas, String contentStr, int textColor, Paint paint) {

        currentRowLyricWidth = paint.measureText(mWordsList.get(mIndex));
        if (singleWidth != currentRowLyricWidth / mWordsList.get(mIndex).length()) {

            singleWidth = currentRowLyricWidth / mWordsList.get(mIndex).length();
        }


        Rect rect = new Rect();
        rect.left = (int) (mX - currentRowLyricWidth / 2);
        rect.top = 0;
        rect.right = (int) (rect.left + clipRectRight);
        rect.bottom = getHeight();
//        //画带颜色的文字 根据mProgress的进度

//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
        paint.setColor(mInProcessColor);
//        paint.setTextSize(30);
//        paint.setTypeface(Typeface.SANS_SERIF);
//        paint.setTextAlign(Paint.Align.CENTER);
        canvas.save();
        canvas.clipRect(rect);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawText(mWordsList.get(mIndex), mX, mMiddleY, paint);
        canvas.restore();
        mOnFocusePaint.setColor(mCurrentColor);
    }

    int translateY;

    public void setTranslateY(int y) {
        translateY = y;
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mX = w * 0.5f;
        mY = h;
        mMiddleY = BIG_SIZE;
    }

    @SuppressLint("SdCardPath")
    private void init() throws IOException {
        setFocusable(true);
        mLoseFocusPaint = new Paint();
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(SMALL_SIZE);
        mLoseFocusPaint.setColor(mSoonColor);
        mOnFocusePaint = new Paint();
        mOnFocusePaint.setAntiAlias(true);
        mOnFocusePaint.setColor(mCurrentColor);
        mOnFocusePaint.setTextSize(BIG_SIZE);
    }

    public float getCurrentRowLyricWidth() {
        return currentRowLyricWidth;
    }

    public void setClipRectRight(int clipRectRight) {
        this.clipRectRight = clipRectRight;
    }

    public float getSingleWidth() {
        return singleWidth;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getDY() {
        return DY;
    }


    public void setCurrentTextSize(int currentTextSize, boolean isNewLine) {
        mCurrentTextSize = currentTextSize;
        if (isNewLine && mWordsList.size() > mIndex) {
            Paint paint = mOnFocusePaint;
            paint.setTextSize(mCurrentTextSize);
            currentRowLyricWidth = paint.measureText(mWordsList.get(mIndex));
            if (singleWidth != currentRowLyricWidth / mWordsList.get(mIndex).length()) {

                singleWidth = currentRowLyricWidth / mWordsList.get(mIndex).length();
            }
        }
    }

    public void setSoonTextSize(int soonTextSize) {
        mSoonTextSize = soonTextSize;
    }

    public void setLrcList(List<String> wordsList) {
        mWordsList = wordsList;
        mIndex = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, showLines * DY);
    }
}