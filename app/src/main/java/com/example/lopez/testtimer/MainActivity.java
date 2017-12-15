package com.example.lopez.testtimer;
//change for github

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private TextView messageTextView;
    private Timer timer;
    private SharedPreferences savedValues;
    private long startMills;
    private long eMills;
    private int countDownload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = (TextView) findViewById(R.id.messageTextView);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
        savedValues.edit().clear().commit();

        startTimer();
    }

    private void startTimer() {
        onResume();
        startMills = System.currentTimeMillis() - eMills;
        timer = new Timer(true);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                eMills = System.currentTimeMillis() - startMills;

                if(((int)eMills/1000)%10 == 1){
                    if(((int)eMills/1000) == 1) {
                        countDownload = 0;
                    } else {
                        download();
                        countDownload++;
                    }
                }
                updateView(eMills, countDownload);
            }
        };

        timer.schedule(task, 0, 1000);

    }

    private void updateView(final long elapsedMillis, final int downloadCount) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis/1000;

            @Override
            public void run() {
                messageTextView.setText("Seconds: " + elapsedSeconds);

            }
        });
    }

    public void stopTimer(View view) {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void startTime(View view) {
        onPause();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Editor editor = savedValues.edit();
        editor.putLong("startMills", startMills);
        editor.putLong("eMills", eMills);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMills = savedValues.getLong("startMills", System.currentTimeMillis());
        eMills = savedValues.getLong("eMills", 0);

    }
    public void download() {
        try{
            // get the URL
            URL url = new URL("http://rss.cnn.com/rss/cnn_tech.rss");

            // get the input stream
            InputStream in = url.openStream();

            // get the output stream
            FileOutputStream out = openFileOutput("news_feed.xml", MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        }
        catch (IOException e) {
            Log.e("News reader", e.toString());
        }
    }
}