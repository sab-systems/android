package sabm.crashandcall;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.AdapterView;


import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Iterator;
import java.util.Set;

import static android.app.Service.START_STICKY;


public class Main extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long CriticalTimeStart;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Called when the activity is first created.
     */
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
        long lastUpdate = System.currentTimeMillis();

        // set inital max value for callingbar
        // set Progress from seekbar to manual change TimerToCall
        SeekBar seek_timer_to_call_bar = (SeekBar) findViewById(R.id.seek_timer_to_call);
        seek_timer_to_call_bar.setProgress(TimerToCall_max);

        //// TODO: 12.02.2017 load bike default profile after first start

        // first_run
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getInt("first_run", 0) == 0) {


            //Toast.makeText(getApplicationContext(), "vlaue is "+accelationSquareRoot, Toast.LENGTH_LONG)
            Toast.makeText(getApplicationContext(), "first run initials profiles", Toast.LENGTH_LONG)
                    .show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("first_run", 1); // value to store
            editor.apply();

            // initialice profiles at first run  (uninstall + reinstall also first run)
            // street
            sensity_shake = 206;
            sensity_speed = 8;
            sensity_x = 85;
            sensity_y = 83;
            sensity_z = 162;
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
            // mtb
            sensity_shake = 312;
            sensity_speed = 15;
            sensity_x = 134;
            sensity_y = 95;
            sensity_z = 183;
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

            // first time load default profle
            profile_bike_default();

        } else {

            // load last used profile
            String last_used_profile = preferences.getString("last_used_profile", "");
            Toast.makeText(getApplicationContext(), "load profile - " + last_used_profile + " -", Toast.LENGTH_LONG)
                    .show();

            if (last_used_profile.equals("street")) {
                // load street
                profile_bike_street();
            } else if (last_used_profile.equals("mtb")) {
                // load mtb
                profile_bike_mtb();
            } else {
                // load default
                profile_bike_default();
            }

        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    // Todo: write logfile

    //// TODO: 11.02.2017 Variablen aufräumen, sind ein paar zu viel übrig
    float maxspeed = 0;
    float xlast;
    float ylast;
    float zlast;
    private long lastUpdate2;
    float float_speed;
    // distance calculated below, but not used at the momen
    int distance = 0;
    int maxdistance = 0;
    // init start values
    int Progress = 0;
    int CallProgress = 0;
    private int TimerToCall = 0;
    int ShakeProgress = 0;
    private int TimerToCall_start = 0;
    // init sensity values
    int sensity_shake = 140;
    private int sensity_speed = 140;
    int sensity_x = 100;
    int sensity_y = 100;
    private int sensity_z = 100;
    // init max values
    int x_max = 0;
    int y_max = 0;
    int z_max = 0;
    int accelationSquareRoot_max = 0;
    private int TimerToCall_max = 30;  // after alarm activated, wait 30sec until send out a message (1sec=TimerToCall_max=10)

    // profile default
    public void profile_bike_default() {
        // based on street profile
        sensity_shake = 206;
        sensity_speed = 8;
        sensity_x = 85;
        sensity_y = 83;
        sensity_z = 162;

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_used_profile", "default"); // value to store
        editor.apply();
    }

    public void load_profile_bike_default(View view) {
        profile_bike_default();
    }

    // profile street
    public void profile_bike_street() {
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

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_used_profile", "street"); // value to store
        editor.apply();

    }

    public void load_profile_bike_street(View view) {
        profile_bike_street();
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

    // profile mtb
    public void profile_bike_mtb() {
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

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_used_profile", "mtb"); // value to store
        editor.apply();
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
        profile_bike_mtb();
    }

    public void sendSMS(View view) {
        // SMS: https://www.sitepoint.com/how-to-handle-sms-in-android/
        EditText messageNumber=(EditText) findViewById(R.id.messageNumber);

        String _messageNumber=messageNumber.getText().toString();
        String messageText = "Hi , Just SMSed to say hello";
        String sent = "SMS_SENT";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(sent), 0);

        //--- Toast when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                if(getResultCode() == Activity.RESULT_OK)
                {
                    Toast.makeText(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "SMS could not sent",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(sent));


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(_messageNumber, null, messageText, null, null);

    }


    // https://code.tutsplus.com/tutorials/android-essentials-using-the-contact-picker--mobile-2017
    private static final int CONTACT_PICKER_RESULT = 1001;
    private String DEBUG_TAG = "sab_debug_tab";
    public void chooseContact(View view) {

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
    // Choose eMail
    protected void onActivityResult_disabled(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Cursor cursor = null;
                    String email = "";
                    try {
                        Uri result = data.getData();
                        Log.v(DEBUG_TAG, "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query for everything email
                        cursor = getContentResolver().query(Email.CONTENT_URI,
                                null, Email.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int emailIdx = cursor.getColumnIndex(Email.DATA);

                        // let's just get the first email
                        if (cursor.moveToFirst()) {
                            email = cursor.getString(emailIdx);
                            Log.v(DEBUG_TAG, "Got email: " + email);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }
                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get email data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        EditText emailEntry = (EditText) findViewById(R.id.messageNumber);
                        emailEntry.setText(email);
                        if (email.length() == 0) {
                            Toast.makeText(this, "No email found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    break;
            }

        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }

    // Choose Number
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Cursor cursor = null;
                    String email = "";
                    String number = "";
                    String lastName = "";
                    try {
                        Uri result = data.getData();
                        Log.v(DEBUG_TAG, "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query
                        //cursor = getContentResolver().query(
                        //      ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        //       null,
                        //       ContactsContract.CommonDataKinds.Phone._ID
                        //               + " = ? ", new String[] { id }, null);

                        cursor = getContentResolver().query(Phone.CONTENT_URI,
                        null, Phone.CONTACT_ID + "=?", new String[] { id },
                        null);

                        int numberIdx = cursor.getColumnIndex(Phone.DATA);

                        if (cursor.moveToFirst()) {
                            number = cursor.getString(numberIdx);
                            // lastName =
                            // cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                        } else {
                            // WE FAILED
                        }
                    } catch (Exception e) {
                        // failed
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        } else {
                        }
                    }
                    EditText numberEditText = (EditText) findViewById(R.id.messageNumber);
                    numberEditText.setText(number);
                    // EditText lastNameEditText =
                    // (EditText)findViewById(R.id.last_name);
                    // lastNameEditText.setText(lastName);

                    break;
            }

        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }





    public void ResetCriticalBar(View view) {
        Progress = 0;
        TimerToCall = 0;
        TimerToCall_start = 0;
        ShakeProgress = 0;
        CallProgress = 0;
        ProgressBar ShakeBar1 = (ProgressBar) findViewById(R.id.ShakeBar1);
        ShakeBar1.setProgress(ShakeProgress);
        ProgressBar CallingBar1 = (ProgressBar) findViewById(R.id.CallingBar1);
        CallingBar1.setProgress(CallProgress);
        TextView sensor_output2 = (TextView) findViewById(R.id.textView_sensor_output);
        sensor_output2.setText("Calling?");
        x_max = 0;
        y_max = 0;
        z_max = 0;
        maxspeed = 0;
        accelationSquareRoot_max = 0;
    }

    private void setTimerToCall() {

        // read seekbar for manually set timer
        SeekBar seek_timer_to_call_bar = (SeekBar) findViewById(R.id.seek_timer_to_call);
        TimerToCall_max = seek_timer_to_call_bar.getProgress();

        // increase calling_bar
        ProgressBar calling_bar = (ProgressBar) findViewById(R.id.CallingBar1);
        calling_bar.setMax(TimerToCall_max);

        // show TimerToCall
        TextView show_timer_to_call = (TextView) findViewById(R.id.textView_Calling);
        show_timer_to_call.setText("Call in " + String.valueOf(TimerToCall_max / 10 + 1) + "sec");
    }


    //public void CallAlarm(View view) {
    public void CallAlarm() {
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

    private void getAccelerometer(SensorEvent event) {
        long actualTime = event.timestamp;

        // read x,y,z acceleration values from sensor
        float[] values = event.values;
        float float_x = values[0];
        float float_y = values[1];
        float float_z = values[2];

        //// TODO: 12.02.2017 Landscape Mode, + needs to switch x,y,z to x=y, y=z, z=x
        // x,y,z

        // Prepare for show on Bar
        int x = (int) float_x * 10;
        int y = (int) float_y * 10;
        int z = (int) float_z * 10;

        // Betrag x,y,z
        if (x < 0) {
            x = x * -1;
        }
        if (y < 0) {
            y = y * -1;
        }
        if (z < 0) {
            z = z * -1;
        }

        ProgressBar accX = (ProgressBar) findViewById(R.id.accX);
        accX.setProgress(x);
        ProgressBar accY = (ProgressBar) findViewById(R.id.accY);
        accY.setProgress(y);
        ProgressBar accZ = (ProgressBar) findViewById(R.id.accZ);
        accZ.setProgress(z);

        // got x,y,z max
        if (x > x_max) {
            x_max = x;
        }
        if (y > y_max) {
            y_max = y;
        }
        if (z > z_max) {
            z_max = z;
        }


        //DebugData
        //TextView sensor_output = (TextView) findViewById(R.id.textView_sensor_output);
        //sensor_output.setText(String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z));
        //Toast.makeText(getApplicationContext(), "vlaue is "+accelationSquareRoot, Toast.LENGTH_LONG)
        //        .show();

        // Acceleration Detect
        // per second
        long diffTimeSec = (actualTime - lastUpdate2) / 100000;
        if (diffTimeSec > 10000) {

            setTimerToCall();

            // speed = acceleration
            float_speed = Math.abs((x - xlast) + (y - ylast) + (z - zlast)) / diffTimeSec * 100000;
            int time = (int) diffTimeSec;
            int speed = (int) float_speed / 100;
            int velocity = speed * time;
            distance = velocity * time + (speed * time ^ 2) / 2;
            if (distance < 0) {
                distance = distance * -1;
            }
            if (distance > maxdistance) {
                maxdistance = distance;
            }

            // remember max speed
            if (speed > maxspeed) {
                maxspeed = speed;
            }

            // Shake Detect
            float float_accelationSquareRoot = (x * x + y * y + z * z)
                    / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            int accelationSquareRoot = (int) float_accelationSquareRoot;
            if (accelationSquareRoot > accelationSquareRoot_max) {
                accelationSquareRoot_max = accelationSquareRoot;
            }

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
            if (accelationSquareRoot_max > sensity_shake) {
                view_sensity_shake.setTextColor(Color.RED);
            } else {
                view_sensity_shake.setTextColor(Color.GRAY);
            }
            // speed
            TextView view_sensity_speed = (TextView) findViewById(R.id.seek_view_speed);
            view_sensity_speed.setText("sensity speed: " + String.valueOf(sensity_speed) + "       # " + String.valueOf(maxspeed));
            if (maxspeed > sensity_speed) {
                view_sensity_speed.setTextColor(Color.RED);
            } else {
                view_sensity_speed.setTextColor(Color.GRAY);
            }
            // x
            TextView view_sensity_x = (TextView) findViewById(R.id.seek_view_x);
            view_sensity_x.setText("sensity x: " + String.valueOf(sensity_x) + "       # " + String.valueOf(x_max));
            if (x_max > sensity_x) {
                view_sensity_x.setTextColor(Color.RED);
            } else {
                view_sensity_x.setTextColor(Color.GRAY);
            }
            // y
            TextView view_sensity_y = (TextView) findViewById(R.id.seek_view_y);
            view_sensity_y.setText("sensity y: " + String.valueOf(sensity_y) + "       # " + String.valueOf(y_max));
            if (y_max > sensity_y) {
                view_sensity_y.setTextColor(Color.RED);
            } else {
                view_sensity_y.setTextColor(Color.GRAY);
            }
            // z
            TextView view_sensity_z = (TextView) findViewById(R.id.seek_view_z);
            view_sensity_z.setText("sensity z: " + String.valueOf(sensity_z) + "       # " + String.valueOf(z_max));
            if (z_max > sensity_z) {
                view_sensity_z.setTextColor(Color.RED);
            } else {
                view_sensity_z.setTextColor(Color.GRAY);
            }

            // Alarm Critical Acceleration // Base=140
            if (maxspeed > sensity_speed || x_max > sensity_x || y_max > sensity_y || z_max > sensity_z || accelationSquareRoot_max > sensity_shake) {

                // Test Alarm
                CallAlarm();   // // TODO: 12.02.2017 silent button?

                // switch "hello world" color
                if (color) {
                    view.setBackgroundColor(Color.GREEN);
                } else {
                    view.setBackgroundColor(Color.RED);
                }
                color = !color;

                // trigger to start counter until send a message
                TimerToCall_start = 1;

            }

            // counter/timer until send a message
            if (TimerToCall_start == 1) {
                int TimerToCallS = TimerToCall;
                TimerToCall = TimerToCallS + 10;
                // increase calling_bar
                ProgressBar calling_bar = (ProgressBar) findViewById(R.id.CallingBar1);
                calling_bar.setProgress(TimerToCall);


                // wait 30sec until send out a message (30sec/TimerToCall_max)
                if (TimerToCall >= TimerToCall_max) {
                    // show CALL text
                    TextView sensor_output2 = (TextView) findViewById(R.id.textView_sensor_output);
                    // sensor_output2.setText("CALL!!!!" + String.valueOf(TimerToCall) + "  of:  " + String.valueOf(TimerToCall_max));
                    sensor_output2.setText("CALL!!!!");
                }
            } // counter/timer until send a message

            lastUpdate2 = actualTime;
            xlast = x;
            ylast = y;
            zlast = z;


        /*
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
        */

        } // if all xxx seconds


    } // getAccelerometer(SensorEvent event)

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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


        @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}