package id.go.pekalongankab.laporbupati;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.Adapter.AdapterDataKomentar;
import id.go.pekalongankab.laporbupati.Model.ModelDataAduan;
import id.go.pekalongankab.laporbupati.Model.ModelDataKomentar;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

public class DetailAduan extends AppCompatActivity {

    ImageView fotoUser, fotoAduan, btnKategori, btnTambahFoto, fotoKomen, btnHapusFoto;
    TextView namaUser, level, tanggal, isiAduan, kategori, status, jmlkomen;
    CardView btnKirim;
    EditText komentar;
    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    List<ModelDataKomentar> mItems;
    ProgressBar loadkomem;
    LinearLayout loaderror;
    String id_aduan, id_user;
    Button btnCobaLagi;
    Bundle bundle;
    RelativeLayout kolomkomen;
    int rowdata;
    SwipeRefreshLayout swLayout;


    Uri fileUri;
    int PICK_IMAGE_REQUEST = 1;
    public final int SELECT_FILE = 1;
    Bitmap bitmap, decoded;
    public final int REQUEST_CAMERA = 0;
    int bitmap_size = 60; // range 1 - 100
    private static final String TAG = MainActivity.class.getSimpleName();

    /* 10.0.2.2 adalah IP Address localhost Emulator Android Studio. Ganti IP Address tersebut dengan
    IP Address Laptop jika di RUN di HP/Genymotion. HP/Genymotion dan Laptop harus 1 jaringan! */
    private String UPLOAD_URL = "http://10.0.2.2/android/upload_image/upload.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    SpotsDialog loading, re;

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
        kolomkomen = (RelativeLayout) findViewById(R.id.kolomkomentar);

        loading = new SpotsDialog(DetailAduan.this, "Sedang mengirim...");
        re = new SpotsDialog(DetailAduan.this, "Memperbarui...");

        mRecyclerview = (RecyclerView)findViewById(R.id.recycleKomentar);
        mItems = new ArrayList<>();
        mAdapter = new AdapterDataKomentar(DetailAduan.this, mItems);
        mManager = new LinearLayoutManager(DetailAduan.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mManager);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setNestedScrollingEnabled(false);

        id_aduan = bundle.getString("id");

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        id_user = pref.getString("id_user", "");

        fotoUser = (ImageView) findViewById(R.id.foto_user);
        fotoAduan = (ImageView) findViewById(R.id.foto_aduan);
        btnKirim = (CardView) findViewById(R.id.btnKirim);
        btnKategori = (ImageView) findViewById(R.id.btnStatus);
        btnTambahFoto = (ImageView) findViewById(R.id.tambah_foto_komentar);
        fotoKomen = (ImageView) findViewById(R.id.foto_komen);
        btnHapusFoto = (ImageView) findViewById(R.id.btnHapusfoto);

        namaUser = (TextView) findViewById(R.id.nama_user);
        level = (TextView) findViewById(R.id.level);
        tanggal = (TextView) findViewById(R.id.tanggal);
        isiAduan = (TextView) findViewById(R.id.isi_aduan);
        kategori = (TextView) findViewById(R.id.kategori);
        status = (TextView) findViewById(R.id.txtStatus);
        jmlkomen = (TextView) findViewById(R.id.jmlkomentar);

        komentar = (EditText) findViewById(R.id.komentar);
        rowdata = 0;

        swLayout = (SwipeRefreshLayout) findViewById(R.id.swLayout);
        swLayout.setColorSchemeResources(R.color.Error, R.color.Info, R.color.Warning);
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swLayout.setRefreshing(false);
                re.show();
                refresh();
                refreshAduan();
            }
        });

        namaUser.setText(bundle.getString("nama"));
        tanggal.setText(bundle.getString("tanggal"));
        isiAduan.setText(bundle.getString("aduan"));
        kategori.setText(bundle.getString("kategori"));

        if (bundle.getString("jmlkomen").equals("null")){
            jmlkomen.setText("0");
        }else{
            jmlkomen.setText(bundle.getString("jmlkomen"));
        }
        if (bundle.getString("level").equals("null")){
            level.setText("0");
        }else{
            level.setText(bundle.getString("level"));
        }

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER_THUMB+bundle.getString("foto_user"))
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
                }else if(fotoKomen.getVisibility() == View.GONE){
                    komen();
                }else if(fotoKomen.getVisibility() == View.VISIBLE){
                    fotokomen();
                }
            }
        });

        if (bundle.getString("status").equals("masuk")){
            btnKategori.setImageResource(R.drawable.ic_info_grey);
            status.setText("Terkirim");
        }else if (bundle.getString("status").equals("diverifikasi")){
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
            kolomkomen.setVisibility(View.GONE);
        }else if(bundle.getString("status").equals("bukan kewenangan")){
            btnKategori.setImageResource(R.drawable.ic_info_red);
            status.setText(bundle.getString("status"));
        }else if(bundle.getString("status").equals("sampah")){
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

        btnHapusFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoKomen.setVisibility(View.GONE);
                btnHapusFoto.setVisibility(view.GONE);
            }
        });
        btnTambahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        refreshAduan();
        loadKomentar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.menuRefresh) {
            re.show();
            refreshAduan();
            refresh();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        if (!(fotoKomen.getVisibility() == View.GONE) || !komentar.getText().toString().isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Perhatian")
                    .setMessage("Apakah anda yakin akan kembali?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        }else{
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!(fotoKomen.getVisibility() == View.GONE) || !komentar.getText().toString().isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Perhatian")
                    .setMessage("Apakah anda yakin akan kembali?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        }else{
            finish();
        }
    }

    private void refreshAduan(){
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_DETAIL_ADUAN+id_aduan, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("volley", "response : "+response.toString());
                        try {
                            re.hide();
                            JSONObject data = response.getJSONObject(0);
                            status.setText(data.getString("status"));
                            level.setText(data.getString("jmladuan"));
                            jmlkomen.setText(data.getString("jmlkomen"));

                            if (status.getText().equals("masuk")){
                                btnKategori.setImageResource(R.drawable.ic_info_grey);
                                status.setText("Terkirim");
                            }else if (status.getText().equals("diverifikasi")){
                                btnKategori.setImageResource(R.drawable.ic_info_blue);
                            }else if(status.getText().equals("didisposisikan")){
                                btnKategori.setImageResource(R.drawable.ic_info_yellow);
                            }else if(status.getText().equals("penanganan")){
                                btnKategori.setImageResource(R.drawable.ic_info_orange);
                            }else if(status.getText().equals("selesai")){
                                btnKategori.setImageResource(R.drawable.ic_info_green);
                                kolomkomen.setVisibility(View.GONE);
                            }else if(status.getText().equals("bukan kewenangan")){
                                btnKategori.setImageResource(R.drawable.ic_info_red);
                                kolomkomen.setVisibility(View.GONE);
                            }else if(status.getText().equals("sampah")){
                                btnKategori.setImageResource(R.drawable.ic_info_red);
                                kolomkomen.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley", "error : "+error.getMessage());
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            //snackBar(R.string.error_koneksi, R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    private void loadKomentar(){
        loadkomem.setVisibility(View.VISIBLE);
        loaderror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_KOMENTAR+id_aduan, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        rowdata = response.length();
                        mItems.clear();
                        loadkomem.setVisibility(View.GONE);
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i< response.length(); i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataKomentar md = new ModelDataKomentar();
                                md.setId_komentar(data.getString("id_komentar"));
                                md.setKomentar(data.getString("komentar"));
                                md.setFoto(data.getString("foto"));
                                md.setRole(data.getString("role"));
                                md.setTanggal(data.getString("dibuat"));
                                md.setId_user(data.getString("id_user"));
                                md.setNama_user(data.getString("nama_user"));
                                md.setThumb_user(data.getString("thumb_user"));
                                md.setId_opd(data.getString("id_opd"));
                                md.setSingkatan(data.getString("singkatan"));
                                md.setThumb_opd(data.getString("thumb_opd"));
                                md.setId_admin(data.getString("id_admin"));
                                md.setThumb_admin(data.getString("thumb_admin"));
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

    private void reloadNewKomentar(){
        loaderror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_KOMENTAR+id_aduan, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadkomem.setVisibility(View.GONE);
                        Log.d("volley", "response : "+response.toString());
                        for (int i = mItems.size(); i< response.length(); i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataKomentar md = new ModelDataKomentar();
                                md.setId_komentar(data.getString("id_komentar"));
                                md.setKomentar(data.getString("komentar"));
                                md.setFoto(data.getString("foto"));
                                md.setRole(data.getString("role"));
                                md.setTanggal(data.getString("dibuat"));
                                md.setId_user(data.getString("id_user"));
                                md.setNama_user(data.getString("nama_user"));
                                md.setThumb_user(data.getString("thumb_user"));
                                md.setId_opd(data.getString("id_opd"));
                                md.setSingkatan(data.getString("singkatan"));
                                md.setThumb_opd(data.getString("thumb_opd"));
                                md.setId_admin(data.getString("id_admin"));
                                md.setThumb_admin(data.getString("thumb_admin"));
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
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }

    private void refresh(){
        //loadkomem.setVisibility(View.VISIBLE);
        //loaderror.setVisibility(View.GONE);
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_KOMENTAR+id_aduan, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        rowdata = response.length();
                        mItems.clear();
                        re.hide();
                        loadkomem.setVisibility(View.GONE);
                        Log.d("volley", "response : "+response.toString());
                        for (int i = 0; i< response.length(); i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ModelDataKomentar md = new ModelDataKomentar();
                                md.setId_komentar(data.getString("id_komentar"));
                                md.setKomentar(data.getString("komentar"));
                                md.setFoto(data.getString("foto"));
                                md.setRole(data.getString("role"));
                                md.setTanggal(data.getString("dibuat"));
                                md.setId_user(data.getString("id_user"));
                                md.setNama_user(data.getString("nama_user"));
                                md.setThumb_user(data.getString("thumb_user"));
                                md.setId_opd(data.getString("id_opd"));
                                md.setSingkatan(data.getString("singkatan"));
                                md.setThumb_opd(data.getString("thumb_opd"));
                                md.setId_admin(data.getString("id_admin"));
                                md.setThumb_admin(data.getString("thumb_admin"));
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
                        //loadkomem.setVisibility(View.GONE);
                        //loaderror.setVisibility(View.VISIBLE);
                        re.hide();
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

    //memilih foto
    private void selectImage() {
        //foto_aduan.setImageResource(0);
        final CharSequence[] items = {"Buka kamera", "Pilih dari galeri"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailAduan.this);
        builder.setTitle("Tambahkan foto");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Buka kamera")) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Pilih dari galeri")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    //menyimpan foto ke direktori internal
    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "laporbupati");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "komentar_" + timeStamp + ".jpg");

        return mediaFile;
    }

    //menampilkan foto terpiih
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        fotoKomen.setImageBitmap(decoded);
        fotoKomen.setVisibility(View.VISIBLE);
        btnHapusFoto.setVisibility(View.VISIBLE);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    //menentukan sumber foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e("CAMERA", fileUri.getPath());

                    bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    setToImageView(getResizedBitmap(bitmap, 256));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(DetailAduan.this.getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, 256));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //mengubah foto ke base64
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //mengirim komentar dengan foto
    private void fotokomen() {
        //menampilkan progress dialog
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_TAMBAH_KOMENTAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("status");

                            if (status.equals("1")) {
                                Log.e("v Add", data.toString());
                                reloadNewKomentar();
                                snackBar("Komentar berhasil ditambahkan", R.color.colorPrimary);
                                komentar.setText("");
                                refreshAduan();
                                fotoKomen.setVisibility(View.GONE);
                                btnHapusFoto.setVisibility(View.GONE);
                            } else {
                                snackBar("Komentar gagal ditambahkan", R.color.Error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();
                        //menampilkan toast
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                        }
                        //Toast.makeText(TulisAduan.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        //Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
                final String id_user = pref.getString("id_user", "");

                //menambah parameter yang di kirim ke web servis
                params.put("foto", getStringImage(decoded));
                params.put("komentar", komentar.getText().toString());
                params.put("id_aduan", id_aduan);
                params.put("id_user", id_user);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    //mengirim komentar tanpa foto
    private void komen() {
        //menampilkan progress dialog
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_TAMBAH_KOMENTAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("status");

                            if (status.equals("1")) {
                                Log.e("v Add", data.toString());
                                reloadNewKomentar();
                                snackBar("Komentar berhasil ditambahkan", R.color.colorPrimary);
                                komentar.setText("");
                                refreshAduan();
                            } else {
                                snackBar("Komentar gagal ditambahkan", R.color.Error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();
                        //menampilkan toast
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                        }
                        //Toast.makeText(TulisAduan.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        //Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
                final String id_user = pref.getString("id_user", "");

                //menambah parameter yang di kirim ke web servis
                params.put("komentar", komentar.getText().toString());
                params.put("id_aduan", id_aduan);
                params.put("id_user", id_user);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}
