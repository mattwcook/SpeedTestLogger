package com.example.speedtestlogger;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class MainActivity extends AppCompatActivity {
    Button startStop;
    Button clearButton;
    Button sendButton;
    TextView displayText;

    Boolean running = false;
    String filename;
    FileWriter logFile;
    File filePath;
    File fileDir;
    String fileHeading = "Time,Download,Upload\r";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startStop = (Button)findViewById(R.id.StartStopButton);
        displayText = (TextView)findViewById(R.id.Display);
        clearButton = (Button)findViewById(R.id.clear);
        sendButton = (Button)findViewById(R.id.sendButton);

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        clearButton.setText("Clear History");
        sendButton.setText("Share");

        filename = "LogData.csv";
        filePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Documents/SpeedLog/" + filename);
        fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Documents/SpeedLog");
        if (filePath.exists()) {
            try {
                logFile = new FileWriter(fileDir.toString() + "/" + filename, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SetButtons();

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void StartStop(View view) throws IOException {
        running = !running;
        SetButtons();
        if (running == true){

            if (fileDir.exists() == false){
                fileDir.mkdirs();
                Clear();
            }

            String message = "Bleeble,box\r";
            logFile.append(message);

            logFile.flush();
            logFile.close();


        }

        /*
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        float download = nc.getLinkDownstreamBandwidthKbps() / 1000;
        float upload = nc.getLinkDownstreamBandwidthKbps() / 1000;
        String resultText = "Download: " + String.valueOf(download) + '\r';
        resultText += "Upload: " + String.valueOf(upload);
        displayText.setText(resultText);

         */
    }
    public void OnClear(View view) throws IOException {
        Clear();
    }
    void Clear()throws IOException {
        logFile = new FileWriter(fileDir.toString() + "/" + filename);
        logFile.write(fileHeading);
        logFile.flush();
        logFile.close();
        SetButtons();
    }
    Boolean IsEmpty(File path) {
        if(path.exists() == false){
            return true;
        }
        try {
            CSVReader reader = new CSVReader(new FileReader(path));
            try {
                if (reader.readAll().size() > 1){
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }
    void SetButtons() {
        if (running == false) {
            if (IsEmpty(filePath)) {
                startStop.setText("Start");
                sendButton.setEnabled(false);
                clearButton.setEnabled(false);
            } else {
                startStop.setText("Append");
                sendButton.setEnabled(true);
                clearButton.setEnabled(true);
            }
        }
        else{
            startStop.setText("Stop");
            sendButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }
    public void Send(View view){

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("application/csv");
        displayText.setText(logFile.toString());
        //intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(logFile.toString()));
        //startActivity(Intent.createChooser(intentShare,"Share Data Log"));



    }
    String GetFileName(){
        String currentTime = String.valueOf(LocalTime.now());
        String name = "Speed Log " + LocalDate.now() + '_' +
        currentTime.substring(0,2) + '-' + currentTime.substring(3,5) + '-' +
        currentTime.substring(6,8) + '-' + currentTime.substring(9,12) + ".csv";
        displayText.setText(filename);
        return name;
    }
}