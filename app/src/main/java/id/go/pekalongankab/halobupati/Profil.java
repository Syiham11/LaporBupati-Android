package id.go.pekalongankab.halobupati;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Profil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setTitle(bundle.getString("nama_user"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
