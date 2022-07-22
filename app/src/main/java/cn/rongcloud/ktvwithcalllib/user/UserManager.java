package cn.rongcloud.ktvwithcalllib.user;

import android.text.TextUtils;
import com.basis.utils.GsonUtil;
import com.basis.utils.SharedPreferUtil;

import cn.rongcloud.ktvwithcalllib.bean.User;

/**
 * @author gyn
 * @date 2022/7/11
 */
public class UserManager {
    private static final String USER = "USER";
    private User user;

    public static UserManager getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final UserManager INSTANCE = new UserManager();
    }


    public void saveUser(User user) {
        SharedPreferUtil.set(USER, GsonUtil.obj2Json(user));
    }

    public User getUser() {
        if (user == null) {
            String json = SharedPreferUtil.get(USER);
            if (!TextUtils.isEmpty(json)) {
                user = GsonUtil.json2Obj(json, User.class);
            }
        }
        return user;
    }
}
