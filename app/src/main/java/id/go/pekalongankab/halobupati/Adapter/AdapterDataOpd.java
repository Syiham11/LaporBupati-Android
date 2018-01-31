package id.go.pekalongankab.halobupati.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
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
import id.go.pekalongankab.halobupati.Model.ModelDataOpd;
import id.go.pekalongankab.halobupati.OnLoadMoreListener;
import id.go.pekalongankab.halobupati.R;
import id.go.pekalongankab.halobupati.Util.ServerAPI;

/**
 * Created by server02 on 12/12/2017.
 */

public class AdapterDataOpd extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Context context;
    private List<ModelDataOpd> mItems;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public AdapterDataOpd(RecyclerView recyclerView, List<ModelDataOpd> mItems, Context context) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_opd, parent, false);
            return new HolderDataOpd(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderDataOpd) {
            ModelDataOpd md = mItems.get(position);
            HolderDataOpd holderDataOpd = (HolderDataOpd) holder;
            String url = "http://"+ServerAPI.IP+"/halobupati/files/opd/source/"+md.getFoto();
            holderDataOpd.id_opd.setText(md.getId_opd());
            holderDataOpd.opd.setText(md.getOpd());
            holderDataOpd.singkatan.setText(md.getSingkatan());
            holderDataOpd.alamat.setText(md.getAlamat());
            Glide.with(context).load(url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .override(300, 150)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataOpd.foto);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
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

    private class HolderDataOpd extends RecyclerView.ViewHolder {
        ModelDataOpd md;
        public TextView id_opd, opd, singkatan, alamat;
        public ImageView foto;

        public HolderDataOpd(View view) {
            super(view);
            id_opd = (TextView) view.findViewById(R.id.id_opd);
            opd = (TextView) view.findViewById(R.id.opd);
            singkatan = (TextView) view.findViewById(R.id.singkatan);
            alamat = (TextView) view.findViewById(R.id.alamat);
            foto = (ImageView) view.findViewById(R.id.foto);

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
