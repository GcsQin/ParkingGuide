package com.parking.parkingguide.customview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.parking.parkingguide.R;

/**
 * Created by 37266 on 2017/4/29.
 */

public class MaterialProgressDialog extends ProgressDialog {
    public MaterialProgressDialog(Context context) {
        super(context);
    }

    public MaterialProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(getContext());
    }
    private void initView(Context context){
        //设置不可取消，点击外部也不可取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.custom_layout_progressdialog);
        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.width=WindowManager.LayoutParams.WRAP_CONTENT;
        params.height=WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
