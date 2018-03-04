package id.go.pekalongankab.laporbupati;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.go.pekalongankab.laporbupati.Adapter.AdapterDataPemberitahuan;
import id.go.pekalongankab.laporbupati.Model.ModelDataKomentar;
import id.go.pekalongankab.laporbupati.Model.ModelDataPemberitahuan;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

public class Pemberitahuan extends AppCompatActivity {

    AdapterDataPemberitahuan mAdapter;
    List<ModelDataPemberitahuan> mItems;
    RecyclerView mRecycleCariAduan;
    SwipeRefreshLayout swLayout;
    int perLoad;
    Bundle bundle;
    public String q;
    ProgressBar loading;
    LinearLayout eror;
    String id_user;
    TextView texterror;
    Button btnCobaLagi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemberitahuan);

        getSupportActionBar().setTitle("Pemberitahuan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        id_user = pref.getString("id_user", "");

        loading = (ProgressBar) findViewById(R.id.loading);
        eror = (LinearLayout) findViewById(R.id.error);
        texterror = (TextView) findViewById(R.id.textError);
        btnCobaLagi = (Button) findViewById(R.id.btnCobalagi);

        mRecycleCariAduan = (RecyclerView) findViewById(R.id.recyclePemberitahuan);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;

        mItems.clear();
        perLoad = 15;

        loadPembritahuan();

        mManager = new LinearLayoutManager(Pemberitahuan.this, LinearLayoutManager.VERTICAL, false);
        mRecycleCariAduan.setLayoutManager(mManager);
        mAdapter = new AdapterDataPemberitahuan(mItems, getApplicationContext());
        mRecycleCariAduan.setAdapter(mAdapter);

        swLayout = (SwipeRefreshLayout) findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mItems.clear();
                        loadPembritahuan();
                        swLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPembritahuan();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadPembritahuan(){
        loading.setVisibility(View.VISIBLE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_PEMBERITAHUAN+id_user, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loading.setVisibility(View.GONE);
                        Log.d("volley", "response : "+response.toString());
                        if (response.length() <= 0){
                            eror.setVisibility(View.VISIBLE);
                            texterror.setText("Anda tidak memiliki pemberitahuan");
                        }else {
                            for (int i = 0; i< response.length(); i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataPemberitahuan md = new ModelDataPemberitahuan();
                                    md.setPemberitahuan(data.getString("pemberitahuan"));
                                    md.setTanggal(data.getString("dibuat"));
                                    mItems.add(md);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley", "error : "+error.getMessage());
                        loading.setVisibility(View.GONE);
                        eror.setVisibility(View.VISIBLE);
                        texterror.setText("Tidak dapat memuat data");
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }
}
