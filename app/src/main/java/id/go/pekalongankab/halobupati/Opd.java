package id.go.pekalongankab.halobupati;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.go.pekalongankab.halobupati.Adapter.AdapterDataOpd;
import id.go.pekalongankab.halobupati.Model.ModelDataOpd;
import id.go.pekalongankab.halobupati.Util.AppController;
import id.go.pekalongankab.halobupati.Util.ServerAPI;


/**
 * A simple {@link Fragment} subclass.
 */
public class Opd extends Fragment {
    AdapterDataOpd mAdapter;
    List<ModelDataOpd> mItems;
    RecyclerView mRecycleopd;
    ProgressDialog pd;
    SwipeRefreshLayout swLayout;
    int jml;
    int perLoad;

    public Opd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view_opd =  inflater.inflate(R.layout.fragment_opd, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Data OPD");
        mRecycleopd = (RecyclerView) view_opd.findViewById(R.id.recycleOpd);
        mItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager;
        pd = new ProgressDialog(getActivity());

        perLoad = 3;

        loadOpd();

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycleopd.setLayoutManager(mManager);
        mAdapter = new AdapterDataOpd(mRecycleopd, mItems, getContext());
        mRecycleopd.setAdapter(mAdapter);

        swLayout = (SwipeRefreshLayout) view_opd.findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mItems.clear();
                        loadOpd();
                        swLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        loadmoreOpd();

        return view_opd;
    }

    private void loadOpd(){
        pd.setMessage("Memuat data...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_OPD, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i<perLoad; i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataOpd md = new ModelDataOpd();
                                md.setId_opd(data.getString("id_opd"));
                                md.setOpd(data.getString("nama_opd"));
                                md.setSingkatan(data.getString("singkatan"));
                                md.setAlamat(data.getString("alamat"));
                                md.setFoto(data.getString("foto"));
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

    private void loadmoreOpd(){
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
                        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_OPD, null,
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
                                                    ModelDataOpd md = new ModelDataOpd();
                                                    md.setId_opd(data.getString("id_opd"));
                                                    md.setOpd(data.getString("nama_opd"));
                                                    md.setSingkatan(data.getString("singkatan"));
                                                    md.setAlamat(data.getString("alamat"));
                                                    md.setFoto(data.getString("foto"));
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
                                        pd.cancel();
                                        snackBar("Tidak ada koneksi internet!", R.color.Error);
                                        Log.d("volley", "error : "+error.getMessage());
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
