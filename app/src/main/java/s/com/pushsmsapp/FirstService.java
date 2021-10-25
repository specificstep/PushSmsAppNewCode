package s.com.pushsmsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FirstService extends Service {
    int mTime ;
    String urlData = "http://portal.specificstep.com/sendnotificationkan.php";
    //String urlData = "http://portal.specificstep.com/sendnotification.php";
    String urlData1 = "http://portal.specificstep.com/sendnotificationkan.php?sim_no=";
    //String urlData1 = "http://portal.specificstep.com/sendnotification.php?sim_no=";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefsDetail" ;
    public static final String DeviceId = "deviceId";
    public static final String ImeiNo = "imeiNo";
    public static final String SimId = "simId";
    DatabaseHandler db;
    List<MesaageHistory> mesaageHistoryList;
    MesaageHistory mesaageHistory;
    String urlMessage;
    int pos;


    /*public static final int notify = 10000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;*/

    @Override
    public void onCreate() {
        super.onCreate();
        mTime = 1;

        /*if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task*/

    }

    /*class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
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
                    Toast.makeText(FirstService.this, "Service is running", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.d("Service","onStartCommand") ;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        //return START_NOT_STICKY;
        System.out.println("Start Command");
        Calendar cal = Calendar.getInstance();
        Intent mIntent = new Intent(this, FirstService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                600 * 1000, pintent);//1000*60 = minute
        Date date = new Date();
        mTime++;
        if(mTime >=25000)
            mTime = 1 ;
        String mDate = date.toString() ;
        /*DeptTable mDeptTable = new DeptTable(mTime,
                mDate);*/

        /*Timer repeatTask=new Timer();

        int repeatInterval=10000; // 10 sec
        db = new DatabaseHandler(this);

        // this task for specified time it will run Repeat
        repeatTask.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
// Here do something
// This task will run every 10 sec repeat


            }
        },0,repeatInterval);*/

        //new AsyncDeptFail().execute(urlData);

        //new AsyncDept().execute(urlData);

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
        Toast.makeText(FirstService.this, "Service is running", Toast.LENGTH_SHORT).show();

        Log.e("Service","onStartCommand  Time: " + mTime) ;
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        //When remove app from background then start it again
        //startService(new Intent(this, FirstService.class));
        Log.d("Service", "TASK REMOVED");

        super.onTaskRemoved(rootIntent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected class AsyncDept extends
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
                    System.out.println("Url response1: " + responseString);

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

        }
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
        }
    }

}
