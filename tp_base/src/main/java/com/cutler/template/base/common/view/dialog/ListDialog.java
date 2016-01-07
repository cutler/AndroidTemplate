package com.cutler.template.base.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cutler.template.base.R;

import java.lang.ref.SoftReference;
import java.util.Arrays;

/**
 * @author cutler
 */
public class ListDialog extends Dialog {

    private SoftReference<Activity> softReference;
    private OnItemSelectedListener onItemSelectedListener;

    public ListDialog(Activity context, int themeResId) {
        super(context, themeResId);
        softReference = new SoftReference<Activity>(context);
    }

    public void initContentView(String title, final String[] items) {
        initContentView(title, items, 0);
    }

    public void initContentView(String title, final String[] items, int dialogHeight) {
        Activity context = softReference.get();
        View rootView = LayoutInflater.from(context).inflate(R.layout.tp_dialog_list, null);
        TextView mTitleTV = (TextView) rootView.findViewById(R.id.title);
        mTitleTV.setText(title);
        ListView mListView = (ListView) rootView.findViewById(R.id.mListView);
        mListView.setAdapter(new ListDialogAdapter(Arrays.asList(items)));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position, items[position]);
                }
            }
        });
        setContentView(rootView);
        setCanceledOnTouchOutside(true);
        // 设置对话框的宽度，要再设置完布局后，再修改对话框的宽度。
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        if (dialogHeight > 0) {
            lp.height = dialogHeight;
        }
        getWindow().setAttributes(lp);
    }

    public static interface OnItemSelectedListener {
        public void onItemSelected(int position, String item);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
}
