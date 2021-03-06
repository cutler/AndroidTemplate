package com.cutler.testapplication.test.manager.model.user;

import com.cutler.template.base.Template;
import com.cutler.template.base.common.manager.EntityManager;
import com.cutler.template.base.util.io.IOUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 本类用来管理当前登录的User对象。
 * 本类中提供了login、logout、setName等方法。
 * 这样做的好处是，对外界提供了修改User对象的统一接口，达到一改全改的目的，并且能够通过观察者机制，及时的通知各个界面进行数据更新。
 *
 * @author cutler
 */
public class LoginUserEntityManager extends EntityManager<User> {
    // 用来保存User对象的信息，文件真实路径为/data/data/com.cutler.testapplication/files/user.txt。
    private File localCacheFile = new File(Template.getApplication().getFilesDir(), "user.txt");

    /**
     * 当用户登录成功后调用此方法，将User对象设置到LoginUserEntityManager中。
     *
     * @param user
     */
    public void login(User user) {
        if (user != null) {
            setData(user);
            writeUserToLocalCache();
        }
    }

    /**
     * 当用户登出时，调用此方法清除本地缓存。
     */
    public void logout() {
        setData(null);
        // 删除本地的缓存文件
        boolean deleteFinish = localCacheFile.delete();
        System.out.println("删除本地缓存文件是否成功：" + deleteFinish);
    }

    /**
     * 修改用户的昵称。
     *
     * @param newName
     */
    public void setName(String newName) {
        if (getData() != null) {
            getData().setName(newName);
            // 通知各个界面进行数据更新。
            notifyObservers();
            // 更新本地缓存。
            writeUserToLocalCache();
        }
    }

    /*
     * 将User对象写入到本地进行缓存。
     */
    private void writeUserToLocalCache() {
        if (getData() != null) {
            // 写入到localCacheFile指向的文件中。
            try {
                IOUtil.stringToOutputStream(getData().toJSON(), new FileOutputStream(localCacheFile));
                System.out.println("写入到本地缓存成功！");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // 单例对象
    private static LoginUserEntityManager inst;

    private LoginUserEntityManager() {
        // 当对象被创建时，尝试从本地缓存中读取User数据。
        User user = null;
        if (localCacheFile.isFile()) {
            try {
                user = User.parseJSON(new JSONObject(IOUtil.inputStream2String(new FileInputStream(localCacheFile), "UTF-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (user != null) { // 加载成功。
            setData(user);
            System.out.println("读取本地缓存成功：" + getData().getName()); //TODO
        } else {
            System.out.println("读取本地缓存失败"); //TODO
        }
    }

    /**
     * 返回单例对象
     *
     * @return
     */
    public static LoginUserEntityManager getInstance() {
        if (inst == null) {
            synchronized (LoginUserEntityManager.class) {
                if (inst == null) {
                    inst = new LoginUserEntityManager();
                }
            }
        }
        return inst;
    }
}
