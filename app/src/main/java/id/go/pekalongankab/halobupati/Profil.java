package id.go.pekalongankab.halobupati;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import id.go.pekalongankab.halobupati.Util.ServerAPI;

import static android.R.id.list;

public class Profil extends AppCompatActivity {

    ImageView imgprofil, bg;
    TextView txno_ktp, txjk, txtmp_lahir, txno_telp, txalamat, txbio, txdibuat, txnama, txemail ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgprofil = (ImageView)findViewById(R.id.imgfotoUser);
        txno_ktp = (TextView)findViewById(R.id.tvKtp);
        txjk = (TextView)findViewById(R.id.tvJk);
        txtmp_lahir = (TextView)findViewById(R.id.tvLahir);
        txdibuat = (TextView)findViewById(R.id.tvBergabung);
        txno_telp = (TextView)findViewById(R.id.tvTelp);
        txalamat = (TextView)findViewById(R.id.tvAlamat);
        txbio = (TextView)findViewById(R.id.tvbio);
        txnama = (TextView)findViewById(R.id.tvnamaUser);
        txemail = (TextView)findViewById(R.id.tvEmail);
        bg = (ImageView) findViewById(R.id.bg);

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        final String id_user = pref.getString("id_user", "");
        final String no_ktp = pref.getString("no_ktp","");
        final String jk = pref.getString("jk","");
        final String tmp_lahir = pref.getString("tmp_lahir","");
        final String tgl = pref.getString("tgl","");
        final String password = pref.getString("password","");
        final String no_telepon = pref.getString("no_telepon","");
        final String alamat = pref.getString("alamat","");
        final String bio = pref.getString("bio","");
        final String dibuat = pref.getString("dibuat","");
        final String nama = pref.getString("nama","");
        final String email = pref.getString("email","");
        final String foto = pref.getString("foto","");

        txnama.setText(nama);
        txbio.setText(bio);
        txno_ktp.setText(no_ktp);

        if (jk.equals("L")){
            txjk.setText("Laki-laki");
        }else{
            txjk.setText("Perempuan");
        }

        txtmp_lahir.setText(tmp_lahir+", "+tgl);
        txno_telp.setText(no_telepon);
        txalamat.setText(alamat);
        txemail.setText(email);

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+foto)
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgprofil);

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+foto)
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bg);

        changeStatusBarColor();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
        if (id == R.id.menuEdit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
}
