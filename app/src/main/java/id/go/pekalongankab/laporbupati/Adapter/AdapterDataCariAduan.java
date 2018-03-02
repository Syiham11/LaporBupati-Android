package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import id.go.pekalongankab.laporbupati.DetailAduan;
import id.go.pekalongankab.laporbupati.Model.ModelDataAduanSaya;
import id.go.pekalongankab.laporbupati.Model.ModelDataCariAduan;
import id.go.pekalongankab.laporbupati.OnLoadMoreListener;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

/**
 * Created by ERIK on 02-Feb-18.
 */

public class AdapterDataCariAduan extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<ModelDataCariAduan> mItems;

    public AdapterDataCariAduan(List<ModelDataCariAduan> mItems, Context context) {
        this.mItems = mItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cari_aduan, parent, false);
        return new HolderDataCariAduan(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ModelDataCariAduan md = mItems.get(position);
        HolderDataCariAduan holderDataCariAduan = (HolderDataCariAduan) holder;
        holderDataCariAduan.nama_user.setText(md.getNama_user());
        holderDataCariAduan.tanggal.setText(md.getTanggal());
        if (md.getAduan().length() <= 200){
            holderDataCariAduan.aduan.setText(md.getAduan());
        }else{
            String aduanp = md.getAduan().substring(0, 200);
            holderDataCariAduan.aduan.setText(aduanp+" ... Selengkapnya");
        }
        holderDataCariAduan.kategori.setText(md.getKategori());
        Glide.with(context).load(ServerAPI.URL_FOTO_USER+md.getFoto_user())
                .thumbnail(0.5f)
                .crossFade()
                .error(R.drawable.ic_no_image_male_white)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holderDataCariAduan.foto_user);
        if (md.getFoto_aduan().isEmpty()){
            ((HolderDataCariAduan) holder).foto_aduan.setVisibility(View.GONE);
        }else{
            ((HolderDataCariAduan) holder).foto_aduan.setVisibility(View.VISIBLE);
            Glide.with(context).load(ServerAPI.URL_FOTO_ADUAN+md.getFoto_aduan())
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataCariAduan.foto_aduan);
        }

        holderDataCariAduan.cardView.setOnClickListener(new View.OnClickListener() {
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
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class HolderDataCariAduan extends RecyclerView.ViewHolder {
        ModelDataAduanSaya md;
        public TextView nama_user, tanggal, aduan, kategori;
        public ImageView foto_user, foto_aduan;
        public CardView cardView;

        public HolderDataCariAduan(View view) {
            super(view);
            nama_user = (TextView) view.findViewById(R.id.pengirim);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            aduan = (TextView) view.findViewById(R.id.isi_aduan);
            kategori = (TextView) view.findViewById(R.id.kategori);
            foto_aduan = (ImageView) view.findViewById(R.id.foto_aduan);
            foto_user = (ImageView) view.findViewById(R.id.foto_user);
            cardView = (CardView) view.findViewById(R.id.cardAduan);

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
