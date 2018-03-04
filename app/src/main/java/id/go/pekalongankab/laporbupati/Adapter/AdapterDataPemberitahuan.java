package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.go.pekalongankab.laporbupati.Model.ModelDataPemberitahuan;
import id.go.pekalongankab.laporbupati.R;

/**
 * Created by erik on 2/18/2018.
 */

public class AdapterDataPemberitahuan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ModelDataPemberitahuan> mItems;

    public AdapterDataPemberitahuan(List<ModelDataPemberitahuan> mItems, Context context) {
        this.mItems = mItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pemberitahuan, parent, false);
        return new HolderDataPemberitahuan(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ModelDataPemberitahuan md = mItems.get(position);
        HolderDataPemberitahuan holderDataPemberitahuan = (HolderDataPemberitahuan) holder;
        holderDataPemberitahuan.pemberitahuan.setText(md.getPemberitahuan());
        holderDataPemberitahuan.tanggal.setText(md.getTanggal());
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class HolderDataPemberitahuan extends RecyclerView.ViewHolder {
        public TextView pemberitahuan, tanggal;

        public HolderDataPemberitahuan(View view) {
            super(view);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            pemberitahuan = (TextView) view.findViewById(R.id.pemberitahuan);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(context, DetailOpd.class);
                    //context.startActivity(i);
                }
            });
        }
    }

}
