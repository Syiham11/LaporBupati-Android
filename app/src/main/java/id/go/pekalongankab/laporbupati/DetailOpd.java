package id.go.pekalongankab.laporbupati;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import id.go.pekalongankab.laporbupati.Util.ServerAPI;

public class DetailOpd extends AppCompatActivity {

    TextView txopd, txsingkatan, txnama_kepala, txalamat, txno_telp, txemail, txfax, txweb, txdesc;
    ImageView foto_opd, bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_opd);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txopd = (TextView) findViewById(R.id.txtnamaopd);
        txsingkatan = (TextView) findViewById(R.id.txtsingkatan);
        txnama_kepala = (TextView) findViewById(R.id.txtnama_kepala);
        txalamat = (TextView) findViewById(R.id.txtalamat);
        txno_telp = (TextView) findViewById(R.id.txtno_telp);
        txemail = (TextView) findViewById(R.id.txtemail);
        txfax = (TextView) findViewById(R.id.txtfax);
        txweb = (TextView) findViewById(R.id.txtwebsite);
        txdesc = (TextView) findViewById(R.id.txtdesc);
        foto_opd = (ImageView) findViewById(R.id.foto_opd);
        bg = (ImageView) findViewById(R.id.bg);

        txopd.setText(bundle.getString("opd"));
        txsingkatan.setText(bundle.getString("singkatan"));
        txnama_kepala.setText(bundle.getString("nama_kepala"));
        txalamat.setText(bundle.getString("alamat"));
        txno_telp.setText(bundle.getString("no_telp"));
        txemail.setText(bundle.getString("email"));
        if (bundle.getString("fax").equals("")){
            txfax.setText("Tidak ada");
        }else{
            txfax.setText(bundle.getString("fax"));
        }
        txweb.setText(bundle.getString("website"));
        txdesc.setText(bundle.getString("deskripsi"));

        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_OPD+bundle.getString("foto"))
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(foto_opd);
        Glide.with(getApplicationContext()).load(ServerAPI.URL_FOTO_OPD+bundle.getString("foto"))
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bg);

        changeStatusBarColor();


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
