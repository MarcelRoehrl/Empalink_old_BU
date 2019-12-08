package de.fraunhofer.iml.empalink.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class MainActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate {

    private static final String EMPATICA_API_KEY = "bdc9dcd5c8134b1893b9cd34d8a6b15a";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;


    private EmpaDeviceManager deviceManager = null;
    private TextView accel_xLabel;
    private TextView accel_yLabel;
    private TextView accel_zLabel;
    private TextView bvpLabel;
    private TextView edaLabel;
    private TextView ibiLabel;
    private TextView temperatureLabel;
    private TextView batteryLabel;
    private TextView statusLabel;
    private TextView deviceNameLabel;
    private LinearLayout dataCnt;
    private com.google.android.material.button.MaterialButton disconnectButton, recordButton, pStressButton, mStressButton;

    private Session session;
    private boolean recording = false;
    private boolean wasConnected = false;
    private boolean wasReady = false;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize vars that reference UI components
        statusLabel = (TextView) findViewById(R.id.status);
        dataCnt = (LinearLayout) findViewById(R.id.dataArea);
        accel_xLabel = (TextView) findViewById(R.id.accel_x);
        accel_yLabel = (TextView) findViewById(R.id.accel_y);
        accel_zLabel = (TextView) findViewById(R.id.accel_z);
        bvpLabel = (TextView) findViewById(R.id.bvp);
        edaLabel = (TextView) findViewById(R.id.eda);
        ibiLabel = (TextView) findViewById(R.id.ibi);
        temperatureLabel = (TextView) findViewById(R.id.temperature);
        batteryLabel = (TextView) findViewById(R.id.battery);
        deviceNameLabel = (TextView) findViewById(R.id.deviceName);

        disconnectButton = findViewById(R.id.disconnectButton);
        recordButton = findViewById(R.id.recordButton);
        pStressButton = findViewById(R.id.pStressButton);
        mStressButton = findViewById(R.id.mStressButton);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else
        {
            initEmpaticaDeviceManager();
        }
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
        if(!recording)
        {
            session = new Session(System.currentTimeMillis(), this);
            recordButton.setText("Aufnahme speichern");
            recording = true;
            show();
        }
        else
            stopAndSaveRecordings();
    }

    private void stopAndSaveRecordings()
    {
        recording = false;
        recordButton.setText("Aufnahme starten");
        hide();
        show();
        session.save();
    }

    public void onPStressClicked(View view)
    {
        final androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("Physischer Stress")
                .setMessage("Geben Sie bitte an wie sehr Sie gestresst sind.");

        final LayoutInflater inflater = this.getLayoutInflater();
        final View ratingBar = inflater.inflate(R.layout.rating_bar, null);
        alertBuilder.setView(ratingBar);

        alertBuilder.setPositiveButton("Eintragen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                session.addPStress((int)((RatingBar)ratingBar.findViewById(R.id.ratingBar)).getRating(), session.getLatestTimestamp());
            }
        })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void onMStressClicked(View view)
    {
        final androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("Mentaler Stress")
                .setMessage("Geben Sie bitte an wie sehr Sie gestresst sind.");

        final LayoutInflater inflater = this.getLayoutInflater();
        final View ratingBar = inflater.inflate(R.layout.rating_bar, null);
        alertBuilder.setView(ratingBar);

        alertBuilder.setPositiveButton("Eintragen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                session.addMStress((int)((RatingBar)ratingBar.findViewById(R.id.ratingBar)).getRating(), session.getLatestTimestamp());
            }
        })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == 0)
        {
            if ( (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                // all permissions granted
                initEmpaticaDeviceManager();
            } else {
                finish();
            }
        }
        if(requestCode == 1)
        {
            if( (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) )
            {
                // all permissions granted
                initEmpaticaDeviceManager();
            } else {
                finish();
            }
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
                updateLabel(deviceNameLabel, "To: " + deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(MainActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
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
            // You should deal with this
            return;
        }
        else if(requestCode == V.REQUEST_FILENAME && resultCode == RESULT_OK)
        {
            Intent intent = new Intent(this, DataDisplayActivity.class);
            intent.putExtra(V.FILENAME_EXTRA, data.getStringExtra(V.FILENAME_EXTRA));
            String test = data.getStringExtra(V.FILENAME_EXTRA);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {

        didUpdateOnWristStatus(status);
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        // Update the UI
        updateLabel(statusLabel, status.name());

        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            // Start scanning
            try {
                deviceManager.startScanning();
                hide();
                wasReady = true;
                // The device manager has established a connection
            } catch (Exception e)
            {
                updateLabel(statusLabel, "No internet connection");
                //Keine Verbindung, versuche es erneut
                initEmpaticaDeviceManager();
            }
        } else if (status == EmpaStatus.CONNECTED) {
            show();
            wasConnected = true;
            connected = true;
            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {
            updateLabel(deviceNameLabel, "");
            if(wasConnected)
            {
                connected = false;
                updateLabel(statusLabel, status.name() + " - Turn on your device");
                deviceManager.startScanning();
            }
            hide();
        }
    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
        updateLabel(accel_xLabel, "" + x);
        updateLabel(accel_yLabel, "" + y);
        updateLabel(accel_zLabel, "" + z);

        if(recording)
            session.addAcc(x,y,z,timestamp);
    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        updateLabel(bvpLabel, "" + bvp);
        if(recording)
            session.addBVP(bvp,timestamp);
    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        updateLabel(batteryLabel, String.format("%.0f %%", battery * 100));
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {
        updateLabel(edaLabel, "" + gsr);
        if(recording)
            session.addEDA(gsr,timestamp);
    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        updateLabel(ibiLabel, "" + ibi);
        if(recording)
            session.addIBI(ibi,timestamp);
    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {
        updateLabel(temperatureLabel, "" + temp);
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

        show();
    }

    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (status == EmpaSensorStatus.ON_WRIST) {

                    ((TextView) findViewById(R.id.wrist_status_label)).setText("ON WRIST");
                }
                else {

                    ((TextView) findViewById(R.id.wrist_status_label)).setText("NOT ON WRIST");
                }
            }
        });
    }

    void show() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dataCnt.setVisibility(View.VISIBLE);
                disconnectButton.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.VISIBLE);
                if(recording) {
                    pStressButton.setVisibility(View.VISIBLE);
                    mStressButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void hide() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dataCnt.setVisibility(View.GONE);
                disconnectButton.setVisibility(View.GONE);
                recordButton.setVisibility(View.GONE);
                pStressButton.setVisibility(View.GONE);
                mStressButton.setVisibility(View.GONE);
            }
        });
    }
}
