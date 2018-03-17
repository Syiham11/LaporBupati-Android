package id.go.pekalongankab.laporbupati;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import dmax.dialog.SpotsDialog;
import id.go.pekalongankab.laporbupati.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Petunjnuk extends Fragment {

    FloatingActionButton fab;
    WebView webView;
    RelativeLayout loadweb;

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
        loadweb = (RelativeLayout) view_petunjuk.findViewById(R.id.loadweb);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        loadweb.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadweb.setVisibility(View.GONE);
                webView.loadUrl(Uri.parse("file:///android_asset/petunjuk.html").toString());
            }
        }, 1000);

        return view_petunjuk;
    }

}
