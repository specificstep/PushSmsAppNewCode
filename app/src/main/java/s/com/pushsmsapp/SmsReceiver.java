
package s.com.pushsmsapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    List<AuthorizeSenderClass> mSenderClasses;
    DatabaseHandler db;
    String senderName, messageBody;
    long dateTime;
    Context mContext;
    private static final int REQUEST_READ_PHONE_STATE = 2;
    public static final String MyPREFERENCES = "MyPrefsDetail" ;
    public static final String DeviceId = "deviceId";
    public static final String ImeiNo = "imeiNo";
    public static final String SimId = "simId";
    SharedPreferences sharedpreferences;
    String urlMessage;
    String messageDate;
    public Map<String, String> msgDate = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Bundle data  = intent.getExtras();
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mContext = context;
        if(data != null) {
            System.out.println("Background app working fine");
            Object[] pdus = (Object[]) data.get("pdus");

            if(pdus != null) {
                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    sender = smsMessage.getDisplayOriginatingAddress();

                    //You must check here if the sender is your provider and not another one with same text.

                    messageBody = smsMessage.getMessageBody();
                    dateTime = smsMessage.getTimestampMillis();

                    Calendar sendTime = Calendar.getInstance();
                    sendTime.setTimeInMillis(dateTime);

                    messageDate = sendTime.get(Calendar.DATE) + "-" +
                            (sendTime.get(Calendar.MONTH) + 1) + "-" + sendTime.get(Calendar.YEAR) +
                            " " + sendTime.get(Calendar.HOUR_OF_DAY) + ":" + sendTime.get(Calendar.MINUTE) +
                            ":" + sendTime.get(Calendar.SECOND);
                    System.out.println("Message Date: " + messageDate);

                    //pass in message body
                    urlMessage = URLEncoder.encode("Sender: " + sender + "\n\n" + messageBody + "\n\n" + "Device Id: " + sharedpreferences.getString(DeviceId, "") + "\n" + "Sim Id: " + sharedpreferences.getString(SimId, "") + "\n" + "Date: " + messageDate);

                    mSenderClasses = new ArrayList<AuthorizeSenderClass>();
                    db = new DatabaseHandler(context);
                    mSenderClasses = db.getAllAuthContacts();

                    for (int j = 0; j < mSenderClasses.size(); j++) {
                        if(!TextUtils.isEmpty(mSenderClasses.get(j).getAuthmessage())) {
                            if (sender.toLowerCase().contains(mSenderClasses.get(j).getAuthsender().toLowerCase()) && messageBody.toLowerCase().contains(mSenderClasses.get(j).getAuthmessage().toLowerCase())) {
                                db.addContact(new MesaageHistory(sender, urlMessage, "fail", messageDate));
                            } else {
                                System.out.println("SMS RECEIVER: Sender is not exist in Authorize contacts list");
                            }
                        } else if(sender.toLowerCase().contains(mSenderClasses.get(j).getAuthsender().toLowerCase())) {
                            db.addContact(new MesaageHistory(sender, urlMessage, "fail", messageDate));
                        }
                    }

            *//*if(sender.equals(MainActivity.number)) {

                if (messageBody.contains(MainActivity.msg)) {
                    //Pass on the text to our listener.
                    Intent myIntent = new Intent("otp");
                    myIntent.putExtra("message", messageBody);
                    myIntent.putExtra("sender", sender);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                }

            }*//*
                    //mListener.messageReceived(messageBody);
                }
            }
        } else {
            System.out.println("Background app working not fine");
        }*/

        Map<String, String> msg = RetrieveMessages(intent);
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mContext = context;
        if(msg == null) {

        } else {
            for (String sender : msg.keySet()) {
                senderName = sender;
                messageBody = msg.get(sender);
                dateTime = Long.parseLong(msgDate.get(sender));
                Calendar sendTime = Calendar.getInstance();
                sendTime.setTimeInMillis(dateTime);

                messageDate = sendTime.get(Calendar.YEAR) + "-" +
                        (sendTime.get(Calendar.MONTH) + 1) + "-" + sendTime.get(Calendar.DATE) +
                        " " + sendTime.get(Calendar.HOUR_OF_DAY) + ":" + sendTime.get(Calendar.MINUTE) +
                        ":" + sendTime.get(Calendar.SECOND);
                System.out.println("Message Date: " + messageDate);

                //pass in message body
                urlMessage = URLEncoder.encode("Sender: " + sender + "\n\n" + messageBody + "\n\n" + "Device Id: " + sharedpreferences.getString(DeviceId, "") + "\n" + "Sim Id: " + sharedpreferences.getString(SimId, "") + "\n" + "Date: " + messageDate);

                mSenderClasses = new ArrayList<AuthorizeSenderClass>();
                db = new DatabaseHandler(context);
                mSenderClasses = db.getAllAuthContacts();

                for (int j = 0; j < mSenderClasses.size(); j++) {
                    if(!TextUtils.isEmpty(mSenderClasses.get(j).getAuthmessage())) {
                        if(!TextUtils.isEmpty(mSenderClasses.get(j).getAuthmsgnocontain())) {
                            if (sender.toLowerCase().contains(mSenderClasses.get(j).getAuthsender().toLowerCase()) && messageBody.toLowerCase().contains(mSenderClasses.get(j).getAuthmessage().toLowerCase()) && !messageBody.toLowerCase().contains(mSenderClasses.get(j).getAuthmsgnocontain().toLowerCase())) {
                                db.addContact(new MesaageHistory(sender, urlMessage, "fail", messageDate));
                            } else {
                                System.out.println("SMS RECEIVER: Sender is not exist in Authorize contacts list");
                            }
                        } else {
                            if (sender.toLowerCase().contains(mSenderClasses.get(j).getAuthsender().toLowerCase()) && messageBody.toLowerCase().contains(mSenderClasses.get(j).getAuthmessage().toLowerCase())) {
                                db.addContact(new MesaageHistory(sender, urlMessage, "fail", messageDate));
                            } else {
                                System.out.println("SMS RECEIVER: Sender is not exist in Authorize contacts list");
                            }
                        }
                    } else if(sender.toLowerCase().contains(mSenderClasses.get(j).getAuthsender().toLowerCase())) {
                        db.addContact(new MesaageHistory(sender, urlMessage, "fail", messageDate));
                    }
                }

            }
        }

    }

    public Map<String, String> RetrieveMessages(Intent intent) {
        Map<String, String> msg = null;
        SmsMessage[] msgs = null;
        Bundle bundle = intent.getExtras();

        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, String>(nbrOfpdus);
                msgDate = new HashMap<String, String>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];

                // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                    String originatinAddress = msgs[i].getOriginatingAddress();

                    // Check if index with number exists
                    if (!msg.containsKey(originatinAddress)) {
                        // Index with number doesn't exist
                        // Save string into associative array with sender number as index
                        msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());
                        msgDate.put(msgs[i].getOriginatingAddress(), String.valueOf(msgs[i].getTimestampMillis()));
                    } else {
                        // Number has been there, add content but consider that
                        // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                        // so just add the part of the current PDU
                        String previousparts = msg.get(originatinAddress);
                        String msgString = previousparts + msgs[i].getMessageBody();
                        msg.put(originatinAddress, msgString);
                        msgDate.put(originatinAddress, String.valueOf(msgs[i].getTimestampMillis()));
                    }
                }
            }
        }

        return msg;

    }

}


