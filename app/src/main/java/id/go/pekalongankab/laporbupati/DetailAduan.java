package id.go.pekalongankab.laporbupati;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import id.go.pekalongankab.laporbupati.Util.ServerAPI;

public class DetailAduan extends AppCompatActivity {

    ImageView fotoUser, fotoAduan, btnKirim, btnLike;
    TextView namaUser, level, tanggal, isiAduan, kategori;
    EditText komentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_aduan);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setTitle("Aduan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
