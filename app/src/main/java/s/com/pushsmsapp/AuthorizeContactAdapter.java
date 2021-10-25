package s.com.pushsmsapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AuthorizeContactAdapter extends RecyclerView.Adapter<AuthorizeContactAdapter.MyViewHolder> {

    private List<AuthorizeSenderClass> dataSet;
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

    public AuthorizeContactAdapter(Context context, List<AuthorizeSenderClass> data) {
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_authorize_contact, parent, false);

        AuthorizeContactAdapter.MyViewHolder myViewHolder = new AuthorizeContactAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final AuthorizeContactAdapter.MyViewHolder holder, final int listPosition) {

        holder.txtSender.setText("Contact: " + dataSet.get(listPosition).getAuthsender());
        if(!TextUtils.isEmpty(dataSet.get(listPosition).getAuthmessage())) {
            holder.txtMsg.setText("Message: " + dataSet.get(listPosition).getAuthmessage());
            holder.txtMsg.setVisibility(View.VISIBLE);
        } else {
            holder.txtMsg.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(dataSet.get(listPosition).getAuthmsgnocontain())) {
            holder.txtMsgNoContain.setText("Message Not Contain: " + dataSet.get(listPosition).getAuthmsgnocontain());
            holder.txtMsgNoContain.setVisibility(View.VISIBLE);
        } else {
            holder.txtMsgNoContain.setVisibility(View.GONE);
        }

        db = new DatabaseHandler(context);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*db.deleteAuthContact(dataSet.get(listPosition));
                notifyDataSetChanged();*/
                AuthorizeContactList.deleteItem(listPosition);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AuthorizeContactList.updateItem(listPosition);
                Intent intent = new Intent(context, AuthorizeSender.class);
                intent.putExtra("from", "edit");
                intent.putExtra("pos", AuthorizeContactList.data.get(listPosition).get_authid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
