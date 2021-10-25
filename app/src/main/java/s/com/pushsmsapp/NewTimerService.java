package s.com.pushsmsapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.ankushgrover.hourglass.Hourglass;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class NewTimerService extends Service {

    private final static String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "s.com.pushsmsapp.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    //public static CountDownTimer cdt = null;
    public static Hourglass hourglass;

    DatabaseHandler db;
    List<MesaageHistory> mesaageHistoryList;
    MesaageHistory mesaageHistory;
    //String urlData1 = "http://portal.specificstep.com/sendnotificationkan.php?sim_no=";
    String urlData1 = "http://portal.specificstep.com/sendnotification.php?sim_no=";
    //String urlData1 = "https://api.telegram.org/bot454849060:AAFLLOx4IH8bJWVhxw3_qm7JpyiieVlGDZs/sendMessage?chat_id=-284238239&parse_mode=html&&text=";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefsDetail" ;
    public static final String DeviceId = "deviceId";
    public static final String ImeiNo = "imeiNo";
    public static final String SimId = "simId";
    String urlMessage;
    int pos;

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("Starting timer...");
        db = new DatabaseHandler(getBaseContext());
        /*cdt = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                System.out.println("Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                System.out.println("Timer finished");
                this.start();
                if(isNetworkAvailable()) {
                    mesaageHistoryList = new ArrayList<MesaageHistory>();
                    mesaageHistoryList = db.getAllContacts();
                    for (int i = 0; i < mesaageHistoryList.size(); i++) {
                        if (mesaageHistoryList.get(i).getStatus().equals("fail")) {
                            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            System.out.println("Sender: " + mesaageHistoryList.get(i).getSender().toString());
                            System.out.println("Message: " + mesaageHistoryList.get(i).getMessage().toString());
                            System.out.println("Device Id: " + sharedpreferences.getString(DeviceId, ""));
                            System.out.println("Sim Id: " + sharedpreferences.getString(SimId, ""));
                            System.out.println("Date: " + mesaageHistoryList.get(i).getDate());
                            urlMessage = URLEncoder.encode(mesaageHistoryList.get(i).getMessage().toString());
                            new AsyncDeptFail().execute(urlData1 + sharedpreferences.getString(SimId, "") + "&text=" + urlMessage,i+"");
                        }
                    }
                }

            }
        };

        cdt.start();*/

        hourglass = new Hourglass(60000, 1000) {
            @Override
            public void onTimerTick(long timeRemaining) {
                // Update UI
                System.out.println("Countdown seconds remaining: " + timeRemaining / 1000);
                bi.putExtra("countdown", timeRemaining);
                sendBroadcast(bi);
            }

            @Override
            public void onTimerFinish() {
                // Timer finished
                System.out.println("Timer finished");
                startTimer();
                if(isNetworkAvailable()) {
                    mesaageHistoryList = new ArrayList<MesaageHistory>();
                    mesaageHistoryList = db.getAllContacts();
                    for (int i = 0; i < mesaageHistoryList.size(); i++) {
                        if (mesaageHistoryList.get(i).getStatus().equals("fail")) {
                            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            System.out.println("Sender: " + mesaageHistoryList.get(i).getSender().toString());
                            System.out.println("Message: " + mesaageHistoryList.get(i).getMessage().toString());
                            System.out.println("Device Id: " + sharedpreferences.getString(DeviceId, ""));
                            System.out.println("Sim Id: " + sharedpreferences.getString(SimId, ""));
                            System.out.println("Date: " + mesaageHistoryList.get(i).getDate());
                            urlMessage = URLEncoder.encode(mesaageHistoryList.get(i).getMessage().toString());
                            new AsyncDeptFail().execute(urlData1 + sharedpreferences.getString(SimId, "") + "&text=" + urlMessage,i+"");
                        }
                    }
                }
            }
        };

        hourglass.startTimer();

    }

    @Override
    public void onDestroy() {

        //cdt.cancel();
        System.out.println("Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
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

}
