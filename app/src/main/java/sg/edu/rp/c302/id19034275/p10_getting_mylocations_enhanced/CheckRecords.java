package sg.edu.rp.c302.id19034275.p10_getting_mylocations_enhanced;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class CheckRecords extends AppCompatActivity {
    ArrayAdapter aa,aaFav;
    ArrayList<String> al,alFav;
    Button btnRefresh,btnFav;
    ListView lv;
    TextView tv;
    String folderLocation;
    ActionBar ab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_records);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnFav = findViewById(R.id.btnFavourite);
        tv = findViewById(R.id.tv);
        lv = findViewById(R.id.lv);
        al = new ArrayList<>();
        alFav = new ArrayList<>();
        tv.setText("Number of records: " + al.size());
        aa = new ArrayAdapter(CheckRecords.this, android.R.layout.simple_list_item_1,al);
        aaFav = new ArrayAdapter(CheckRecords.this, android.R.layout.simple_list_item_1,alFav);

        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.clear();
                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
                File targetFile = new File(folderLocation,"data2.txt");
                if (targetFile.exists() == true){
                    String data = "";
                    try{
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while(line != null){
                            data += line +"\n";
                            al.add(line);
                            line = br.readLine();

                        }
                        br.close();
                        reader.close();
                    }catch (Exception e){
                        Toast.makeText(CheckRecords.this,"Failed to read!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Log.d("Content",data);
                    tv.setText("Number of records: " + al.size());
                    aa.notifyDataSetChanged();
                    lv.setAdapter(aa);
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(CheckRecords.this);
                dialog.setMessage("Add this location in your favourite list?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int x) {
                        try {
                            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
                            File targetFile = new File(folderLocation, "favorites.txt");
                            FileWriter writer = new FileWriter(targetFile, true);
                            writer.write(al.get(i) + "\n");
                            writer.flush();
                            writer.close();
                        } catch (Exception e) {
                            Log.d("folder",folderLocation.toString());
                            Toast.makeText(CheckRecords.this, "Failed to write!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alFav.clear();
                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
                File targetFile = new File(folderLocation,"favorites.txt");
                if (targetFile.exists() == true){
                    String data = "";
                    try{
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while(line != null){
                            data += line +"\n";
                            alFav.add(line);
                            line = br.readLine();

                        }
                        br.close();
                        reader.close();
                    }catch (Exception e){
                        Toast.makeText(CheckRecords.this,"Failed to read!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Log.d("Content",data);
                    tv.setText("Number of records: " + alFav.size());
                    aaFav.notifyDataSetChanged();
                    lv.setAdapter(aaFav);
                }
            }
        });

    }
}