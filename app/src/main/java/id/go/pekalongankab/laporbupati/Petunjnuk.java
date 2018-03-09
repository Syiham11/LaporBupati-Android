package id.go.pekalongankab.laporbupati;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Petunjnuk extends Fragment {

    FloatingActionButton fab;
    WebView webView;
    SpotsDialog dialog;

    public Petunjnuk() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view_petunjuk = inflater.inflate(R.layout.fragment_petunjnuk, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Petunjuk");
        fab = (FloatingActionButton) ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab.hide();
        webView = (WebView) view_petunjuk.findViewById(R.id.webPetunjuk);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());

        dialog = new SpotsDialog(getActivity(), "Memuat data...");

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.hide();
                webView.loadUrl(Uri.parse("file:///android_asset/petunjuk.html").toString());
            }
        }, 1000);

        return view_petunjuk;
    }

}
