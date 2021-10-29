package s.com.pushsmsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AllEmailListActivity extends AppCompatActivity {

    RecyclerView emaillistrecyclerview;
    EmailListAdapter emailListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static List<EmailDetailsListModel> data;
    DatabaseHandler db;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_email_list);
        db = new DatabaseHandler(this);

        init();
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

    }
}