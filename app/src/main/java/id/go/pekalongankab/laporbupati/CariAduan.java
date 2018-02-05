package id.go.pekalongankab.laporbupati;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CariAduan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_aduan);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setTitle("Hasil pencarian : "+bundle.getString("query"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
