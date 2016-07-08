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

public class LunchDisplay extends Activity implements AsyncResponse {

    GetLunchMenuFromServer asyncTask = new GetLunchMenuFromServer();
    private TextView entree;
    private TextView veggie;
    private TextView sides;
    private TextView soups;
    private TextView deli;

    public int day;
    public int currentDay;

    private LunchMenu weeklyMenu;

    private static final String WEBSERVER = "https://grover.ssfs.org/menus/word/document.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_display);

        entree = (TextView) findViewById(R.id.lunch_entree);
        veggie = (TextView) findViewById(R.id.veggie_entree);
        sides = (TextView) findViewById(R.id.sides);
        soups = (TextView) findViewById(R.id.soups);
        deli = (TextView) findViewById(R.id.deli);

        /*
        Creates spinner object and populates with the days of the week from the strings.xml file.
        Listener is added so that when a day of the week is clicked, the menu for that day is
        loaded.
         */
        Spinner spinner = (Spinner) findViewById(R.id.days_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        if (weeklyMenu != null) {
                            updateMenuItems(position);
                        }
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

        // Starts the AsyncTask that actually retrieves the lunch data from the server.
        asyncTask.delegate = this;
        asyncTask.execute(WEBSERVER);

       /*
       Used to initially set up the spinner with the current day of the week.  Checks to make sure
       that the current day is not Sunday (1) or Saturday (7).  Since there are only 5 week days,
       must subtract 2 from the calendar day to get the right array index of the spinner.
        */
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day != 1 && day != 7) {
            currentDay = day - 2;
            spinner.setSelection(currentDay);
        } else {
            spinner.setSelection(0);
        }
    }

    public void processFinish(String output){
        weeklyMenu = new LunchMenu(output);
        updateMenuItems(currentDay);
    }

    public void updateMenuItems(int day) {
        /*
        Method called when a new value is chosen from the spinner.  The index (day) of the spinner
        is passed to this method.
         */
        entree.setText(weeklyMenu.getLunchEntree(day));
        veggie.setText(weeklyMenu.getVegetarianEntree(day));
        sides.setText(weeklyMenu.getSides(day));
        soups.setText(weeklyMenu.getSoups(day));
        deli.setText(weeklyMenu.getDeli(day));
    }


    public class GetLunchMenuFromServer extends AsyncTask<String, Integer, String> {
        public AsyncResponse delegate = null;

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
            information from the current day to populate the initial menu.  Further updates
            will come when a different date is selected in the spinner.
             */
            LunchDisplay.this.weeklyMenu = new LunchMenu(result);
            LunchDisplay.this.updateMenuItems(currentDay);
            delegate.processFinish(result);
//            entree.setText(weeklyMenu.getLunchEntree(currentDay));
//            veggie.setText(weeklyMenu.getVegetarianEntree(currentDay));
//            sides.setText(weeklyMenu.getSides(currentDay));
//            soups.setText(weeklyMenu.getSoups(currentDay));
//            deli.setText(weeklyMenu.getDeli(currentDay));
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

                return readIt(is);
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

        return new String(total);
    }


}
