package s.com.pushsmsapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MessageHistoryAdapter extends RecyclerView.Adapter<MessageHistoryAdapter.MyViewHolder> {

    private List<MesaageHistory> dataSet;
    DatabaseHandler db;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtSender;
        TextView txtMsg;
        //TextView txtStatus;
        ImageView delete, status;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtSender = (TextView) itemView.findViewById(R.id.txtHistorySender);
            this.txtMsg = (TextView) itemView.findViewById(R.id.txtHistoryMsg);
            this.delete = (ImageView) itemView.findViewById(R.id.btnDeleteHistory);
            //this.txtStatus = (TextView) itemView.findViewById(R.id.txtHistoryStatus);
            this.status = (ImageView) itemView.findViewById(R.id.btnStatus);
        }
    }

    public MessageHistoryAdapter(Context context, List<MesaageHistory> data) {
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_message_history, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.txtSender.setText(dataSet.get(listPosition).getSender());
        holder.txtMsg.setText(URLDecoder.decode(dataSet.get(listPosition).getMessage()));
        //holder.txtStatus.setText("Status: " + dataSet.get(listPosition).getStatus());
        if(dataSet.get(listPosition).getStatus().equals("success")) {
            holder.status.setBackground(context.getDrawable(R.drawable.ic_done_all));
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorGreen)));
        } else {
            holder.status.setBackground(context.getDrawable(R.drawable.ic_done));
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorDarkGrey)));
        }

        db = new DatabaseHandler(context);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageHistoryActivity.deleteItem(listPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
