package s.com.pushsmsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class Constants {

    public static AlertDialog dialog;

    public static boolean checkInternet(Activity activity) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            System.out.println("Network available.");
            if(getInetAddressByName("www.google.com")) {
                System.out.println("google find successful.");
                if(getInetAddressByName("portal.specificstep.com")) {
                    connected = true;
                    System.out.println("specific find successful.");
                } else {
                    connected = false;
                    System.out.println("Please check your mobile data or wifi connection.");
                    showErrorInternetDialog(activity,"Please check your mobile data or wifi connection and try again later.");
                }
            } else {
                connected = false;
                System.out.println("Please check your mobile data connection.");
                showErrorInternetDialog(activity,"Please check your mobile data connection and try again later.");
            }
        } else {
            System.out.println("Network not available.");
            connected = false;
            showNoInternetDialog(activity);
        }
        return connected;
    }

    public static boolean getInetAddressByName(String name) {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>()
        {

            @Override
            protected Boolean doInBackground(String... params)
            {
                try {
                    InetAddress address = InetAddress.getByName(params[0]);
                    return !address.equals("");
                } catch (UnknownHostException e) {
                    return false;
                }
            }
        };
        try {
            return task.execute(name).get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }

    }

    public static void showNoInternetDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("No Internet")
                .setMessage("Please check your internet connection.")
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                        ((Activity) context).startActivityForResult(settingsIntent, 9003);
                    }
                })
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public static void showErrorInternetDialog(final Activity context, String msg) {
        try {
            if (dialog == null || !dialog.isShowing()) {
                dialog = new AlertDialog.Builder(context)
                        .setTitle("Error!")
                        .setMessage(msg)
                        .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                                ((Activity) context).startActivityForResult(settingsIntent, 9003);
                            }
                        })
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
