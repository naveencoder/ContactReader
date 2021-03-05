package com.example.usercontact.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;


public class PhoneStateReceiver extends BroadcastReceiver {

    static final String TAG="State";
    static final String TAG1=" Inside State";
    static Boolean recordStarted;
    public static String phoneNumber;
    public static String name;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(intent.getAction().equals("android.intent.action.PHONE_STATE")){

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Log.d(TAG, "Inside Extra state off hook");
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.e(TAG, "outgoing number : " + number);
                if(number!=null){
                    EventBus.getDefault().post(number);
                    Toast.makeText(context, "incoming number : " + number, Toast.LENGTH_SHORT).show();
                }
            }

            else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Log.e(TAG, "Inside EXTRA_STATE_RINGING");
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(number!=null){
                    EventBus.getDefault().post(number);
                    Toast.makeText(context, "incoming number : " + number, Toast.LENGTH_SHORT).show();
                }
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.d(TAG, "Inside EXTRA_STATE_IDLE");
//                Toast.makeText(context, "Inside EXTRA_STATE_IDLE : ", Toast.LENGTH_SHORT).show();
            }
        }


        Boolean switchCheckOn = pref.getBoolean("switchOn", true);
        if (switchCheckOn) {
            try {
                System.out.println("Receiver Start");
                Bundle extras = intent.getExtras();
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                Log.d(TAG, " onReceive: " + state);
//                Toast.makeText(context, "Call detected(Incoming/Outgoing) " + state, Toast.LENGTH_SHORT).show();

                if (extras != null) {
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        Log.d(TAG1, " Inside " + state);
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                        int j = pref.getInt("numOfCalls", 0);
                        pref.edit().putInt("numOfCalls", ++j).apply();
                        Log.d(TAG, "onReceive: num of calls " + pref.getInt("numOfCalls", 0));

                        Log.d(TAG1, " recordStarted in offhook: " + recordStarted);
                        Log.d(TAG1, " Inside " + state);

                        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        Log.d(TAG1, " Phone Number in receiver " + phoneNumber);

                        if (pref.getInt("numOfCalls", 1) == 1) {
                            int serialNumber = pref.getInt("serialNumData", 1);
                            pref.edit().putInt("serialNumData", ++serialNumber).apply();
                            pref.edit().putBoolean("recordStarted", true).apply();
                        }

                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        int k = pref.getInt("numOfCalls", 1);
                        pref.edit().putInt("numOfCalls", --k).apply();
                        int l = pref.getInt("numOfCalls", 0);
                        Log.d(TAG1, " Inside " + state);
                        recordStarted = pref.getBoolean("recordStarted", false);
                        Log.d(TAG1, " recordStarted in idle :" + recordStarted);
                        if (recordStarted && l == 0) {
                            Log.d(TAG1, " Inside to stop recorder " + state);
                            pref.edit().putBoolean("recordStarted", false).apply();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
