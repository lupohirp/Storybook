package com.paki.storybook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.paki.storybook.StorybookContentProvider.MySQLiteOpenHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StoryBookStaticImport extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getCalls();
		getSms();

		StorybookActivity.flag = 1;
	}

	/*
	 * /* METHODS TO GET STATIC INFORMATIONS :) @author Pasquale Lodise
	 */

	// METHOD TO GET CALLS

	public void getCalls() {
		/** Declare a Contacts Cursor which have a list of all contacts */
		Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
				null, null, null, null);
		Cursor contactscursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		/** Move while cursor has another istance to process */
		while (cursor.moveToNext()) {
			/** Get the contact name and the date pointed by cursor at that time */
			String name = cursor.getString(cursor
					.getColumnIndex(CallLog.Calls.CACHED_NAME));
			String date = cursor.getString(cursor
					.getColumnIndex(CallLog.Calls.DATE));
			String formattedDate = formatDate(date);

			String contact_ID = null;
			String nameverified = null;
			while (contactscursor.moveToNext()) {
				String contactName = contactscursor
						.getString(contactscursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (contactName == name)
					nameverified = contactName;
			}

			// if (contact_ID == null) {
			// contact_ID = "Contact is not incontact managaer!!!!";

			// }

			// Putting Values to DB
			Log.v(StorybookContentProvider.TAG_LOG,
					"sto salvando dalle chiamate:" + name);
			ContentValues cValues = new ContentValues();
			cValues.put(StorybookContentProvider.CONTACT, name);
			cValues.put(StorybookContentProvider.EVENT_TYPE, "Chiamata");
			getContentResolver().insert(StorybookContentProvider.CONTENT_URI,
					cValues);
		}

		cursor.close();
		// close cursor
		contactscursor.close();

	}

	private String formatDate(String date) {
		long parsedate = Long.parseLong(date);

		Date realdate = new Date(parsedate);
		DateFormat df = new DateFormat();
		String format = (String) df.format("dd/MM/yyyy", realdate);
		Log.v(StorybookContentProvider.TAG_LOG, "data della chiamata" + format);

		return format;
	}

	// Method to get SMS/MMS from Device :)

	public void getSms() {
		Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/inbox/"), null, null, null, null);
		while (cursor.moveToNext()) {
			// obtain information about SMS/MMS
			String contact_address = cursor.getString(cursor
					.getColumnIndex("address"));
			String contact_ID = null;
			String returned_name = findNameByAddress(this, contact_address);
			String dateCol = cursor.getString(cursor.getColumnIndex("date"));
			// Getting the contact_number
			Cursor contactscursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			while (contactscursor.moveToNext()) {
				String contact_name = contactscursor.getString(contactscursor
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
				if (contact_name == returned_name) {
					contact_ID = contactscursor.getString(contactscursor
							.getColumnIndex(ContactsContract.Contacts._ID));

				}
			}

			contactscursor.close();
			// Putting the values :)

			ContentValues cValues = new ContentValues();
			cValues.put(StorybookContentProvider.CONTACT, returned_name);
		//	cValues.put(StorybookContentProvider.CONTACT_ID, contact_ID);
		//	cValues.put(StorybookContentProvider.EVENT_TYPE, "SMS/MMS");
			getContentResolver().insert(StorybookContentProvider.CONTENT_URI,
					cValues);

		}
		cursor.close();

	}

	/*
	 * Method to get name from Messages (we get a number) Helper Method :)
	 */

	private String findNameByAddress(Context ct, String addr) {
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

}
