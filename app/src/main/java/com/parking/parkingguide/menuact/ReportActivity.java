package com.parking.parkingguide.menuact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parking.parkingguide.R;
import com.parking.parkingguide.bean.BmobReport;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ReportActivity extends AppCompatActivity {
    EditText etName,etContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_report);
        etName= (EditText) findViewById(R.id.et_reParkName);
        etContent=(EditText)findViewById(R.id.et_reParkDescri);
    }
    public void sumbitReport(View view){
        String name=etName.getText().toString();
        String content=etContent.getText().toString();
        if(!name.isEmpty()&&!content.isEmpty()){
            BmobReport bmobReport=new BmobReport();
            bmobReport.setName(name);
            bmobReport.setContent(content);
            bmobReport.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Toast.makeText(ReportActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ReportActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(ReportActivity.this,"所填信息不能为空!",Toast.LENGTH_SHORT).show();
        }
    }
}
