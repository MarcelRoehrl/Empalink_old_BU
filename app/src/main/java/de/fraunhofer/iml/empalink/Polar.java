package de.fraunhofer.iml.empalink;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.reactivestreams.Publisher;

import java.util.Set;
import java.util.UUID;

import de.fraunhofer.iml.empalink.Activities.MainActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarEcgData;
import polar.com.sdk.api.model.PolarHrData;
import polar.com.sdk.api.model.PolarOhrData;
import polar.com.sdk.api.model.PolarSensorSetting;

import static polar.com.sdk.api.model.PolarOhrData.OHR_DATA_TYPE.PPG3_AMBIENT1;

public class Polar
{
    private static final String TAG = "Polar";
    private Context context;
    private Activity activity;
    private PolarBleApi api;
    private Disposable scanDisposable;
    private String DEVICE_ID = "";
    private TextView statusLabel, captionLabel, batteryLabel;

    private Disposable ecgDisposable;

    public Polar(Context context, Activity activity, TextView statusLabel, TextView captionLabel, TextView batteryLabel)
    {
        this.context = context;
        this.activity = activity;
        this.statusLabel = statusLabel;
        this.captionLabel = captionLabel;
        this.batteryLabel = batteryLabel;

        api = PolarBleApiDefaultImpl.defaultImplementation(context, PolarBleApi.ALL_FEATURES);

        api.setPolarFilter(true);

        api.setApiLogger(s -> Log.d("API Logger", s));

        api.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void blePowerStateChanged(boolean powered) {
                Log.d("MyApp", "BLE power: " + powered);
            }

            @Override
            public void deviceConnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d("MyApp", "CONNECTED: " + polarDeviceInfo.deviceId);
                DEVICE_ID = polarDeviceInfo.deviceId;

                updateLabel(statusLabel, "Verbunden");
            }

            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {
                Log.d("MyApp", "CONNECTING: " + polarDeviceInfo.deviceId);
                DEVICE_ID = polarDeviceInfo.deviceId;

                updateLabel(statusLabel, "Verbinde");
                updateLabel(captionLabel, "mit " + DEVICE_ID);
            }

            @Override
            public void deviceDisconnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d("MyApp", "DISCONNECTED: " + polarDeviceInfo.deviceId);

                updateLabel(statusLabel, "Keine Verbindung");
                updateLabel(captionLabel, "suche " + DEVICE_ID);

                ecgDisposable = null;

                try {
                    api.connectToDevice(DEVICE_ID);
                } catch (PolarInvalidArgument polarInvalidArgument) {
                    polarInvalidArgument.printStackTrace();
                }
            }

            @Override
            public void streamingFeaturesReady(@NonNull final String identifier,
                                               @NonNull final Set<PolarBleApi.DeviceStreamingFeature> features) {
                for (PolarBleApi.DeviceStreamingFeature feature : features) {
                    Log.d("MyApp", "Streaming feature " + feature.toString() + " is ready");

                    if(feature.name().equals("ECG"))
                    {
                        if (ecgDisposable == null) {
                            ecgDisposable = api.requestStreamSettings(DEVICE_ID, PolarBleApi.DeviceStreamingFeature.ECG)
                                    .toFlowable()
                                    .flatMap((Function<PolarSensorSetting, Publisher<PolarEcgData>>) polarEcgSettings -> {
                                        PolarSensorSetting sensorSetting = polarEcgSettings.maxSettings();
                                        return api.startEcgStreaming(DEVICE_ID, sensorSetting);
                                    }).subscribe(
                                            polarEcgData -> {
                                                for (Integer microVolts : polarEcgData.samples) {
                                                    Log.d(TAG, "    yV: " + microVolts);
                                                }
                                            },
                                            throwable -> Log.e(TAG, "" + throwable),
                                            () -> Log.d(TAG, "complete")
                                    );
                        } else {
                            // NOTE stops streaming if it is "running"
                            ecgDisposable.dispose();
                            ecgDisposable = null;
                        }
                    }
                }
            }

            @Override
            public void hrFeatureReady(String identifier) {
            }

            @Override
            public void disInformationReceived(String identifier, UUID uuid, String value) {
            }

            @Override
            public void batteryLevelReceived(String identifier, int level) {
                batteryLabel.setText("  Akku: "+ level);
            }

            @Override
            public void hrNotificationReceived(String identifier, PolarHrData data) {
            }

            @Override
            public void polarFtpFeatureReady(String s) {
            }
        });
    }

    public void startScanning()
    {
        if (scanDisposable == null)
        {
            scanDisposable = api.searchForDevice().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            polarDeviceInfo -> {
                                if (polarDeviceInfo.isConnectable)
                                {
                                    DEVICE_ID = polarDeviceInfo.deviceId;
                                    try {
                                        api.connectToDevice(DEVICE_ID);
                                    } catch (PolarInvalidArgument polarInvalidArgument) {
                                        polarInvalidArgument.printStackTrace();
                                    }
                                    Log.d(TAG, "polar device found id: " + polarDeviceInfo.deviceId + " address: " + polarDeviceInfo.address + " rssi: " + polarDeviceInfo.rssi + " name: " + polarDeviceInfo.name + " isConnectable: " + polarDeviceInfo.isConnectable);
                                }
                            });
        }
    }

    public void stopScanning()
    {
        if(scanDisposable != null)
        {
            scanDisposable.dispose();
            scanDisposable = null;
        }
    }

    public void disconnect()
    {
        try {
            api.disconnectFromDevice(DEVICE_ID);
        } catch (PolarInvalidArgument polarInvalidArgument) {
            polarInvalidArgument.printStackTrace();
        }
    }

    public void onPause() {
       api.backgroundEntered();
    }

    public void onResume() {
       api.foregroundEntered();
    }

    public void onDestroy() {
       api.shutDown();
    }

    public void updateLabel(final TextView label, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

}
