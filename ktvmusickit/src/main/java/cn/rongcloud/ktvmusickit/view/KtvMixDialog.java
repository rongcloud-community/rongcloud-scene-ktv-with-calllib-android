package cn.rongcloud.ktvmusickit.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseBottomSheetDialog;

import cn.rongcloud.ktvmusickit.R;
import cn.rongcloud.ktvmusickit.model.MixConfig;
import cn.rongcloud.ktvmusickit.model.RCSKTVScreenRole;
import cn.rongcloud.ktvmusickit.listener.KtvKitMixDialogListener;
import cn.rongcloud.ktvmusickit.music.KtvMusicManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 混响音效弹框
 */
public class KtvMixDialog extends BaseBottomSheetDialog {
    private RCSKTVScreenRole mRcsKtvScreenRole;
    private RecyclerView mRecyclerView;
    private TextView mVocalVolumeText;

    private SeekBar mStyleVolumeSeekbar;
    private SeekBar mVocalVolumeSeekbar;

    private TextView mReduceText;
    private TextView mLiftScaleText;
    private TextView mAddText;


    private Switch mEarSwitch;
    private Switch mIntonationSwitch;

    private List<String> reverberationList = new ArrayList<>();
    private KtvKitMixDialogListener mKtvKitMixDialogListener;
    RecyclerView.Adapter adapter;
    MixConfig mMixConfig;

    /**
     * 混响弹框构造方法
     *
     * @param ktvKitMixDialogListener 回调监听，不需要回调的话直接传null即可
     */
    public KtvMixDialog(KtvKitMixDialogListener ktvKitMixDialogListener, List<String> reverberationList, RCSKTVScreenRole rcsktvScreenRole) {
        super(R.layout.ktv_mix_dialog);
        mKtvKitMixDialogListener = ktvKitMixDialogListener;
        mRcsKtvScreenRole = rcsktvScreenRole;
        if (this.reverberationList != null) {
            this.reverberationList.clear();
        }
        if (reverberationList == null) {
            reverberationList = new ArrayList<>();
        }
        this.reverberationList = reverberationList;
    }

    @Override
    public void initView() {
        mRecyclerView = getView().findViewById(R.id.recyclerView);
        mStyleVolumeSeekbar = getView().findViewById(R.id.style_volume_seekbar);
        mVocalVolumeText = getView().findViewById(R.id.vocal_volume_text);
        mVocalVolumeSeekbar = getView().findViewById(R.id.vocal_volume_seekbar);
        mReduceText = getView().findViewById(R.id.reduce_text);
        mLiftScaleText = getView().findViewById(R.id.lift_scale_text);
        mAddText = getView().findViewById(R.id.add_text);
        mEarSwitch = getView().findViewById(R.id.ear_switch);
        mIntonationSwitch = getView().findViewById(R.id.intonation_switch);
        //设置权限决定显示哪个
        if (mRcsKtvScreenRole == RCSKTVScreenRole.KTV_SCREEN_ROLE_LEAD_SINGER) {
            mVocalVolumeText.setVisibility(View.VISIBLE);
            mVocalVolumeSeekbar.setVisibility(View.VISIBLE);
        } else {
            mVocalVolumeText.setVisibility(View.GONE);
            mVocalVolumeSeekbar.setVisibility(View.GONE);
        }

        //设置音效列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view =
                        LayoutInflater.from(getActivity()).inflate(R.layout.ktv_mix_reverberation_item,
                                null);
                MyHolder myHolder = new MyHolder(view);
                return myHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((MyHolder) holder).setView(position);
            }

            @Override
            public int getItemCount() {
                return reverberationList.size();
            }
        };

        mRecyclerView.setAdapter(adapter);

        //注册观察者
        KtvMusicManager.getInstance().getMixConfigLiveData().observe(this, new Observer<MixConfig>() {
            @Override
            public void onChanged(MixConfig mixConfig) {
                if (mixConfig != null) {
                    mMixConfig = mixConfig;
                    setValue();
                }
            }
        });

    }


    public void setValue() {
        //音效列表
        adapter.notifyDataSetChanged();
        //设置耳返
        mEarSwitch.setChecked(mMixConfig.ear);
        //设置音乐声
        mStyleVolumeSeekbar.setProgress(mMixConfig.styleVolume);
        //设置人声
        mVocalVolumeSeekbar.setProgress(mMixConfig.vocalVolume);
        //设置升降调
        mLiftScaleText.setText(mMixConfig.liftScale + "");
        //设置音准器
        mIntonationSwitch.setChecked(mMixConfig.intonation);
    }


    @Override
    protected void initListener() {
        super.initListener();

        mStyleVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMixConfig.styleVolume = progress;
                    if (mKtvKitMixDialogListener != null) {
                        mKtvKitMixDialogListener.accompVolumeChanged(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mVocalVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMixConfig.vocalVolume = progress;
                    if (mKtvKitMixDialogListener != null) {
                        mKtvKitMixDialogListener.vocalVolumeChanged(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mReduceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMixConfig.liftScale = mMixConfig.liftScale - 10;
                mLiftScaleText.setText(mMixConfig.liftScale + "");
                if (mKtvKitMixDialogListener != null) {
                    mKtvKitMixDialogListener.toneStepperChanged(mMixConfig.liftScale);
                }
            }
        });
        mAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMixConfig.liftScale = mMixConfig.liftScale + 10;
                mLiftScaleText.setText(mMixConfig.liftScale + "");
                if (mKtvKitMixDialogListener != null) {
                    mKtvKitMixDialogListener.toneStepperChanged(mMixConfig.liftScale);
                }
            }
        });
        mEarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMixConfig.ear = isChecked;
                if (mKtvKitMixDialogListener != null) {
                    mKtvKitMixDialogListener.earReturnChanged(isChecked);
                }
            }
        });
        mIntonationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMixConfig.intonation = isChecked;
                if (mKtvKitMixDialogListener != null) {
                    mKtvKitMixDialogListener.pincherChanged(isChecked);
                }
            }
        });
    }

    class MyHolder extends RecyclerView.ViewHolder {
        View view;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setView(int position) {
            TextView textView = view.findViewById(R.id.ktv_reverberation_text);
            textView.setText(reverberationList.get(position));
            if (mMixConfig.mixTypeIndex == position) {
                textView.setBackgroundResource(R.drawable.ktv_ic_mix_circle);
            } else {
                textView.setBackgroundResource(R.drawable.ktv_ic_mix_circle_gray);
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMixConfig.mixTypeIndex = position;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    if (mKtvKitMixDialogListener != null) {
                        mKtvKitMixDialogListener.reverbChanged(position);
                    }
                }
            });
        }
    }
}
