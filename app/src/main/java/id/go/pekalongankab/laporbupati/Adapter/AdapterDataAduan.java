package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import id.go.pekalongankab.laporbupati.DetailAduan;
import id.go.pekalongankab.laporbupati.Model.ModelDataAduan;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

/**
 * Created by server02 on 12/12/2017.
 */

public class AdapterDataAduan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ModelDataAduan> mItems;

    public AdapterDataAduan(List<ModelDataAduan> mItems, Context context) {
        this.mItems = mItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_aduan, parent, false);
        return new HolderDataAduan(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ModelDataAduan md = mItems.get(position);
        final HolderDataAduan holderDataAduan = (HolderDataAduan) holder;
        holderDataAduan.nama_user.setText(md.getNama_user());
        holderDataAduan.tanggal.setText(md.getTanggal());
        if (md.getAduan().length() <= 200){
            holderDataAduan.aduan.setText(md.getAduan());
        }else{
            String aduanp = md.getAduan().substring(0, 200);
            holderDataAduan.aduan.setText(aduanp+" ... Selengkapnya");
        }
        holderDataAduan.kategori.setText(md.getKategori());
        Glide.with(context).load(ServerAPI.URL_FOTO_USER+md.getFoto_user())
                .thumbnail(0.5f)
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holderDataAduan.foto_user);
        if (md.getFoto_aduan().isEmpty()){
            ((HolderDataAduan) holder).foto_aduan.setVisibility(View.GONE);
        }else{
            ((HolderDataAduan) holder).foto_aduan.setVisibility(View.VISIBLE);
            Glide.with(context).load(ServerAPI.URL_FOTO_ADUAN+md.getFoto_aduan())
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataAduan.foto_aduan);
        }

        holderDataAduan.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailAduan.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                i.putExtra("id", md.getId_aduan());
                i.putExtra("foto_user", md.getFoto_user());
                i.putExtra("nama", md.getNama_user());
                i.putExtra("level", "Level");
                i.putExtra("tanggal", md.getTanggal());
                i.putExtra("aduan", md.getAduan());
                i.putExtra("foto_aduan", md.getFoto_aduan());
                i.putExtra("kategori", md.getKategori());
                i.putExtra("status", md.getStatus());
                i.putExtra("katgori", md.getKategori());
                i.putExtra("long", md.getLongi());
                i.putExtra("lat", md.getLati());
                context.startActivity(i);
            }
        });

        if (md.getStatus().equals("diverifikasi")){
            holderDataAduan.btninfo.setImageResource(R.drawable.ic_info_blue);
        }else if(md.getStatus().equals("didisposisikan")){
            holderDataAduan.btninfo.setImageResource(R.drawable.ic_info_yellow);
        }else if(md.getStatus().equals("penanganan")){
            holderDataAduan.btninfo.setImageResource(R.drawable.ic_info_orange);
        }else if(md.getStatus().equals("selesai")){
            holderDataAduan.btninfo.setImageResource(R.drawable.ic_info_green);
        }else if(md.getStatus().equals("bukan kewenangan")){
            holderDataAduan.btninfo.setImageResource(R.drawable.ic_info_red);
        }

        holderDataAduan.btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class HolderDataAduan extends RecyclerView.ViewHolder {
        ModelDataAduan md;
        public TextView nama_user, tanggal, aduan, kategori;
        public ImageView foto_user, foto_aduan, btninfo;
        CardView cardView;

        public HolderDataAduan(View view) {
            super(view);
            nama_user = (TextView) view.findViewById(R.id.pengirim);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            aduan = (TextView) view.findViewById(R.id.isi_aduan);
            kategori = (TextView) view.findViewById(R.id.kategori);
            foto_aduan = (ImageView) view.findViewById(R.id.foto_aduan);
            foto_user = (ImageView) view.findViewById(R.id.foto_user);
            cardView = (CardView) view.findViewById(R.id.cardAduan);
            btninfo = (ImageView) view.findViewById(R.id.btnInfo);
        }
    }
}
