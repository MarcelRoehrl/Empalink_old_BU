package de.fraunhofer.iml.empalink.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import de.fraunhofer.iml.empalink.R;
import de.fraunhofer.iml.empalink.V;

public class FilechooserActivity extends ListActivity
{
    private final String no_recordings = "Es wurde noch keine Aufzeichnung gefunden";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filechooser);

        ArrayList<String> stringlist = new ArrayList<String>();

        String temppath;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            temppath = getApplicationContext().getFilesDir().getPath();
        else
            temppath = getApplicationContext().getResources().getString(R.string.path);

        File directory = new File(temppath);
        FileFilter csvFilter = new FileFilter() {
            public boolean accept(File file) {
                //if the file extension is .csv return true, else false
                if (file.getName().endsWith(".csv")) {
                    return true;
                }
                return false;
            }
        };
        File[] files = directory.listFiles(csvFilter);

        if(files == null  || files.length == 0)
            stringlist.add(no_recordings);
        else {
            for (int i = 0; i < files.length; i++) {
                stringlist.add(files[i].getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stringlist);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        String selection = (String)l.getItemAtPosition(position);
        if(!selection.equals(no_recordings))
        {
            Intent intent = new Intent();
            intent.putExtra(V.FILENAME_EXTRA, selection);
            setResult(RESULT_OK, intent);
            finish();
         }
        else
            onBackPressed();
    }
}
