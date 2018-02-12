package id.go.pekalongankab.laporbupati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.go.pekalongankab.laporbupati.Adapter.AdapterDataKomentar;
import id.go.pekalongankab.laporbupati.Model.ModelDataAduan;
import id.go.pekalongankab.laporbupati.Model.ModelDataKomentar;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

public class DetailAduan extends AppCompatActivity {

    ImageView fotoUser, fotoAduan, btnKirim, btnLike;
    TextView namaUser, level, tanggal, isiAduan, kategori;
    EditText komentar;
    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    List<ModelDataKomentar> mItems;
    ProgressDialog pd;
    SwipeRefreshLayout swLayout;
    String id_aduan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_aduan);
        final Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setTitle("Aduan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerview = (RecyclerView)findViewById(R.id.recycleKomentar);
        pd = new ProgressDialog(DetailAduan.this);
        mItems = new ArrayList<>();
        mAdapter = new AdapterDataKomentar(DetailAduan.this, mItems);
        mManager = new LinearLayoutManager(DetailAduan.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mManager);
        mRecyclerview.setAdapter(mAdapter);

        id_aduan = bundle.getString("id");

        fotoUser = (ImageView) findViewById(R.id.foto_user);
        fotoAduan = (ImageView) findViewById(R.id.foto_aduan);
        btnKirim = (ImageView) findViewById(R.id.btnKirim);
        btnLike = (ImageView) findViewById(R.id.btnLike);

        namaUser = (TextView) findViewById(R.id.nama_user);
        level = (TextView) findViewById(R.id.level);
        tanggal = (TextView) findViewById(R.id.tanggal);
        isiAduan = (TextView) findViewById(R.id.isi_aduan);
        kategori = (TextView) findViewById(R.id.kategori);

        komentar = (EditText) findViewById(R.id.komentar);

        namaUser.setText(bundle.getString("nama"));
        level.setText(bundle.getString("level"));
        tanggal.setText(bundle.getString("tanggal"));
        isiAduan.setText(bundle.getString("aduan"));
        kategori.setText(bundle.getString("kategori"));

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+bundle.getString("foto_user"))
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(fotoUser);

        if (bundle.getString("foto_aduan").isEmpty()){
            fotoAduan.setVisibility(View.GONE);
        }else {
            fotoAduan.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_ADUAN+bundle.getString("foto_aduan"))
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(fotoAduan);
        }

        fotoAduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailAduan.this, LihatFoto.class);
                i.putExtra("source", "aduan");
                i.putExtra("foto_aduan", bundle.getString("foto_aduan"));
                startActivity(i);
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (komentar.getText().toString().isEmpty()){
                    komentar.setError("Tidak dapat mengirimkan komentar kosong!");
                }
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLike.setImageResource(R.drawable.ic_like_filled);
            }
        });

        loadKomentar();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadKomentar(){
        pd.setMessage("Memuat data...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_KOMENTAR+id_aduan, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i< response.length(); i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataKomentar md = new ModelDataKomentar();
                                md.setId_komentar(data.getString("id_komentar"));
                                md.setKomentar(data.getString("komentar"));
                                md.setTanggal(data.getString("dibuat"));
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
                        pd.cancel();
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
