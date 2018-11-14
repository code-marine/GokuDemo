package com.contri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class ContriActivity extends AppCompatActivity {
    private Spinner contri_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_contri);
        contri_list = findViewById(R.id.contri_spinner);
        ArrayList<String> options = new ArrayList<String>();
        options.add("Shivam");
        options.add("Dawar");
        options.add("IGI" );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, options);

        contri_list.setAdapter(adapter);
    }
}
