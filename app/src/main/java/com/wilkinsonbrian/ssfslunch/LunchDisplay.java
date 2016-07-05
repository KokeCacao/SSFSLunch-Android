package com.wilkinsonbrian.ssfslunch;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LunchDisplay extends Activity {
    private TextView textView;
    private static final String DEBUG_TAG = "HttpExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_display);
        textView = (TextView) findViewById(R.id.url_result);
        new GetLunchMenuFromServer().execute("https://grover.ssfs.org/menus/word/document.xml");

    }

    public class GetLunchMenuFromServer extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {

                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);

        }

        private String downloadUrl(String myurl) throws  IOException {
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                String contentAsString = readIt(is);
                LunchMenu weeklyMenu = new LunchMenu(contentAsString);
                contentAsString = weeklyMenu.newMenu;
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));

        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line + "\n");
        }
        //Reader reader = new InputStreamReader(stream, "UTF-8");
       // char[] buffer = new char[len];
        //reader.read(buffer);
        return new String(total);
    }

}
