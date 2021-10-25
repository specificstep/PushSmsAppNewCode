package s.com.pushsmsapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MessageHistoryActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public LinearLayout lnrDeleteAll;
    public static TextView txtNoData;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static List<MesaageHistory> data;
    public static DatabaseHandler db;
    public static Activity activity;
    SwipeRefreshLayout swipeRefreshLayout;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);

        initialize();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data = db.getAllContacts();
                Collections.reverse(data);
                if(data.size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                    adapter = new MessageHistoryAdapter(MessageHistoryActivity.this, data);
                    recyclerView.setAdapter(adapter);
                    lnrDeleteAll.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    lnrDeleteAll.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        lnrDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteListDialog();
            }
        });

    }

    public void initialize() {

        activity = MessageHistoryActivity.this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_history);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        db = new DatabaseHandler(this);

        data = new ArrayList<MesaageHistory>();
        txtNoData = (TextView) findViewById(R.id.txtNoDataHistory);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        lnrDeleteAll = (LinearLayout) findViewById(R.id.lnrDeleteAllHistory);
        dialog = new BottomSheetDialog(MessageHistoryActivity.this,R.style.MyAlertDialogStyle);

    }

    public void showDeleteListDialog() {
        final View dialogView = View.inflate(this,R.layout.dialog_delete_all,null);
        dialog.setContentView(dialogView);
        FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);

        final LinearLayout btnDeleteAll = (LinearLayout) dialog.findViewById(R.id.lnrDeleteAll);
        final LinearLayout lnrDeleteAllSeven = (LinearLayout) dialog.findViewById(R.id.lnrDeleteAllSeven);
        final LinearLayout lnrDeleteAllFifteen = (LinearLayout) dialog.findViewById(R.id.lnrDeleteAllFifteen);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnDeleteAllCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteAll.setBackground(getResources().getDrawable(R.drawable.round_corner_color_primary));
                new CountDownTimer(2000,1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                        db.deleteAllContacts();
                        recyclerView.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                        lnrDeleteAll.setVisibility(View.GONE);
                    }
                }.start();
            }
        });

        lnrDeleteAllSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrDeleteAllSeven.setBackground(getResources().getDrawable(R.drawable.round_corner_color_primary));
                new CountDownTimer(2000,1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                        dialog.dismiss();
                        db.deleteSevenDaysContacts();
                        data = db.getAllContacts();
                        Collections.reverse(data);
                        if(data.size()>0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                            adapter = new MessageHistoryAdapter(MessageHistoryActivity.this, data);
                            recyclerView.setAdapter(adapter);
                            lnrDeleteAll.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.VISIBLE);
                            lnrDeleteAll.setVisibility(View.GONE);
                        }
                    }
                }.start();
            }
        });

        lnrDeleteAllFifteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrDeleteAllFifteen.setBackground(getResources().getDrawable(R.drawable.round_corner_color_primary));
                new CountDownTimer(2000,1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                        dialog.dismiss();
                        db.deleteFifteenDaysContacts();
                        data = db.getAllContacts();
                        Collections.reverse(data);
                        if(data.size()>0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                            adapter = new MessageHistoryAdapter(MessageHistoryActivity.this, data);
                            recyclerView.setAdapter(adapter);
                            lnrDeleteAll.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.VISIBLE);
                            lnrDeleteAll.setVisibility(View.GONE);
                        }
                    }
                }.start();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public static void deleteItem(int pos) {
        db.deleteContact(data.get(pos));
        data.remove(pos);
        adapter.notifyDataSetChanged();
        data = db.getAllContacts();
        Collections.reverse(data);
        if(data.size()>0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            adapter = new MessageHistoryAdapter(activity, data);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        data = db.getAllContacts();
        Collections.reverse(data);
        if(data.size()>0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            adapter = new MessageHistoryAdapter(MessageHistoryActivity.this, data);
            recyclerView.setAdapter(adapter);
            lnrDeleteAll.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
            lnrDeleteAll.setVisibility(View.GONE);
        }
    }

}
