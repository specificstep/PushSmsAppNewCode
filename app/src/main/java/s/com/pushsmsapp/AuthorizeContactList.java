package s.com.pushsmsapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import s.com.pushsmsapp.RestApi.ApiManager;
import s.com.pushsmsapp.RestApi.ApiResponseInterface;
import s.com.pushsmsapp.RestApi.AppConstant;

public class AuthorizeContactList extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    public static RecyclerView recyclerView;
    public static TextView txtNoData;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static List<AuthorizeSenderClass> data;
    public static List<AuthorizeSenderClass> dataImport;
    public static List<AuthorizeSenderClass> dataImportMerge;
    public static DatabaseHandler db;
    //FloatingActionButton add;
    public static Activity activity;
    //Button btnImport, btnExport;
    ContactsService myService;
    ApiManager mApiManager;
    private ApiResponseInterface mInterFace;
    List<Integer> importPos;
    BottomSheetDialog dialog;
    String preferenceStr = "PushSMSPref";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_contact_list);

        initialize();

        /*add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AuthorizeSender.class);
                intent.putExtra("from", "add");
                intent.putExtra("pos", 0);
                startActivity(intent);
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.contains("mobile") && !TextUtils.isEmpty(sharedPreferences.getString("mobile", ""))) {
                    makeImportContacts();
                } else {
                    showAddMobileDialog();
                }
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.contains("mobile") && !TextUtils.isEmpty(sharedPreferences.getString("mobile", ""))) {
                    if(data.size()>0) {
                        Gson gson = new Gson();
                        String contact = gson.toJson(data);
                        makeExportContacts(contact);
                    }
                } else {
                    showAddMobileDialog();
                }
            }
        });*/

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(AuthorizeContactList.this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Import")
                .setResId(R.drawable.ic_import)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xffd84315)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Export")
                .setResId(R.drawable.ic_import_export)
                .setIconNormalColor(0xff283593)
                .setIconPressedColor(0xff1a237e)
                .setLabelColor(0xff283593)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Add Contact")
                .setResId(R.drawable.ic_add)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(3)
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(3)
        ;
        rfabHelper = new RapidFloatingActionHelper(
                AuthorizeContactList.this,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();

    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        //Toast.makeText(AuthorizeContactList.this, "clicked label: " + position, Toast.LENGTH_SHORT).show();
        if(position == 0) {
            if(sharedPreferences.contains("mobile") && !TextUtils.isEmpty(sharedPreferences.getString("mobile", ""))) {
                makeImportContacts();
            } else {
                showAddMobileDialog();
            }
        } else if(position == 1) {
            if(sharedPreferences.contains("mobile") && !TextUtils.isEmpty(sharedPreferences.getString("mobile", ""))) {
                if(data.size()>0) {
                    Gson gson = new Gson();
                    String contact = gson.toJson(data);
                    makeExportContacts(contact);
                }
            } else {
                showAddMobileDialog();
            }
        } else if(position == 2) {
            Intent intent = new Intent(getApplicationContext(),AuthorizeSender.class);
            intent.putExtra("from", "add");
            intent.putExtra("pos", 0);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        //Toast.makeText(AuthorizeContactList.this, "clicked icon: " + position, Toast.LENGTH_SHORT).show();
        if(position == 0) {
            if(sharedPreferences.contains("mobile") && !TextUtils.isEmpty(sharedPreferences.getString("mobile", ""))) {
                makeImportContacts();
            } else {
                showAddMobileDialog();
            }
        } else if(position == 1) {
            if(sharedPreferences.contains("mobile") && !TextUtils.isEmpty(sharedPreferences.getString("mobile", ""))) {
                if(data.size()>0) {
                    Gson gson = new Gson();
                    String contact = gson.toJson(data);
                    makeExportContacts(contact);
                }
            } else {
                showAddMobileDialog();
            }
        } else if(position == 2) {
            Intent intent = new Intent(getApplicationContext(),AuthorizeSender.class);
            intent.putExtra("from", "add");
            intent.putExtra("pos", 0);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    public void initialize() {
        activity = AuthorizeContactList.this;
        sharedPreferences = getSharedPreferences(preferenceStr, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        dialog = new BottomSheetDialog(AuthorizeContactList.this,R.style.MyAlertDialogStyle);
        myService = new ContactsService("PushSMS");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_authrize);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        db = new DatabaseHandler(this);
        //add = (FloatingActionButton) findViewById(R.id.btnAddAuthorize);
        data = new ArrayList<AuthorizeSenderClass>();
        txtNoData = (TextView) findViewById(R.id.txtNoDataAuthorize);
        /*btnImport = (Button) findViewById(R.id.btnImportAuthorize);
        btnExport = (Button) findViewById(R.id.btnExportAuthorize);*/

        rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.activity_main_rfal);
        rfaBtn = (RapidFloatingActionButton) findViewById(R.id.activity_main_rfab);

    }

    public void showAddMobileDialog() {
        final View dialogView = View.inflate(this,R.layout.popup_add_mobile,null);
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

        Button submit = (Button) dialog.findViewById(R.id.btnAddContactSubmit);
        Button cancel = (Button) dialog.findViewById(R.id.btnAddContactCancel);
        final EditText edtMobile = (EditText) dialog.findViewById(R.id.edtAddContactMobile);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(edtMobile.getText().toString())) {
                    myEdit.putString("mobile", edtMobile.getText().toString());
                    myEdit.commit();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AuthorizeContactList.this, "Enter Mobile No", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        /*dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });*/

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    /*private void revealShow(View dialogView, boolean b, final BottomSheetDialog dialog) {

        final View view = dialogView.findViewById(R.id.lnrAcledgerDialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (fab.getX() + (fab.getWidth()/2));
        int cy = (int) (fab.getY())+ fab.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(1000);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(1000);
            anim.start();
        }

    }*/

    public static void deleteItem(int pos) {
        db.deleteAuthContact(data.get(pos));
        data.remove(pos);
        adapter.notifyDataSetChanged();
        data = db.getAllAuthContacts();
        if(data.size()>0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            adapter = new AuthorizeContactAdapter(activity, data);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNetwork();
        data = db.getAllAuthContacts();
        Collections.reverse(data);
        if(data.size()>0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            adapter = new AuthorizeContactAdapter(AuthorizeContactList.this, data);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }
    }

    public void makeImportContacts() {
        String mobile = sharedPreferences.getString("mobile", "");
        if (Constants.checkInternet(AuthorizeContactList.this)) {
            mApiManager.makeCompanyRequest(mobile, "", AppConstant.GET_AUTHORIZE_CONTACTS);
        } else {
            //Constants.showNoInternetDialog(TransactionListDetailActivity.this);
        }
    }

    public void makeExportContacts(String str) {
        String mobile = sharedPreferences.getString("mobile", "");
        if (Constants.checkInternet(AuthorizeContactList.this)) {
            mApiManager.makeCompanyRequest(mobile, str, AppConstant.ADD_AUTHORIZE_CONTACTS);
        } else {
            //Constants.showNoInternetDialog(TransactionListDetailActivity.this);
        }
    }

    private void setupNetwork() {
        mInterFace = new ApiResponseInterface() {

            @Override
            public void isError(String errorMsg, int errorCode) {
                if (errorCode == AppConstant.ERROR_CODE) {
                    // error from server
                    //Toast.makeText(HomeActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                } else if (errorCode == AppConstant.NO_NETWORK_ERROR_CODE) {
                    // show no network screen with refresh button
                    Constants.showNoInternetDialog(AuthorizeContactList.this);
                }
            }

            @Override
            public void isUserDisabled(String errorMsg, int errorCode) {

            }

            @Override
            public void isSuccess(Object response, int ServiceCode) {
                if (ServiceCode == AppConstant.ADD_AUTHORIZE_CONTACTS) {
                    String res = (String) response;
                    try {
                        JSONObject object = new JSONObject(res);
                        Toast.makeText(AuthorizeContactList.this, object.getString("msg"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (ServiceCode == AppConstant.GET_AUTHORIZE_CONTACTS) {
                    String res = (String) response;
                    try {
                        JSONObject object = new JSONObject(res);
                        if(object.getString("status").equals("1")) {
                            String message = object.getString("msg");
                            Gson gson = new Gson();
                            dataImport = new ArrayList<>();
                            dataImport = gson.fromJson(message, new TypeToken<ArrayList<AuthorizeSenderClass>>(){}.getType());
                            if(dataImport.size()>0) {
                                importPos = new ArrayList<>();
                                db.deleteAllAuthContacts();
                                data.clear();
                                for(AuthorizeSenderClass r:dataImport){
                                    System.out.println(r);
                                    db.addAuthContact(r);
                                }
                                data = db.getAllAuthContacts();
                                if(data.size()>0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    txtNoData.setVisibility(View.GONE);
                                    adapter = new AuthorizeContactAdapter(AuthorizeContactList.this, data);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    txtNoData.setVisibility(View.VISIBLE);
                                }
                            }
                            Toast.makeText(AuthorizeContactList.this, "Import Successfully", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
        };
        mApiManager = new ApiManager(AuthorizeContactList.this, mInterFace);
    }

}
