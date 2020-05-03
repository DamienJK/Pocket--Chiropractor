package com.example.mainactivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import 	android.os.PowerManager;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
 //   PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    float MinimumPosition; //lowest y value allowed set to -10 on default stopping the notifications from starting automatically
    final String KEY ="MIN_KEY";
    Sensor accelerometer; // sensor of position and rate of movement
    TextView Y_Value; // android UI text item set to the current y value
    TextView cPosition;
    boolean PositionSet = false; // bool to control flow if the position has been set
    Button SelectMinimumPositionBtn; // android button to select new minimum

    float LoadValue(){
        float x =0f;
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Toast.makeText(MainActivity.this, " Selected Point : " + sharedPreferences.getFloat(KEY,x), Toast.LENGTH_SHORT).show();
        return  sharedPreferences.getFloat(KEY,x);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //starts void on the creation of the app's instance
        setContentView(R.layout.activity_main); // set the view to the xml file activity_main in the res/ layout file
        Y_Value = (TextView) findViewById(R.id.yValue); // find an item with id yValue set it to the text view
        cPosition = (TextView) findViewById(R.id.cPosition); // find an item with id yValue set it to the text view
        SelectMinimumPositionBtn = findViewById(R.id.btn1); // same to the button object
        Toolbar toolbar = findViewById(R.id.toolbar); // find the ui tool bar set in the activity main by default
        setSupportActionBar(toolbar); // android void to access the toolbar and set it as an object
        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).setTitle("Pocket Chiropractor"); // changing the title of the toolbar
        MinimumPosition = LoadValue();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // set the sensor manager to a sensor service to access class values
        assert sensorManager != null;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // do the same to the acceleometer
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); //create a listener that is in this class under this context using the sensor accemeter
        if(MinimumPosition !=0){
            cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
            PositionSet =true;

        }
        SelectMinimumPositionBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PositionSet = true; // start static flow void inside of on value changed in response to button click
                cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
            }
        });


    }

    // this void is abstract in Sensor Event Listener but does not preform a function
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { // if the accelometer changes value this void is triggered
        cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
        Y_Value.setText("Position:" + String.format("%.2f", sensorEvent.values[1])); //set text to current y position
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        if(MinimumPosition<0){
            MinimumPosition=0;
            cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
            PositionSet=false;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (sensorEvent.values[1] < MinimumPosition && pm.isInteractive()) { //if current postion is less than minimum
            Log.println(Log.DEBUG,"bad","Too Low Correct Your Posture");
            String message = "Too Low Correct Your Posture";
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            Uri uri = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(message);        // Configure the notification channel.
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.enableVibration(true);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_spine)
                    .setTicker("Hearty365")
                    .setPriority(Notification.DEFAULT_SOUND)
                    .setContentTitle("Correction")
                    .setContentText(message)
                    .setContentInfo("Correction");
            assert notificationManager != null;
            notificationManager.notify(/*notification id*/1, notificationBuilder.build());
        }else if(sensorEvent.values[1] > MinimumPosition ){
            Log.println(Log.DEBUG,"bad","Should stop");
        }
        if (PositionSet) { //Control flow of void
            MinimumPosition = sensorEvent.values[1]; //set minimum position to current
            Toast.makeText(MainActivity.this, " Selected Point : " + MinimumPosition, Toast.LENGTH_SHORT).show(); // toast to user
            cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(KEY, MinimumPosition);
            // Replace `putInt` with `putString` if your value is a String and not an Integer.
            editor.apply();
            cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
            PositionSet = false; // turn off statement
        }
    }
}

