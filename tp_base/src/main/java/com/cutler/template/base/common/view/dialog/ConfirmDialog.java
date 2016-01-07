package com.cutler.template.base.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


import com.cutler.template.base.R;

import java.lang.ref.SoftReference;

/**
 * @author cutler
 */
public class ConfirmDialog extends Dialog {

    private SoftReference<Activity> softReference;
    private EditText mEditText;
    private View.OnClickListener onClickListener;

    public ConfirmDialog(Activity context, int themeResId) {
        super(context, themeResId);
        softReference = new SoftReference<Activity>(context);
    }

    public void initContentView(String title, String content) {
        Activity context = softReference.get();
        View rootView = LayoutInflater.from(context).inflate(R.layout.tp_dialog_confirm, null);

        ((TextView) rootView.findViewById(R.id.title)).setText(title);
        ((TextView) rootView.findViewById(R.id.msg)).setText(content);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
                dismiss();
            }
        };
        rootView.findViewById(R.id.ok).setOnClickListener(listener);
        rootView.findViewById(R.id.cancel).setOnClickListener(listener);
        setContentView(rootView);
        setCanceledOnTouchOutside(true);
        // 设置对话框的宽度，要再设置完布局后，再修改对话框的宽度。
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        getWindow().setAttributes(lp);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
