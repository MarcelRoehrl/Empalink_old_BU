package de.fraunhofer.iml.empalink.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xw.repo.BubbleSeekBar;

import de.fraunhofer.iml.empalink.R;
import de.fraunhofer.iml.empalink.V;

public class SurveyActivity extends AppCompatActivity
{
    private BubbleSeekBar slider1, slider2, slider3, slider4, slider5, slider6, slider7;
    private double startStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        setActionbarTitle("1");

        startStamp = getIntent().getDoubleExtra(V.TIMESTAMP_EXTRA, System.currentTimeMillis());

        slider1 = findViewById(R.id.slider1);
        slider2 = findViewById(R.id.slider2);
        slider3 = findViewById(R.id.slider3);
        slider4 = findViewById(R.id.slider4);
        slider5 = findViewById(R.id.slider5);
        slider6 = findViewById(R.id.slider6);
        slider7 = findViewById(R.id.slider7);

        slider1.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "sehr einfach");
                array.put(20, "sehr schwierig");
                return array;
            }
        });

        slider2.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gering");
                array.put(20, "hoch");
                return array;
            }
        });

        slider3.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gering");
                array.put(20, "hoch");
                return array;
            }
        });

        slider4.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gering");
                array.put(20, "hoch");
                return array;
            }
        });

        slider5.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gut");
                array.put(20, "schlecht");
                return array;
            }
        });

        slider6.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gering");
                array.put(20, "hoch");
                return array;
            }
        });

        slider7.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "gering");
                array.put(20, "hoch");
                return array;
            }
        });
    }

    private void setActionbarTitle(String s) {
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Fragebogen (Seite "+s+"/5)" + "</font>"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onSubmit(View view)
    {
        String survey = startStamp + "";
        int id;

        survey += ";" + slider1.getProgress() + ";" + slider2.getProgress() + ";" + slider3.getProgress() + ";" + slider4.getProgress() + ";" + slider5.getProgress() + ";" + slider6.getProgress() + ";" + slider7.getProgress();

        id = ((RadioGroup)findViewById( R.id.rg1 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg2 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg3 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg4 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg5 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg6 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg7 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg8 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg9 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg10 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg11 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg12 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg13 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg14 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg15 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg16 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg17 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg18 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg19 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rg20 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());

        id = ((RadioGroup)findViewById( R.id.rgi1 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rgi2 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rgi3 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rgi4 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rgi5 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rgi6 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        id = ((RadioGroup)findViewById( R.id.rgi7 )).getCheckedRadioButtonId();
        survey += ";" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());

        id = ((RadioGroup)findViewById( R.id.rgo )).getCheckedRadioButtonId();
        String oAns = "" + (id == -1 ? -1 : ((RadioButton)findViewById(id)).getText().toString());
        String oAnsNumber = "-1";
        if(oAns.equals(getResources().getString(R.string.o1)))
            oAnsNumber = "0";
        else if(oAns.equals(getResources().getString(R.string.o2)))
            oAnsNumber = "1";
        else if(oAns.equals(getResources().getString(R.string.o3)))
            oAnsNumber = "2";
        else if(oAns.equals(getResources().getString(R.string.o4)))
            oAnsNumber = "3";
        else if(oAns.equals(getResources().getString(R.string.o5)))
            oAnsNumber = "4";
        else if(oAns.equals(getResources().getString(R.string.o6)))
            oAnsNumber = "5";
        else if(oAns.equals(getResources().getString(R.string.o7)))
            oAnsNumber = "6";

        survey += ";" + oAnsNumber;

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", survey);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onPage1(View view) {
        findViewById(R.id.page1).setVisibility(View.GONE);
        setActionbarTitle("2");
        findViewById(R.id.page2).setVisibility(View.VISIBLE);
    }

    public void onPage2(View view) {
        findViewById(R.id.page2).setVisibility(View.GONE);
        setActionbarTitle("3");
        findViewById(R.id.page3).setVisibility(View.VISIBLE);
    }

    public void onPage3(View view) {
        findViewById(R.id.page3).setVisibility(View.GONE);
        setActionbarTitle("4");
        findViewById(R.id.page4).setVisibility(View.VISIBLE);
    }

    public void onPage4(View view) {
        findViewById(R.id.page4).setVisibility(View.GONE);
        setActionbarTitle("5");
        findViewById(R.id.page5).setVisibility(View.VISIBLE);
    }
}