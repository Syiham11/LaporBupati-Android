package id.go.pekalongankab.laporbupati;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.go.pekalongankab.laporbupati.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Petunjnuk extends Fragment {


    public Petunjnuk() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view_petunjuk = inflater.inflate(R.layout.fragment_petunjnuk, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Petunjuk");
        ((MainActivity) getActivity()).findViewById(R.id.fab).setVisibility(View.GONE);

        return view_petunjuk;
    }

}
