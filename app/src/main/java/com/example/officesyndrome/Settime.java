package com.example.officesyndrome;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;

public class Settime extends AppCompatActivity{

    ArrayList<String> itemlist;
    ArrayAdapter<String> adapter;
    TimePicker timepicker;
    Button bt_save;
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settime);

        timepicker = (TimePicker) findViewById(R.id.timePicker1);
        bt_save = (Button)findViewById(R.id.b_save);
        lv = (ListView)findViewById(R.id.list_viwe);
        timepicker.setIs24HourView(true);

        itemlist = new ArrayList<>();

        adapter = new ArrayAdapter<String>(Settime.this ,android.R.layout.simple_list_item_multiple_choice,itemlist);

        View.OnClickListener addlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemlist.add(timepicker.getCurrentHour().toString()+":"+timepicker.getCurrentMinute().toString());
                adapter.notifyDataSetChanged();
            }
        };

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();

                int count = lv.getCount();

                for (int item=count-1; item>=0; item--){
                    if (positionchecker.get(item)){
                        adapter.remove(itemlist.get(item));
                        Toast.makeText(Settime.this,"Delete Item",Toast.LENGTH_SHORT).show();

                    }
                }

                positionchecker.clear();

                adapter.notifyDataSetChanged();

                return false;
            }
        });

        bt_save.setOnClickListener(addlistener);
        lv.setAdapter(adapter);

    }
}


