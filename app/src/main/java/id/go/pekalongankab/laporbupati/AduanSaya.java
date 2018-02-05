package id.go.pekalongankab.laporbupati;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import id.go.pekalongankab.laporbupati.Adapter.AdapterDataAduanSaya;
import id.go.pekalongankab.laporbupati.Model.ModelDataAduanSaya;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;


/**
 * A simple {@link Fragment} subclass.
 */
public class AduanSaya extends Fragment {

    AdapterDataAduanSaya mAdapter;
    List<ModelDataAduanSaya> mItems;
    RecyclerView mRecycleaduansaya;
    ProgressDialog pd;
    SwipeRefreshLayout swLayout;
    int perLoad;
    String id_user;

    public AduanSaya() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).setActionBarTitle("Aduan Saya");
        View aduan_saya = inflater.inflate(R.layout.fragment_aduan_saya, container, false);

        mRecycleaduansaya = (RecyclerView) aduan_saya.findViewById(R.id.recycleAduanSaya);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        pd = new ProgressDialog(getActivity());
        mItems.clear();

        perLoad = 5;

        SharedPreferences pref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        id_user = pref.getString("id_user", "");

        loadAduan();

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycleaduansaya.setLayoutManager(mManager);
        mAdapter = new AdapterDataAduanSaya(mRecycleaduansaya, mItems, getContext());
        mRecycleaduansaya.setAdapter(mAdapter);

        swLayout = (SwipeRefreshLayout) aduan_saya.findViewById(R.id.swLayout);
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

        return aduan_saya;
    }

    private void loadAduan(){
        pd.setMessage("Memuat data...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN_SAYA+id_user, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        Log.d("volley", "response : "+response.toString());
                        if(response.length() < 0){
                            snackBar("Anda tidak memiliki aduan yang diterima oleh admin", R.color.Info);
                        }else{
                            for (int i = 0; i< perLoad; i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelDataAduanSaya md = new ModelDataAduanSaya();
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
                        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN_SAYA+id_user, null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        pd.cancel();
                                        if (response.length() > perLoad){
                                            int index = mItems.size();
                                            int end = index + perLoad;
                                            for (int i = index; i<end; i++){
                                                try {
                                                    JSONObject data = response.getJSONObject(i);
                                                    ModelDataAduanSaya md = new ModelDataAduanSaya();
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
                                            Log.d("volley", "response : "+response.toString());
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

    public void snackBar(String pesan, int color){
        Snackbar snackbar = Snackbar.make(getActivity()
                .findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snackbar.show();
    }

}
