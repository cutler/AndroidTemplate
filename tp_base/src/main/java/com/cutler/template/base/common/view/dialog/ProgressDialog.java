package com.cutler.template.base.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cutler.template.base.R;

/**
 * @author cutler
 */
public class ProgressDialog {

    /**
     * 进度条对话框
     */
    private static Dialog dialog;

    /**
     * 显示模式进度条对话框。
     */
    public static void showProgressDialog(Activity context, String message) {
        showProgressDialog(context, message, false);
    }

    public static void showProgressDialog(Activity context, String message, boolean isCancelable) {
        if (dialog == null && context != null) {
            dialog = new Dialog(context, R.style.CustomerDialog);
            View rootView = LayoutInflater.from(context).inflate(R.layout.tp_dialog_progress, null);
            if (message != null) {
                TextView textView = (TextView) rootView.findViewById(R.id.msg);
                textView.setText(message);
            }
            dialog.setContentView(rootView);
            dialog.setCancelable(isCancelable);
            // 设置对话框的宽度，要再设置完布局后，再修改对话框的宽度。
            WindowManager windowManager = context.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = display.getWidth();
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        }
    }


    public static boolean isShowing() {
        return dialog != null;
    }

    /**
     * 关闭进度条对话框。
     */
    public static void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
    }

}
