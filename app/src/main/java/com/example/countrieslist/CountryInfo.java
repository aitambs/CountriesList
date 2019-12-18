package com.example.countrieslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CountryInfo extends AppCompatActivity {

    private Country currentCountry;
    private List<Country> borderingCountriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_info);
        currentCountry = getIntent().getParcelableExtra("country");
        if (currentCountry == null) finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (String s :
                currentCountry.borders) {
            borderingCountriesList.add(MainActivity.countryMap.get(s));
        }
        TextView title = findViewById(R.id.title_name);
        TextView subtitle = findViewById(R.id.subtitle_native_name);
        RecyclerView recyclerView = findViewById(R.id.bordered_countries);
        title.setText(currentCountry.name);
        subtitle.setText(currentCountry.nativeName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CountryAdapter(borderingCountriesList));
    }
}
