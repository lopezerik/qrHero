package com.example.cis433group.qrhero;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UnshortenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unshorten);

        Intent intent = getIntent();
        final String link = intent.getStringExtra("link");

        final TextView oldLink = findViewById(R.id.link_name);
        final TextView newLink = findViewById(R.id.new_link_name);

        oldLink.setText(link);

        Button unshorten = findViewById(R.id.unshorten_button);

        unshorten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLink.setText("unshortened link here");

                try{
                    URL url = new URL(link);
                    new MyTask().execute(url);


                } catch (MalformedURLException me){
                    System.out.println("new URL failed");

                }
            }
        });
    }

    private class MyTask extends AsyncTask<URL, Void, String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            try{

                return getFinalURL(url.toString());
            } catch (IOException ie){
                System.out.print("io exc");
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            TextView newLink = findViewById(R.id.new_link_name);
            newLink.setText("THE LINK " + result);

        }

    }

    public static String getFinalURL(String url) throws IOException{

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        connection.getInputStream();

        if(connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || connection.getResponseCode()
                == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirected = connection.getHeaderField("Location");
            return getFinalURL(redirected);
        }
        return  url;
    }
}
