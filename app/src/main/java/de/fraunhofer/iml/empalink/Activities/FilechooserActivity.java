package de.fraunhofer.iml.empalink.Activities;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import de.fraunhofer.iml.empalink.R;
import de.fraunhofer.iml.empalink.V;

public class FilechooserActivity extends ListActivity
{
    private final String no_recordings = "Es wurde keine Aufnahme gefunden";
    private String filename;
    private ArrayAdapter<String> adapter;

    private static final int REQUEST_CREATE_CSV = 79;

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

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stringlist);
        setListAdapter(adapter);
    }

    public void showMenu (View view)
    {
        PopupMenu menu = new PopupMenu (this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            menu.setGravity(Gravity.RIGHT);
        }
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                Intent intent;
                switch (id)
                {
                    case R.id.item_show:
                        intent = new Intent();
                        intent.putExtra(V.FILENAME_EXTRA, filename);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case R.id.item_move:
                        intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.putExtra(Intent.EXTRA_TITLE, filename);
                        intent.setType("text/csv");
                        startActivityForResult(intent, REQUEST_CREATE_CSV);
                        break;
                    case R.id.item_delete:
                        File fdelete = new File(getPath());
                        if (fdelete.delete()) {
                            Toast.makeText(getApplicationContext(), filename+ " wurde gelöscht", Toast.LENGTH_LONG).show();
                            adapter.remove(filename);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Aufnahme konnte nicht gelöscht werden", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.menu_list);
        menu.show();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        filename = (String)l.getItemAtPosition(position);

        if(!filename.equals(no_recordings))
            showMenu(v);
        else
            onBackPressed();
    }

    private String getPath()
    {
        String path;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            path = getApplicationContext().getFilesDir().getPath();
        } else {
            path = getResources().getString(R.string.path);
        }
        return path + File.separator + filename;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CREATE_CSV && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();

            String inPath = getPath();

            try {//Datei kopieren
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                BufferedReader bufferedReader = new BufferedReader(new FileReader(inPath));

                String line = bufferedReader.readLine();
                while(line != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    line = bufferedReader.readLine();
                }
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Aufnahme wurde verschoben", Toast.LENGTH_LONG).show();
        }
    }
}
