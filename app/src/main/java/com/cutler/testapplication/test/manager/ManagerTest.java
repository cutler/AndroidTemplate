package com.cutler.testapplication.test.manager;

import android.test.AndroidTestCase;

import com.cutler.testapplication.test.manager.model.user.LoginUserEntityManager;
import com.cutler.testapplication.test.manager.model.user.User;

public class ManagerTest {

    /**
     * 用户登录
     */
    public static void testLogin() {
        // 假设下面这个user对象是登录接口返回来的。
        User user = new User();
        user.setId(10001);
        user.setName("Tom");
        user.setSignature("Hello World!!!");
        // 将User对象设置到LoginUserEntityManager中。
        LoginUserEntityManager.getInstance().login(user);
        // 设置完毕之后，我们就可以在程序中的任意地方获取并修改User对象的信息了。
        System.out.println("修改之前的昵称为：" + LoginUserEntityManager.getInstance().getData().getName());
        LoginUserEntityManager.getInstance().setName("Jerry");
        System.out.println("修改之后的昵称为：" + LoginUserEntityManager.getInstance().getData().getName());
    }

    /**
     * 用户注销登录。 请先执行testLogin()，以便将User对象缓存到本地。
     */
    public static void testLogout() {
        LoginUserEntityManager.getInstance().logout();
    }
}
