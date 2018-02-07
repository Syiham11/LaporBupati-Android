package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import id.go.pekalongankab.laporbupati.DetailOpd;
import id.go.pekalongankab.laporbupati.Model.ModelDataOpd;
import id.go.pekalongankab.laporbupati.OnLoadMoreListener;
import id.go.pekalongankab.laporbupati.R;
import id.go.pekalongankab.laporbupati.Util.ServerAPI;

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
            final ModelDataOpd md = mItems.get(position);
            HolderDataOpd holderDataOpd = (HolderDataOpd) holder;
            holderDataOpd.id_opd.setText(md.getId_opd());
            holderDataOpd.opd.setText(md.getOpd());
            holderDataOpd.singkatan.setText(md.getSingkatan());
            holderDataOpd.alamat.setText(md.getAlamat());
            Glide.with(context).load(ServerAPI.URL_FOTO_OPD+md.getFoto())
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.drawable.no_image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holderDataOpd.foto);
            holderDataOpd.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("opd", md.getOpd());
                    bundle.putString("singkatan", md.getSingkatan());
                    bundle.putString("alamat", md.getAlamat());
                    bundle.putString("nama_kepala", md.getNamaKepala());
                    bundle.putString("deskripsi", md.getDeskripsi());
                    bundle.putString("no_telp", md.getNoTelp());
                    bundle.putString("email", md.getEmail());
                    bundle.putString("fax", md.getFax());
                    bundle.putString("website", md.getWebsite());
                    bundle.putString("foto", md.getFoto());
                    Intent i = new Intent(context, DetailOpd.class);
                    i.putExtras(bundle);
                    context.startActivity(i);
                }
            });
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
        public CardView cardView;

        public HolderDataOpd(View view) {
            super(view);
            id_opd = (TextView) view.findViewById(R.id.id_opd);
            opd = (TextView) view.findViewById(R.id.opd);
            singkatan = (TextView) view.findViewById(R.id.singkatan);
            alamat = (TextView) view.findViewById(R.id.alamat);
            foto = (ImageView) view.findViewById(R.id.foto);
            cardView = (CardView) view.findViewById(R.id.cardOpd);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }
    }
}
