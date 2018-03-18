package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import id.go.pekalongankab.laporbupati.DetailAduan;
import id.go.pekalongankab.laporbupati.LihatFoto;
import id.go.pekalongankab.laporbupati.Model.ModelDataKomentar;
import id.go.pekalongankab.laporbupati.R;

/**
 * Created by erik on 12/02/2018.
 */

public class AdapterDataKomentar extends RecyclerView.Adapter<AdapterDataKomentar.HolderDataKomentar> {

    private List<ModelDataKomentar> mItems;
    private Context context;

    public AdapterDataKomentar(Context context, List<ModelDataKomentar> items){
        this.mItems = items;
        this.context = context;
    }

    // Inflate the layout when viewholder created
    @Override
    public HolderDataKomentar onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_komentar,parent,false);
        HolderDataKomentar holderDataKomentar = new HolderDataKomentar(layout);
        return holderDataKomentar;
    }

    // Bind data
    @Override
    public void onBindViewHolder(final HolderDataKomentar holder, final int position) {
        ModelDataKomentar md = mItems.get(position);
        HolderDataKomentar holderDataKomentar = (HolderDataKomentar) holder;
        holderDataKomentar.komentar.setText(md.getKomentar());
        holderDataKomentar.tglkomentar.setText("-- "+md.getTanggal()+" --");

        if (md.getFoto().isEmpty()){
            holderDataKomentar.fotoKomen.setVisibility(View.GONE);
        }else{
            holderDataKomentar.fotoKomen.setVisibility(View.VISIBLE);
        }

        holderDataKomentar.fotoKomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LihatFoto.class);
                i.putExtra("source", "komentar");
                context.startActivity(i);
            }
        });

        if (md.getRole().equals("1")){
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.setMargins(0,0,100,0);
            holderDataKomentar.role.setLayoutParams(layoutParams);
        }else{
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(100,0,0,0);
            holderDataKomentar.cardKomen.setCardBackgroundColor(Color.parseColor("#DC1CAF9A"));
            holderDataKomentar.komentar.setTextColor(Color.parseColor("#ffffff"));
            holderDataKomentar.role.setLayoutParams(layoutParams);
        }

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class HolderDataKomentar extends RecyclerView.ViewHolder {
        TextView komentar, tglkomentar;
        ImageView fotoKomen;
        CardView cardKomen;
        RelativeLayout role;
        ModelDataKomentar md;

        public HolderDataKomentar(View view){
            super(view);

            komentar = (TextView) view.findViewById(R.id.komentar);
            tglkomentar = (TextView) view.findViewById(R.id.tanggalkomen);
            fotoKomen = (ImageView) view.findViewById(R.id.fotoKomen);
            cardKomen = (CardView) view.findViewById(R.id.cardkomentar);
            role = (RelativeLayout) view.findViewById(R.id.relAlign);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detail = new Intent(context, DetailSekolah.class);
                    detail.putExtra("id", md.getId());
                    detail.putExtra("nama_sekolah", md.getNamaSekolah());
                    detail.putExtra("alamat_sekolah", md.getAlamatSekkolah());
                    detail.putExtra("telp_sekolah", md.getNo_sekolah());
                    detail.putExtra("min", md.getMin());
                    detail.putExtra("email_sekolah", md.getEmail_sekolah());
                    detail.putExtra("website_sekolah", md.getWebsite_sekolah());
                    detail.putExtra("kuota", md.getKuota());
                    detail.putExtra("info_umum", md.getInfo_umum());
                    //detail.putExtra("detail_siswa", md.getDetail_siswa());

                    context.startActivity(detail);
                }
            });*/
        }
    }
}
