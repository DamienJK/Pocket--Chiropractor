package com.example.mainactivity;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    float MinimumPosition = -10f; //lowest y value allowed set to -10 on default stopping the notifications from starting automatically
    private SensorManager sensorManager; // android class to handle all sensor types and data
    Sensor accelerometer; // sensor of position and rate of movement
    TextView Y_Value; // android UI text item set to the current y value
    TextView cPosition;
    boolean PositionSet = false; // bool to control flow if the position has been set
    Button SelectMinimumPositionBtn; // android button to select new minimum

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //starts void on the creation of the app's instance
        setContentView(R.layout.activity_main); // set the view to the xml file activity_main in the res/ layout file
        Y_Value = (TextView) findViewById(R.id.yValue); // find an item with id yValue set it to the text view
        cPosition = (TextView) findViewById(R.id.cPosition); // find an item with id yValue set it to the text view
        SelectMinimumPositionBtn = findViewById(R.id.btn1); // same to the button object
        Toolbar toolbar = findViewById(R.id.toolbar); // find the ui tool bar set in the activity main by default
        setSupportActionBar(toolbar); // android void to access the toolbar and set it as an object
        getSupportActionBar().setTitle("Pocket Chiropractor"); // changing the title of the toolbar
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // set the sensor manager to a sensor service to access class values
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // do the same to the acceleometer
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); //create a listener that is in this class under this context using the sensor accemeter
        SelectMinimumPositionBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // if the button is pressed this will run
                // start static flow void inside of on value changed in response to button click
                PositionSet = true;
            }
        });
    }

    // this void is abstract in Sensor Event Listener but does not preform a function
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { // if the accelometer changes value this void is triggered
        Y_Value.setText("Position:  " + String.format("%.2f", sensorEvent.values[1])); //set text to current y position

        if (sensorEvent.values[1] < MinimumPosition) { //if current postion is less than minimum

            String message = "You have been out of your PositionSet range for too long.";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this) // create a notification
                    .setSmallIcon(R.drawable.ic_spine) // find icon and set it to notification
                    .setContentTitle("Correct Your Posture") // message to user
                    .setContentText(message) // set content as the string
                    .setAutoCancel(true); //  can be turned on without user

            Intent intent = new Intent(MainActivity.this, NotificationActivity.class); //create an intent as notification
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("message", message);

            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE
            );// build and push the notification

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri); // create sound on notification

            notificationManager.notify(0, builder.build());

        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE
            );
            notificationManager.cancel(0); // turn off the notification on fixed position
        }
        if (PositionSet) { //Control flow of void
            MinimumPosition = sensorEvent.values[1]; //set minimum position to current
            Toast.makeText(MainActivity.this, " Selected Point : " + MinimumPosition, Toast.LENGTH_SHORT).show(); // toast to user
            cPosition.setText(" Minimum: "+ MinimumPosition); // set our min text
            PositionSet = false; // turn off statement
        }
    }
}

