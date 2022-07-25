package cn.rongcloud.ktvmusickit.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.NetUtil;

import cn.rongcloud.ktvmusickit.R;

import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import cn.rongcloud.ktvmusickit.callback.DataCallback;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.callback.SongInfoCallBack;
import cn.rongcloud.ktvmusickit.listener.KtvSongNetworkInterfaceListener;
import cn.rongcloud.ktvmusickit.model.Music;
import cn.rongcloud.ktvmusickit.model.MusicDetailBean;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

/**
 * 普通歌曲列表
 */
public class SongListFragment extends BaseFragment {
    private ViewGroup mNullRelay;
    RecyclerView mRecyclerView;
    KtvSongNetworkInterfaceListener ktvSongNetworkInterfaceListener;
    private List<Music> mMusicList = new ArrayList<>();
    private RecyclerAdapter mRecyclerAdapter;
    private String mCategoryId = "";
    private KtvSongPlatformDialog mKtvSongPlatformDialog;
    private SmartRefreshLayout srlRefresh;
    private ClassicsHeader classicsHeader;
    private ClassicsFooter classicsFooter;
    private TextView tvEmpty;
    /**
     * 页码
     */
    private int mListPage = 1;

    public SongListFragment(KtvSongNetworkInterfaceListener rcMusicKitListener, String category, KtvSongPlatformDialog ktvSongPlatformDialog) {
        ktvSongNetworkInterfaceListener = rcMusicKitListener;
        mKtvSongPlatformDialog = ktvSongPlatformDialog;
        mCategoryId = category;
    }

    @Override
    public int setLayoutId() {
        return R.layout.song_list_layout;
    }


    @Override
    public void init() {
        mNullRelay = getView().findViewById(R.id.null_relay);
        mNullRelay.setVisibility(View.INVISIBLE);
        classicsHeader = getView().findViewById(R.id.header);
        classicsHeader.setAccentColor(Color.WHITE);
        classicsFooter = getView().findViewById(R.id.footer);
        classicsFooter.setAccentColor(Color.WHITE);
        srlRefresh = getLayout().findViewById(R.id.srl_refresh);
        tvEmpty = getLayout().findViewById(R.id.null_text);
        tvEmpty.setText("暂无歌曲");
        getView().findViewById(R.id.line).setVisibility(View.GONE);
        getView().findViewById(R.id.wheat_control).setVisibility(View.GONE);
        mRecyclerView = getView().findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        srlRefresh.setEnableLoadMore(true);
        srlRefresh.setEnableRefresh(true);
        srlRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (NetUtil.isNetworkAvailable()) {
                    loadRefreshMusicList();
                } else {
                    KToast.show("请检查您的网络是否正常~");
                    srlRefresh.finishRefresh();
                }
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (NetUtil.isNetworkAvailable()) {
                    mListPage++;
                    loadMoreMusicList();
                } else {
                    KToast.show("请检查您的网络是否正常~");
                    srlRefresh.finishLoadMore();
                }
            }
        });
        if (NetUtil.isNetworkAvailable()) {
            loadRefreshMusicList();
        } else {
            mNullRelay.setVisibility(View.VISIBLE);
        }
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
                    ktvSongNetworkInterfaceListener.onLoadMusicInfo(musicBean.getMusicId(), new DataCallback<MusicDetailBean>() {

                        @Override
                        public void onResult(MusicDetailBean musicDetailBean) {
                            if (musicDetailBean != null && musicDetailBean.getFileSize() > 0 && musicDetailBean.getFileUrl() != null) {
                                clickText.setBackgroundResource(R.drawable.click_song_shape);
                                clickText.setText("点歌");
                                musicDetailBean.setMusicName(musicBean.getMusicName());
                                musicDetailBean.setAuthorName(KtvMusicManager.getInstance().convertToMusic(mMusicList.get(position)));
                                ktvSongNetworkInterfaceListener.onDownloadLrc(musicDetailBean);
                                ktvSongNetworkInterfaceListener.onDownloadMusic(musicDetailBean, new SongInfoCallBack<Boolean>() {
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
        ktvSongNetworkInterfaceListener.onSelectedMusicList(new KtvDataCallBack<List<ClickedMusic>>() {

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

    /**
     * 获取最新第一页列表
     */
    public void loadRefreshMusicList() {
        ktvSongNetworkInterfaceListener.onLoadMusicListByCategory(1, mCategoryId, new DataCallback<List<Music>>() {
            @Override
            public void onResult(List<Music> music) {
                if (music == null || music.size() == 0) {
                    KToast.show("刷新音乐列表错误~");
                } else {
                    mMusicList.clear();
                    mMusicList.addAll(music);
                    if (mRecyclerAdapter != null) {
                        mRecyclerAdapter.notifyDataSetChanged();
                    }
                    mListPage = 1;
                }
                setNullRelay();
                srlRefresh.finishRefresh();
                srlRefresh.finishLoadMore();
            }
        });
    }

    /**
     * 获取更多音乐列表信息
     */
    public void loadMoreMusicList() {
        ktvSongNetworkInterfaceListener.onLoadMusicListByCategory(mListPage, mCategoryId, new DataCallback<List<Music>>() {
            @Override
            public void onResult(List<Music> music) {
                if (music == null || music.size() == 0) {
                    KToast.show("已经到底了~");
                    if (mListPage > 1) {
                        mListPage--;
                    }
                } else {
                    mMusicList.addAll(music);
                    if (mRecyclerAdapter != null) {
                        mRecyclerAdapter.notifyDataSetChanged();
                        setNullRelay();
                    }
                }
                srlRefresh.finishRefresh();
                srlRefresh.finishLoadMore();
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
}
