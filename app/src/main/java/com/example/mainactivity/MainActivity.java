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
    boolean PositionSet = false; // bool to control flow if the position has been set
    Button SelectMinimumPositionBtn; // android button to select new minimum

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //starts void on the creation of the app's instance
        setContentView(R.layout.activity_main); // set the view to the xml file activity_main in the res/ layout file
        Y_Value = (TextView) findViewById(R.id.yValue); // find an item with id yValue set it to the text view
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
        Y_Value.setText("Position:  " + String.format("%.2f", sensorEvent.values[1]));

        if (sensorEvent.values[1] < MinimumPosition) {

            String message = "You have been out of your PositionSet range for too long.";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.drawable.ic_spine)
                    .setContentTitle("Correct Your Posture")
                    .setContentText(message)
                    .setAutoCancel(true);

            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("message", message);

            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE
            );

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);

            notificationManager.notify(0, builder.build());

        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE
            );
            notificationManager.cancel(0);
        }
        if (PositionSet) { //Control flow of void
            MinimumPosition = sensorEvent.values[1];
            Toast.makeText(MainActivity.this, " Selected Point : " + MinimumPosition,
                    Toast.LENGTH_SHORT).show();
            PositionSet = false;
        }
    }
}


        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.PositionSetOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .PositionSetAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_PositionSettings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

 */