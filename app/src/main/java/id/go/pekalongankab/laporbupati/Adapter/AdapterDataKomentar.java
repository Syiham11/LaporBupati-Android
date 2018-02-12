package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
        holderDataKomentar.tglkomentar.setText(md.getTanggal());
        /*if (Integer.parseInt(holder.md.getNilai_siswa()) <= Integer.parseInt(holder.md.getMin())){
            holder.tvnama_siswa.setTextColor(R.color.bg_screen1);
            holder.tvno_daftar.setTextColor(R.color.bg_screen1);
        }*/

        /*holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("detail", "Detail Sekolah");
                Intent intent = new Intent(context, DetailSekolah.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });*/

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class HolderDataKomentar extends RecyclerView.ViewHolder {
        TextView komentar, tglkomentar;
        //CardView card;
        ModelDataKomentar md;

        public HolderDataKomentar(View view){
            super(view);

            komentar = (TextView) view.findViewById(R.id.komentar);
            tglkomentar = (TextView) view.findViewById(R.id.tanggalkomen);

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
