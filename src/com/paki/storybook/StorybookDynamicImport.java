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
import android.provider.ContactsContract;
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
		provider = LocationManager.getBestProvider(criteria, true);

		// Call to method to capture outgoing calls
		monitorOutgoingCalls();
		// Call to method to capture incoming calls
		CaptureCall();
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
	 * Method to Capture incoming calls
	 */

	public void CaptureCall() {
		
		
		
		// Declare a telephony manager
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// Declare a phoneStateListener
		PhoneStateListener mPhoneListener;
		// Switching trough various cases
		mPhoneListener = new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				String phonenumber=null;
				String currentDate;
				String Loc;
				ContentValues cv = new ContentValues();
				try {
					switch (state) {
					case TelephonyManager.CALL_STATE_RINGING:
						Log.v(StorybookContentProvider.TAG_LOG,
								"sei in chiamata");
	
						phonenumber= findNameByAddress(StorybookDynamicImport.this, incomingNumber);
						currentDate = formatDate(System.currentTimeMillis());
	
						// Sending a statusbar Notification
						CallNotification = notificationBuilder("StoryBook",
								"Ho salvato l'evento chiamata!", null);
						NotificationManager.notify(1, CallNotification);

						// Getting the position
						LocationManager.requestSingleUpdate(criteria,
								locationListener, looper);
						Location location = LocationManager
								.getLastKnownLocation(provider);
						Loc=GetAddressLocation(location);
						
						cv.put(StorybookContentProvider.CONTACT, phonenumber);
						Log.v(StorybookContentProvider.TAG_LOG, "Salvando" + phonenumber);
						cv.put(StorybookContentProvider.DATE, currentDate);
						Log.v(StorybookContentProvider.TAG_LOG, "Data" + currentDate);
						cv.put(StorybookContentProvider.EVENT_TYPE, "Chiamata");
						cv.put(StorybookContentProvider.LOCATION, Loc);
						Log.v(StorybookContentProvider.TAG_LOG, "DOVE" + Loc);
						getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
						
					

						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};

		// Setting listener
		tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/*
	 * Method to capture outgoing calls TO-DO: save data in database and get
	 * position
	 */

	public void monitorOutgoingCalls() {

		// get a broadcast receiver

	BroadcastReceiver callRcv = new BroadcastReceiver() {
			
			String currentDate;
			String Loc;
			String person = null;
			ContentValues cv = new ContentValues();
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// Capture the call
				if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
					String number = getResultData();
					setResultData(null);
					Log.v(StorybookContentProvider.TAG_LOG,
							"Outgoing call logged");
					Log.v(StorybookContentProvider.TAG_LOG,number);
					
					person = findNameByAddress(StorybookDynamicImport.this, number);
					currentDate = formatDate(System.currentTimeMillis());
					// Getting the position
					LocationManager.requestSingleUpdate(criteria,
							locationListener, looper);
					Location location = LocationManager
							.getLastKnownLocation(provider);
					Loc=GetAddressLocation(location);
					
					
					cv.put(StorybookContentProvider.CONTACT, person);
					Log.v(StorybookContentProvider.TAG_LOG, "Salvando" + person);
					cv.put(StorybookContentProvider.DATE, currentDate);
					Log.v(StorybookContentProvider.TAG_LOG, "Data" + currentDate);
					cv.put(StorybookContentProvider.EVENT_TYPE, "Chiamata");
					cv.put(StorybookContentProvider.LOCATION, Loc);
					Log.v(StorybookContentProvider.TAG_LOG, "DOVE" + Loc);
					getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
					
					
					// Sending a statusbar Notification
					CallNotification = notificationBuilder("StoryBook",
							"Ho salvato l'evento chiamata!", null);
					NotificationManager.notify(1, CallNotification);

				}

			}

		};

		// Setting Listener
		registerReceiver(callRcv, new IntentFilter(
				Intent.ACTION_NEW_OUTGOING_CALL));

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

	protected String GetAddressLocation(Location location) {
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

					//for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
						//sb.append(address.getAddressLine(i)).append("\n");

					//sb.append(address.getLocality()).append("\n");
				}
				//addressstring = sb.toString();
				//addressstring.
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		Toast.makeText(this, "la tua posizione è:" + address,
				Toast.LENGTH_SHORT).show();
		return address;
	}
	
	
	
	protected String formatDate(long date) {

		Date realdate = new Date(date);
		DateFormat df = new DateFormat();
		String format = (String) df.format("dd/MM/yyyy", realdate);
		//Log.v(StorybookContentProvider.TAG_LOG, "data della chiamata" + format);

		return format;
	}
	
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

	/*
	 * NESTED CLASS TO CAPTURE SENDING AND RECEIVING SMS
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
							
							String number = sms_sent_cursor.getString(sms_sent_cursor
									.getColumnIndex("address"));
							
							person = findNameByAddress(StorybookDynamicImport.this, number);
							
							currentDate = formatDate(System.currentTimeMillis());
							// Getting the position
							LocationManager.requestSingleUpdate(criteria,
									locationListener, looper);
							Location location = LocationManager
									.getLastKnownLocation(provider);
							Loc=GetAddressLocation(location);
							
							
							cv.put(StorybookContentProvider.CONTACT, person);
							Log.v(StorybookContentProvider.TAG_LOG, "Salvando" + person);
							cv.put(StorybookContentProvider.DATE, currentDate);
							Log.v(StorybookContentProvider.TAG_LOG, "Data" + currentDate);
							cv.put(StorybookContentProvider.EVENT_TYPE, "Sms inviato");
							cv.put(StorybookContentProvider.LOCATION, Loc);
							Log.v(StorybookContentProvider.TAG_LOG, "DOVE" + Loc);
							getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
							
							Log.v(StorybookContentProvider.TAG_LOG, "Saved sms event in Database");
						
							// Send a statusbar notification
							SmsNotification = notificationBuilder("StoryBook","Ho salvato l'evento SMS inviato!!", null);
							NotificationManager.notify(1, SmsNotification);

						}
					} else if (Integer.parseInt(protocol) == 0) {
						int type = sms_sent_cursor.getInt(sms_sent_cursor
								.getColumnIndex("type"));
						Log.e(StorybookContentProvider.TAG_LOG, "SMS Type : "
								+ type);
						// if sms is fully received
						if (type == 1) {
							
							String number = sms_sent_cursor.getString(sms_sent_cursor
									.getColumnIndex("address"));
							
							person = findNameByAddress(StorybookDynamicImport.this, number);
							
							currentDate = formatDate(System.currentTimeMillis());
							// Getting the position
							LocationManager.requestSingleUpdate(criteria,
									locationListener, looper);
							Location location = LocationManager
									.getLastKnownLocation(provider);
							Loc=GetAddressLocation(location);
							
							
							cv.put(StorybookContentProvider.CONTACT, person);
							Log.v(StorybookContentProvider.TAG_LOG, "Salvando" + person);
							cv.put(StorybookContentProvider.DATE, currentDate);
							Log.v(StorybookContentProvider.TAG_LOG, "Data" + currentDate);
							cv.put(StorybookContentProvider.EVENT_TYPE, "Sms ricevuto!");
							cv.put(StorybookContentProvider.LOCATION, Loc);
							Log.v(StorybookContentProvider.TAG_LOG, "DOVE" + Loc);
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
