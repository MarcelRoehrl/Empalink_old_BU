package de.fraunhofer.iml.empalink.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import de.fraunhofer.iml.empalink.R;

public class SubjectInfoActivity extends AppCompatActivity {

    private NumberPicker agepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);

        agepicker = findViewById(R.id.age);
        agepicker.setMinValue(16);
        agepicker.setMaxValue(80);
        agepicker.setValue(30);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Weitere Angaben" + "</font>"));
    }

    public void startRecording(View view) {
        String info = "";

        int id = ((RadioGroup)findViewById( R.id.sis )).getCheckedRadioButtonId();
        String sAns = "" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        String sAnsNumber = "-1";
        if(sAns.equals(getResources().getString(R.string.sis1)))
            sAnsNumber = "0";
        else if(sAns.equals(getResources().getString(R.string.sis2)))
            sAnsNumber = "1";
        info += sAnsNumber + ";";

        info += agepicker.getValue()+";";

        id = ((RadioGroup)findViewById( R.id.sim )).getCheckedRadioButtonId();
        String mAns = "" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        String mAnsNumber = "-1";
        if(mAns.equals(getResources().getString(R.string.sim1)))
            mAnsNumber = "0";
        else if(mAns.equals(getResources().getString(R.string.sim2)))
            mAnsNumber = "1";
        else if(mAns.equals(getResources().getString(R.string.sim3)))
            mAnsNumber = "2";
        else if(mAns.equals(getResources().getString(R.string.sim4)))
            mAnsNumber = "3";
        info += mAnsNumber;

        if(sAnsNumber.equals("-1") || mAnsNumber.equals("-1"))
        {
            Toast.makeText(this, "Bitte wählen Sie bei allen Fragen eine Antwortmöglichkeit aus", Toast.LENGTH_LONG).show();
        }
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", info);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Aufnahme wurde abgebrochen", Toast.LENGTH_LONG).show();
        finish();
    }
}