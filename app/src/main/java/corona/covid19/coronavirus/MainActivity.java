package corona.covid19.coronavirus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    public final Context context = this;
    ProgressDialog spotDlg;
    TextView deathText, caseText, recoverText, countryWiseText;
    TextView countryName, countryCase, countryDeath, countryRecovered;
    Button aboutCard, refreshCard, countryReportCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        deathText = (TextView) findViewById(R.id.deathText);
        caseText = (TextView) findViewById(R.id.caseText);
        recoverText = (TextView) findViewById(R.id.recoverText);

        countryName = (TextView) findViewById(R.id.countryName);
        countryCase = (TextView) findViewById(R.id.countryCase);
        countryDeath = (TextView) findViewById(R.id.countryDeath);
        countryRecovered = (TextView) findViewById(R.id.countryRecovered);

        aboutCard = (Button) findViewById(R.id.aboutCard);
        refreshCard = (Button) findViewById(R.id.refreshCard);
        countryReportCard = (Button) findViewById(R.id.countryReportCard);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getOverAllReport();
            getCountryName();
        } else {
            Toast.makeText(context, "Internet connection required", Toast.LENGTH_LONG).show();
        }

        aboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogAbout = new Dialog(context);
                dialogAbout.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogAbout.setContentView(R.layout.about);
                TextView textAbout = (TextView) dialogAbout.findViewById(R.id.text);
                dialogAbout.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/buygamecode"));
                        startActivity(browserIntent);
                    }
                }, 5000);

            }
        });

        refreshCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    getOverAllReport();
                    getCountryName();
                } else {
                    Toast.makeText(context, "Internet connection required", Toast.LENGTH_LONG).show();
                }
            }
        });



        countryReportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountryReportActivity();
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
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

    void openCountryReportActivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            Intent myIntent = new Intent(MainActivity.this, CountryReportActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else {
            Toast.makeText(context, "Internet connection required", Toast.LENGTH_LONG).show();
        }
    }


    public static String getFormatedNumber(String number) {
        if (!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        } else {
            return "0";
        }
    }

    void getOverAllReport() {
        spotDlg = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        spotDlg.setMessage("Please wait...");
        spotDlg.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://coronavirus-19-api.herokuapp.com/all",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("Death: ", obj.getString("deaths"));
                            deathText.setText(getFormatedNumber(obj.getString("deaths")));
                            caseText.setText(getFormatedNumber(obj.getString("cases")));
                            recoverText.setText(getFormatedNumber(obj.getString("recovered")));
                        } catch (JSONException | NumberFormatException e) {
                            e.printStackTrace();
                        }
                        hidePDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spotDlg.dismiss();
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

    String countryReportUrl = "";

    void getCountryName() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ip-api.com/json";
        countryReportUrl = "";
        countryReportUrl = "https://coronavirus-19-api.herokuapp.com/countries/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            countryReportUrl = countryReportUrl + obj.getString("country");
                            myCountrReport(countryReportUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
            }
        });

        queue.add(stringRequest);
    }

    void myCountrReport(final String countryReportUrl) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, countryReportUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Country not found")) {
                            italyCountrReport();
                        } else {
                            try {
                                JSONObject obj = new JSONObject(response);

                                countryName.setText(obj.getString("country"));
                                countryCase.setText(getFormatedNumber(obj.getString("cases")));
                                countryDeath.setText(getFormatedNumber(obj.getString("deaths")));
                                countryRecovered.setText(getFormatedNumber(obj.getString("recovered")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }


    void italyCountrReport() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String countryReportUrl = "https://coronavirus-19-api.herokuapp.com/countries/Italy";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, countryReportUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            countryName.setText(obj.getString("country"));
                            countryCase.setText(getFormatedNumber(obj.getString("cases")));
                            countryDeath.setText(getFormatedNumber(obj.getString("deaths")));
                            countryRecovered.setText(getFormatedNumber(obj.getString("recovered")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

}
