package com.cutler.template.test.manager.model.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.json.JSONObject;

import com.cutler.template.MainApplication;
import com.cutler.template.common.manager.EntityManager;
import com.cutler.template.common.manager.ModelCallback;
import com.cutler.template.util.IOUtil;

/**
 * 本类用来管理当前登录的User对象。
 * 本类中提供了login、logout、setName等方法，这样做的好处是，对外界提供了修改User对象的统一接口，达到一改全改的目的，并且能够通过观察者机制，及时的通知各个界面进行数据更新。
 * @author cutler
 */
public class LoginUserEntityManager extends EntityManager<User> {
	// 用来保存User对象的信息，文件真实路径为/data/data/com.cutler.template/files/user.txt。
	private File localCacheFile = new File(MainApplication.getInstance().getFilesDir(), "user.txt");
	
	/**
	 * 通常用户登录的代码会写在登录界面相关的地方，因此对于LoginUserEntityManager来说，此方法不需要写具体实现。
	 */
	@Override
	protected void fetchData(ModelCallback callback) { }
	
	/**
	 * 当用户登录成功后调用此方法，将User对象设置到LoginUserEntityManager中。
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
		System.out.println("删除本地缓存文件是否成功："+deleteFinish);
	}
	
	/**
	 * 修改用户的昵称。
	 * @param newName
	 */
	public void setName(String newName) {
		if(getData() != null){
			getData().setName(newName);
			// 通知各个界面进行数据更新。
			notifyDataLoaded(getData());
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
				System.out.println("写入到本地缓存成功！"); //TODO
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从本地读取缓存的用户信息。
	 */
	@Override
	protected EntityManager.DataWithExpireTime<User> loadFromLocalCache() {
		// 尝试从本地读取缓存的User对象
		if (localCacheFile.isFile()) {
			try {
				User user = User.parseJSON(new JSONObject(IOUtil.inputStream2String(new FileInputStream(localCacheFile), "UTF-8")));
				if (user != null) {
					return new DataWithExpireTime<User>(user, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 若没有读取到，则可以返回null。
		return null;
	}

	// 单例对象
	private static LoginUserEntityManager inst;
	
	private LoginUserEntityManager() {
		// 当对象被创建时，尝试从本地缓存中读取User数据。
		DataWithExpireTime<User> val = loadFromLocalCache();
		if (val != null) { // 加载成功。
			setData(val.getData());
			setExpireTime(val.getExpireTime());
			System.out.println("读取本地缓存成功：" + getData().getName()); //TODO
		} else {
			System.out.println("读取本地缓存失败"); //TODO
		}
	}
	
	/**
	 * 返回单例对象
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
