package s.com.pushsmsapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class AuthorizeSender extends AppCompatActivity {

    EditText edtSender, edtMsg, edtMsgNoContain;
    TextView submit;
    DatabaseHandler db;
    String from = "";
    int pos;
    TextInputLayout tilSender, tilMsg, tilMsgNoContain;
    AuthorizeSenderClass senderClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_sender);

        edtSender = (EditText) findViewById(R.id.auth_sender);
        edtMsg = (EditText) findViewById(R.id.auth_msg);
        edtMsgNoContain = (EditText) findViewById(R.id.auth_msg_no_contain);
        submit = (TextView) findViewById(R.id.auth_submit);
        tilSender = (TextInputLayout) findViewById(R.id.til_auth_sender);
        tilMsg = (TextInputLayout) findViewById(R.id.til_auth_msg);
        tilMsgNoContain = (TextInputLayout) findViewById(R.id.til_auth_msg_no_contain);
        db = new DatabaseHandler(this);

        try {
            from = getIntent().getStringExtra("from");
            pos = getIntent().getIntExtra("pos", 0);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if(from.equals("edit")) {
            senderClass = db.getAuthContact(pos);
            edtSender.setText(senderClass.getAuthsender());
            edtMsg.setText(senderClass.getAuthmessage());
            edtMsgNoContain.setText(senderClass.getAuthmsgnocontain());
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(varify()) {
                    if(from.equals("add")) {
                        db.addAuthContact(new AuthorizeSenderClass(edtSender.getText().toString(), edtMsg.getText().toString(), edtMsgNoContain.getText().toString()));
                        MainActivity.number = edtSender.getText().toString();
                        MainActivity.msg = edtMsg.getText().toString();
                        MainActivity.msgNoContain = edtMsgNoContain.getText().toString();
                        AuthorizeSender.this.finish();
                    } else if(from.equals("edit")) {
                        int status = db.updateAuthContact(new AuthorizeSenderClass(pos, edtSender.getText().toString(), edtMsg.getText().toString(), edtMsgNoContain.getText().toString()));
                        MainActivity.number = edtSender.getText().toString();
                        MainActivity.msg = edtMsg.getText().toString();
                        MainActivity.msgNoContain = edtMsgNoContain.getText().toString();
                        AuthorizeSender.this.finish();
                    }
                }
            }
        });

    }

    public boolean varify() {
        if(TextUtils.isEmpty(edtSender.getText().toString())) {
            tilSender.setError("Enter Sender");
            //Toast.makeText(getApplicationContext(),"Enter Sender",Toast.LENGTH_LONG).show();
            return false;
        }/* else if(TextUtils.isEmpty(edtMsg.getText().toString())) {
            Toast.makeText(getApplicationContext(),"Enter Message",Toast.LENGTH_LONG).show();
            return false;
        }*/ else {
            return true;
        }
    }

}
