package id.go.pekalongankab.halobupati;


import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.go.pekalongankab.halobupati.Adapter.AdapterDataAduan;
import id.go.pekalongankab.halobupati.Adapter.AdapterDataOpd;
import id.go.pekalongankab.halobupati.Model.ModelDataAduan;
import id.go.pekalongankab.halobupati.Model.ModelDataOpd;
import id.go.pekalongankab.halobupati.Util.AppController;
import id.go.pekalongankab.halobupati.Util.ServerAPI;


/**
 * A simple {@link Fragment} subclass.
 */
public class Aduan extends Fragment {

    AdapterDataAduan mAdapter;
    List<ModelDataAduan> mItems;
    RecyclerView mRecycleaduan;
    ProgressDialog pd;
    SwipeRefreshLayout swLayout;


    public Aduan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).setActionBarTitle("Data Aduan");
        View view_aduan = inflater.inflate(R.layout.fragment_aduan, container, false);

        mRecycleaduan = (RecyclerView) view_aduan.findViewById(R.id.recycleAduan);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        pd = new ProgressDialog(getActivity());

        loadAduan();

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycleaduan.setLayoutManager(mManager);
        mAdapter = new AdapterDataAduan(mRecycleaduan, mItems, getContext());
        mRecycleaduan.setAdapter(mAdapter);

        swLayout = (SwipeRefreshLayout) view_aduan.findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mItems.clear();
                        swLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return  view_aduan;
    }

    private void loadAduan(){
        pd.setMessage("Memuat data...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_ADUAN, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i< response.length(); i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataAduan md = new ModelDataAduan();
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        snackBar("Tidak ada koneksi internet!", R.color.Error);
                        Log.d("volley", "error : "+error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
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
