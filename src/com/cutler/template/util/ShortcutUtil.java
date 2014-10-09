package com.cutler.template.util;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 快捷方式
 * @author cutler
 *
 */
public class ShortcutUtil {
	
	/**
	 * 快捷方式已经存在，并且是当前应用程序创建出来的。
	 */
	public static final int STATE_EXIST_AND_IS_SELF = 1;
	
	/**
	 * 快捷方式已经存在，但是是其他应用程序创建出来的。
	 */
	public static final int STATE_EXIST_AND_IS_OTHER = 2;
	
	/**
	 * 快捷方式不存在。
	 */
	public static final int STATE_NOT_EXIST = 3;
	
	/**
	 * 在桌面上创建一个快捷方式。
	 * @param context
	 * @param clazz 点击快捷方式后，要启动的Activity
	 * @param icon 快捷方式的图标
	 * @param textResId 快捷方式的标题的资源Id
	 */
	public static void createDesktopShortCut(Context context, 
			Class<? extends Activity> clazz, Bitmap icon, int textResId) {
		Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		Intent myIntent = new Intent(context, clazz);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myIntent.setAction("android.intent.action.MAIN");  
		myIntent.addCategory("android.intent.category.LAUNCHER"); 
		// 不允许重复创建  
		addIntent.putExtra("duplicate", false);
		// 快捷方式的标题
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(textResId));
		// 快捷方式的图标
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		// 快捷方式的动作
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
		context.sendBroadcast(addIntent);
	}
	
	/**
	 * 创建快捷方式的图标(由一个背景图片加几个图标组合成的图片)
	 * @param context
	 * @param icons 图标
	 * @return
	 */
	public static Bitmap createDesktopShortCutIcon(Context context, BitmapDrawable[] icons){
		// 背景框的宽高
		BitmapDrawable drawable = icons[0];
		int borderWidth = drawable.getBitmap().getWidth(), borderHeight = drawable.getBitmap().getHeight();
		// 背景框内部，每个小图标的宽高
		int itemWidth, itemHeight;
		if(icons.length <= 2){
			itemWidth = borderWidth - 20; itemHeight = borderHeight - 20;
		} else {
			itemWidth = (int) (borderWidth/2.5); itemHeight = (int) (borderHeight/2.5);
		}
		// 将icons里的所有元素的内容都绘制到这个Bitmap上。
		Bitmap bitmap = Bitmap.createBitmap(borderWidth, borderHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		for (int i = 0; i < icons.length; i++) {
			drawable = icons[i];
			if(i == 0){
				// 设置Drawable的尺寸，否则将不会draw任何内容到bitmap中。
				drawable.setBounds(0, 0, borderWidth, borderHeight);
				drawable.draw(canvas);
			} else {
				drawable = ImageUtil.resizeBitmap(context, drawable, itemWidth, itemHeight);
				float x = (float) (((i + 1) % 2 + 1) == 1 ? 
						(borderWidth/2 - itemWidth)/1.5:borderWidth/2+(borderWidth/2 - itemWidth)/2.5),
					  y = (float) (2 / i >= 1 ? 
								  (borderHeight/2 - itemHeight)/1.5:borderHeight/2+(borderHeight/2 - itemHeight)/2.5);
				if (icons.length == 2) {
					x = (borderWidth - itemWidth)/2;
					y = (borderHeight - itemHeight)/2;
				}
				canvas.drawBitmap(drawable.getBitmap(), x, y, paint);
			}
		}
		return bitmap;
	}
	
	/**
	 * 检测快捷方式是否存在。
	 * @param context
	 * @param textResId	快捷方式的标题
	 * @return
	 */
	public static int hasShortcut(Context context, int textResId){
        int state = STATE_NOT_EXIST;
        String AUTHORITY = getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor cursor = context.getContentResolver().query(CONTENT_URI,
                new String[]{ "title","intent"}, "title=?",
                new String[]{context.getString(textResId)}, null);
        String intent = "intent";
        while (cursor != null && cursor.moveToNext()) {
        	intent = new String(cursor.getString(cursor.getColumnIndex("intent")));
        	if(intent.indexOf(context.getPackageName()) >= 0){
        		state = STATE_EXIST_AND_IS_SELF;
        		break;
        	} else {
        		state = STATE_EXIST_AND_IS_OTHER;
        	}
        }
        if (cursor != null) {
			cursor.close();
		}
        // 如果快捷方式已经存在，则修改它的名称为默认名称，以防止别人修改。TODO 有些机型出bug
 		if (state == STATE_EXIST_AND_IS_SELF) {
 			ContentValues values = new ContentValues();
 			values.put("title", context.getString(textResId));
 			context.getContentResolver().update(CONTENT_URI, values, "intent=?", new String[]{intent});
 		}
        return state ;
	}
	
	/**
	 * 删除快捷方式。
	 * @param context
	 * @param textResId
	 */
	public static void delShortcut(Context context, Class<? extends Activity> clazz, int textResId){
		// 第一种方式：通过标准的方式，发送广播删除。
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");  
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(textResId));  
        Intent intent = new Intent(context, clazz);  
        intent.setAction("android.intent.action.MAIN");  
        intent.addCategory("android.intent.category.LAUNCHER");  
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);  
        context.sendBroadcast(shortcut);
        // 第二种方式：通过操作数据库来删除。	如果以后android的数据库字段改名了，则就得修改下面的代码，目前是可以使用的。
        // 把所有非当前应用程序创建的同名快捷方式都他妈的给我删除了。操！
// 		String AUTHORITY = getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");
// 	    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
// 	    context.getContentResolver().delete(CONTENT_URI, "title=? and intent not like ? ", new String[]{text,
// 	    		 "%"+context.getPackageName()+"%"});
    }    
	
	/*
	 * hasShortcut的辅助方法。
	 * @param context
	 * @param permission
	 * @return
	 */
	private static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }
}
