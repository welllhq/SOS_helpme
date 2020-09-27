package com.example.sos_helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.AlarmManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 *定时器这项功能太难了我放弃了，以后有空再弄吧
 */
public class Timer_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_activity);
        final TimePicker timePicker = findViewById(R.id.TimePicker);
        timePicker.setIs24HourView(true);
        Button time_confirm_button=findViewById(R.id.Time_confirm_button);
        Button cancel_button = findViewById(R.id.cancel_button);
        final AlarmManager alarmManager =(AlarmManager)getSystemService(Context.ALARM_SERVICE);

        time_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.sos_helpme.broadcast");
                intent.setComponent(new ComponentName("com.example.sos_helpme","com.example.sos_helpme.MyReceiver"));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Timer_activity.this,1,intent,0);
                //设置闹钟
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND,0);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                Toast.makeText(Timer_activity.this, "Timer has be set.Don't forget to cancel it if nothing happen", Toast.LENGTH_SHORT).show();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.sos_helpme.broadcast");
                intent.setComponent(new ComponentName("com.example.sos_helpme","com.example.sos_helpme.MyReceiver"));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Timer_activity.this,1,intent,0);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(Timer_activity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}