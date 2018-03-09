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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    ImageView fotoUser, fotoAduan, btnKirim, btnKategori;
    TextView namaUser, level, tanggal, isiAduan, kategori, lokasi, status;
    EditText komentar;
    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    List<ModelDataKomentar> mItems;
    ProgressBar loadkomem;
    LinearLayout loaderror;
    SwipeRefreshLayout swLayout;
    String id_aduan;
    Button btnCobaLagi;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_aduan);
        bundle = getIntent().getExtras();
        getSupportActionBar().setTitle("Aduan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        loadkomem = (ProgressBar) findViewById(R.id.loadingkomen);
        loaderror = (LinearLayout) findViewById(R.id.loaderror);
        btnCobaLagi = (Button) findViewById(R.id.btnCobalagi);

        mRecyclerview = (RecyclerView)findViewById(R.id.recycleKomentar);
        mItems = new ArrayList<>();
        mAdapter = new AdapterDataKomentar(DetailAduan.this, mItems);
        mManager = new LinearLayoutManager(DetailAduan.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mManager);
        mRecyclerview.setAdapter(mAdapter);

        id_aduan = bundle.getString("id");

        fotoUser = (ImageView) findViewById(R.id.foto_user);
        fotoAduan = (ImageView) findViewById(R.id.foto_aduan);
        btnKirim = (ImageView) findViewById(R.id.btnKirim);
        btnKategori = (ImageView) findViewById(R.id.btnStatus);

        namaUser = (TextView) findViewById(R.id.nama_user);
        level = (TextView) findViewById(R.id.level);
        tanggal = (TextView) findViewById(R.id.tanggal);
        isiAduan = (TextView) findViewById(R.id.isi_aduan);
        kategori = (TextView) findViewById(R.id.kategori);
        lokasi = (TextView) findViewById(R.id.txtLonglat);
        status = (TextView) findViewById(R.id.txtStatus);

        komentar = (EditText) findViewById(R.id.komentar);

        lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar("Lokasi tidak ditemukan!", R.color.Error);
            }
        });

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

        if (bundle.getString("status").equals("diverifikasi")){
            btnKategori.setImageResource(R.drawable.ic_info_blue);
            status.setText(bundle.getString("status"));
        }else if(bundle.getString("status").equals("didisposisikan")){
            btnKategori.setImageResource(R.drawable.ic_info_yellow);
            status.setText(bundle.getString("status"));
        }else if(bundle.getString("status").equals("penanganan")){
            btnKategori.setImageResource(R.drawable.ic_info_orange);
            status.setText(bundle.getString("status"));
        }else if(bundle.getString("status").equals("selesai")){
            btnKategori.setImageResource(R.drawable.ic_info_green);
            status.setText(bundle.getString("status"));
        }else if(bundle.getString("status").equals("bukan kewenangan")){
            btnKategori.setImageResource(R.drawable.ic_info_red);
            status.setText(bundle.getString("status"));
        }

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.clear();
                loadKomentar();
            }
        });

        loadKomentar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lokasi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuLokasi) {
            snackBar("Lokasi tidak ditemukan!", R.color.Error);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadKomentar(){
        loadkomem.setVisibility(View.VISIBLE);
        loaderror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_KOMENTAR+id_aduan, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadkomem.setVisibility(View.GONE);
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
                        loadkomem.setVisibility(View.GONE);
                        loaderror.setVisibility(View.VISIBLE);
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
