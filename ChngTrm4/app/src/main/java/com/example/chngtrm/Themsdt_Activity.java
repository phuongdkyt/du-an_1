package com.example.chngtrm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Themsdt_Activity extends AppCompatActivity implements View.OnClickListener {

    private String sdt;
EditText edt1;
TextView t1;
Button btn1,btn2;
Boolean check=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themsdt_);
        edt1=findViewById(R.id.nhapsdt);
        t1=findViewById(R.id.thongtinsdt);
        btn1=findViewById(R.id.xacnhan1);
        btn2=findViewById(R.id.thoat1);
      Intent i=getIntent();
     String s= i.getStringExtra("sdt");
      t1.setText("SĐT hiện tại:"+s);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.xacnhan1:
                String s=edt1.getText().toString();
                if(!s.equals("")) {
                    check=true;
                    sdt = edt1.getText().toString();
                    t1.setText(sdt + "\n" + "Nhấn lưu vào thoát để lưu SĐT");
                    Toast.makeText(Themsdt_Activity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(Themsdt_Activity.this, "Bạn chưa nhập SĐT", Toast.LENGTH_SHORT).show();
                break;
            case R.id.thoat1:
                if(check==true) {
                    final Intent data = new Intent();
                    data.putExtra("key1", sdt);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                    break;
                }
                else Toast.makeText(Themsdt_Activity.this, "Bạn chưa nhấn xác nhận,vui lòng nhấn xác nhận để thoát", Toast.LENGTH_SHORT).show();
        }
    }
}
