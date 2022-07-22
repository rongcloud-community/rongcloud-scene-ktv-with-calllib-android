package cn.rongcloud.ktvwithcalllib;

import android.app.Application;
import android.text.TextUtils;

import com.basis.net.oklib.wrapper.OkHelper;
import com.basis.net.oklib.wrapper.interfaces.IHeader;
import com.basis.utils.Logger;
import com.basis.utils.SystemUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.rongcloud.ktvwithcalllib.message.RCKTVRefreshMessage;
import cn.rongcloud.ktvwithcalllib.bean.User;
import cn.rongcloud.ktvwithcalllib.user.UserManager;
import cn.rongcloud.rtc.api.RCRTCAudioRouteManager;
import io.rong.imlib.RongIMClient;
import okhttp3.Headers;

/**
 * @author gyn
 * @date 2022/7/11
 */
public class AppApplication extends Application {

    public final static String APP_KEY = "pvxdm17jpw7ar";

    @Override
    public void onCreate() {
        super.onCreate();
        // 过滤非主进程
        if (!TextUtils.equals(SystemUtil.getProcessName(), getPackageName())) {
            return;
        }

        initCallLib();
        initOkhttp();
    }

    private void initCallLib() {
        // 初始化 im
        RongIMClient.init(this, APP_KEY);
        // 注册自定义消息
        RongIMClient.registerMessageType(Arrays.asList(RCKTVRefreshMessage.class));
        User user = UserManager.getInstance().getUser();
        if (user != null) {
            // 建立 im 连接
            RongIMClient.connect(user.getToken(), new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String t) {
                    Logger.d("im 连接成功");
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode e) {
                    Logger.e("im 连接失败：" + e.name());
                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {

                }
            });
        }
        // 初始化音频路由
        RCRTCAudioRouteManager.getInstance().init(getApplicationContext());
    }

    private void initOkhttp() {
        // 设置统一请求头，用来请求服务端接口
        OkHelper.get().setHeadCacher(new IHeader() {
            @Override
            public Map<String, String> onAddHeader() {
                Map header = new HashMap();
                header.put("BusinessToken", BuildConfig.BUSINESS_TOKEN);
                if (UserManager.getInstance().getUser() != null && !TextUtils.isEmpty(UserManager.getInstance().getUser().getAuthorization())) {
                    header.put("Authorization", UserManager.getInstance().getUser().getAuthorization());
                }
                return header;
            }

            @Override
            public void onCacheHeader(Headers headers) {

            }
        });
    }
}
