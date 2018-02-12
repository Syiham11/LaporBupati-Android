package id.go.pekalongankab.laporbupati;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import id.go.pekalongankab.laporbupati.Adapter.AdapterDataCariAduan;
import id.go.pekalongankab.laporbupati.Model.ModelDataCariAduan;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

import static java.security.AccessController.getContext;

public class CariAduan extends AppCompatActivity {

    AdapterDataCariAduan mAdapter;
    List<ModelDataCariAduan> mItems;
    RecyclerView mRecycleCariAduan;
    ProgressDialog pd;
    SwipeRefreshLayout swLayout;
    int perLoad;
    Bundle bundle;
    public String q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_aduan);
        bundle = getIntent().getExtras();
        q = bundle.getString("query");
        getSupportActionBar().setTitle(q);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecycleCariAduan = (RecyclerView) findViewById(R.id.recycleCariAduan);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        pd = new ProgressDialog(CariAduan.this);
        mItems.clear();

        perLoad = 5;

        loadAduan();

        mManager = new LinearLayoutManager(CariAduan.this, LinearLayoutManager.VERTICAL, false);
        mRecycleCariAduan.setLayoutManager(mManager);
        mAdapter = new AdapterDataCariAduan(mRecycleCariAduan, mItems, getApplicationContext());
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
                        loadAduan();
                        swLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        loadMoreAduan();
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
        pd.setMessage("Mencari data...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_CARI_ADUAN+q.replaceAll(" ", "%20"), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        Log.d("volley", "response : "+response.toString());
                        if (response.length() <= 0){
                            snackBar("Hasil pencarian '"+q+"' tidak ditemukan!", R.color.Error);
                        }else{
                            for (int i = 0; i< perLoad; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataCariAduan md = new ModelDataCariAduan();
                                    md.setId_aduan(data.getString("id_aduan"));
                                    md.setNama_user(data.getString("nama_user"));
                                    md.setTanggal(data.getString("tanggal"));
                                    md.setAduan(data.getString("aduan"));
                                    md.setKategori(data.getString("kategori"));
                                    md.setStatus(data.getString("status"));
                                    md.setFoto_aduan(data.getString("lampiran"));
                                    md.setFoto_user(data.getString("foto"));
                                    md.setStatus(data.getString("status"));
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
                        pd.cancel();
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    private void loadMoreAduan(){
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mItems.add(null);
                mAdapter.notifyItemInserted(mItems.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mItems.remove(mItems.size() - 1);
                        mAdapter.notifyItemRemoved(mItems.size());
                        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_CARI_ADUAN+q.replaceAll(" ", "%20"), null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        pd.cancel();
                                        Log.d("volley", "response : "+response.toString());
                                        if (response.length() > perLoad){
                                            int index = mItems.size();
                                            int end = index + perLoad;
                                            for (int i = index; i<end; i++){
                                                try {
                                                    JSONObject data = response.getJSONObject(i);
                                                    ModelDataCariAduan md = new ModelDataCariAduan();
                                                    md.setNama_user(data.getString("nama_user"));
                                                    md.setTanggal(data.getString("tanggal"));
                                                    md.setAduan(data.getString("aduan"));
                                                    md.setKategori(data.getString("kategori"));
                                                    md.setStatus(data.getString("status"));
                                                    md.setFoto_aduan(data.getString("lampiran"));
                                                    md.setFoto_user(data.getString("foto"));
                                                    md.setStatus(data.getString("status"));
                                                    mItems.add(md);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            mAdapter.notifyDataSetChanged();
                                            mAdapter.setLoaded();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("volley", "error : "+error.getMessage());
                                        pd.cancel();
                                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                                        }
                                    }
                                });
                        AppController.getInstance().addToRequestQueue(requestData);
                    }
                }, 3000);
            }
        });

    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }
}
