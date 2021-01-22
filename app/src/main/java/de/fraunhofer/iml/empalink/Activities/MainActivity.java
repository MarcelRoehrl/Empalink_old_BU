package de.fraunhofer.iml.empalink.Activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;

import de.fraunhofer.iml.empalink.R;
import de.fraunhofer.iml.empalink.Session;
import de.fraunhofer.iml.empalink.V;
import de.fraunhofer.iml.empalink.ConfigurationProfileExceptionHandler;

public class MainActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate {

    private static final String EMPATICA_API_KEY = "bdc9dcd5c8134b1893b9cd34d8a6b15a";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 2;
    private static final int REQUEST_SURVEY = 3;

    private double updated_pulse = 0;

    private Vibrator vibrator;

    private EmpaDeviceManager deviceManager = null;

    private TextView statusLabel, captionLabel, batteryLabel;
    private TextView eda_value, ibi_value, bpm_value, acc_value, temp_value;
    private com.google.android.material.button.MaterialButton showDataButton, backgroundShowDataButton;
    private com.google.android.material.floatingactionbutton.FloatingActionButton surveyFAB;
    private ImageButton recordButton;
    private ImageView connection_icon;
    private com.google.android.material.card.MaterialCardView livedata_card;
    private Session session;
    private boolean recording = false;
    private boolean wasConnected = false;
    private boolean wasReady = false;
    private boolean connected = false;
    private boolean between_updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusLabel = findViewById(R.id.status);
        eda_value = findViewById(R.id.eda_value);
        ibi_value = findViewById(R.id.ibi_value);
        bpm_value = findViewById(R.id.bpm_value);
        acc_value = findViewById(R.id.acc_value);
        temp_value = findViewById(R.id.temp_value);
        batteryLabel = findViewById(R.id.battery);
        captionLabel = findViewById(R.id.caption);
        livedata_card = findViewById(R.id.livedata_card);
        connection_icon = findViewById(R.id.connection_icon);

        recordButton = findViewById(R.id.recordButton);
        surveyFAB = findViewById(R.id.surveyFAB);
        showDataButton = findViewById(R.id.showDataButton);
        backgroundShowDataButton = findViewById(R.id.backgroundShowDataButton);

        vibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);

        Thread.setDefaultUncaughtExceptionHandler(new ConfigurationProfileExceptionHandler(this, MainActivity.class));

        checkPermissions();
        //show(); //TODO nur zum testen, entweder show zum testen oder checkPermissions für die runtime
    }

    private void checkPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ( Build.VERSION.SDK_INT >= 29 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            if (Build.VERSION.SDK_INT < 29)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {
            initEmpaticaDeviceManager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == 0)
        {
            if ( (grantResults.length >= 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) )
            {   // all permissions granted
                show();
                initEmpaticaDeviceManager();
            } else {
                informAndFinish();
            }
        }
        else if(requestCode == 1)
        {
            if( (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) )
            {   // all permissions granted
                show();
                initEmpaticaDeviceManager();
            } else {
                informAndFinish();
            }
        }
        else if(requestCode == 2)
        {
            if ( (grantResults.length >= 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) )
            {   // all permissions granted
                show();
                initEmpaticaDeviceManager();
            } else {
                informAndFinish();
            }
        }
    }

    public void informAndFinish()
    {
        hide();
        showDataButton.setVisibility(View.INVISIBLE);

        final androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("Keine Berechtigung")
                .setMessage("Bitte Berechtigungen erteilen, da sonst eine Verbindung zu der E4 und die Aufzeichnung der Daten nicht möglich ist");

        alertBuilder.setNegativeButton("App beenden", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { finish(); } })
        .setPositiveButton("Berechtigungen erteilen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                checkPermissions();
            }
        }).show();
    }

    public void onShowDataClicked(View view)
    {
        startActivityForResult(new Intent(this, FilechooserActivity.class), V.REQUEST_FILENAME);
    }

    public void onDisconnectClicked(View view)
    {
        if(recording)
            stopAndSaveRecordings();

        if (deviceManager != null) {

            deviceManager.disconnect();
            connected = false;
        }
    }

    public void onRecordClicked(View view)
    {
        vibrate();
        if(!recording)
        {
            Toast.makeText(MainActivity.this, "Aufnahme gestartet", Toast.LENGTH_SHORT).show();
            session = new Session(System.currentTimeMillis(), this);
            session.startWriter();
            recordButton.setBackground(getDrawable(R.drawable.pause));
            recording = true;
            show();
        }
        else
        {
            androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
            alertBuilder.setTitle("Aufnahme speichern")
                    .setMessage("Wollen Sie die Aufnahme wirklich beenden und abspeichern?");

            alertBuilder.setPositiveButton("Aufnahme speichern", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast.makeText(MainActivity.this, "Aufnahme wird gespeicher, bitte warten", Toast.LENGTH_LONG).show();
                    stopAndSaveRecordings();
                    Toast.makeText(MainActivity.this, "Aufnahme beendet und gespeichert", Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("Aufnahme fortsetzen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Die Aufnahme wird fortgeführt", Toast.LENGTH_LONG).show();
                }
            })
            .show();
        }
    }

    private void stopAndSaveRecordings()
    {
        recording = false;
        recordButton.setBackground(getDrawable(R.drawable.play));
        hide();
        show();
        session.closeWriter();
    }

    public void onSurveyClicked(View view)
    {
        vibrate();
        startActivityForResult(new Intent(this, SurveyActivity.class), REQUEST_SURVEY);
        /*androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("Körperliche Anforderungen")
                .setMessage("Wie hoch waren die körperlichen Anforderungen der Aufgabe?");

        final LayoutInflater inflater = this.getLayoutInflater();
        final View ratingSlider = inflater.inflate(R.layout.rating_slider, null);
        alertBuilder.setView(ratingSlider);

        final BubbleSeekBar slider = ratingSlider.findViewById(R.id.slider);
        slider.setThumbColor(ContextCompat.getColor(this, R.color.physical_red));
        slider.setSecondTrackColor(ContextCompat.getColor(this, R.color.physical_red));
        slider.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gering");
                array.put(20, "hoch");

                return array;
            }
        });


        alertBuilder.setPositiveButton("Eintragen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                   session.addPStress(slider.getProgress());
                }
            })
            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();*/
    }

    private void vibrate()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(200);
        }
    }

    private void initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);

            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceManager != null) {
            deviceManager.stopScanning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceManager != null) {
            deviceManager.cleanUp();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (deviceManager != null && wasReady && !connected) {
            deviceManager.startScanning();
        }
    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager.stopScanning();
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);
                updateLabel(captionLabel, "mit " + deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(MainActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void didFailedScanning(int errorCode) {
        //TODO neu aus dem SDK ohne Dokumentation...
    }

    @Override
    public void bluetoothStateChanged() {
        //TODO neu aus dem SDK ohne Dokumentation...
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            updateLabel(statusLabel, "Bitte bluetooth aktivieren und die App neu starten um sich mit einer E4 verbinden zu können");
            //TODO Listener auf bluetooth setzen um beim aktivieren weiter zu suchen
            return;
        }
        else if(requestCode == V.REQUEST_FILENAME && resultCode == RESULT_OK)
        {
            Intent intent = new Intent(this, DataDisplayActivity.class);
            intent.putExtra(V.FILENAME_EXTRA, data.getStringExtra(V.FILENAME_EXTRA));
            String test = data.getStringExtra(V.FILENAME_EXTRA);
            startActivity(intent);
        }
        else if(requestCode == REQUEST_SURVEY && resultCode == RESULT_OK)
        {
            String survey = data.getStringExtra("result");
            session.addSurvey(survey);
            Toast.makeText(MainActivity.this, "Fragebogen abgespeichert", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {

        didUpdateOnWristStatus(status);
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {

        if (status == EmpaStatus.READY)
        {// The device manager is ready for use
            updateLabel(statusLabel, "Bereit");
            updateLabel(captionLabel, "E4 bitte einschalten");
            try {// Start scanning
                deviceManager.startScanning();
                hide();
                wasReady = true;
                // The device manager has established a connection
            } catch (Exception e)
            {//Keine Verbindung, versuche es erneut
                updateLabel(statusLabel, "Keine Internetverbindung");
                initEmpaticaDeviceManager();
            }
        }
        else if (status == EmpaStatus.CONNECTED)
        {
            updateLabel(statusLabel, "Verbunden");
            show();
            wasConnected = true;
            connected = true;
            connection_icon.setImageDrawable(getDrawable(R.drawable.connected));
        }
        else if (status == EmpaStatus.DISCONNECTED)
        {// The device manager disconnected from a device
            connection_icon.setImageDrawable(getDrawable(R.drawable.disconnected));
            if(wasConnected)
            {
                if(recording)
                    stopAndSaveRecordings();
                connected = false;
                updateLabel(statusLabel, "Verbindung getrennt");
                updateLabel(captionLabel, "E4 bitte erneut einschalten");
                deviceManager.startScanning();
            }
            hide();
        }
        else if (status == EmpaStatus.CONNECTING)
        {
            updateLabel(statusLabel, "Verbinde");
        }
    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
        float val = V.calcNormedAcc(x,y,z);
        String vals = ""+Math.round(val*100f)/100f;
        updateLabel(acc_value, vals);

        if(recording)
            session.addAcc(x,y,z,timestamp);
    }

    private String padRight(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < length) {
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        if(recording)
        {
            session.addBVP(bvp, timestamp);

            //Peaks berechnen und speichern
            /*if(updated_pulse == 0)
                updated_pulse = timestamp+V.MED_PULSE_RANGE;
            else if(!between_updated && timestamp >= updated_pulse-(V.MED_PULSE_RANGE/2))
            {
                between_updated = true;
                double temp = updated_pulse-V.MED_PULSE_RANGE-V.MED_PULSE_OVERLAP;
                updateLabel(bpm_value, "" + Math.round(session.getLatestPulse(temp, false)*100f)/100f);
            }
            else if(timestamp >= updated_pulse)
            {
                between_updated = false;
                double temp = updated_pulse-V.MED_PULSE_RANGE-V.MED_PULSE_OVERLAP;
                updated_pulse = timestamp+V.MED_PULSE_RANGE;
                updateLabel(bpm_value, "" + Math.round(session.getLatestPulse(temp, true)*100f)/100f);
            }*/
        }
    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteryLabel.setVisibility(View.VISIBLE);
            }
        });
        updateLabel(batteryLabel, "  Akku: " + String.format("%.0f %%", battery * 100));
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {
        updateLabel(eda_value, "" + Math.round(gsr*1000f)/1000f);
        if(recording)
            session.addEDA(gsr,timestamp);
    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        updateLabel(ibi_value, "" + Math.round(ibi*1000f)/1000f);
        updateLabel(bpm_value, "" + Math.round((60/ibi)*100f)/100f);
        if(recording)
            session.addIBI(ibi,timestamp);
    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {
        updateLabel(temp_value, "" + temp);
        if(recording)
            session.addTemp(temp,timestamp);
    }

    // Update a label with some text, making sure this is run in the UI thread
    private void updateLabel(final TextView label, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

    @Override
    public void didReceiveTag(double timestamp) {

    }

    @Override
    public void didEstablishConnection() {
    }

    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {

//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (status == EmpaSensorStatus.ON_WRIST) {
//
//                    ((TextView) findViewById(R.id.wrist_status_label)).setText("ON WRIST");
//                }
//                else {
//
//                    ((TextView) findViewById(R.id.wrist_status_label)).setText("NOT ON WRIST");
//                }
//            }
//        });
    }

    void show() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                backgroundShowDataButton.setVisibility(View.GONE);
                showDataButton.setVisibility(View.VISIBLE);
                livedata_card.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.VISIBLE);
                if(recording) {
                    surveyFAB.show();
                }
            }
        });
    }

    void hide() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                backgroundShowDataButton.setVisibility(View.VISIBLE);
                livedata_card.setVisibility(View.GONE);
                recordButton.setVisibility(View.GONE);
                surveyFAB.hide();
                updateLabel(batteryLabel,null);
            }
        });
    }
}
