package id.go.pekalongankab.laporbupati;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

import static android.content.ContentValues.TAG;

public class Login extends Activity {

    Button btnLogin;
    ProgressDialog pd;
    EditText txtemail, txtpass;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        changeStatusBarColor();
        btnLogin = (Button)findViewById(R.id.btnLogin);
        txtemail = (EditText)findViewById(R.id.txtemail);
        txtpass = (EditText)findViewById(R.id.txtpass);
        pd = new ProgressDialog(Login.this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
            }
        });

        TextView linkforget =(TextView)findViewById(R.id.linkforget);
        linkforget.setClickable(true);
        linkforget.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.google.com'>klik di sini</a>";
        linkforget.setText(Html.fromHtml(text));

        // ngecek apakah user udah login atau belum
        pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        final int login = pref.getInt("login", 0);
        if (login == 1) {
            // kalau user ternyata udah login langsung di lempar ke main activity tanpa harus login terlebih dahulu
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getPermissionsgaleri();

        // ketika login button di klik
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = txtemail.getText().toString();
                String password = txtpass.getText().toString();

                // ngecek apakah inputannya kosong atau tidak
                if (email.isEmpty()){
                    // jika inputan kosong tampilkan pesan
                    snackBar("Harap isi email anda!", R.color.Error);
                    txtemail.setError("Harap isi email anda!");
                    txtemail.setFocusable(true);
                }else if (password.isEmpty()){
                    // jika inputan kosong tampilkan pesan
                    snackBar("Harap isi password anda!", R.color.Error);
                    txtpass.setError("Harap isi password anda!");
                    txtpass.setFocusable(true);
                } else {
                    // login user
                    checkLogin(email, password);
                }
            }

        });
    }

    /**
     * Making notification bar transparent
     */
    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void checkLogin(final String email, final String password) {
        pd.setMessage("Login...");
        pd.setCancelable(false);
        pd.show();
        // Tag biasanya digunakan ketika ingin membatalkan request volley
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerAPI.URL_Login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try
                {
                    JSONObject data = new JSONObject(response);
                    String code = data.getString("code");
                    // ngecek node error dari api
                    if (code.equals("1")) {
                        // user berhasil login
                        String id_user = data.getString("id_user");
                        String nama_user = data.getString("nama_user");
                        String no_ktp = data.getString("no_ktp");
                        String jk = data.getString("jk");
                        String tmp_lahir = data.getString("tmp_lahir");
                        String tgl_lahir = data.getString("tgl_lahir");
                        String password = data.getString("password");
                        String no_telepon = data.getString("no_telepon");
                        String alamat = data.getString("alamat");
                        String bio = data.getString("bio");
                        String dibuat = data.getString("dibuat");
                        String email = data.getString("email");
                        String foto = data.getString("foto");

                        // buat session user yang sudah login yang menyimpan nama,apikey,alamat dan email
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("id_user", id_user);
                        editor.putString("no_ktp",no_ktp);
                        editor.putString("jk",jk);
                        editor.putString("tmp_lahir",tmp_lahir);
                        editor.putString("tgl",tgl_lahir);
                        editor.putString("password",password);
                        editor.putString("no_telepon",no_telepon);
                        editor.putString("alamat",alamat);
                        editor.putString("bio",bio);
                        editor.putString("dibuat",dibuat);
                        editor.putString("nama",nama_user);
                        editor.putString("email",email);
                        editor.putString("foto",foto);
                        editor.putInt("login", 1);
                        editor.commit();

                        //jika sudah masuk ke mainactivity
                        Intent intent = new Intent(Login.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // terjadi error dan tampilkan pesan error dari API
                        pd.cancel();
                        snackBar("Email atau kata sandi salah! Harap periksa kembali", R.color.Error);
                    }
                } catch (JSONException e) {
                    // JSON error
                    pd.cancel();
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                pd.cancel();
                //cek error timeout, noconnection dan network error
                if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                    snackBar("Tidak dapat terhubung ke server! periksa koneksi internet anda.", R.color.Error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // kirim parameter ke server
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("pass", password);

                return params;
            }
        };
        // menggunakan fungsi volley adrequest yang kita taro di appcontroller
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    snackBar("Perizinan ditolak! Pergi ke pengaturan aplikasi untuk mengizinkan!", R.color.Error);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getPermissionsgaleri() {
    /* Check and Request permission */
        if(ContextCompat.checkSelfPermission(Login.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Login.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2 );
        }
    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }
}
