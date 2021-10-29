package s.com.pushsmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;


public class SendEmailActivity extends AppCompatActivity {

    Button btnSubmit;
    private EditText user;
    private EditText pass;
    private EditText subject;
    private EditText body;
    private EditText recipient;
    public static DatabaseHandler db;
    List<EmailDetailsListModel> emailDetailsListModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email2);

        db = new DatabaseHandler(this);
        init();
    }

    private void init() {
        btnSubmit = findViewById(R.id.btnSubmit);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);
        recipient = (EditText) findViewById(R.id.recipient);

        user.setText("noreplyobjectcodes@gmail.com");
        pass.setText("Project$$567");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String[] recipients = {recipient.getText().toString()};
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail(user.getText().toString(), pass.getText()
                .toString());
        email.m.set_from(user.getText().toString());
        email.m.setBody(body.getText().toString());
        email.m.set_to(recipients);
        email.m.set_subject(subject.getText().toString());
        email.execute();
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Mail m;
        SendEmailActivity activity;

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

                    db.addEmaildetail(new EmailDetailsListModel(str_emai, str_body, str_subject.toString()));
                    emailDetailsListModels = db.getAllEmailidDetails();
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