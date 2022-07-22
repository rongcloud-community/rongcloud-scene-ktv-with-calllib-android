//package cn.rongcloud.ktvmusickit.view;
//
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.basis.ui.BaseActivity;
//import com.example.ktv.R;
//import com.google.android.material.tabs.TabLayout;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.rongcloud.config.router.RouterPath;
//import io.rong.imkit.utils.StatusBarUtil;
//
///**
// * 主房间测试类
// */
//@Route(path = RouterPath.ROUTER_KTV_ROOM)
//public class KtvTestMainActivity extends BaseActivity implements View.OnClickListener {
//
//    @Override
//    public int setLayoutId() {
//        return R.layout.ktv_main;
//    }
//
//    @Override
//    public void init() {
//        initView();
//    }
//
//    private ViewPager2 vp_switch;
//    private TabLayout tab_switch;
//    private int currentIndex = 0;
//
//    protected void initView() {
//        StatusBarUtil.setStatusBarFontIconDark(this, StatusBarUtil.TYPE_M, true);
//        vp_switch = findViewById(R.id.vp_switch);
//
//        tab_switch = findViewById(R.id.tab_switch);
//
//        vp_switch.setCurrentItem(currentIndex);
//        List<Fragment> fragments =new ArrayList<>();
//        fragments.add(new KtvTestMainFragment());
//        vp_switch.setAdapter(new VPAdapter(KtvTestMainActivity.this, fragments));
//
////        new TabLayoutMediator(tab_switch, vp_switch, new TabLayoutMediator.TabConfigurationStrategy() {
////            @Override
////            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
////                tab.setText(onSetSwitchTitle()[position]);
////            }
////        }).attach();
//
//        getView(R.id.fl_back).setOnClickListener(this);
//
//        getWrapBar().setHide(true).work();
//
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//        super.onPointerCaptureChanged(hasCapture);
//    }
//
//    public class VPAdapter extends FragmentStateAdapter {
//        private List<Fragment> fragments;
//
//        public VPAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
//            super(fragmentActivity);
//            this.fragments = fragments;
//        }
//
//        @NonNull
//        @Override
//        public Fragment createFragment(int position) {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return null == fragments ? 0 : fragments.size();
//        }
//    }
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        if (R.id.fl_back == id) {
//            onBackCode();
//        }
//    }
//}
