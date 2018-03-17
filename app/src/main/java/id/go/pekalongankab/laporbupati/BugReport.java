package id.go.pekalongankab.laporbupati;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.go.pekalongankab.laporbupati.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BugReport extends Fragment {

    FloatingActionButton fab;

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

        return view_bug;
    }

}
