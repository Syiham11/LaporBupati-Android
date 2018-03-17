package id.go.pekalongankab.laporbupati;


import android.app.ProgressDialog;
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
import id.go.pekalongankab.laporbupati.Adapter.AdapterDataOpd;
import id.go.pekalongankab.laporbupati.Model.ModelDataOpd;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;


/**
 * A simple {@link Fragment} subclass.
 */
public class Opd extends Fragment {
    AdapterDataOpd mAdapter;
    List<ModelDataOpd> mItems;
    RecyclerView mRecycleopd;
    SwipeRefreshLayout swLayout;
    int index;
    LinearLayout eror;
    SpotsDialog dialog;
    FloatingActionButton fab;
    Button cobaLagi;
    int Alldata;
    ProgressBar loadingbar;
    CardView loadingmore;
    boolean loaded;

    public Opd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view_opd =  inflater.inflate(R.layout.fragment_opd, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Data OPD");
        fab = (FloatingActionButton) ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab.show();

        eror = (LinearLayout) view_opd.findViewById(R.id.error);
        dialog = new SpotsDialog(getActivity(), "Memuat data...");
        cobaLagi = (Button) view_opd.findViewById(R.id.btnCobalagi);
        loadingbar = (ProgressBar) view_opd.findViewById(R.id.loadingbar);
        loadingmore = (CardView) view_opd.findViewById(R.id.loadingmore);

        mRecycleopd = (RecyclerView) view_opd.findViewById(R.id.recycleOpd);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        mItems.clear();
        loaded = false;

        loadOpd();

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycleopd.setLayoutManager(mManager);
        mAdapter = new AdapterDataOpd(mItems, getContext());
        mRecycleopd.setAdapter(mAdapter);

        mRecycleopd.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadmoreOpd();
                    }
                }
            }
        });

        swLayout = (SwipeRefreshLayout) view_opd.findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swLayout.setRefreshing(false);
                refresh();
            }
        });

        cobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOpd();
            }
        });

        return view_opd;
    }

    private void loadOpd(){
        //dialog.show();
        loadingbar.setVisibility(View.VISIBLE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_OPD, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //dialog.hide();
                        loaded = true;
                        mItems.clear();
                        loadingbar.setVisibility(View.GONE);
                        eror.setVisibility(View.GONE);
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i<ServerAPI.perLoadOpd; i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataOpd md = new ModelDataOpd();
                                md.setId_opd(data.getString("id_opd"));
                                md.setOpd(data.getString("nama_opd"));
                                md.setSingkatan(data.getString("singkatan"));
                                md.setAlamat(data.getString("alamat"));
                                md.setFoto(data.getString("foto"));
                                md.setNamaKepala(data.getString("nama_kepala"));
                                md.setNoTelp(data.getString("no_telp"));
                                md.setEmail(data.getString("email"));
                                md.setFax(data.getString("fax"));
                                md.setWebsite(data.getString("website"));
                                md.setDeskripsi(data.getString("deskripsi"));
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

    private void loadmoreOpd(){
        if (mItems.size() < Alldata){
            //dialog.show();
            loadingmore.setVisibility(View.VISIBLE);
            eror.setVisibility(View.GONE);
            JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_OPD, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //dialog.hide();
                            loadingmore.setVisibility(View.GONE);
                            eror.setVisibility(View.GONE);
                            Log.d("volley", "response : "+response.toString());
                            index = mItems.size();
                            int end = index + ServerAPI.perLoadOpd;
                            for (int i = index; i<end; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataOpd md = new ModelDataOpd();
                                    md.setId_opd(data.getString("id_opd"));
                                    md.setOpd(data.getString("nama_opd"));
                                    md.setSingkatan(data.getString("singkatan"));
                                    md.setAlamat(data.getString("alamat"));
                                    md.setFoto(data.getString("foto"));
                                    md.setNamaKepala(data.getString("nama_kepala"));
                                    md.setNoTelp(data.getString("no_telp"));
                                    md.setEmail(data.getString("email"));
                                    md.setFax(data.getString("fax"));
                                    md.setWebsite(data.getString("website"));
                                    md.setDeskripsi(data.getString("deskripsi"));
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
                            if (loaded == false){
                                eror.setVisibility(View.VISIBLE);
                            }
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
        dialog.show();
        loadingbar.setVisibility(View.GONE);
        eror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_OPD, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.hide();
                        mItems.clear();
                        loadingbar.setVisibility(View.GONE);
                        eror.setVisibility(View.GONE);
                        Alldata = response.length();
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i<ServerAPI.perLoadOpd; i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataOpd md = new ModelDataOpd();
                                md.setId_opd(data.getString("id_opd"));
                                md.setOpd(data.getString("nama_opd"));
                                md.setSingkatan(data.getString("singkatan"));
                                md.setAlamat(data.getString("alamat"));
                                md.setFoto(data.getString("foto"));
                                md.setNamaKepala(data.getString("nama_kepala"));
                                md.setNoTelp(data.getString("no_telp"));
                                md.setEmail(data.getString("email"));
                                md.setFax(data.getString("fax"));
                                md.setWebsite(data.getString("website"));
                                md.setDeskripsi(data.getString("deskripsi"));
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
