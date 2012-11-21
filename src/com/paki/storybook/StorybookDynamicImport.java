package com.paki.storybook;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/*
 * THIS CLASS IS THE SERVICE OF THE APPLICATION!! 
 * IT DO BACKGROUND TASKS NEEEDED FOR DYNAMIC IMPORT!!!
 * 
 */

@SuppressLint("ParserError")
public class StorybookDynamicImport extends Service {

	/*
	 * private variables for notifications
	 */

	private Notification CallNotification;
	private Notification SmsNotification;
	private Notification CameraNotification;
	NotificationManager NotificationManager;

	/*
	 * Private variables for monitor location and geocode
	 */

	private LocationManager LocationManager;
	private final Criteria criteria = new Criteria();
	private static int minUpdateTime = 0;
	private static int minUpdateDistance = 0;
	Looper looper;
	String provider;
	private final LocationListener locationListener = new LocationListener() {

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

		}
	};
	
	
	public StorybookDynamicImport(){
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	
	public void onCreate() {
		super.onCreate();
		String svcName = Context.NOTIFICATION_SERVICE;
		String svcName1 = Context.LOCATION_SERVICE;

		// Setting Notification Manager and Location Manager

		NotificationManager = (NotificationManager) getSystemService(svcName);
		LocationManager = (LocationManager) getSystemService(svcName1);

		// Setting criterias to match best provider

		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(true);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
		criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

		// Getting the best-available provider
		provider = LocationManager.GPS_PROVIDER;
	    if (!LocationManager.isProviderEnabled(provider)) 
		        provider = LocationManager.NETWORK_PROVIDER; 

		// Call to method to capture outgoing calls
		TrackCalls();
		// Call to method to capture incoming and outcoming sms
		TrackSms();
		// Call to camera listener
		try {
			monitorcamera();
		} catch (MalformedMimeTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	/*
	 * Method to capture outgoing calls TO-DO: save data in database and get
	 * position
	 */

	public void TrackCalls() {

		CalllogObserver calllogobserver = null;
		
		// register a contentObserver
		
		if(calllogobserver == null){
			Uri uri = CallLog.Calls.CONTENT_URI;
			calllogobserver=new CalllogObserver(new Handler(), StorybookDynamicImport.this);
			StorybookDynamicImport.this.getContentResolver().registerContentObserver(uri, true, calllogobserver);
		}


		
	
	}

	/*
	 * Method to capture photo taken with camera TO-DO:TAG-ACTIVITY
	 */

	public void monitorcamera() throws MalformedMimeTypeException {

		// get the broadcast receiver
		BroadcastReceiver cameraRcv = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.v(StorybookContentProvider.TAG_LOG,
						"camera: hai scattato una fotoo!!!");
				CameraNotification = notificationBuilder("Storybook", "Seleziona per taggare qualcuno!", PhotoTagger.class);
				NotificationManager.notify(1,CameraNotification);
			

			}

		};

		// Setting the listener

		IntentFilter filter = new IntentFilter();
		filter.addAction(Camera.ACTION_NEW_PICTURE);
		filter.addDataType("image/*");
		registerReceiver(cameraRcv, filter);
	}

	/*
	 * Method to capture incoming and outcoming sms
	 */

	public void TrackSms() {

		final Uri STATUS_URI = Uri.parse("content://sms");
		SmsSentReceivedObserver smsSentReceivedObserver = null;

		if (smsSentReceivedObserver == null) {
			smsSentReceivedObserver = new SmsSentReceivedObserver(
					new Handler(), this);
			this.getContentResolver().registerContentObserver(STATUS_URI, true,
					smsSentReceivedObserver);
		}
	}

	/*
	 * Private Method to build notification
	 */

	private Notification notificationBuilder(String title, String content, Class<?> cls) {

		Intent notificationIntent;
		PendingIntent contentIntent = null;
		// Getting the notification builder

		Notification.Builder builder = new Notification.Builder(
				StorybookDynamicImport.this);
	
		if(cls != null){
			notificationIntent = new Intent(this, cls);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		}
		
		builder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("Notification")
				.setWhen(System.currentTimeMillis())
				.setDefaults(
						Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setAutoCancel(true)
				.setContentInfo(content)
				.setTicker(title);
				if (cls != null)
					builder.setContentIntent(contentIntent);
				builder.setContentTitle(title);
				

		// Build notification

		Notification notification = builder.getNotification();
		

		return notification;
	}

	/*
	 * Private method to get location
	 */

	public String GetAddressLocation(Location location) {
		//String addressstring = "No address found";
		String address = null;

		// Get Latitude and Longitude

		if (location != null) {
			double lat = location.getLatitude();
			double lon = location.getLongitude();

			// Calls to Maps app
			// ("WARNING!!!! MUST BE INSTALLED GOOGLE MAPS ON DEVICE!!")

			Geocoder gc = new Geocoder(this, Locale.getDefault());

			try {
				// Getting the addresses
				List<Address> addresses = gc.getFromLocation(lat, lon, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					address = addresses.get(0).getLocality();
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		Toast.makeText(this, "la tua posizione è:" + address,
				Toast.LENGTH_SHORT).show();
		return address;
	}
	
	/*
	 * Private method for format a date starting from a long type
	 */
	
	protected String formatDate(long date) {

		Date realdate = new Date(date);
		DateFormat df = new DateFormat();
		String format = (String) df.format("dd/MM/yyyy", realdate);

		return format;
	}
	
	/*
	 * Private method to get Names from person (lookup in ContactsContract)
	 */
	
	protected String findNameByAddress(Context ct, String addr) {
		Uri myPerson = Uri.withAppendedPath(
				ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
				Uri.encode(addr));
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
		Cursor cursor = ct.getContentResolver().query(myPerson, projection,
				null, null, null);

		if (cursor.moveToFirst()) {
			String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			Log.e("", "Found contact name");
			cursor.close();

			return name;
		}

		cursor.close();
		Log.e("", "Not Found contact name");

		return addr;
	}
	
	
	/**
	 * 
	 * @author Paki
	 * NESTED CLASS FOR CALL OBSERVER 
	 */
	
	public class CalllogObserver extends ContentObserver{
		
		// Get context
		private Context mContext;

		public CalllogObserver(Handler handler, Context ctx) {
			super(handler);
			mContext = ctx;
		}

		public boolean deliverSelfNotifications() {
			return true;
		}
		
		long lastTimeofCall = 0L;
		long lastTimeofUpdate = 0L;
		long threshold_time = 10000;
		ContentValues cv = new ContentValues();
		
		public void onChange(boolean selfChange) {
			
			super.onChange(selfChange);

		        lastTimeofCall = System.currentTimeMillis();

		        if(lastTimeofCall - lastTimeofUpdate > threshold_time){

		         //write your code to find updated contacts here
		        	Cursor c1 = getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null,null,null); 
		        	  if(c1.moveToLast()){
		        		// get the name
		        		String name = c1.getString(c1.getColumnIndex(CallLog.Calls.CACHED_NAME));
		        		//get the date
		        		String currentDate =formatDate(Long.parseLong(c1.getString(c1.getColumnIndex(CallLog.Calls.DATE))));
		        		// find the final event of call
		        		String type = null;
		        		int i =c1.getColumnIndex(CallLog.Calls.TYPE);
		        		switch(c1.getInt(i)){
		        				 //if is receiving
		        				 case android.provider.CallLog.Calls.INCOMING_TYPE:
		        					 type = "Chiamata ricevuta";
		        					 break;
		        			     //if is missed
		        				 case android.provider.CallLog.Calls.MISSED_TYPE:
		        					 type = "Chiamata persa";
		        					 break;
		        			     //if is doing a call
		        				 case android.provider.CallLog.Calls.OUTGOING_TYPE:
		        					 type = "Chiamata effettuata";
		        					 break;
		        		}
		        		
		        		
		        		Uri lookupUri = Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, Uri.encode(c1.getString(c1.getColumnIndex(CallLog.Calls._ID))));
						Log.e(StorybookContentProvider.TAG_LOG, "URI:" + lookupUri);
						
						// Getting the position
						LocationManager.requestSingleUpdate(criteria,locationListener, looper);
						Location location = LocationManager.getLastKnownLocation(provider);
						String Loc=GetAddressLocation(location);
		        		
						cv.put(StorybookContentProvider.CONTACT, name);
						Log.v(StorybookContentProvider.TAG_LOG, "Salvando" + name);
						cv.put(StorybookContentProvider.DATE, currentDate);
						Log.v(StorybookContentProvider.TAG_LOG, "Data" + currentDate);
						cv.put(StorybookContentProvider.EVENT_TYPE, type);
						cv.put(StorybookContentProvider.LOCATION, Loc);
						Log.v(StorybookContentProvider.TAG_LOG, "DOVE" + Loc);
						cv.put(StorybookContentProvider.URI, lookupUri.toString());
						getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
		        		
		        		CallNotification = notificationBuilder("Storybook", "Ho salvato l'evento chiamata", null);
		        		NotificationManager.notify(1,CallNotification);
		        		
		        	         }  

		          lastTimeofUpdate = System.currentTimeMillis();
		        }
	}
			}
	


	/**
	 * 
	 * @author Paki
	 * NESTED CLASS FOR SMS OBSERVER
	 */

	
	
	public class SmsSentReceivedObserver extends ContentObserver {

		// Get context
		private Context mContext;

		public SmsSentReceivedObserver(Handler handler, Context ctx) {
			super(handler);
			mContext = ctx;
		}

		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.e(StorybookContentProvider.TAG_LOG,
					"Notification on SMS observer");
			final Uri STATUS_URI = Uri.parse("content://sms");
			String currentDate;
			String Loc;
			String person = null;
			ContentValues cv = new ContentValues();
			Cursor sms_sent_cursor = mContext.getContentResolver().query(
					STATUS_URI, null, null, null, null);
			if (sms_sent_cursor != null) {
				if (sms_sent_cursor.moveToFirst()) {
					String protocol = sms_sent_cursor.getString(sms_sent_cursor
							.getColumnIndex("protocol"));
					
					Log.e(StorybookContentProvider.TAG_LOG, "protocol : "
							+ protocol);

					// Control protocol: null- i'm sending message; 0- i'm
					// receiving message

					if (protocol == null) {
						int type = sms_sent_cursor.getInt(sms_sent_cursor
								.getColumnIndex("type"));
						Log.e(StorybookContentProvider.TAG_LOG, "SMS Type : "
								+ type);
						// if sms is sended 
						if (type == 2) {
							
							//Getting the number
							
							String number = sms_sent_cursor.getString(sms_sent_cursor
									.getColumnIndex("address"));
							
							//find person
							
							person = findNameByAddress(StorybookDynamicImport.this, number);
				
							// Getting the name
							
							currentDate = formatDate(System.currentTimeMillis());
							
							// Getting the position
							LocationManager.requestSingleUpdate(criteria,
									locationListener, looper);
							Location location = LocationManager
									.getLastKnownLocation(provider);
							Loc=GetAddressLocation(location);
							
							//Getting the URL for conversation
							
							int threadId = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("thread_id"));
							Uri lookupUri = Uri.withAppendedPath(Uri.parse("content://mms-sms/conversations/"), Integer.toString(threadId));
					       
							
							
							cv.put(StorybookContentProvider.CONTACT, person);
							Log.v(StorybookContentProvider.TAG_LOG, "Salvando" + person);
							cv.put(StorybookContentProvider.DATE, currentDate);
							Log.v(StorybookContentProvider.TAG_LOG, "Data" + currentDate);
							cv.put(StorybookContentProvider.EVENT_TYPE, "Sms inviato");
							cv.put(StorybookContentProvider.LOCATION, Loc);
							cv.put(StorybookContentProvider.URI, lookupUri.toString());
							Log.v(StorybookContentProvider.TAG_LOG, "DOVE" + Loc);
							getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
							
							Log.v(StorybookContentProvider.TAG_LOG, "Saved sms event in Database");
						
							// Send a statusbar notification
							SmsNotification = notificationBuilder("StoryBook","Ho salvato l'evento SMS inviato!!", null);
							NotificationManager.notify(1, SmsNotification);

						}
					} else  {
						int type = sms_sent_cursor.getInt(sms_sent_cursor
								.getColumnIndex("type"));
						Log.e(StorybookContentProvider.TAG_LOG, "SMS Type : "
								+ type);
						// if sms is fully received
						if (type == 2) {
							
							//get the number
							
							String number = sms_sent_cursor.getString(sms_sent_cursor
									.getColumnIndex("address"));
							
							
							//find person
							
							person = findNameByAddress(StorybookDynamicImport.this, number);
							
							//find date
							
							currentDate = formatDate(System.currentTimeMillis());
							
							// Getting the position
							LocationManager.requestSingleUpdate(criteria,
									locationListener, looper);
							Location location = LocationManager
									.getLastKnownLocation(provider);
							Loc=GetAddressLocation(location);
							
							// getting the URL for recall from StoryBook app
							
							int threadId = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("thread_id"));
							Uri lookupUri = Uri.withAppendedPath(Uri.parse("content://mms-sms/conversations/"), Integer.toString(threadId));
					        Log.v(StorybookContentProvider.TAG_LOG, "URI:" + lookupUri ); 
							
							//putting into Database
					        
							cv.put(StorybookContentProvider.CONTACT, person);
							cv.put(StorybookContentProvider.DATE, currentDate);
							cv.put(StorybookContentProvider.EVENT_TYPE, "Sms ricevuto");
							cv.put(StorybookContentProvider.LOCATION, Loc);
							cv.put(StorybookContentProvider.TAG_LOG, lookupUri.toString());
							getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
							
							
							Log.v(StorybookContentProvider.TAG_LOG, "Sms Received event saved");
							// Send a statusbar notification
							SmsNotification = notificationBuilder("StoryBook",
									"Ho salvato l'evento SMS ricevuto!!", null);
							NotificationManager.notify(1, SmsNotification);
						}

					}
				} else
					Log.e(StorybookContentProvider.TAG_LOG,
							"Send Cursor is Empty");

				super.onChange(selfChange);

			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
