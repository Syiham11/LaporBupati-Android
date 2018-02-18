package id.go.pekalongankab.laporbupati;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

import static android.content.ContentValues.TAG;

public class Register extends Activity {

    TextView login;
    SpotsDialog dialog;
    EditText ktp, nama, telp, email, pass, ulangi;
    Button daftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ktp = (EditText) findViewById(R.id.txtKtp);
        nama = (EditText) findViewById(R.id.txtNama);
        telp = (EditText) findViewById(R.id.txtTelp);
        email = (EditText) findViewById(R.id.txtEmail);
        pass = (EditText) findViewById(R.id.txtpass);
        ulangi = (EditText) findViewById(R.id.txtUlangi);
        daftar = (Button) findViewById(R.id.btnDaftar);
        dialog = new SpotsDialog(Register.this, "Sedang mendaftar...");

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sktp = ktp.getText().toString();
                String snama = nama.getText().toString();
                String stelp = telp.getText().toString();
                String semail = email.getText().toString();
                String spass = pass.getText().toString();
                String sulangi = ulangi.getText().toString();

                if (sktp.isEmpty()){
                    snackBar("Harap isi nomor KTP anda!", R.color.Error);
                    ktp.setFocusable(true);
                }else if(snama.isEmpty()){
                    snackBar("Harap isi nama lengkap anda!", R.color.Error);
                    nama.setFocusable(true);
                }else if(stelp.isEmpty()){
                    snackBar("Harap isi nomor telepon anda!", R.color.Error);
                    telp.setFocusable(true);
                }else if(semail.isEmpty()){
                    snackBar("Harap isi email anda!", R.color.Error);
                    email.setFocusable(true);
                }else if(spass.isEmpty()){
                    snackBar("Harap isi kata sandi anda!", R.color.Error);
                    pass.setFocusable(true);
                }else if(sulangi.isEmpty()){
                    snackBar("Harap isi konfirmasi kata sandi anda!", R.color.Error);
                    ulangi.setFocusable(true);
                }else if(!(spass.equals(sulangi))){
                    snackBar("Kata sandi dan konfirmasi kata sandi tidak cocok!", R.color.Error);
                    ulangi.setFocusable(true);
                }else{
                    register(sktp, snama, stelp, semail, spass);
                }
            }
        });

        login = (TextView) findViewById(R.id.linklogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        changeStatusBarColor();
    }

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

    private void register(final String ktp, final String nama, final String telp, final String email, final String pass) {
        dialog.show();
        // Tag biasanya digunakan ketika ingin membatalkan request volley
        String tag_string_req = "req_daftar";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerAPI.URL_DAFTAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Daftar Response: " + response.toString());

                try
                {
                    JSONObject data = new JSONObject(response);
                    String code = data.getString("code");
                    // ngecek node error dari api
                    if (code.equals("1")) {
                        dialog.hide();
                        dialog();
                    } else {
                        // terjadi error dan tampilkan pesan error dari API
                        dialog.hide();
                        snackBar("Sayang sekali pendaftaran telah gagal!", R.color.Error);
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
                params.put("ktp", ktp);
                params.put("nama", nama);
                params.put("telp", telp);
                params.put("email", email);
                params.put("pass", pass);

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

    private void dialog(){
        new AlertDialog.Builder(this)
                .setTitle("Perhatian")
                .setMessage("Pendaftaran berhasil")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent intent = new Intent(Register.this,
                                Login.class);
                        startActivity(intent);
                    }
                })
                .show();
    }
}
