package cn.rongcloud.ktvmusickit.view;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.KToast;

import cn.rongcloud.ktvmusickit.R;

import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.listener.KtvSongListDialogListener;
import cn.rongcloud.ktvmusickit.listener.KtvSongNetworkInterfaceListener;
import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲列表总弹框
 */
public class KtvSongPlatformDialog extends BaseBottomSheetDialog {
    private ViewPager2 mOutViewPager;
    private TextView mClickSongText;
    private TextView mClickedSongText;

    private ImageView mDialogBgImage;

    private KtvSongListDialogListener mKtvSongListDialogListener;
    private KtvSongNetworkInterfaceListener mKtvSongNetworkInterfaceListener;

    /**
     * 已点列表标识
     */
    private KtvMusicManager.SelectType selectType = KtvMusicManager.SelectType.MUSIC;
    AllSongListFragment mAllSongListFragment;
    ClickedSongListFragment mClickedSongListFragment;
    /**
     * 正在播放的歌的id
     */
    private String mPlayingId = "";

    public KtvSongPlatformDialog(KtvSongListDialogListener ktvSongListDialogListener,
                                 KtvMusicManager.SelectType selectType, KtvSongNetworkInterfaceListener rcMusicKitListener, String playingId) {
        super(R.layout.ktv_song_platform_dialog);
        mKtvSongListDialogListener = ktvSongListDialogListener;
        mKtvSongNetworkInterfaceListener = rcMusicKitListener;
        this.selectType = selectType;
        mPlayingId = playingId;
    }

    @Override
    public void initView() {
        mClickSongText = getView().findViewById(R.id.click_song_text);
        mClickedSongText = getView().findViewById(R.id.clicked_song_text);
        mClickSongText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOutViewPager != null) {
                    mOutViewPager.setCurrentItem(0);
                }
            }
        });
        mClickedSongText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOutViewPager != null) {
                    mOutViewPager.setCurrentItem(1);
                }
            }
        });
        mOutViewPager = getView().findViewById(R.id.view_pager);

        mOutViewPager.setUserInputEnabled(false);
        List<Fragment> fragments = new ArrayList<>();
        mAllSongListFragment = new AllSongListFragment(mKtvSongNetworkInterfaceListener, this);
        fragments.add(mAllSongListFragment);
        mClickedSongListFragment = new ClickedSongListFragment(this, mKtvSongNetworkInterfaceListener, mKtvSongListDialogListener, mPlayingId);
        fragments.add(mClickedSongListFragment);

        FragmentStateAdapter fragmentStateAdapter =
                new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
                    @Override
                    public int getItemCount() {
                        return fragments.size();
                    }

                    @NonNull
                    @Override
                    public Fragment createFragment(int position) {
                        return fragments.get(position);
                    }
                };
        mOutViewPager.setAdapter(fragmentStateAdapter);
        mOutViewPager.setOffscreenPageLimit(2);
        mOutViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    mClickSongText.setTextColor(getResources().getColor(R.color.ktv_song_platform_title_selected_text_color));
                    mClickedSongText.setTextColor(getResources().getColor(R.color.ktv_song_platform_title_normal_text_color));
                } else {
                    mClickSongText.setTextColor(getResources().getColor(R.color.ktv_song_platform_title_normal_text_color));
                    mClickedSongText.setTextColor(getResources().getColor(R.color.ktv_song_platform_title_selected_text_color));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        if (selectType == KtvMusicManager.SelectType.MUSIC) {
            mOutViewPager.setCurrentItem(0, false);
        } else {
            mOutViewPager.setCurrentItem(1, false);
        }
    }


    /**
     * 通知或传递给已点列表刷新数据
     *
     * @param list
     */
    public void refreshClickList(List<ClickedMusic> list) {
        if (mClickedSongListFragment != null) {
            mClickedSongListFragment.loadClickedSongList(list);
        }
    }

    /**
     * 设置已点个数
     *
     * @param str
     */
    public void setClickedSongText(String str) {
        if (mClickedSongText != null) {
            mClickedSongText.setText(str);
        }
    }

    /**
     * 弹框消失时候向主房间传递当前最新已点歌曲列表，但是目前看需求并不需要，
     * 且房间任何时候都可以直接通过此onLoadMusicList获取最新已点列表
     *
     * @param dialog
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
//        如果房间需要此场景列表可以放开注释
        if (mKtvSongListDialogListener != null) {
            //获取最新已点音乐列表页
            KtvMusicManager.getInstance().onSelectedMusicList(new KtvDataCallBack<List<ClickedMusic>>() {
                @Override
                public void onResult(List<ClickedMusic> list) {
                    if (list == null) {
                        KToast.show("获取最新已点列表异常~");
                        return;

                    }
                    mKtvSongListDialogListener.disMissDialogClickedList(list);
                }
            });
        }
    }
}