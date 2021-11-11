package s.com.pushsmsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import static s.com.pushsmsapp.Constants.email_password;
import static s.com.pushsmsapp.Constants.owner_emailid;

public class AllEmailListActivity extends AppCompatActivity {

    RecyclerView emaillistrecyclerview;
    EmailListAdapter emailListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static List<EmailDetailsListModel> data;
    DatabaseHandler db;
    FloatingActionButton floatingActionButton;
    String email_address = "";
    List<EmailDetailsListModel> emailDetailsListModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_email_list);
        db = new DatabaseHandler(this);

        init();

        try {
//            email_address

            email_address = getIntent().getStringExtra("email_address");
            Log.d("email_address", email_address);

            if (!email_address.isEmpty()) {
                showDialog(AllEmailListActivity.this);
            }
        } catch (Exception e) {

        }
    }

    private void init() {
        data = db.getAllEmailidDetails();

        floatingActionButton = findViewById(R.id.fab);
        emaillistrecyclerview = findViewById(R.id.emaillistrecyclerview);
        emaillistrecyclerview.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        emaillistrecyclerview.setLayoutManager(layoutManager);

        emailListAdapter = new EmailListAdapter(AllEmailListActivity.this, data);
        emaillistrecyclerview.setAdapter(emailListAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialog(AllEmailListActivity.this);
                Intent intent = new Intent(AllEmailListActivity.this, SendEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDialog(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.test_email_dialog, null);
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(view);

//        dialog.setContentView(R.layout.test_email_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button txt_cancel = (Button) view.findViewById(R.id.buttonCancel);
        TextInputEditText editTextText = (TextInputEditText) view.findViewById(R.id.editTextText);
        editTextText.setText("Test");
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button txt_send = (Button) view.findViewById(R.id.buttonAdd);
        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                dialog.cancel();
                sendMessage(editTextText.getText().toString());
            }
        });

        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllEmailListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendMessage(String body_str) {
        emailDetailsListModels = db.getAllEmailidDetails();
        List<String> emailList = new ArrayList<String>();

        for (int i = 0; i < emailDetailsListModels.size(); i++) {
            emailList.add(emailDetailsListModels.get(i).getEmailid());
        }
        String[] recipients = emailList.toArray(new String[emailList.size()]);
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail(owner_emailid, email_password);
        email.m.set_from(owner_emailid);
        email.m.setBody(body_str);
        email.m.set_to(recipients);
        email.m.set_subject("New SMS message");
        email.execute();
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Mail m;
        AllEmailListActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    String str_body = m.getBody();
                    String str_subject = m.get_subject();
                    String[] str_emailid = m.get_to();
                    String str_emai = str_emailid[0];
                    Log.d("mail_data", String.valueOf(m));
                    Log.d("str_emai", str_emai);
                    Log.d("str_subject", str_subject);
                    Log.d("str_body", str_body);

                    db.updateEmailContact(new EmailDetailsListModel(str_emai, str_body, str_subject.toString()));
                    emailDetailsListModels = db.getAllEmailidDetails();
//                    emailListAdapter = new EmailListAdapter(AllEmailListActivity.this, data);
//                    emaillistrecyclerview.setAdapter(emailListAdapter);
                    emailListAdapter.notifyDataSetChanged();
                    Log.d("emailDetailsListModels", String.valueOf(emailDetailsListModels.size()));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            displayMessage("Email sent.");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            displayMessage("Email failed to send.");
                        }
                    });
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        displayMessage("Authentication failed.");
                    }
                });
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        displayMessage("Email failed to send.");
                    }
                });
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        displayMessage("Unexpected error occured.");
//                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }
                });
//              displayMessage("Unexpected error occured.");
//                Toast.makeText(activity, "\"Unexpected error occured.\"", Toast.LENGTH_SHORT).show();

                return false;
            }
        }
    }

    public void displayMessage(String message) {
//        Snackbar.make(findViewById(R.id.submit), message, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}