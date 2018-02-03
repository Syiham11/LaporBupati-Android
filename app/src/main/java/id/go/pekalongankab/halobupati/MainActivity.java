package id.go.pekalongankab.halobupati;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import id.go.pekalongankab.halobupati.Util.ServerAPI;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txnama, txemail;
    ImageView imgfoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        txnama = (TextView)header.findViewById(R.id.nama);
        txemail = (TextView)header.findViewById(R.id.email);
        imgfoto = (ImageView)header.findViewById(R.id.foto);

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        final String id_user = pref.getString("id_user", "");
        final String nama_user = pref.getString("nama", "");
        final String email = pref.getString("email", "");
        final String foto = pref.getString("foto", "");

        txnama.setText(nama_user);
        txemail.setText(email);
        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_USER+foto)
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgfoto);

        imgfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Profil.class);
                i.putExtra("nama_user", nama_user);
                i.putExtra("id_user", id_user);
                startActivity(i);
            }
        });

        //inisialisasi fragment
        Aduan fraduan = new Aduan();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fraduan);
        fragmentTransaction.commit();

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
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Cari aduan...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
        if (id == R.id.menuEdit) {
            Intent i = new Intent(MainActivity.this, TulisAduan.class);
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

        if (id == R.id.nav_tulis_aduan) {
            Intent i = new Intent(MainActivity.this, TulisAduan.class);
            startActivity(i);
        } else if (id == R.id.nav_aduan) {
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
        } else if (id == R.id.nav_update) {

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
        editor.putInt("login", 0);
        editor.clear();
        editor.commit();
        // pergi ke login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
