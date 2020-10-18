package com.example.weatherapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

//ok

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView cityName;
    Button searchButton;
    TextView result;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.exit:
                new exit_dialog().show(getSupportFragmentManager(), "exit");
                break;

            case R.id.preferiti:
                String s = "ferraccio";
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Activity2.class);
                startActivity(intent);
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private static final String[] c = new String[] {
            "Belgium", "France", "Italy", "Germany","Rieti","Avezzano", "Spain"
    };


    class Weather extends AsyncTask<String,Void,String>{  //First String means URL is in String, Void mean nothing, Third String means Return type will be String

        @Override
        protected String doInBackground(String... address) {
            //String... means multiple address can be send. It acts as array
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Establish connection with address
                connection.connect();

                //retrieve data from url
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Retrieve data and return it as String
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1){
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




    public void search(View view){
        cityName = findViewById(R.id.Ab);
        searchButton = findViewById(R.id.searchButton);
        result = findViewById(R.id.resut);

        String cName = cityName.getText().toString();

        Log.d("qua", "search:"+cName);

        String content;
        Weather weather = new Weather();
        try {
            //cLondon,uk&APPID=5dde592a280bd928166a6bc8d93ccc53

            content = weather.execute("http://api.openweathermap.org/data/2.5/weather?q=" +
                    cName+"&APPID=5dde592a280bd928166a6bc8d93ccc53").get();
            //First we will check data is retrieve successfully or not
            Log.d("contentData",content);

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemperature = jsonObject.getString("main"); //this main is not part of weather array, it's seperate variable like weather
            double visibility;
//          Log.i("weatherData",weatherData);
            //weather data is in Array
            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String description = "";
            String temperature = "";

            for(int i=0; i<array.length(); i++){
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");

            visibility = Double.parseDouble(jsonObject.getString("visibility"));
            //By default visibility is in meter
            int visibilityInKilometer = (int) visibility/1000;

            Log.i("Temperature",temperature);

            /*Log.i("main",main);
            Log.i("description",description);*/

            double t = Double.valueOf(temperature) - 273.15;
            t =Double.parseDouble(new DecimalFormat("##.##").format(t));


            String resultText = "Main :                     "+main+
                    "\nDescription :        "+description +
                    "\nTemperature :        "+t +"*C"+
                    "\nVisibility :              "+visibilityInKilometer+" KM";
            hideKeyboard(this);
            result.setText(resultText);

            //Now we will show this result on screen

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, c);
       AutoCompleteTextView auto = (AutoCompleteTextView) findViewById(R.id.Ab);



        auto.setAdapter(adapter);







    }
}
