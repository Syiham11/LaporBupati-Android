package id.go.pekalongankab.laporbupati.Adapter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.go.pekalongankab.laporbupati.AduanSaya;
import id.go.pekalongankab.laporbupati.MainActivity;
import id.go.pekalongankab.laporbupati.Model.ModelDataPemberitahuan;
import id.go.pekalongankab.laporbupati.R;

/**
 * Created by erik on 2/18/2018.
 */

public class AdapterDataPemberitahuan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ModelDataPemberitahuan> mItems;
    public static final int NOTIFICATION_ID = 1;

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

        holderDataPemberitahuan.cardPemberitahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("source", "notif");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Lapor Bupati")
                        .setContentText("Aduan anda telah diverifikasi")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        });
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class HolderDataPemberitahuan extends RecyclerView.ViewHolder {
        public TextView pemberitahuan, tanggal;
        CardView cardPemberitahuan;

        public HolderDataPemberitahuan(View view) {
            super(view);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            pemberitahuan = (TextView) view.findViewById(R.id.pemberitahuan);
            cardPemberitahuan = (CardView) view.findViewById(R.id.cardPemberitahuan);


            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(context, DetailOpd.class);
                    //context.startActivity(i);
                }
            });*/
        }
    }

}
