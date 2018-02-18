package id.go.pekalongankab.laporbupati.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import id.go.pekalongankab.laporbupati.Model.ModelDataAduanSaya;
import id.go.pekalongankab.laporbupati.Model.ModelDataPemberitahuan;
import id.go.pekalongankab.laporbupati.OnLoadMoreListener;
import id.go.pekalongankab.laporbupati.R;

/**
 * Created by erik on 2/18/2018.
 */

public class AdapterDataPemberitahuan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Context context;
    private List<ModelDataPemberitahuan> mItems;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public AdapterDataPemberitahuan(RecyclerView recyclerView, List<ModelDataPemberitahuan> mItems, Context context) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pemberitahuan, parent, false);
            return new HolderDataPemberitahuan(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterDataPemberitahuan.HolderDataPemberitahuan) {
            final ModelDataPemberitahuan md = mItems.get(position);
            HolderDataPemberitahuan holderDataPemberitahuan = (HolderDataPemberitahuan) holder;
            holderDataPemberitahuan.pemberitahuan.setText(md.getPemberitahuan());
            holderDataPemberitahuan.tanggal.setText(md.getTanggal());

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
