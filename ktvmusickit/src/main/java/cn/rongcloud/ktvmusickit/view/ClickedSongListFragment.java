package cn.rongcloud.ktvmusickit.view;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.NetUtil;

import cn.rongcloud.ktvmusickit.R;

import com.basis.utils.ResUtil;
import com.basis.widget.dialog.VRCenterDialog;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import cn.rongcloud.ktvmusickit.callback.DataCallback;
import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvmusickit.listener.KtvSongListDialogListener;
import cn.rongcloud.ktvmusickit.listener.KtvSongNetworkInterfaceListener;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 已点歌列表
 */
public class ClickedSongListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private KtvSongNetworkInterfaceListener ktvSongNetworkInterfaceListener;
    private KtvSongListDialogListener mKtvSongListDialogListener;
    private List<ClickedMusic> mMusicList = new ArrayList<>();
    private RecyclerAdapter mRecyclerAdapter;
    public static String path = "";
    private RelativeLayout nullRelay;
    private KtvSongPlatformDialog mKtvSongPlatformDialog;
    private SmartRefreshLayout srlRefresh;
    private ClassicsHeader classicsHeader;
    private View line;
    private TextView wheatControlText;
    /**
     * 取消控麦传递的bean模型
     */
    private ClickedMusic controlMicClickedMusic;

    /**
     * 正在播放的歌的id
     */
    private String mPlayingId = "";

    public ClickedSongListFragment(KtvSongPlatformDialog ktvSongPlatformDialog, KtvSongNetworkInterfaceListener rcMusicKitListener, KtvSongListDialogListener ktvSongListDialogListener, String playingId) {
        mKtvSongPlatformDialog = ktvSongPlatformDialog;
        ktvSongNetworkInterfaceListener = rcMusicKitListener;
        mKtvSongListDialogListener = ktvSongListDialogListener;
        mPlayingId = playingId;
    }

    @Override
    public int setLayoutId() {
        return R.layout.song_list_layout;
    }


    @Override
    public void init() {
        nullRelay = getView().findViewById(R.id.null_relay);
        nullRelay.setVisibility(View.INVISIBLE);
        classicsHeader = getView().findViewById(R.id.header);
        classicsHeader.setAccentColor(Color.WHITE);
        srlRefresh = getLayout().findViewById(R.id.srl_refresh);
        srlRefresh.setEnableRefresh(true);
        srlRefresh.setEnableLoadMore(false);
        mRecyclerView = getView().findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new RecyclerAdapter();
        line = getView().findViewById(R.id.line);
        wheatControlText = getView().findViewById(R.id.wheat_control);
        //是房主且使用显示控麦功能
        if (KtvMusicManager.getInstance().isHomeOwner() && KtvMusicManager.getInstance().isShowControlMic()) {
            line.setVisibility(View.VISIBLE);
            wheatControlText.setVisibility(View.VISIBLE);
            wheatControlText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mKtvSongListDialogListener != null) {
                        //通知房间控麦不用传参，取消控麦则传递已点bean模型
                        mKtvSongListDialogListener.controlMic(wheatControlText.getText().toString().equals("控麦") ? null : controlMicClickedMusic);
                        mKtvSongPlatformDialog.dismiss();
                    }
                }
            });
        } else {
            line.setVisibility(View.GONE);
            wheatControlText.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(mRecyclerAdapter);
        srlRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (NetUtil.isNetworkAvailable()) {
                    loadRefreshClickedMusicList();
                } else {
                    KToast.show("请检查您的网络是否正常~");
                    srlRefresh.finishRefresh();
                }
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }
        });
        if (NetUtil.isNetworkAvailable()) {
            loadClickedSongList(null);
        } else {
            nullRelay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新已点列表
     */
    public void loadRefreshClickedMusicList() {
        ktvSongNetworkInterfaceListener.onSelectedMusicList(new KtvDataCallBack<List<ClickedMusic>>() {
            @Override
            public void onResult(List<ClickedMusic> list) {
                if (list == null) {
                    KToast.show("获取已点列表失败~");
                    return;
                }
                setListInfo(list);
                srlRefresh.finishRefresh();
            }
        });
    }

    public class RecyclerAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.song_list_clicked_item, null);
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
        TextView numberText;
        MusicPlayingView playingView;
        ImageView imageView;
        TextView songNameText;
        TextView authorText;
        TextView chorusSoloText;
        ImageView toppingImage;
        ImageView deleteImage;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            numberText = view.findViewById(R.id.number_text);
            playingView = view.findViewById(R.id.mpv_playing);
            imageView = view.findViewById(R.id.image);
            songNameText = view.findViewById(R.id.song_name_text);
            authorText = view.findViewById(R.id.author_text);
            chorusSoloText = view.findViewById(R.id.chorus_solo_text);
            toppingImage = view.findViewById(R.id.topping_image);
            deleteImage = view.findViewById(R.id.delete_image);
        }

        public void setView(int position) {
            ClickedMusic clickedSongBean = mMusicList.get(position);
            numberText.setText((position + 1) + "");
            if (mPlayingId.equals(clickedSongBean.getMusicId()) && position == 0) {
                numberText.setVisibility(View.INVISIBLE);
                playingView.setVisibility(View.VISIBLE);
                playingView.start();
            } else {
                numberText.setVisibility(View.VISIBLE);
                playingView.setVisibility(View.INVISIBLE);
                playingView.stop();
            }
            ImageLoader.loadUrl(imageView, clickedSongBean.getUserPortrait(), R.color.ktv_basis_white);
            songNameText.setText(clickedSongBean.getMusicName());
            authorText.setText(clickedSongBean.getArtistName());
            if (1 == clickedSongBean.getSolo()) {
                chorusSoloText.setBackgroundResource(R.drawable.ktv_solo_text_shape);
                chorusSoloText.setText("独唱");
            } else {
                chorusSoloText.setBackgroundResource(R.drawable.ktv_chorus_text_shape);
                chorusSoloText.setText("合唱");
            }
            if (clickedSongBean.getSongId().equals("seat")) {
                chorusSoloText.setVisibility(View.INVISIBLE);
            } else {
                chorusSoloText.setVisibility(View.VISIBLE);
            }
            if (position <= 1) {
                toppingImage.setVisibility(View.INVISIBLE);
            } else {
                setImageVisible(toppingImage, clickedSongBean.getUserId());
            }
            setImageVisible(deleteImage, clickedSongBean.getUserId());
            toppingImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (KtvMusicManager.getInstance().isHomeOwner()) {
                        //是房主则直接置顶
                        ktvSongNetworkInterfaceListener.showLoading("置顶请求中。。。");
                        ktvSongNetworkInterfaceListener.onTopMusic(mMusicList.get(position), new DataCallback<Boolean>() {
                            @Override
                            public void onResult(Boolean isTop) {
                                ktvSongNetworkInterfaceListener.hideLoading();
                                //如果置顶成功，则请求并刷新列表
                                if (isTop) {
                                    mKtvSongListDialogListener.changeSelectedList();
                                    loadClickedSongList(null);
                                }
                            }
                        });
                    } else {
                        //不是房主则通知房主，让房主置顶这首歌并回调结果给当前用户
                        mKtvSongListDialogListener.applyForTopTheMusic(clickedSongBean);
                        KToast.show("置顶申请已提交");
                    }
                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VRCenterDialog centerDialog = new VRCenterDialog(getActivity(), null);
                    TextView view = new TextView(getContext());
                    view.setTextColor(ResUtil.getColor(com.basis.R.color.basis_color_secondary));
                    view.setText("是否删除歌曲");
                    view.setGravity(Gravity.CENTER_HORIZONTAL);
                    View.OnClickListener cancel = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != centerDialog) centerDialog.dismiss();
                        }
                    };
                    View.OnClickListener ok = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != centerDialog) {
                                centerDialog.dismiss();
                            }
                            ktvSongNetworkInterfaceListener.showLoading("删除请求中。。。");
                            ktvSongNetworkInterfaceListener.onDeleteMusic(mMusicList.get(position), new KtvDataCallBack<Boolean>() {
                                @Override
                                public void onResult(Boolean isSuccess) {
                                    ktvSongNetworkInterfaceListener.hideLoading();
                                    //如果删除成功，则请求并刷新列表
                                    if (isSuccess) {
                                        mKtvSongListDialogListener.changeSelectedList();
                                        loadClickedSongList(null);
                                    }
                                }
                            });
                        }
                    };
                    String title = "提示";
                    centerDialog.replaceContent(title, -1, "取消", -1, null, "同意", R.color.ktv_song_platform_title_selected_text_color, ok, view);
                    centerDialog.show();
                }
            });
        }

        /**
         * 设置置顶和删除按钮显隐状态
         *
         * @param imageView
         * @param itemUserId 条目点歌的userid
         */
        public void setImageVisible(ImageView imageView, String itemUserId) {
            if (KtvMusicManager.getInstance().isHomeOwner()) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                if (itemUserId.equals(KtvMusicManager.getInstance().getUserId())) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    /**
     * 重新刷新请求已点歌曲列表
     */
    public void loadClickedSongList(List<ClickedMusic> list) {
        if (list != null) {
            //这里是点歌后请求点歌列表传递过来的点歌列表集合,
            //词集合第一作用是刷新已点列表，第二作用是传递给房间最新集合列表由房间判断是否立刻播放
            setListInfo(list);
            mKtvSongListDialogListener.selectedMusic(mMusicList);
        } else {
            loadRefreshClickedMusicList();
        }
    }

    /**
     * 设置刷新当前列表信息数据
     *
     * @param list 列表
     */
    public void setListInfo(List<ClickedMusic> list) {
        mMusicList.clear();
        mMusicList.addAll(list);
        if (mMusicList.size() > 0) {
            mPlayingId = mMusicList.get(0).getMusicId();
        }
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.notifyDataSetChanged();
            setNullRelay();
        }
        if (mKtvSongPlatformDialog != null && mMusicList != null) {
            mKtvSongPlatformDialog.setClickedSongText("已点（" + mMusicList.size() + "）");
            if (KtvMusicManager.getInstance().isHomeOwner() && KtvMusicManager.getInstance().isShowControlMic()) {
                wheatControlText.setText("控麦");
                //检查列表中是否有控麦item，有则将控麦按钮置为取消控麦
                for (int i = 0; i < mMusicList.size(); i++) {
                    if (mMusicList.get(i).getMusicName().equals("控麦")) {
                        controlMicClickedMusic = mMusicList.get(i);
                        wheatControlText.setText("取消控麦");
                        return;
                    }
                }
            }
        }
    }

    public void setNullRelay() {
        if (mMusicList.size() == 0) {
            nullRelay.setVisibility(View.VISIBLE);
        } else {
            nullRelay.setVisibility(View.INVISIBLE);
        }
    }
}
