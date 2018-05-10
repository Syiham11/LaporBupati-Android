package id.go.pekalongankab.laporbupati;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.AppController;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

/**
 * A simple {@link Fragment} subclass.
 */
public class BugReport extends Fragment {

    FloatingActionButton fab;
    String email;
    TextView txemail, txmasukan;
    Button Kirim;
    private static final String TAG = MainActivity.class.getSimpleName();
    String tag_json_obj = "json_obj_req";
    SpotsDialog loading;

    public BugReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view_bug = inflater.inflate(R.layout.fragment_bug_report, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Laporkan Kesalahan");
        fab = (FloatingActionButton) ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab.hide();

        txemail = (TextView) view_bug.findViewById(R.id.email);
        txmasukan = (TextView) view_bug.findViewById(R.id.masukan);
        Kirim = (Button) view_bug.findViewById(R.id.btnKirim);

        loading = new SpotsDialog(getActivity(), "Mengirim masukan...");

        SharedPreferences pref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        email = pref.getString("email", "");
        txemail.setText(email);
        txemail.setEnabled(false);

        Kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txmasukan.getText().toString().isEmpty()){
                    txmasukan.setError("Harap isi masukan dan saran anda.");
                    snackBar("Harap isi masukan dan saran anda.", R.color.Error);
                }else{
                    kirimMasukan(txemail.getText().toString(), txmasukan.getText().toString());
                }
            }
        });


        return view_bug;
    }

    private void kirimMasukan(final String email, final String masukan){
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_KIRIM_MASUKAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("code");

                            if (status.equals("1")) {
                                Log.e("v Add", data.toString());
                                alerter();
                            } else {
                                snackBar("Masukan gagal dikirim!", R.color.Error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();
                        //menampilkan toast
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Tidak dapat terhubung ke server! periksa koneksi internet!", R.color.Error);
                        }
                        //Toast.makeText(TulisAduan.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        //Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("email", email);
                params.put("masukan", masukan);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    public void snackBar(int pesan, int color){
        Snackbar snackbar = Snackbar.make(getView(), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snackbar.show();
    }

    public void snackBar(String pesan, int color){
        Snackbar snackbar = Snackbar.make(getView(), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snackbar.show();
    }

    private void alerter(){
        new AlertDialog.Builder(getContext())
                .setTitle("Masukan anda telah kami terima")
                .setMessage("Terima kasih telah membantu kami memperbaiki Lapor Bupati")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        txmasukan.setText("");
                    }
                    })
                .show();
    }

}
