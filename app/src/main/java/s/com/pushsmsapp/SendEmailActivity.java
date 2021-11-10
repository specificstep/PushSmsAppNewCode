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
import com.google.android.material.textfield.TextInputEditText;

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

    TextInputEditText editTextGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email2);

        db = new DatabaseHandler(this);
        init();
    }

    private void init() {
        editTextGoal = findViewById(R.id.editTextGoal);
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
//                sendMessage();
                Intent intent = new Intent(SendEmailActivity.this, AllEmailListActivity.class);
                intent.putExtra("email_address", editTextGoal.getText().toString().trim());
                startActivity(intent);
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SendEmailActivity.this, AllEmailListActivity.class);
        intent.putExtra("email_address", "");
        startActivity(intent);
    }
}