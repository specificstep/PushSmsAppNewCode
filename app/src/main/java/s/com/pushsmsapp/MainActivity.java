package s.com.pushsmsapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String urlData = "http://api.telegram.org/bot454849060:AAFLLOx4IH8bJWVhxw3_qm7JpyiieVlGDZs/sendMessage?chat_id=-284238239&parse_mode=html&&text=";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int REQUEST_READ_PHONE_STATE = 2;
    CardView cardAuthorize, cardHistory, emailList;
    private TransparentProgressDialog transparentProgressDialog;
    TextView txtBackgroundState;
    String otp_generated;
    DatabaseHandler db;
    String message;
    String sendername;
    public static String number = "+918780558490";
    public static String msg = "otp";
    public static String msgNoContain = "otp";
    List<AuthorizeSenderClass> mSenderClasses;
    public static final String MyPREFERENCES = "MyPrefsDetail";
    public static final String DeviceId = "deviceId";
    public static final String ImeiNo = "imeiNo";
    public static final String SimId = "simId";
    SharedPreferences.Editor editor;
    public int stateBackground = 0;
    LinearLayout lnrTimer;

    public static final String COUNTDOWN_BR = "s.com.pushsmsapp.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    List<MesaageHistory> mesaageHistoryList;
    MesaageHistory mesaageHistory;
    String urlMessage;
    int pos;
    String urlData1 = "http://portal.specificstep.com/sendnotificationkan.php?sim_no=";

    private static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int REQUEST_CODE_SIGN_IN = 100;
    //private GoogleSignInClient mGoogleSignInClient;
    TextView txtRestartTimer;
    LinearLayout lnrRestartTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        if (NewTimerService.hourglass == null) {
            startService(new Intent(this, NewTimerService.class));
            txtBackgroundState.setText("Stop Background Service");
            stateBackground = 1;
            lnrTimer.setBackgroundColor(getResources().getColor(R.color.colorRed));
            System.out.println("Started service");
            startBroadCastReceiver();
        }

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            getDeviceId();
            getIMEINumber();
            getSimId();
            System.out.println("Imei no:" + sharedpreferences.getString(ImeiNo, ""));
        }

        cardAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthorizeContactList.class);
                startActivity(intent);
            }
        });

        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessageHistoryActivity.class);
                startActivity(intent);
            }
        });
        emailList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AllEmailListActivity.class);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        lnrTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateBackground == 0) {
                    NewTimerService.hourglass.resumeTimer();
                    txtBackgroundState.setText("Stop Background Service");
                    stateBackground = 1;
                    lnrTimer.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    //startService(new Intent(MainActivity.this, NewTimerService.class));
                    System.out.println("Started service");
                    startBroadCastReceiver();
                } else {
                    NewTimerService.hourglass.pauseTimer();
                    txtBackgroundState.setText("Start Background Service");
                    stateBackground = 0;
                    lnrTimer.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    stopService(new Intent(MainActivity.this, NewTimerService.class));
                    System.out.println("Stoped service");
                    killBroadCastReceiver();
                }
            }
        });

        lnrRestartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTimerService.hourglass.stopTimer();
            }
        });

    }

    private void startBroadCastReceiver() {
        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, SmsReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void killBroadCastReceiver() {
        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, SmsReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected class AsyncDeptFail extends
            AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... voids) {
            /*RestAPI api = new RestAPI();
            try {

                api.AddDepartmentDetails("" + params[0].getNo(),
                        params[0].getName());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncCreateUser", e.getMessage());

            }*/
            String url = voids[0];
            pos = Integer.parseInt(voids[1]);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpGet);
                //HttpResponse response = httpclient.execute(new HttpGet(url));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    response.getEntity().writeTo(out);
                    String responseString = out.toString();
                    out.close();
                    System.out.println("Url response: " + responseString);

                    return responseString;
                    //..more logic

                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception e) {
                System.out.println("MainActivity Error: " + e.toString());
                return null;
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mesaageHistory = new MesaageHistory();
            mesaageHistory.set_id(mesaageHistoryList.get(pos).get_id());
            mesaageHistory.setDate(mesaageHistoryList.get(pos).getDate());
            mesaageHistory.setStatus("success");
            mesaageHistory.setMessage(mesaageHistoryList.get(pos).getMessage());
            mesaageHistory.setSender(mesaageHistoryList.get(pos).getSender());
            db.updateContact(mesaageHistory);
            //cdt.start();
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(NewTimerService.COUNTDOWN_BR));
        System.out.println("Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        System.out.println("Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, NewTimerService.class));
        System.out.println("Stopped service");
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            System.out.println("Countdown seconds remaining: " + millisUntilFinished / 1000);
            txtRestartTimer.setText(millisUntilFinished / 1000 + "");
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void initialize() {
        txtBackgroundState = (TextView) findViewById(R.id.txtBackgroundState);
        lnrTimer = (LinearLayout) findViewById(R.id.lnrBackgroundState);
        db = new DatabaseHandler(this);
        cardAuthorize = (CardView) findViewById(R.id.cardAuthorize);
        cardHistory = (CardView) findViewById(R.id.cardHistory);
        emailList = (CardView) findViewById(R.id.emailList);
        txtRestartTimer = (TextView) findViewById(R.id.txtTimerRestartText);
        lnrRestartTimer = (LinearLayout) findViewById(R.id.lnrRestartTimer);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission granted");
                    getDeviceId();
                    getIMEINumber();
                    getSimId();
                    System.out.println("Imei no:" + sharedpreferences.getString(ImeiNo, ""));
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    System.out.println("Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public String getDeviceId() {

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        editor = sharedpreferences.edit();
        editor.putString(DeviceId, android_id);
        editor.commit();
        System.out.println("IDS: Device Id: " + android_id);
        return android_id;

    }

    public String getIMEINumber() {
        String no = "";
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            System.out.println("IDS: IMEI No: " + mngr.getDeviceId());
            no = mngr.getDeviceId();
            editor = sharedpreferences.edit();
            editor.putString(ImeiNo, no);
            editor.commit();
        }
        return no;
    }

    public String getSimId() {
        String no = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            System.out.println("IDS: Sim Id: " + mTelephonyMgr.getSubscriberId());
            no = mTelephonyMgr.getSubscriberId();
            editor = sharedpreferences.edit();
            editor.putString(SimId, no);
            editor.commit();
        }
        return no;
    }

}