package cn.rongcloud.ktvwithcalllib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.Logger;

import cn.rongcloud.ktvmusickit.songutil.LrcAnalysis;
import cn.rongcloud.ktvwithcalllib.bean.User;
import cn.rongcloud.ktvwithcalllib.user.UserManager;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallSession;

/**
 * @author gyn
 * @date 2022/7/11
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] PERMISSIONS = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
    };

    private EditText etCall;
    private Button btnCall;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        addListener();
    }


    private void initView() {
        TextView tvUser = findViewById(R.id.tv_user);
        User user = UserManager.getInstance().getUser();
        tvUser.setText(String.format("用户名：%s\n手机号：%s", user.getName(), user.getPhone()));

        etCall = findViewById(R.id.et_call);
        btnCall = findViewById(R.id.btn_call);
        btnCall.setOnClickListener(this::onClick);
    }


    private void addListener() {
        // 注册呼叫监听，开发者根据自己需求注册，这里是演示放在了首页
        RongCallClient.setReceivedCallListener(new IRongReceivedCallListener() {
            @Override
            public void onReceivedCall(RongCallSession callSession) {
                // 有人呼入跳到接听页面做接听或拒接处理
                KTVRoomActivity.response(MainActivity.this, callSession);
            }

            @Override
            public void onCheckPermission(RongCallSession callSession) {
                if (!checkPermissions()) {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1000);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_call) {
            if (checkPermissions()) {
                String phone = etCall.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(MainActivity.this, "请输入要呼叫的用户手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                getUserIdByPhone(phone);
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1000);
            }
        }
    }


    private boolean checkPermissions() {
        boolean isDenied = false;
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
                isDenied = true;
                break;
            }
        }

        return !isDenied;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissions()) {
            RongCallClient.getInstance().onPermissionGranted();
        } else {
            RongCallClient.getInstance().onPermissionDenied();
        }
    }

    /**
     * 通过电话获取对方用户uid，拨打电话是需要知道对方uid的
     *
     * @param phone
     */
    private void getUserIdByPhone(final String phone) {
        String url = Constant.USER_INFO + phone;
        OkApi.get(url, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    if (result.getBody() != null) {
                        String uid = result.getBody().getAsJsonObject().get("uid").getAsString();
                        String name = result.getBody().getAsJsonObject().get("name").getAsString();
                        KTVRoomActivity.call(MainActivity.this, uid, name);
                    }
                } else {
                    KToast.show("号码未注册");
                }
            }
        });
    }
}
