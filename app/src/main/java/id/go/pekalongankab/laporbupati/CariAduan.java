package id.go.pekalongankab.laporbupati;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.Adapter.AdapterDataAduan;
import id.go.pekalongankab.laporbupati.Model.ModelDataAduan;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

public class CariAduan extends AppCompatActivity {

    AdapterDataAduan mAdapter;
    List<ModelDataAduan> mItems;
    RecyclerView mRecycleCariAduan;
    SwipeRefreshLayout swLayout;
    Bundle bundle;
    public String q;
    int Alldata;
    ProgressBar loading;
    LinearLayout eror;
    TextView texterror;
    Button btnCobaLagi;
    boolean loaded;
    SpotsDialog dialog;
    CardView loadingmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_aduan);
        bundle = getIntent().getExtras();
        q = bundle.getString("query");
        getSupportActionBar().setTitle(q);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new SpotsDialog(CariAduan.this, "Memuat data...");

        loading = (ProgressBar) findViewById(R.id.loading);
        eror = (LinearLayout) findViewById(R.id.error);
        texterror = (TextView) findViewById(R.id.textError);
        btnCobaLagi = (Button) findViewById(R.id.btnCobalagi);
        loadingmore = (CardView) findViewById(R.id.loadingmore);

        mRecycleCariAduan = (RecyclerView) findViewById(R.id.recycleCariAduan);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;

        loaded = false;

        mItems.clear();

        loadAduan();

        mManager = new LinearLayoutManager(CariAduan.this, LinearLayoutManager.VERTICAL, false);
        mRecycleCariAduan.setLayoutManager(mManager);
        mAdapter = new AdapterDataAduan(mItems, getApplicationContext());
        mRecycleCariAduan.setAdapter(mAdapter);

        mRecycleCariAduan.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // Recycle view scrolling down...
                    if(recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) == false){
                        loadMoreAduan();
                    }
                }
            }
        });

        swLayout = (SwipeRefreshLayout) findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAduan();
                        swLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.clear();
                loadAduan();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Telusuri aduan...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                q = query;
                getSupportActionBar().setTitle(q);
                mItems.clear();
                loadAduan();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void loadAduan(){
        loading.setVisibility(View.VISIBLE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_CARI_ADUAN+q.replaceAll(" ", "%20"), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loading.setVisibility(View.GONE);
                        eror.setVisibility(View.GONE);
                        mItems.clear();
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
                        if (response.length() <= 0){
                            loaded = false;
                            eror.setVisibility(View.VISIBLE);
                            texterror.setText("Hasil pencarian '"+q+"' tidak ditemukan!");
                            snackBar("Hasil pencarian '"+q+"' tidak ditemukan!", R.color.Error);
                        }else{
                            loaded = true;
                            for (int i = 0; i< ServerAPI.perLoadAduan; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataAduan md = new ModelDataAduan();
                                    md.setId_aduan(data.getString("id_aduan"));
                                    md.setNama_user(data.getString("nama_user"));
                                    md.setTanggal(data.getString("dibuat"));
                                    md.setAduan(data.getString("aduan"));
                                    md.setKategori(data.getString("kategori"));
                                    md.setStatus(data.getString("status"));
                                    md.setFoto_aduan(data.getString("lampiran"));
                                    md.setFoto_user(data.getString("foto"));
                                    md.setStatus(data.getString("status"));
                                    md.setLongi(data.getString("longitude"));
                                    md.setLati(data.getString("latitude"));
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
                        if (loaded == false){
                            eror.setVisibility(View.VISIBLE);
                        }
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar(R.string.error_koneksi, R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    private void loadMoreAduan(){
        if (mItems.size() < Alldata){
            //dialog.show();
            loadingmore.setVisibility(View.VISIBLE);
            eror.setVisibility(View.GONE);
            JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_CARI_ADUAN+q.replaceAll(" ", "%20"), null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //dialog.hide();
                            loadingmore.setVisibility(View.GONE);
                            eror.setVisibility(View.GONE);
                            Log.d("volley", "response : "+response.toString());
                            int index = mItems.size();
                            int end = index + ServerAPI.perLoadAduan;
                            for (int i = index; i<end; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataAduan md = new ModelDataAduan();
                                    md.setId_aduan(data.getString("id_aduan"));
                                    md.setNama_user(data.getString("nama_user"));
                                    md.setTanggal(data.getString("dibuat"));
                                    md.setAduan(data.getString("aduan"));
                                    md.setKategori(data.getString("kategori"));
                                    md.setStatus(data.getString("status"));
                                    md.setFoto_aduan(data.getString("lampiran"));
                                    md.setFoto_user(data.getString("foto"));
                                    md.setStatus(data.getString("status"));
                                    md.setLongi(data.getString("longitude"));
                                    md.setLati(data.getString("latitude"));
                                    mItems.add(md);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("volley", "error : "+error.getMessage());
                            //dialog.hide();
                            loadingmore.setVisibility(View.GONE);
                            if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                                snackBar(R.string.error_koneksi, R.color.Error);
                            }
                        }
                    });
            AppController.getInstance().addToRequestQueue(requestData);
        }else{
            snackBar(R.string.info_batas_akhir, R.color.Info);
        }
    }

    private void snackBar(int pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }
}
