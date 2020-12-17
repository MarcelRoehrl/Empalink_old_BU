package de.fraunhofer.iml.empalink.Activities;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xw.repo.BubbleSeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.SparseArray;
import android.view.View;

import de.fraunhofer.iml.empalink.R;

public class SurveyActivity extends AppCompatActivity
{
    private BubbleSeekBar slider1, slider2, slider3, slider4, slider5, slider6, slider7;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("Fragebogen");

        FloatingActionButton closeFAB = findViewById(R.id.closeFAB);
        closeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

    @Override
    public void onBackPressed() {
        finish();
    }
}