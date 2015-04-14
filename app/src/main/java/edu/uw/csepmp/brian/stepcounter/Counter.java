package edu.uw.csepmp.brian.stepcounter;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class Counter extends ActionBarActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z, last_speed;
    private static final String LOG_TAG = "Counter";
    private int steps = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_counter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        TextView stepCountView = (TextView) findViewById(R.id.stepCountText);
        stepCountView.setText(Integer.toString(steps));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        Float MIN_SPEED = 15f;
        Float MIN_CHANGE = 45f;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);

            if ((curTime - lastUpdate) > 200) {
                lastUpdate = curTime;

                float speed = (x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                float speed_change = speed - last_speed;

                Log.d(LOG_TAG, "Current speed " + speed + " difference " + speed_change);
                if ( speed_change > MIN_CHANGE &&  speed > MIN_SPEED && speed_change > speed ) {
                    Log.d(LOG_TAG, "Got Step " + speed);
                    steps++;
                    TextView stepCountView = (TextView) findViewById(R.id.stepCountText);
                    stepCountView.setText(Integer.toString(steps));
                }

                last_x = x;
                last_y = y;
                last_z = z;
                last_speed = speed;
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link android.hardware.SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
