package sabm.crashandcall;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


public class Main extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    private long CriticalTimeStart;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }


    //// TODO: 11.02.2017 Variablen aufräumen, sind ein paar zu viel übrig
    int Progress=0;
    int CallProgress=0;
    int AccProgress=0;
    int ShakeProgress=0;
    int sensity_shake=140;
    int sensity_speed=140;
    int sensity_x=100;
    int sensity_y=100;
    int sensity_z=100;
    int x_max=0;
    int y_max=0;
    int z_max=0;
    int accelationSquareRoot_max=0;

    public void profile_bike(View view) {
        sensity_x = 30;
        sensity_y = 100;
        sensity_z = 100;
        sensity_speed = 20;
        sensity_shake = 100;

        // set seekbars
        // x
        SeekBar seekbar_sensity_x = (SeekBar) findViewById(R.id.sensity_x);
        seekbar_sensity_x.setProgress(sensity_x);
        // y
        SeekBar seekbar_sensity_y = (SeekBar) findViewById(R.id.sensity_y);
        seekbar_sensity_y.setProgress(sensity_y);
        // z
        SeekBar seekbar_sensity_z = (SeekBar) findViewById(R.id.sensity_z);
        seekbar_sensity_z.setProgress(sensity_z);
        // speed
        SeekBar seekbar_sensity_speed = (SeekBar) findViewById(R.id.sensity_speed);
        seekbar_sensity_speed.setProgress(sensity_speed);
        // shake
        SeekBar seekbar_sensity_shake = (SeekBar) findViewById(R.id.sensity_shake);
        seekbar_sensity_shake.setProgress(sensity_shake);

    }

    public void save_profile_bike_street(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // read sensities, set by user by seekbars
        // shake
        SeekBar seekbar_sensity_shake = (SeekBar) findViewById(R.id.sensity_shake);
        sensity_shake = seekbar_sensity_shake.getProgress();
        // speed
        SeekBar seekbar_sensity_speed = (SeekBar) findViewById(R.id.sensity_speed);
        sensity_speed = seekbar_sensity_speed.getProgress();
        // x
        SeekBar seekbar_sensity_x = (SeekBar) findViewById(R.id.sensity_x);
        sensity_x = seekbar_sensity_x.getProgress();
        // y
        SeekBar seekbar_sensity_y = (SeekBar) findViewById(R.id.sensity_y);
        sensity_y = seekbar_sensity_y.getProgress();
        // z
        SeekBar seekbar_sensity_z = (SeekBar) findViewById(R.id.sensity_z);
        sensity_z = seekbar_sensity_z.getProgress();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("street_stored_sensity_shake", sensity_shake); // value to store
        editor.apply();
        editor.putInt("street_stored_sensity_speed", sensity_speed); // value to store
        editor.apply();
        editor.putInt("street_stored_sensity_x", sensity_x); // value to store
        editor.apply();
        editor.putInt("street_stored_sensity_y", sensity_y); // value to store
        editor.apply();
        editor.putInt("street_stored_sensity_z", sensity_z); // value to store
        editor.apply();

    }

    public void load_profile_bike_street(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int stored_sensity_shake = preferences.getInt("street_stored_sensity_shake", 0);
        int stored_sensity_speed = preferences.getInt("street_stored_sensity_speed", 0);
        int stored_sensity_x = preferences.getInt("street_stored_sensity_x", 0);
        int stored_sensity_y = preferences.getInt("street_stored_sensity_y", 0);
        int stored_sensity_z = preferences.getInt("street_stored_sensity_z", 0);

        // set seekbars

        // shake
        SeekBar seekbar_sensity_shake = (SeekBar) findViewById(R.id.sensity_shake);
        seekbar_sensity_shake.setProgress(stored_sensity_shake);
        // speed
        SeekBar seekbar_sensity_speed = (SeekBar) findViewById(R.id.sensity_speed);
        seekbar_sensity_speed.setProgress(stored_sensity_speed);
        // x
        SeekBar seekbar_sensity_x = (SeekBar) findViewById(R.id.sensity_x);
        seekbar_sensity_x.setProgress(stored_sensity_x);
        // y
        SeekBar seekbar_sensity_y = (SeekBar) findViewById(R.id.sensity_y);
        seekbar_sensity_y.setProgress(stored_sensity_y);
        // z
        SeekBar seekbar_sensity_z = (SeekBar) findViewById(R.id.sensity_z);
        seekbar_sensity_z.setProgress(stored_sensity_z);

    }

    public void save_profile_bike_mtb(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // read sensities, set by user by seekbars
        // shake
        SeekBar seekbar_sensity_shake = (SeekBar) findViewById(R.id.sensity_shake);
        sensity_shake = seekbar_sensity_shake.getProgress();
        // speed
        SeekBar seekbar_sensity_speed = (SeekBar) findViewById(R.id.sensity_speed);
        sensity_speed = seekbar_sensity_speed.getProgress();
        // x
        SeekBar seekbar_sensity_x = (SeekBar) findViewById(R.id.sensity_x);
        sensity_x = seekbar_sensity_x.getProgress();
        // y
        SeekBar seekbar_sensity_y = (SeekBar) findViewById(R.id.sensity_y);
        sensity_y = seekbar_sensity_y.getProgress();
        // z
        SeekBar seekbar_sensity_z = (SeekBar) findViewById(R.id.sensity_z);
        sensity_z = seekbar_sensity_z.getProgress();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("mtb_stored_sensity_shake", sensity_shake); // value to store
        editor.apply();
        editor.putInt("mtb_stored_sensity_speed", sensity_speed); // value to store
        editor.commit();
        editor.putInt("mtb_stored_sensity_x", sensity_x); // value to store
        editor.commit();
        editor.putInt("mtb_stored_sensity_y", sensity_y); // value to store
        editor.commit();
        editor.putInt("mtb_stored_sensity_z", sensity_z); // value to store
        editor.commit();

    }

    public void load_profile_bike_mtb(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int stored_sensity_shake = preferences.getInt("mtb_stored_sensity_shake", 0);
        int stored_sensity_speed = preferences.getInt("mtb_stored_sensity_speed", 0);
        int stored_sensity_x = preferences.getInt("mtb_stored_sensity_x", 0);
        int stored_sensity_y = preferences.getInt("mtb_stored_sensity_y", 0);
        int stored_sensity_z = preferences.getInt("mtb_stored_sensity_z", 0);

        // set seekbars

        // shake
        SeekBar seekbar_sensity_shake = (SeekBar) findViewById(R.id.sensity_shake);
        seekbar_sensity_shake.setProgress(stored_sensity_shake);
        // speed
        SeekBar seekbar_sensity_speed = (SeekBar) findViewById(R.id.sensity_speed);
        seekbar_sensity_speed.setProgress(stored_sensity_speed);
        // x
        SeekBar seekbar_sensity_x = (SeekBar) findViewById(R.id.sensity_x);
        seekbar_sensity_x.setProgress(stored_sensity_x);
        // y
        SeekBar seekbar_sensity_y = (SeekBar) findViewById(R.id.sensity_y);
        seekbar_sensity_y.setProgress(stored_sensity_y);
        // z
        SeekBar seekbar_sensity_z = (SeekBar) findViewById(R.id.sensity_z);
        seekbar_sensity_z.setProgress(stored_sensity_z);
    }

    public void ResetCriticalBar(View view) {
        Progress = 0;
        AccProgress = 0;
        ShakeProgress = 0;
        CallProgress = 0;
        ProgressBar AccelerationBar1 = (ProgressBar) findViewById(R.id.AccelerationBar);
        AccelerationBar1.setProgress(AccProgress);
        ProgressBar ShakeBar1 = (ProgressBar) findViewById(R.id.ShakeBar1);
        ShakeBar1.setProgress(ShakeProgress);
        ProgressBar CallingBar1 = (ProgressBar) findViewById(R.id.CallingBar1);
        CallingBar1.setProgress(CallProgress);
        TextView sensor_output2 = (TextView) findViewById(R.id.textView_sensor_output);
        sensor_output2.setText("Calling?");
        x_max=0;
        y_max=0;
        z_max=0;
        maxspeed = 0;
        accelationSquareRoot_max = 0;

    }


    //public void CallAlarm(View view) {
    public void CallAlarm (){
        // SOUND notification
        //Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.audio)
                .setContentTitle("title")
                .setContentText("message")
                .setSound(soundUri); //This sets the sound to play

        //Display notification
        notificationManager.notify(0, mBuilder.build());
    }

    float maxspeed=0;
    int maxdistance=0;
    int distance=0;
    float xlast;
    float ylast;
    float zlast;
    long lastUpdate2;
    float float_speed;
    float speed=0;
    private void getAccelerometer(SensorEvent event) {
        long actualTime = event.timestamp;

        // read x,y,z acceleration values from sensor
        float[] values = event.values;
        float float_x = values[0];
        float float_y = values[1];
        float float_z = values[2];

        // x,y,z

        // Prepare for show on Bar
        int x = (int) float_x * 10;
        int y = (int) float_y * 10;
        int z = (int) float_z * 10;

        // Betrag x,y,z
        if (x < 0) { x = x * -1; }
        if (y < 0) { y = y * -1; }
        if (z < 0) { z = z * -1; }

        ProgressBar accX = (ProgressBar) findViewById(R.id.accX);
        accX.setProgress(x);
        ProgressBar accY = (ProgressBar) findViewById(R.id.accY);
        accY.setProgress(y);
        ProgressBar accZ = (ProgressBar) findViewById(R.id.accZ);
        accZ.setProgress(z);

        // got x,y,z max
        if (x > x_max) { x_max = x; }
        if (y > y_max) { y_max = y; }
        if (z > z_max) { z_max = z; }


        //DebugData
        //TextView sensor_output = (TextView) findViewById(R.id.textView_sensor_output);
        //sensor_output.setText(String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z));
        //Toast.makeText(getApplicationContext(), "vlaue is "+accelationSquareRoot, Toast.LENGTH_LONG)
        //        .show();

        // Acceleration Detect
        // per second
        long diffTimeSec = (actualTime - lastUpdate2)/100000;
        if (diffTimeSec > 10000) {

            // speed = acceleration
            float_speed = Math.abs((x - xlast) + (y - ylast) + (z - zlast)) / diffTimeSec * 100000;
            int time = (int) diffTimeSec;
            int speed = (int) float_speed / 100 ;
            int velocity = speed * time;
            distance = velocity * time + (speed * time^2) / 2;
            if ( distance < 0 ) { distance = distance * -1;}
            if ( distance > maxdistance ) { maxdistance = distance;}

            // remember max speed
            if ( speed > maxspeed ) {maxspeed = speed;}

            // Shake Detect
            float float_accelationSquareRoot = (x * x + y * y + z * z)
                    / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            int accelationSquareRoot = (int) float_accelationSquareRoot;
            if (accelationSquareRoot > accelationSquareRoot_max) { accelationSquareRoot_max = accelationSquareRoot; }

            // Show Bars
            // Show x,y,z and accelerationSquareRoot values
            TextView xyz = (TextView) findViewById(R.id.xyz);
            xyz.setText("x,y,z,shake,speed:             " + String.valueOf(x) + "    " + String.valueOf(y) + "    " + String.valueOf(z) + "    " + String.valueOf(accelationSquareRoot) + "    " + String.valueOf(speed));

            // Show x,y,z max values
            TextView xyz_max = (TextView) findViewById(R.id.xyz_max);
            xyz_max.setText("x,y,z,shake,speed_max    " + String.valueOf(x_max) + "    " + String.valueOf(y_max) + "    " + String.valueOf(z_max) + "    " + String.valueOf(accelationSquareRoot_max) + "    " + String.valueOf(maxspeed));


            // read sensities, set by user by seekbars
            // shake
            SeekBar seekbar_sensity_shake = (SeekBar) findViewById(R.id.sensity_shake);
            sensity_shake = seekbar_sensity_shake.getProgress();
            // speed
            SeekBar seekbar_sensity_speed = (SeekBar) findViewById(R.id.sensity_speed);
            sensity_speed = seekbar_sensity_speed.getProgress();
            // x
            SeekBar seekbar_sensity_x = (SeekBar) findViewById(R.id.sensity_x);
            sensity_x = seekbar_sensity_x.getProgress();
            // y
            SeekBar seekbar_sensity_y = (SeekBar) findViewById(R.id.sensity_y);
            sensity_y = seekbar_sensity_y.getProgress();
            // z
            SeekBar seekbar_sensity_z = (SeekBar) findViewById(R.id.sensity_z);
            sensity_z = seekbar_sensity_z.getProgress();


            // show read sensity value in textview
            // shake
            TextView view_sensity_shake = (TextView) findViewById(R.id.seek_view_shake);
            view_sensity_shake.setText("sensity shake: " + String.valueOf(sensity_shake) + "       # " + String.valueOf(accelationSquareRoot_max));
            if (accelationSquareRoot_max > sensity_shake) { view_sensity_shake.setTextColor(Color.RED);} else {view_sensity_shake.setTextColor(Color.GRAY);}
            // speed
            TextView view_sensity_speed = (TextView) findViewById(R.id.seek_view_speed);
            view_sensity_speed.setText("sensity speed: " + String.valueOf(sensity_speed) + "       # " + String.valueOf(maxspeed));
            if (maxspeed > sensity_speed) { view_sensity_speed.setTextColor(Color.RED);} else {view_sensity_speed.setTextColor(Color.GRAY);}
            // x
            TextView view_sensity_x = (TextView) findViewById(R.id.seek_view_x);
            view_sensity_x.setText("sensity x: " + String.valueOf(sensity_x) + "       # " + String.valueOf(x_max));
            if (x_max > sensity_x) { view_sensity_x.setTextColor(Color.RED);} else {view_sensity_x.setTextColor(Color.GRAY);}
            // y
            TextView view_sensity_y = (TextView) findViewById(R.id.seek_view_y);
            view_sensity_y.setText("sensity y: " + String.valueOf(sensity_y) + "       # " + String.valueOf(y_max));
            if (y_max > sensity_y) { view_sensity_y.setTextColor(Color.RED);} else {view_sensity_y.setTextColor(Color.GRAY);}
            // z
            TextView view_sensity_z = (TextView) findViewById(R.id.seek_view_z);
            view_sensity_z.setText("sensity z: " + String.valueOf(sensity_z) + "       # " + String.valueOf(z_max));
            if (z_max > sensity_z) { view_sensity_z.setTextColor(Color.RED);} else {view_sensity_z.setTextColor(Color.GRAY);}

            // Alarm Critical Acceleration // Base=140
            if ( maxspeed > sensity_speed || x_max > sensity_x || y_max > sensity_y || z_max > sensity_z || accelationSquareRoot_max > sensity_shake) {
              // Test Alarm
              CallAlarm();
              // Test Timer
                int AccProgressS = AccProgress;
                AccProgress = AccProgressS + 10;
                ProgressBar AccelerationBar1 = (ProgressBar) findViewById(R.id.AccelerationBar);
                AccelerationBar1.setProgress(AccProgress);
                TextView sensor_output2 = (TextView) findViewById(R.id.textView_sensor_output);
                sensor_output2.setText("CALL!!!!");
            }

            lastUpdate2 = actualTime;
            xlast = x;
            ylast = y;
            zlast = z;


        // Alarm Shake detect
        if (accelationSquareRoot >= 500) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            //Debug: SensorData
            //TextView sensor_output2 = (TextView) findViewById(R.id.textView_sensor_output2);
            //sensor_output2.setText(String.valueOf(accelationSquareRoot));

            int AccProgressS = AccProgress;
            AccProgress = AccProgressS + 20;
            if (AccProgress == 100) {
                CriticalTimeStart = SystemClock.currentThreadTimeMillis();
            }
            ProgressBar ShakeBar1 = (ProgressBar) findViewById(R.id.ShakeBar1);
            ShakeBar1.setProgress(AccProgress);


            if (color) {
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            color = !color;

            // SOUND notification
            //Define Notification Manager
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

            //Define sound URI
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.audio)
                    .setContentTitle("title")
                    .setContentText("message")
                    .setSound(soundUri); //This sets the sound to play

            //Display notification
            notificationManager.notify(0, mBuilder.build());

            if ( speed > 100 ) {
                // and shake alarm
                TextView sensor_output2 = (TextView) findViewById(R.id.textView_sensor_output);
                sensor_output2.setText("CALL!!!!");

            }

        }

        } // if all xxx seconds

        // if comes 20sec quitness after strange shake, it might be an accident
        if (Progress >= 100) {
            int CallProgressS = CallProgress;
            CallProgress = CallProgressS + 10;
            ProgressBar CallingBar1 = (ProgressBar) findViewById(R.id.CallingBar1);
            CallingBar1.setProgress(CallProgress);
        } else {
            CallProgress = 0;
            ProgressBar CallingBar1 = (ProgressBar) findViewById(R.id.CallingBar1);
            CallingBar1.setProgress(CallProgress);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                //sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}