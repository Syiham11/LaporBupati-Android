package id.go.pekalongankab.laporbupati;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class AduanSaya extends Fragment {

    AdapterDataAduan mAdapter;
    List<ModelDataAduan> mItems;
    RecyclerView mRecycleaduansaya;
    LinearLayout eror;
    SwipeRefreshLayout swLayout;
    String id_user;
    Button btnCobaLagi;
    SpotsDialog dialog;
    FloatingActionButton fab;
    int Alldata;
    boolean loaded;
    ProgressBar loadingbar;
    CardView loadingmore;

    public AduanSaya() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).setActionBarTitle("Aduan Saya");
        fab = (FloatingActionButton) ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab.show();
        View aduan_saya = inflater.inflate(R.layout.fragment_aduan_saya, container, false);

        eror = (LinearLayout) aduan_saya.findViewById(R.id.error);
        btnCobaLagi = (Button) aduan_saya.findViewById(R.id.btnCobalagi);
        dialog = new SpotsDialog(getActivity(), "Memuat data...");
        loadingbar = (ProgressBar) aduan_saya.findViewById(R.id.loadingbar);
        loadingmore = (CardView)  aduan_saya.findViewById(R.id.loadingmore);

        mRecycleaduansaya = (RecyclerView) aduan_saya.findViewById(R.id.recycleAduanSaya);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        mItems.clear();

        SharedPreferences pref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        id_user = pref.getString("id_user", "");
        loaded = false;

        loadAduan();

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycleaduansaya.setLayoutManager(mManager);
        mAdapter = new AdapterDataAduan(mItems, getContext());
        mRecycleaduansaya.setAdapter(mAdapter);

        mRecycleaduansaya.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    fab.show();
                } else if (dy > 0) {
                    fab.hide();
                    // Recycle view scrolling down...
                    if(recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) == false){
                        loadMoreAduan();
                    }
                }
            }
        });

        swLayout = (SwipeRefreshLayout) aduan_saya.findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swLayout.setRefreshing(false);
                refresh();
            }
        });

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.clear();
                loadAduan();
            }
        });

        return aduan_saya;
    }

    private void loadAduan(){
        //dialog.show();
        loadingbar.setVisibility(View.VISIBLE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN_SAYA+id_user, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //dialog.show();
                        loadingbar.setVisibility(View.GONE);
                        loaded = true;
                        mItems.clear();
                        eror.setVisibility(View.GONE);
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
                        if(response.length() <= 0){
                            snackBar(R.string.error_koneksi, R.color.Error);
                        }else{
                            for (int i = 0; i< ServerAPI.perLoadAduan; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataAduan md = new ModelDataAduan();
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
                        //dialog.show();
                        loadingbar.setVisibility(View.GONE);
                        if (loaded == false){
                            eror.setVisibility(View.VISIBLE);
                        }
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            //snackBar(R.string.error_koneksi, R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    private void loadMoreAduan(){
        if (mItems.size() < Alldata){
            dialog.show();
            eror.setVisibility(View.GONE);
            JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN_SAYA+id_user, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            dialog.hide();
                            eror.setVisibility(View.GONE);
                            int index = mItems.size();
                            int end = index + ServerAPI.perLoadAduan;
                            for (int i = index; i<end; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataAduan md = new ModelDataAduan();
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
                            Log.d("volley", "response : "+response.toString());
                            mAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("volley", "error : "+error.getMessage());
                            dialog.hide();
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

    private void refresh(){
        dialog.show();
        loadingbar.setVisibility(View.GONE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN_SAYA+id_user, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.show();
                        loadingbar.setVisibility(View.GONE);
                        loaded = true;
                        mItems.clear();
                        eror.setVisibility(View.GONE);
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
                        if(response.length() <= 0){
                            snackBar(R.string.error_koneksi, R.color.Error);
                        }else{
                            for (int i = 0; i< ServerAPI.perLoadAduan; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataAduan md = new ModelDataAduan();
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
                        dialog.show();
                        loadingbar.setVisibility(View.GONE);
                        if (loaded == false){
                            eror.setVisibility(View.VISIBLE);
                        }
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            //snackBar(R.string.error_koneksi, R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    public void snackBar(int pesan, int color){
        Snackbar snackbar = Snackbar.make(getView(), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snackbar.show();
    }

}
