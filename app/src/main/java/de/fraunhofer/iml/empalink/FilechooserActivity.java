package de.fraunhofer.iml.empalink;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class FilechooserActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filechooser);

        ArrayList<String> stringlist = new ArrayList<String>();

        File directory = new File(getApplicationContext().getResources().getString(R.string.path));
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            stringlist.add(files[i].getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stringlist);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        Intent intent = new Intent();
        intent.putExtra(V.FILENAME_EXTRA, (String)l.getItemAtPosition(position));
        setResult(RESULT_OK, intent);
        finish();
    }
}
