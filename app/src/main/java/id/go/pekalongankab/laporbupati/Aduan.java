package id.go.pekalongankab.laporbupati;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;

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
public class Aduan extends Fragment {

    AdapterDataAduan mAdapter;
    List<ModelDataAduan> mItems;
    RecyclerView mRecycleaduan;
    ProgressDialog pd;
    SwipeRefreshLayout swLayout;
    LinearLayout eror;
    FloatingActionButton fab;
    Button btnCobaLagi;
    int Alldata;
    boolean loaded;
    ProgressBar loadingbar;
    CardView loadingmore;
    SpotsDialog dialog;


    public Aduan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).setActionBarTitle("Lapor Bupati");
        fab = (FloatingActionButton) ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab.show();
        View view_aduan = inflater.inflate(R.layout.fragment_aduan, container, false);

        eror = (LinearLayout) view_aduan.findViewById(R.id.error);
        btnCobaLagi = (Button) view_aduan.findViewById(R.id.btnCobalagi);
        loadingbar = (ProgressBar) view_aduan.findViewById(R.id.loadingbar);
        loadingmore = (CardView) view_aduan.findViewById(R.id.loadingmore);

        mRecycleaduan = (RecyclerView) view_aduan.findViewById(R.id.recycleAduan);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        pd = new ProgressDialog(getActivity());
        dialog = new SpotsDialog(getActivity(), "Memuat data...");
        mItems.clear();
        loaded = false;

        loadAduan();

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycleaduan.setLayoutManager(mManager);
        mAdapter = new AdapterDataAduan(mItems, getContext());
        mRecycleaduan.setAdapter(mAdapter);

        mRecycleaduan.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        swLayout = (SwipeRefreshLayout) view_aduan.findViewById(R.id.swLayout);
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
                loadAduan();
            }
        });

        return  view_aduan;
    }

    private void loadAduan(){
        /*pd.setMessage("Memuat data...");
        pd.setCancelable(false);
        pd.show();*/
        //dialog.show();
        loadingbar.setVisibility(View.VISIBLE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //pd.cancel();
                        //dialog.hide();
                        loadingbar.setVisibility(View.GONE);
                        loaded = true;
                        mItems.clear();
                        eror.setVisibility(View.GONE);
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
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
                                md.setFoto_user(data.getString("thumb"));
                                md.setStatus(data.getString("status"));
                                md.setLongi(data.getString("longitude"));
                                md.setLati(data.getString("latitude"));
                                md.setJmladuan(data.getString("jmladuan"));
                                md.setJmlkomen(data.getString("jmlkomen"));
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
                        //pd.cancel();
                        //dialog.hide();
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
            //dialog.show();
            loadingmore.setVisibility(View.VISIBLE);
            eror.setVisibility(View.GONE);
            JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("volley", "response : "+response.toString());
                            //dialog.hide();
                            loadingmore.setVisibility(View.GONE);
                            loaded = true;
                            eror.setVisibility(View.GONE);
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
                                    md.setFoto_user(data.getString("thumb"));
                                    md.setStatus(data.getString("status"));
                                    md.setLongi(data.getString("longitude"));
                                    md.setLati(data.getString("latitude"));
                                    md.setJmladuan(data.getString("jmladuan"));
                                    md.setJmlkomen(data.getString("jmlkomen"));
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
                                //snackBar(R.string.error_koneksi, R.color.Error);
                            }
                        }
                    });
            AppController.getInstance().addToRequestQueue(requestData);
        }else{
            snackBar(R.string.info_batas_akhir, R.color.Info);
        }

    }

    private void refresh(){
        /*pd.setMessage("Memuat data...");
        pd.setCancelable(false);
        pd.show();*/
        dialog.show();
        loadingbar.setVisibility(View.GONE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //pd.cancel();
                        dialog.hide();
                        loadingbar.setVisibility(View.GONE);
                        loaded = true;
                        mItems.clear();
                        eror.setVisibility(View.GONE);
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
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
                                md.setFoto_user(data.getString("thumb"));
                                md.setStatus(data.getString("status"));
                                md.setLongi(data.getString("longitude"));
                                md.setLati(data.getString("latitude"));
                                md.setJmladuan(data.getString("jmladuan"));
                                md.setJmlkomen(data.getString("jmlkomen"));
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
                        //pd.cancel();
                        dialog.hide();
                        loadingbar.setVisibility(View.GONE);
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

    public void snackBar(int pesan, int color){
        Snackbar snackbar = Snackbar.make(getView(), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snackbar.show();
    }

}
