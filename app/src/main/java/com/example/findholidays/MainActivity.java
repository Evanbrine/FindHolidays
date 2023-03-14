package com.example.findholidays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findholidays.model.ListHolidaysInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity implements GetDataFromInternet.AsyncResponse, MyAdapter.ListItemClickListener {

    private static final String TAG = "MainActivity";
    private Toast toast;
    private ListHolidaysInfo listHolidaysInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            URL url = new URL("https://calendarific.com/api/v2/holidays?api_key=0df1391df1090de09ccbf9bf3e91039759b3ce75&country=RU&year=2023");

            new GetDataFromInternet(this).execute(url);

        } catch (MalformedURLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void processFinish(String output) {
        Log.d(TAG, "processFinished: " +output);

        try{
            JSONObject outputJSON = new JSONObject(output);
            JSONObject responseJSONObject = outputJSON.getJSONObject("response");
            JSONArray array = responseJSONObject.getJSONArray("holidays");
            int length = array.length();

            listHolidaysInfo = new ListHolidaysInfo(length);

            ArrayList<String> namesHolidays = new ArrayList<String>();

            for(int i=0; i<length; i++){
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");

                JSONObject obj_date = obj.getJSONObject("date");
                String data_iso = obj_date.getString("iso");

                namesHolidays.add(name);
                Log.d(TAG, "processFinish: " +name+" "+data_iso);
                listHolidaysInfo.addHoliday(name, data_iso, i);
            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, namesHolidays);
            //ListView listHolidays = findViewById(R.id.listHolidays);
            //listHolidays.setAdapter(adapter);

            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(new MyAdapter(listHolidaysInfo, length, this));

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        CharSequence text = listHolidaysInfo.listHolidaysInfo[clickedItemIndex].getHoliday_name();
        int duration = Toast.LENGTH_SHORT;
        if (toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}