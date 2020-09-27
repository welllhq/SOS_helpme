package com.example.sos_helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Add_contacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        final EditText urgentMSG = (EditText)findViewById(R.id.urgent_msg);
        final EditText urgent_contact1 = (EditText)findViewById(R.id.urgent_contact1);
        final EditText urgent_contact2 = (EditText)findViewById(R.id.urgent_contact2);
        final EditText urgent_contact3 = (EditText)findViewById(R.id.urgent_contact3);
        Button save_button = (Button)findViewById(R.id.SAVE_button);

        final SharedPreferences sp =  getSharedPreferences("urgentMSG",MODE_PRIVATE);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uMSG = urgentMSG.getText().toString().trim();
                String uc1 = urgent_contact1.getText().toString().trim();
                String uc2 = urgent_contact2.getText().toString().trim();
                String uc3 = urgent_contact3.getText().toString().trim();
                SharedPreferences.Editor editor = sp.edit();
                if(TextUtils.isEmpty(uMSG)||TextUtils.isEmpty(uc1)||TextUtils.isEmpty(uc2)||TextUtils.isEmpty(uc3)){
                    Toast.makeText(Add_contacts.this, "所有输入不得为空！", Toast.LENGTH_SHORT).show();
                }else{
                    editor.putString("urgentMSG",uMSG);
                    editor.putString("urgent_contact1",uc1);
                    editor.putString("urgent_contact2",uc2);
                    editor.putString("urgent_contact3",uc3);
                    editor.commit();
                    Toast.makeText(Add_contacts.this, "已保存", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}