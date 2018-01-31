package id.go.pekalongankab.halobupati;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailOpd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_opd);
        getSupportActionBar().setTitle("Detail Opd");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
