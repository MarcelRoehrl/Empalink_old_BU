package de.fraunhofer.iml.empalink.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
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

        File directory = new File(getApplicationContext().getResources().getString(R.string.path));
        File[] files = directory.listFiles();

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
