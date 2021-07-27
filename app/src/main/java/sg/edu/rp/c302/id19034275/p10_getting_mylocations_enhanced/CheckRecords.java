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
    Button btnRefresh, btnFavorites;
    TextView tvRecords;
    ListView lv;
    ArrayAdapter aa;
    ArrayList<String> al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_records);

        btnRefresh = findViewById(R.id.btnRefresh);
        tvRecords = findViewById(R.id.tvNumofRecords);
        btnFavorites = findViewById(R.id.btnFavourite);
        lv = findViewById(R.id.lv);

        al = new ArrayList<>();

        // Read file from P10LocationData2.txt to Activity
        String folder = getFilesDir().getAbsolutePath() + "/P10";
        File file = new File(folder, "P10LocationData.txt");
        if (file.exists() == true) {
            int numberOfRecords = 0;
            try {
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);

                String line = br.readLine();
                while (line != null) {
                    al.add(line);
                    line = br.readLine();
                    numberOfRecords += 1;
                }
                aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
                lv.setAdapter(aa);
                tvRecords.setText("Number of records: " + numberOfRecords);
                br.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    al.clear();
                    File file = new File(folder, "P10LocationData.txt");
                    if (file.exists() == true) {
                        int numberOfRecords = 0;
                        try {
                            FileReader reader = new FileReader(file);
                            BufferedReader br = new BufferedReader(reader);

                            String line = br.readLine();
                            while (line != null) {
                                al.add(line);
                                line = br.readLine();
                                numberOfRecords += 1;
                            }
                            aa = new ArrayAdapter(CheckRecords.this, android.R.layout.simple_list_item_1, al);
                            lv.setAdapter(aa);
                            tvRecords.setText("Number of records: " + numberOfRecords);
                            br.close();
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedData = al.get(i);
                    Toast.makeText(CheckRecords.this, al.get(i), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckRecords.this);
                    builder.setMessage("Add this location in your favourite list?");
                    builder.setNegativeButton("No", null);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                String folderLocation_II = getFilesDir().getAbsolutePath() + "/P10";
                                ;
                                File targetFile_I = new File(folderLocation_II, "favorites.txt");
                                FileWriter write_I = new FileWriter(targetFile_I, true);
                                write_I.write(selectedData + "\n");
                                write_I.flush();
                                write_I.close();
                            } catch (Exception e) {
                                Toast.makeText(CheckRecords.this, "Failed to write!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.show();
                }
            });

            btnFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aa.clear();
                    //Read the file from favorites.txt
                    File targetFile = new File(folder, "favorites.txt");
                    if (targetFile.exists() == true) {
                        try {
                            FileReader reader = new FileReader(targetFile);
                            BufferedReader br = new BufferedReader(reader);
                            //While it returns something where content is present,
                            // read line until end
                            al.clear();
                            String line = br.readLine();
                            while (line != null) {
                                al.add(line + "\n");
                                line = br.readLine();
                            }
                            tvRecords.setText("Number of records: " + al.size());
                            br.close();
                            reader.close();
                        } catch (Exception e) {
                            Toast.makeText(CheckRecords.this, "Failed to read!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        aa.notifyDataSetChanged();
                    }
                }
            });

        }

    }
}