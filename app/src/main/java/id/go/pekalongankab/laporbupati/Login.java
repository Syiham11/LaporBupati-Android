package id.go.pekalongankab.laporbupati;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

import static android.content.ContentValues.TAG;

public class Login extends Activity {

    Button btnLogin;
    EditText txttelp, txtpass;
    TextView daftar;
    private SharedPreferences pref;
    SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        changeStatusBarColor();
        btnLogin = (Button)findViewById(R.id.btnLogin);
        txttelp = (EditText)findViewById(R.id.txttelp);
        txtpass = (EditText)findViewById(R.id.txtpass);
        daftar = (TextView) findViewById(R.id.linkdaftar) ;
        dialog = new SpotsDialog(Login.this, "Login...");

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
            }
        });

        TextView linkforget =(TextView)findViewById(R.id.linkforget);
        linkforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, ForgetPassword.class);
                startActivity(i);
            }
        });

        // ngecek apakah user udah login atau belum
        pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        final int login = pref.getInt("login", 0);
        if (login == 1) {
            // kalau user ternyata udah login langsung di lempar ke main activity tanpa harus login terlebih dahulu
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

        // ketika login button di klik
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String telp = txttelp.getText().toString().replaceAll("'","");
                String password = txtpass.getText().toString().replaceAll("'", "");

                // ngecek apakah inputannya kosong atau tidak
                if (telp.isEmpty()){
                    // jika inputan kosong tampilkan pesan
                    snackBar("Harap isi Nomor telepon anda!", R.color.Error);
                    txttelp.setFocusable(true);
                }else if (password.isEmpty()){
                    // jika inputan kosong tampilkan pesan
                    snackBar("Harap isi password anda!", R.color.Error);
                    txtpass.setFocusable(true);
                } else {
                    // login user
                    checkLogin(telp, password);
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
        dialog.show();
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
                    if (code.equals("2")){
                        dialog.hide();
                        snackBar("Akun anda belum diaktifkan! silahkan periksa email anda untuk mengaktifkan", R.color.Error);
                    }else if (code.equals("1")) {
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
                        dialog.hide();
                        snackBar("Nomor telepon atau kata sandi salah! Harap periksa kembali", R.color.Error);
                    }
                } catch (JSONException e) {
                    // JSON error
                    dialog.hide();
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                dialog.hide();
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
                params.put("telp", email);
                params.put("pass", password);

                return params;
            }
        };
        // menggunakan fungsi volley adrequest yang kita taro di appcontroller
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }

    //setting dialog
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Peringatan!");
        builder.setMessage("Aplikasi ini membutuhkan izin kamera dan penyimpanan agar berfungsi secara maksimal.");
        builder.setPositiveButton("KE PENGATURAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    //ke pengaturan
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
