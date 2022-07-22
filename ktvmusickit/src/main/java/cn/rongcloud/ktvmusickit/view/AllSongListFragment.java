package cn.rongcloud.ktvmusickit.view;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.NetUtil;
import com.basis.utils.SoftKeyboardUtils;

import cn.rongcloud.ktvmusickit.R;

import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import cn.rongcloud.ktvmusickit.callback.DataCallback;
import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.callback.SongInfoCallBack;
import cn.rongcloud.ktvmusickit.listener.KtvSongNetworkInterfaceListener;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.ktvmusickit.model.ChannelSheetBean;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.model.Music;
import cn.rongcloud.ktvmusickit.model.MusicDetailBean;
import cn.rongcloud.ktvmusickit.model.Sheet;
import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

/**
 * 歌曲列表中间框
 */
public class AllSongListFragment extends BaseFragment {
    private ViewGroup mSearchRelay;
    private ViewGroup mSongTypeRelay;
    private EditText editText;
    private ImageView ivClear;
    private ViewPager2 mViewPager;

    KtvSongNetworkInterfaceListener mKtvSongNetworkInterfaceListener;
    List<SongListFragment> fragments = new ArrayList<>();
    private KtvSongPlatformDialog mKtvSongPlatformDialog;

    private ViewGroup searchLayout;
    private RelativeLayout mNullRelay;
    private TextView nullText;
    private RecyclerView mRecyclerView;
    private List<Music> mMusicList = new ArrayList<>();
    private RecyclerAdapter mRecyclerAdapter;
    private SmartRefreshLayout srlRefresh;

    public AllSongListFragment(KtvSongNetworkInterfaceListener rcMusicKitListener, KtvSongPlatformDialog ktvSongPlatformDialog) {
        this.mKtvSongNetworkInterfaceListener = rcMusicKitListener;
        mKtvSongPlatformDialog = ktvSongPlatformDialog;
    }

    @Override
    public int setLayoutId() {
        return R.layout.song_list_outer_layout;
    }


    @Override
    public void init() {
        mSearchRelay = getView().findViewById(R.id.search_relay);
        mSongTypeRelay = getView().findViewById(R.id.song_type_layout);
        editText = getView().findViewById(R.id.song_edit);
        ivClear = getView().findViewById(R.id.iv_clear);
//        mDialogBgImage = getView().findViewById(R.id.dialog_bg_image);

        mViewPager = getView().findViewById(R.id.vp_switch);
        initSearchView();

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
        mViewPager.setAdapter(fragmentStateAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < mSongTypeRelay.getChildCount(); i++) {
                    View tagView = mSongTypeRelay.getChildAt(i);
                    TextView tagText = tagView.findViewById(R.id.tag_text);
                    View bottomView = tagView.findViewById(R.id.bottom_view);

                    if (position == i) {
                        tagText.setTextColor(getResources().getColor(R.color.ktv_song_platform_title_selected_text_color));
                        bottomView.setBackgroundResource(R.drawable.song_type_shape);
                        bottomView.setVisibility(View.VISIBLE);
                    } else {
                        tagText.setTextColor(getResources().getColor(R.color.ktv_song_platform_title_normal_text_color));
                        bottomView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        if (NetUtil.isNetworkAvailable()) {
            mKtvSongNetworkInterfaceListener.onLoadMusicCategory(new DataCallback<List<ChannelSheetBean>>() {
                @Override
                public void onResult(List<ChannelSheetBean> channelSheetBeanList) {
                    if (channelSheetBeanList == null) {
                        KToast.show("获取不到电台列表~");
                        return;
                    }
                    fragments = new ArrayList<>();

                    List<Sheet> list = channelSheetBeanList.get(0).getRecord();
                    for (int i = 0; i < list.size(); i++) {
                        Sheet musicCategory = list.get(i);
                        if (getContext() != null) {
                            //每次信息列表循环都添加一个tag
                            View tagView = LayoutInflater.from(getContext()).inflate(R.layout.music_tag_item, null);
                            TextView tagText = tagView.findViewById(R.id.tag_text);
                            tagText.setText(musicCategory.getSheetName());
                            int finalI = i;
                            tagView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mViewPager.setCurrentItem(finalI);
                                }
                            });
                            mSongTypeRelay.addView(tagView);
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tagView.getLayoutParams();
                            if (i > 0) {
                                layoutParams.setMargins(dip2pix(25), 0, 0, 0);
                            } else {
                                layoutParams.setMargins(0, 0, 0, 0);
                            }

                            //每次信息列表循环都添加一个歌曲列表fragment
                            fragments.add(new SongListFragment(mKtvSongNetworkInterfaceListener, musicCategory.getSheetId() + "", mKtvSongPlatformDialog));
                        }
                    }
                    fragmentStateAdapter.notifyDataSetChanged();
                }
            });
        } else {
            searchLayout.setVisibility(View.VISIBLE);
            mNullRelay.setVisibility(View.VISIBLE);
        }


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Editable e = editText.getText();
                String keywords = e.toString().trim();
                if (e == null || e.toString().isEmpty() || keywords.isEmpty()) {
                    KToast.show("不能为空~");
                    return true;
                }

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!NetUtil.isNetworkAvailable()) {
                        KToast.show("请检查您的网络是否正常~");
                        return true;
                    }

                    mKtvSongNetworkInterfaceListener.onSearchMusic(keywords, new DataCallback<List<Music>>() {
                        @Override
                        public void onResult(List<Music> music) {
                            mMusicList.clear();
                            if (music != null) {
                                mMusicList.addAll(music);
                            }

                            if (mRecyclerAdapter != null) {
                                mRecyclerAdapter.notifyDataSetChanged();
                                if (mMusicList == null || mMusicList.size() == 0) {
                                    KToast.show("未找到搜索结果");
                                }
                                setNullRelay();
                            }
                            SoftKeyboardUtils.hideSoftKeyboard(editText);
                        }
                    });
                }
                return true;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSongTypeRelay.setVisibility(View.INVISIBLE);
                    mViewPager.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.VISIBLE);
                    ivClear.setVisibility(View.VISIBLE);
                    mMusicList.clear();
                    mRecyclerAdapter.notifyDataSetChanged();
                    setNullRelay();
                }
            }
        });
        ivClear.setOnClickListener(v -> {
            editText.setText("");
            editText.clearFocus();
            SoftKeyboardUtils.hideSoftKeyboard(editText);
            mSongTypeRelay.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.INVISIBLE);
            ivClear.setVisibility(View.GONE);
        });
    }

    public void initSearchView() {
        searchLayout = getView().findViewById(R.id.search_layout);
        mNullRelay = getView().findViewById(R.id.null_relay);
        nullText = getView().findViewById(R.id.null_text);
        mNullRelay.setVisibility(View.INVISIBLE);
        nullText.setText("未找到搜索结果");
        srlRefresh = getLayout().findViewById(R.id.srl_refresh);
        mRecyclerView = getView().findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        srlRefresh.setEnableLoadMore(false);
        srlRefresh.setEnableRefresh(false);
        searchLayout.setVisibility(View.INVISIBLE);
        ivClear.setVisibility(View.INVISIBLE);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.song_list_item, null);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MyHolder) holder).setView(position);
        }

        @Override
        public int getItemCount() {
            return mMusicList.size();
        }
    }


    class MyHolder extends RecyclerView.ViewHolder {
        View view;
        RoundedImageView imageView;
        TextView songNameText;
        TextView authorText;
        TextView clickText;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            imageView = view.findViewById(R.id.image);
            songNameText = view.findViewById(R.id.song_name_text);
            authorText = view.findViewById(R.id.author_text);
            clickText = view.findViewById(R.id.click_text);
        }

        public void setView(int position) {
            ImageLoader.loadUrl(imageView, mMusicList.get(position).getCover().get(0).getUrl(), R.color.ktv_basis_white);
            songNameText.setText(mMusicList.get(position).getMusicName());
            authorText.setText(KtvMusicManager.getInstance().convertToMusic(mMusicList.get(position)));
            clickText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetUtil.isNetworkAvailable()) {
                        KToast.show("请检查您的网络是否正常~");
                    }
                    Music musicBean = mMusicList.get(position);
                    mKtvSongNetworkInterfaceListener.onLoadMusicInfo(musicBean.getMusicId(), new DataCallback<MusicDetailBean>() {

                        @Override
                        public void onResult(MusicDetailBean musicDetailBean) {
                            if (musicDetailBean != null && musicDetailBean.getFileSize() > 0 && musicDetailBean.getFileUrl() != null) {
                                clickText.setBackgroundResource(R.drawable.click_song_shape);
                                clickText.setText("点歌");
                                musicDetailBean.setMusicName(musicBean.getMusicName());
                                musicDetailBean.setAuthorName(KtvMusicManager.getInstance().convertToMusic(mMusicList.get(position)));
                                mKtvSongNetworkInterfaceListener.onDownloadLrc(musicDetailBean);
                                mKtvSongNetworkInterfaceListener.onDownloadMusic(musicDetailBean, new SongInfoCallBack<Boolean>() {
                                    @Override
                                    public void onDownProgress(float progress) {
                                        clickText.setBackgroundResource(R.color.ktv_basis_transparent);
                                        clickText.setText("正在下载..." + (int) progress + "%");
                                    }

                                    @Override
                                    public void onResult(Boolean result) {
                                        clickText.setBackgroundResource(R.drawable.click_song_shape);
                                        clickText.setText("点歌");
                                        if (result) {
                                            KToast.show("点歌成功");
                                            srlRefresh.finishRefresh(true);
//                                            srlRefresh.finishLoadMore();
                                            //到这说明点歌成功了
                                            //这是刷新请求已点歌曲列表
                                            loadClickedSongList();
                                        } else {
                                            KToast.show("点歌失败");
                                            //到这说明点歌失败了
                                        }
                                    }
                                }, true);
                            } else {
                                KToast.show("获取歌词路径信息失败~~");
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 重新刷新请求已点歌曲列表
     */
    public void loadClickedSongList() {
        mKtvSongNetworkInterfaceListener.onSelectedMusicList(new KtvDataCallBack<List<ClickedMusic>>() {

            @Override
            public void onResult(List<ClickedMusic> list) {
                if (list == null) {
                    KToast.show("已点列表获取异常~");
                    return;
                }
                mKtvSongPlatformDialog.refreshClickList(list);
            }
        });
    }

    public void setNullRelay() {
        if (mMusicList.size() == 0) {
            mNullRelay.setVisibility(View.VISIBLE);
        } else {
            mNullRelay.setVisibility(View.INVISIBLE);
        }
    }

    public int dip2pix(int dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
