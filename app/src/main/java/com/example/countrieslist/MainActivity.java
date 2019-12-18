package com.example.countrieslist;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class MainActivity extends AppCompatActivity {

    private static final String COUNTRIES_KEY = "countries";
    private static List<Country> countryList = new ArrayList<>();
    static Map<String,Country> countryMap = new HashMap<>();
    private static final String URL="https://restcountries.eu/rest/v2/all?fields=name;nativeName;alpha3Code;borders;area";

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private CountryAdapter adapter;
    private ProgressBar pleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pleaseWait=findViewById(R.id.please_wait);
        sharedPreferences = getSharedPreferences("default",MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (countryList.isEmpty() || countryMap.isEmpty()){ //Check if need to reload the List to Memory
            if (sharedPreferences.contains(COUNTRIES_KEY)){ //Check if list is already on device.
                fillCountryListAndMap(sharedPreferences.getString(COUNTRIES_KEY,""));
                fillAdapter();
                return;
            }

            pleaseWait.setVisibility(View.VISIBLE); //Show animation while waiting

            OkHttpClient client;

            //The following is a solution for a bug in old versions of Android (pre API-21),
            // which resulted in timeout.
            // See: https://github.com/square/okhttp/issues/4378#issuecomment-437571609
            if (Build.VERSION.SDK_INT<21) {
                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                        .supportsTlsExtensions(true)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                        .build();

                client = new OkHttpClient.Builder()
                        .connectionSpecs(Collections.singletonList(spec))
                        .build();
            } else {
                client = new OkHttpClient();
            }

            Request request = new Request.Builder().url(URL).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.error)
                                    .setMessage(R.string.check_connection)//Show user-friendly message. Use e.getMessage() for actual error reason
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            finish(); // Exit on error
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                        }
                    });
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error: "+response); // On any error (eg. 400s code)
                    }
                    String body = response.body().string();
                    sharedPreferences.edit().putString(COUNTRIES_KEY,body).apply(); //Save Country list locally
                    fillCountryListAndMap(body);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillAdapter();
                        }
                    });
                }
            });
        } else {
            fillAdapter();
        }
    }

    //Fill the Recyclerview with the data
    private void fillAdapter(){
        if (recyclerView==null) { //Skipped if already in memory
            recyclerView = findViewById(R.id.mainList);
            adapter = new CountryAdapter(countryList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(new ItemClickListener(this));
        }
        pleaseWait.setVisibility(View.GONE); //Hide wait animation
    }

    //Fill vars with data
    private void fillCountryListAndMap(String data){
        Gson gson = new Gson();
        countryList=gson.fromJson(data, new TypeToken<List<Country>>(){}.getType());
        Collections.sort(countryList, new SortByName());
        for (Country c :
                countryList) {
            countryMap.put(c.alpha3Code,c);
        }
    }

    //Prepare Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.sort_by_name_ascending || id==R.id.sort_by_name_descending){
            Collections.sort(adapter.dataSet,new SortByName()); //Use SortByName Comparator
        }
        if (id==R.id.sort_by_area_ascending || id==R.id.sort_by_area_descending){
            Collections.sort(adapter.dataSet,new SortByArea()); //Use SortByArea Comparator
        }
        if (id==R.id.sort_by_name_descending || id==R.id.sort_by_area_descending){
            Collections.reverse(adapter.dataSet); //Reverse Order if needed.
        }
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}
