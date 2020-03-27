package corona.covid19.coronavirus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import corona.covid19.coronavirus.Adapter.CountryReportAdapter;
import corona.covid19.coronavirus.Model.CountryReportModel;

public class CountryReportActivity extends AppCompatActivity {

    public final Context context = this;
    ProgressDialog spotDlg;
    private RecyclerView recyclerView;
    private CountryReportAdapter adapter;
    private List<CountryReportModel> itemList;
    EditText search_et;
    String searchStr = "";
    private InterstitialAd mInterstitialAd;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_report);


        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        imgView = (ImageView) findViewById(R.id.imgView);
        search_et = (EditText) findViewById(R.id.search_et);
        getAllCountryReport();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        itemList = new ArrayList<>();
        adapter = new CountryReportAdapter(this, context, itemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchStr = search_et.getText().toString();
                if (searchStr.matches("")) {
                    restartActivity();
                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/buygamecode"));
                startActivity(browserIntent);
            }
        });
    }

    void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        showInterstitialAd();
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public static String getFormatedNumber(String number) {
        if (!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        } else {
            return "0";
        }
    }

    void getAllCountryReport() {
        spotDlg = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        spotDlg.setMessage("Please wait...");
        spotDlg.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://coronavirus-19-api.herokuapp.com/countries",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() < 1) {
                                Toast.makeText(context, "Sorry something went wrong.", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objInner = jsonArray.getJSONObject(i);
                                    CountryReportModel items = new CountryReportModel();
                                    items.setCountry(objInner.getString("country"));
                                    items.setCases(getFormatedNumber(objInner.getString("cases")));
                                    items.setDeaths(getFormatedNumber(objInner.getString("deaths")));
                                    items.setTodayCases(getFormatedNumber(objInner.getString("todayCases")));
                                    items.setTodayDeaths(getFormatedNumber(objInner.getString("todayDeaths")));
                                    items.setRecovered(getFormatedNumber(objInner.getString("recovered")));
                                    items.setActive(getFormatedNumber(objInner.getString("active")));
                                    items.setCritical(getFormatedNumber(objInner.getString("critical")));
                                    items.setCasesPerOneMillion(getFormatedNumber(objInner.getString("casesPerOneMillion")));
                                    itemList.add(items);
                                }
                            }
                        } catch (JSONException | NumberFormatException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        hidePDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spotDlg.dismiss();
                        Toast.makeText(CountryReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (spotDlg != null) {
            spotDlg.dismiss();
            spotDlg = null;
        }
    }

}
