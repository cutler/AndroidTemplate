package com.cutler.template.base.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.cutler.template.base.R;

import java.lang.ref.SoftReference;

/**
 * @author cutler
 */
public class InputDialog extends Dialog {

    private SoftReference<Activity> softReference;
    private EditText mEditText;
    private OnSubmitClickListener onSubmitClickListener;

    public InputDialog(Activity context, int themeResId) {
        super(context, themeResId);
        softReference = new SoftReference<Activity>(context);
    }

    public void initContentView(String title, String hint) {
        Activity context = softReference.get();
        View rootView = LayoutInflater.from(context).inflate(R.layout.tp_dialog_input, null);
        Button button = (Button) rootView.findViewById(R.id.ok);
        TextView mTitleTV = (TextView) rootView.findViewById(R.id.title);
        mTitleTV.setText(title);
        mEditText = (EditText) rootView.findViewById(R.id.mContentET);
        mEditText.setHint(hint);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (onSubmitClickListener != null) {
                    onSubmitClickListener.onSubmitClicked(mEditText.getText().toString());
                }
                dismiss();
            }
        });
        setContentView(rootView);
        setCanceledOnTouchOutside(true);
        // 设置对话框的宽度，要再设置完布局后，再修改对话框的宽度。
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        getWindow().setAttributes(lp);
    }

    public void setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        this.onSubmitClickListener = onSubmitClickListener;
    }

    public static interface OnSubmitClickListener {
        public void onSubmitClicked(String content);
    }

    public void setOnlyNumber() {
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
    }

}
