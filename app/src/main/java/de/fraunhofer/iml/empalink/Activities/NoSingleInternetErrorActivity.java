package de.fraunhofer.iml.empalink.Activities;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import de.fraunhofer.iml.empalink.R;

public class NoSingleInternetErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_single_internet_error);

        final androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("Keine Internetverbindung")
                .setMessage("Um diese App zu nutzen wird eine einmalige Internetverbindung benötigt, danach kann die App auch offline genutzt werden. Bitte starten Sie die App mit einer bestehenden Verbindung.");

        alertBuilder.setPositiveButton("App beenden", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        }).show();
    }
}
