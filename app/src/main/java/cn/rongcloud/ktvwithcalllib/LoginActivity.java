package cn.rongcloud.ktvwithcalllib;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.SharedPreferUtil;
import com.basis.widget.loading.LoadTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.rongcloud.ktvwithcalllib.bean.User;
import cn.rongcloud.ktvwithcalllib.user.UserManager;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnLogin;
    private LoadTag loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserManager.getInstance().getUser() != null) {
            launchMainActivity();
            return;
        }
        setContentView(R.layout.activity_login);
        initView();
    }

    private void launchMainActivity() {
        MainActivity.launch(this);
        finish();
    }

    private void initView() {
        etPhone = findViewById(R.id.et_uid);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(view -> login());
        loadingView = new LoadTag(this, "登录中");
    }

    private void login() {
        String phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            KToast.show("请输入手机号");
            return;
        }
        loadingView.show();
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("verifyCode", "123456");
        params.put("deviceId", getDeviceId());
        params.put("region", "86");
        params.put("platform", "mobile");
        // 添加统计信息
        params.put("platformType", "android");//平台统计
        OkApi.post(Constant.LOGIN_URL, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    User user = result.get(User.class);
                    user.setPhone(phone);
                    UserManager.getInstance().saveUser(user);
                    connect(user.getToken());
                } else {
                    KToast.show(result.getMessage());
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show(msg);
            }
        });
    }

    private void connect(String token) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String t) {
                loadingView.dismiss();
                Logger.d("im 连接成功");
                KToast.show("登录成功");
                launchMainActivity();
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode e) {
                loadingView.dismiss();
                Logger.e("im 连接失败：" + e.name());
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {

            }
        });
    }

    private final static String KEY_DEVICE_ID = "cn.rc.demo.device_id";

    private static String getDeviceId() {
        String deviceId = SharedPreferUtil.get(KEY_DEVICE_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        String deviceShort =
                "35" + Build.BOARD.length() % 10
                        + Build.BRAND.length() % 10
                        + Build.CPU_ABI.length() % 10
                        + Build.DEVICE.length() % 10
                        + Build.MANUFACTURER.length() % 10
                        + Build.MODEL.length() % 10
                        + Build.PRODUCT.length() % 10;


        String serial = new UUID((long) deviceShort.hashCode(), (long) Build.SERIAL.hashCode()).toString();
        deviceId = new UUID((long) deviceShort.hashCode(), (long) serial.hashCode()).toString();
        SharedPreferUtil.set(KEY_DEVICE_ID, deviceId);
        return deviceId;
    }
}