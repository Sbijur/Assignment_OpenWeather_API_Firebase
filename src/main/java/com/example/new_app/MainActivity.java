package com.example.new_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.*;

public class MainActivity extends AppCompatActivity {

    private TextView weatherTextView;
    private OkHttpClient client;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        weatherTextView = findViewById(R.id.Info);

        client = new OkHttpClient();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        Button b=findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchWeatherData();
            }
        });
    }

    private void fetchWeatherData() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                TextView t=findViewById(R.id.city);

                String url="https://api.openweathermap.org/data/2.5/weather?appid=3db243a99d95c2fb26f2ef85d09a7a5f&q="+t.getText().toString().toLowerCase();

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        final String jsonData = response.body().string();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                weatherTextView.setText(jsonData);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                weatherTextView.setText("Request failed: " + response.message());
                            }
                        });
                    }
                } catch (IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherTextView.setText("Error: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

}