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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import id.go.pekalongankab.laporbupati.DetailAduan;
import id.go.pekalongankab.laporbupati.LihatFoto;
import id.go.pekalongankab.laporbupati.Model.ModelDataKomentar;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;
import id.go.pekalongankab.laporbupati.Util.Tanggal;

/**
 * Created by erik on 12/02/2018.
 */

public class AdapterDataKomentar extends RecyclerView.Adapter<AdapterDataKomentar.HolderDataKomentar> {

    private List<ModelDataKomentar> mItems;
    private Context context;
    Tanggal tanggal = new Tanggal();

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
        final ModelDataKomentar md = mItems.get(position);
        HolderDataKomentar holderDataKomentar = (HolderDataKomentar) holder;


        if (md.getRole().equals("1")){
            holderDataKomentar.role1.setVisibility(View.VISIBLE);
            holderDataKomentar.role2.setVisibility(View.GONE);
            holderDataKomentar.komentar1.setText(md.getKomentar());
            holderDataKomentar.tglkomentar1.setText(tanggal.tanggal(md.getTanggal()));

            if (md.getId_admin() != "null"){
                holderDataKomentar.nama_pengirim1.setText("Admin Lapor Bupati");
                Glide.with(context).load(R.mipmap.ic_launcher)
                        .thumbnail(0.5f)
                        .crossFade()
                        .error(R.drawable.ic_no_image_male_white)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holderDataKomentar.foto_pengirim1);
            }else if(md.getId_opd() != "null"){
                holderDataKomentar.nama_pengirim1.setText(md.getSingkatan());
                Glide.with(context).load(ServerAPI.URL_FOTO_OPD_THUMB+md.getThumb_opd())
                        .thumbnail(0.5f)
                        .crossFade()
                        .error(R.drawable.ic_no_image_male_white)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holderDataKomentar.foto_pengirim1);
            }

            if (md.getFoto().isEmpty()){
                holderDataKomentar.fotoKomen1.setVisibility(View.GONE);
            }else{
                holderDataKomentar.fotoKomen1.setVisibility(View.VISIBLE);
                Glide.with(context).load(ServerAPI.URL_FOTO_KOMEN+md.getFoto())
                        .thumbnail(0.5f)
                        .crossFade()
                        .error(R.drawable.ic_no_image_male_white)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holderDataKomentar.fotoKomen1);
            }

            holderDataKomentar.fotoKomen1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, LihatFoto.class);
                    i.putExtra("source", "komentar");
                    i.putExtra("foto_komen", md.getFoto());
                    context.startActivity(i);
                }
            });
        }else{
            holderDataKomentar.role1.setVisibility(View.GONE);
            holderDataKomentar.role2.setVisibility(View.VISIBLE);
            holderDataKomentar.komentar2.setText(md.getKomentar());
            holderDataKomentar.tglkomentar2.setText(tanggal.tanggal(md.getTanggal()));
            holderDataKomentar.nama_pengirim2.setText(md.getNama_user());

            Glide.with(context).load(ServerAPI.URL_FOTO_USER_THUMB+md.getThumb_user())
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.ic_no_image_male_white)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataKomentar.foto_pengirim2);

            if (md.getFoto().isEmpty()){
                holderDataKomentar.fotoKomen2.setVisibility(View.GONE);
            }else{
                holderDataKomentar.fotoKomen2.setVisibility(View.VISIBLE);
                Glide.with(context).load(ServerAPI.URL_FOTO_KOMEN+md.getFoto())
                        .thumbnail(0.5f)
                        .crossFade()
                        .error(R.drawable.ic_no_image_male_white)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holderDataKomentar.fotoKomen2);
            }

            holderDataKomentar.fotoKomen2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, LihatFoto.class);
                    i.putExtra("source", "komentar");
                    i.putExtra("foto_komen", md.getFoto());
                    context.startActivity(i);
                }
            });
        }

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class HolderDataKomentar extends RecyclerView.ViewHolder {
        TextView komentar1, tglkomentar1, nama_pengirim1, komentar2, tglkomentar2, nama_pengirim2;
        ImageView fotoKomen1, foto_pengirim1, fotoKomen2, foto_pengirim2;
        RelativeLayout role1, role2;
        ModelDataKomentar md;

        public HolderDataKomentar(View view){
            super(view);

            komentar1 = (TextView) view.findViewById(R.id.komentar1);
            tglkomentar1 = (TextView) view.findViewById(R.id.tanggal1);
            nama_pengirim1 = (TextView) view.findViewById(R.id.nama_pengirim1);
            fotoKomen1 = (ImageView) view.findViewById(R.id.foto_komen1);
            foto_pengirim1 = (ImageView) view.findViewById(R.id.foto_pengirim1);

            komentar2 = (TextView) view.findViewById(R.id.komentar2);
            tglkomentar2 = (TextView) view.findViewById(R.id.tanggal2);
            nama_pengirim2 = (TextView) view.findViewById(R.id.nama_pengirim2);
            fotoKomen2 = (ImageView) view.findViewById(R.id.foto_komen2);
            foto_pengirim2 = (ImageView) view.findViewById(R.id.foto_pengirim2);
            role1 = (RelativeLayout) view.findViewById(R.id.role1);
            role2 = (RelativeLayout) view.findViewById(R.id.role2);

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
