package s.com.pushsmsapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EmailListAdapter extends RecyclerView.Adapter<EmailListAdapter.MyViewHolder> {

    private List<EmailDetailsListModel> dataSet;
    DatabaseHandler db;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtSender;
        TextView txtMsg;
        TextView txtMsgNoContain;
        ImageView delete, edit;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtSender = (TextView) itemView.findViewById(R.id.txtAuthSender);
            this.txtMsg = (TextView) itemView.findViewById(R.id.txtAuthMsg);
            this.txtMsgNoContain = (TextView) itemView.findViewById(R.id.txtAuthMsgNoContain);
            this.delete = (ImageView) itemView.findViewById(R.id.btnDeleteAuth);
            this.edit = (ImageView) itemView.findViewById(R.id.btnDeleteEdit);
        }
    }

    public EmailListAdapter(Context context, List<EmailDetailsListModel> data) {
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_authorize_contact, parent, false);

        EmailListAdapter.MyViewHolder myViewHolder = new EmailListAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final EmailListAdapter.MyViewHolder holder, final int listPosition) {

        holder.txtSender.setText("Contact: " + dataSet.get(listPosition).getEmailid());
        if (!TextUtils.isEmpty(dataSet.get(listPosition).getEmailbody())) {
            holder.txtMsg.setText("Message: " + dataSet.get(listPosition).getEmailbody());
            holder.txtMsg.setVisibility(View.VISIBLE);
        } else {
            holder.txtMsg.setVisibility(View.GONE);
        }


        db = new DatabaseHandler(context);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*db.deleteAuthContact(dataSet.get(listPosition));
                notifyDataSetChanged();*/
                db.deleteEmail(dataSet.get(listPosition));
                Intent intent = new Intent(context, AllEmailListActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

//        holder.edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //AuthorizeContactList.updateItem(listPosition);
//                Intent intent = new Intent(context, AuthorizeSender.class);
//                intent.putExtra("from", "edit");
//                intent.putExtra("pos", AuthorizeContactList.data.get(listPosition).get_authid());
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
