package id.go.pekalongankab.halobupati.Adapter;

import android.content.Context;
import android.content.Intent;
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

import id.go.pekalongankab.halobupati.DetailOpd;
import id.go.pekalongankab.halobupati.Model.ModelDataAduan;
import id.go.pekalongankab.halobupati.Model.ModelDataOpd;
import id.go.pekalongankab.halobupati.OnLoadMoreListener;
import id.go.pekalongankab.halobupati.R;
import id.go.pekalongankab.halobupati.Util.ServerAPI;

/**
 * Created by ERIK on 01-Feb-18.
 */

public class AdapterDataAduan extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Context context;
    private List<ModelDataAduan> mItems;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public AdapterDataAduan(RecyclerView recyclerView, List<ModelDataAduan> mItems, Context context) {
        this.mItems = mItems;
        this.context = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_aduan, parent, false);
            return new AdapterDataAduan.HolderDataAduan(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new AdapterDataAduan.HolderDataAduan(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterDataAduan.HolderDataAduan) {
            ModelDataAduan md = mItems.get(position);
            AdapterDataAduan.HolderDataAduan holderDataAduan = (AdapterDataAduan.HolderDataAduan) holder;
            holderDataAduan.nama_user.setText(md.getNama_user());
            holderDataAduan.tanggal.setText(md.getTanggal());
            holderDataAduan.aduan.setText(md.getAduan());
            holderDataAduan.kategori.setText(md.getKategori());
            Glide.with(context).load(ServerAPI.URL_FOTO_USER+md.getFoto_user())
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataAduan.foto_user);
            Glide.with(context).load(ServerAPI.URL_FOTO_ADUAN+md.getFoto_aduan())
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataAduan.foto_aduan);
        } else if (holder instanceof AdapterDataAduan.LoadingViewHolder) {
            AdapterDataAduan.LoadingViewHolder loadingViewHolder = (AdapterDataAduan.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    private class HolderDataAduan extends RecyclerView.ViewHolder {
        ModelDataAduan md;
        public TextView nama_user, tanggal, aduan, kategori;
        public ImageView foto_user, foto_aduan;

        public HolderDataAduan(View view) {
            super(view);
            nama_user = (TextView) view.findViewById(R.id.pengirim);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            aduan = (TextView) view.findViewById(R.id.isi_aduan);
            kategori = (TextView) view.findViewById(R.id.kategori);
            foto_aduan = (ImageView) view.findViewById(R.id.foto_aduan);
            foto_user = (ImageView) view.findViewById(R.id.foto_user);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailOpd.class);
                    context.startActivity(i);
                }
            });
        }
    }

}
