package id.go.pekalongankab.halobupati;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.go.pekalongankab.halobupati.Util.AppController;
import id.go.pekalongankab.halobupati.Util.ServerAPI;

public class TulisAduan extends AppCompatActivity {

    ImageView foto_pengirim, foto_aduan, tambahfoto, btnHapusfoto;
    EditText isi_aduan;
    Bitmap bitmap, decoded;
    Spinner kategori;
    String txtkategori;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    private static final String TAG = MainActivity.class.getSimpleName();

    /* 10.0.2.2 adalah IP Address localhost Emulator Android Studio. Ganti IP Address tersebut dengan
    IP Address Laptop jika di RUN di HP/Genymotion. HP/Genymotion dan Laptop harus 1 jaringan! */
    private String UPLOAD_URL = "http://10.0.2.2/android/upload_image/upload.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tulis_aduan);
        getSupportActionBar().setTitle("Buat Aduan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foto_pengirim = (ImageView) findViewById(R.id.foto_pengirim);
        foto_aduan = (ImageView) findViewById(R.id.foto_aduan);
        kategori = (Spinner) findViewById(R.id.spnkategori);
        isi_aduan = (EditText)findViewById(R.id.isi_aduan);
        tambahfoto = (ImageView) findViewById(R.id.tambah_foto);
        btnHapusfoto = (ImageView) findViewById(R.id.btnHapusfoto);

        kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //ketika memilih salah satu item
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                txtkategori = kategori.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        btnHapusfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto_aduan.setVisibility(View.GONE);
                btnHapusfoto.setVisibility(View.GONE);
            }
        });

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        final String foto = pref.getString("foto","");

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+foto)
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(foto_pengirim);

        tambahfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
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
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!(isi_aduan.getText().toString().isEmpty())){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        /*MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Query Hint");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuKirim) {
            if (isi_aduan.getText().toString().isEmpty()){
                isi_aduan.setFocusable(true);
                isi_aduan.setError("Aduan tidak boleh kosong!");
                snackBar("Aduan tidak boleh kosong!", R.color.Error);
            }else if (isi_aduan.getText().toString().length() < 50 ){
                isi_aduan.setFocusable(true);
                isi_aduan.setError("Aduan harus lebih dari 50 karakter!");
                snackBar("Aduan harus lebih dari 50 karakter!", R.color.Error);
            }else if (kategori.getSelectedItem().toString().equals("Pilih Kategori")){
                kategori.setFocusable(true);
                snackBar("Kategori harus dipilih!", R.color.Error);
            }else if (kategori.getSelectedItem().toString().equals("Infrastruktur")){
                if (foto_aduan.getVisibility() == View.GONE){
                    snackBar("Aduan kategori Infrastruktur harus ada foto!", R.color.Error);
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("Perhatian")
                            .setMessage("Apakah anda yakin akan mengirimkan aduan?")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadImage();
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .show();
                }
            }else if(kategori.getSelectedItem().toString().equals("Non Infrastruktur")){
                if (foto_aduan.getVisibility() == View.GONE){
                    new AlertDialog.Builder(this)
                            .setTitle("Perhatian")
                            .setMessage("Apakah anda yakin akan mengirimkan aduan?")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadAduan();
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .show();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("Perhatian")
                            .setMessage("Apakah anda yakin akan mengirimkan aduan?")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadImage();
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        foto_aduan.setImageBitmap(decoded);
        btnHapusfoto.setVisibility(View.VISIBLE);
        foto_aduan.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Sedang mengirim...", "Harap Tunggu", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_TAMBAH_ADUAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("status");

                            if (status.equals("1")) {
                                Log.e("v Add", data.toString());
                                alerter();

                            } else {
                                snackBar("Aduan gagal dikirim!", R.color.Error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();
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
                params.put("foto_aduan", getStringImage(decoded));
                params.put("aduan", isi_aduan.getText().toString());
                params.put("kategori", txtkategori);
                params.put("id_user", id_user);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void uploadAduan() {
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Sedang mengirim...", "Harap Tunggu", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_TAMBAH_ADUAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("status");

                            if (status.equals("1")) {
                                Log.e("v Add", data.toString());
                                alerter();
                            } else {
                                snackBar("Aduan gagal dikirim!", R.color.Error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();
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
                params.put("aduan", isi_aduan.getText().toString());
                params.put("kategori", txtkategori);
                params.put("id_user", id_user);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }

    private void alerter(){
        new AlertDialog.Builder(this)
                .setTitle("Laporan berhasil dikirim")
                .setMessage("Aduan akan diperiksa oleh admin sebelum ditampilkan ke publik")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(TulisAduan.this, MainActivity.class);
                        finish();
                        startActivity(i);
                    }
                })
                .show();
    }
}
