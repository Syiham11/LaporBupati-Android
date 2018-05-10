package id.go.pekalongankab.laporbupati;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.Util.PrefManager;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txnama, txemail;
    ImageView imgfoto, bgnav;
    View view;
    RequestQueue mRequestQueque;
    SpotsDialog dialog;
    public FloatingActionButton fab;
    String versi;
    PrefManager prefManager;
    Toolbar toolbar;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        Bundle bundle = getIntent().getExtras();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TulisAduan.class);
                startActivity(i);
            }
        });

        //mendapatkan versi
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versi = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        txnama = (TextView)header.findViewById(R.id.nama);
        txemail = (TextView)header.findViewById(R.id.email);
        imgfoto = (ImageView)header.findViewById(R.id.foto);
        bgnav = (ImageView)header.findViewById(R.id.bgNav);

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        final String id_user = pref.getString("id_user", "");
        final String nama_user = pref.getString("nama", "");
        final String email = pref.getString("email", "");
        final String foto = pref.getString("foto", "");
        final String jmladuan = pref.getString("jmladuan", "");

        txnama.setText(nama_user);
        txemail.setText(email);
        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+foto)
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgfoto);

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+foto)
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.bg_splash)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bgnav);

        imgfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Profil.class);
                i.putExtra("nama_user", nama_user);
                i.putExtra("id_user", id_user);
                i.putExtra("jmladuan", jmladuan);
                startActivity(i);
            }
        });

        //inisialisasi fragment
        if (bundle.getString("source").equals("login")){
            Aduan fraduan = new Aduan();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fraduan);
            fragmentTransaction.commit();
        }else{
            AduanSaya fraduan = new AduanSaya();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fraduan);
            fragmentTransaction.commit();
        }

        //saat aplikasi pertama kali dibuka
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()){
            prefManager.setFirstTimeLaunch(false);
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.fab))
                    .setBackButtonDismissEnabled(true)
                    .setBackgroundColour(Color.parseColor("#DC1CAF9A"))
                    .setPrimaryText("Mengirim aduan")
                    .setSecondaryText("Tekan tombol ini untuk memulai membuat aduan")
                    .show();

            /*final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(this)
                    .setPrimaryText("Menu search")
                    .setSecondaryText("Menu search");
            tapTargetPromptBuilder.setTarget(toolbar.getChildAt(0));
            tapTargetPromptBuilder.show();*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        changeStatusBarColor();

        /*navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notif, menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Telusuri aduan...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pindahKeCari(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuNotif) {
            Intent i = new Intent(MainActivity.this, Pemberitahuan.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_aduan) {
            Aduan fraduan = new Aduan();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fraduan);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_aduan_saya) {
            AduanSaya fraduansaya = new AduanSaya();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fraduansaya);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_opd) {
            Opd opd = new Opd();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, opd);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_masukan) {
            BugReport bug = new BugReport();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, bug);
            fragmentTransaction.commit();
        }  else if (id == R.id.nav_petunjuk) {
            Petunjnuk petunjuk = new Petunjnuk();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, petunjuk);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_tentang) {
            new AlertDialog.Builder(this)
                    .setTitle("Lapor Bupati")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage("versi "+versi+"\n\n" +
                            "Lapor Bupati merupakan sistem informasi pengaduan masyarakat berbasis android dan web yang digunakan sebagai sarana penyampaian laporan, keluhan, maupun aspirasi masyarakat Kabupaten Pekalongan.\n" +
                            "\n" +
                            "Lapor Bupati melibatkan partisipasi publik dan bersifat dua arah sehingga dapat tercipta komunikasia antara masyarakat dengan penyelenggara. Masyarakat dapat menyampaikan pengaduan yang nantinya akan ditindaklanjuti oleh Organisasi Perangkat Daerah (OPD) terkait. Lapor Bupati dikembangkan dalam rangka peningkatan kualitas pelayanan publik di Kabupaten Pekalongan.")
                    .setCancelable(false)
                    .setNegativeButton("OK", null)
                    .show();
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar?")
                    .setMessage("Anda yakin ingin keluar?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            logoutUser();
                        }
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    public void logoutUser() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id_user", "");
        editor.putString("no_ktp","");
        editor.putString("jk","");
        editor.putString("tmp_lahir","");
        editor.putString("tgl","");
        editor.putString("password","");
        editor.putString("no_telepon","");
        editor.putString("alamat","");
        editor.putString("bio","");
        editor.putString("dibuat","");
        editor.putString("nama","");
        editor.putString("email","");
        editor.putString("foto","");
        editor.putString("jmladuan","");
        editor.putInt("login", 0);
        editor.clear();
        editor.commit();
        // pergi ke login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void pindahKeCari(String query){
        Intent i = new Intent(MainActivity.this, CariAduan.class);
        i.putExtra("query", query);
        startActivity(i);
    }

    public void snackBar(View view, String pesan){
        Snackbar snackbar = Snackbar.make(view, pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Error));
        snackbar.show();
    }
}
