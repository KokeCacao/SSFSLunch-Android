package com.wilkinsonbrian.ssfslunch;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LunchDisplay extends Activity {
    private TextView textView;
    private TextView entree;
    private TextView veggie;
    private TextView sides;
    private TextView soups;
    private TextView deli;

    public int day;
    public int currentDay;

    private LunchMenu weeklyMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_display);
        textView = (TextView) findViewById(R.id.url_result);
        entree = (TextView) findViewById(R.id.lunch_entree);
        veggie = (TextView) findViewById(R.id.veggie_entree);
        sides = (TextView) findViewById(R.id.sides);
        soups = (TextView) findViewById(R.id.soups);
        deli = (TextView) findViewById(R.id.deli);

        textView.setMovementMethod(new ScrollingMovementMethod());
        Spinner spinner = (Spinner) findViewById(R.id.days_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.v("Got", "This right");
                        updateMenuItems(position);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
        new GetLunchMenuFromServer().execute("https://grover.ssfs.org/menus/word/document.xml");
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day != 1 && day != 7) {
            currentDay = day - 2;
            spinner.setSelection(currentDay);
        } else {
            spinner.setSelection(0);
        }
    }

    public void updateMenuItems(int day) {
        entree.setText(weeklyMenu.getLunchEntree(day));
        veggie.setText(weeklyMenu.getVegetarianEntree(day));
        sides.setText(weeklyMenu.getSides(day));
        soups.setText(weeklyMenu.getSoups(day));
        deli.setText(weeklyMenu.getDeli(day));
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
            /*
            This method is where the UI is first updated.  The default action is to use the
            information from the current day to populate the initial menu.
             */
            weeklyMenu = new LunchMenu(result);
            entree.setText(weeklyMenu.getLunchEntree(currentDay));
            veggie.setText(weeklyMenu.getVegetarianEntree(currentDay));
            sides.setText(weeklyMenu.getSides(currentDay));
            soups.setText(weeklyMenu.getSoups(currentDay));
            deli.setText(weeklyMenu.getDeli(currentDay));
        }

        private String downloadUrl(String myurl) throws  IOException {
            InputStream is = null;

            try {

                /*
                Reads the XML file from server (grover) and returns the raw xml
                 */
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.connect();
                is = conn.getInputStream();

                String contentAsString = readIt(is);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }
    /*
    Takes the URL data and appends each line of XML one by one to the Stringbuilder.
    The final string returned is the complete XML file with all the tags.
     */
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
