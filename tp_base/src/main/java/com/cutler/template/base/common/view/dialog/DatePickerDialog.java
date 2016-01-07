package com.cutler.template.base.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.cutler.template.base.R;
import com.cutler.template.base.common.view.picker.DatePicker;

import java.lang.ref.SoftReference;
import java.util.Calendar;

/**
 * @author cutler
 */
public class DatePickerDialog extends Dialog {
    private SoftReference<Activity> softReference;
    private OnDatePickerListener onDatePickerListener;

    public DatePickerDialog(Activity context, int themeResId) {
        super(context, themeResId);
        softReference = new SoftReference<Activity>(context);
    }

    public void initContentView(String title, Calendar calendar) {
        Activity context = softReference.get();
        View rootView = LayoutInflater.from(context).inflate(R.layout.tp_dialog_picker_date, null);
        TextView mTitleTV = (TextView) rootView.findViewById(R.id.title);
        Button okBtn = (Button) rootView.findViewById(R.id.ok);
        final DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (onDatePickerListener != null) {
                    onDatePickerListener.onDatePicked(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDay());
                }
            }
        });
        mTitleTV.setText(title);
        if (calendar != null) {
            datePicker.setCalendar(calendar);
        }
        setContentView(rootView);
        setCanceledOnTouchOutside(true);
        // 设置对话框的宽度，要再设置完布局后，再修改对话框的宽度。
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        getWindow().setAttributes(lp);
    }

    public void setOnDatePickerListener(OnDatePickerListener onDatePickerListener) {
        this.onDatePickerListener = onDatePickerListener;
    }

    public static interface OnDatePickerListener {
        public void onDatePicked(int year, int month, int day);
    }

}
