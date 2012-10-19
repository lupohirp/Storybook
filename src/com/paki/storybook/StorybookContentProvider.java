package com.paki.storybook;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;

public class StorybookContentProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.paki.storybook/items");

	public static final String KEY_ID = "_id";
	public static final String CONTACT = "contact";
	public static final String CONTACT_ID = "contact_id";
	public static final String EVENT_TYPE = "event_type";

	public static final String TAG_LOG = "DATAMANAGERACTIVITY";

	public static MySQLiteOpenHelper myOpenHelper;

	@Override
	public boolean onCreate() {
		// Construct the underlying database.
		// Defer opening the database until you need to perform
		// a query or transaction.
		myOpenHelper = new MySQLiteOpenHelper(getContext(),
				MySQLiteOpenHelper.DATABASE_NAME, null,
				MySQLiteOpenHelper.DATABASE_VERSION);

		return true;

	}

	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;

	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.paki.storybook", "items", ALLROWS);
		uriMatcher.addURI("com.paki.storybook", "items/#", SINGLE_ROW);
	}

	@Override
	public String getType(Uri uri) {
		// Return a string that identifies the MIME type
		// for a Content Provider URI
		switch (uriMatcher.match(uri)) {
		case ALLROWS:
			return "vnd.android.cursor.dir/vnd.paki.items";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.paki.items";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Open a read-only database.
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();

		// Replace these with valid SQL statements if necessary.
		String groupBy = null;
		String having = null;

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(KEY_ID + "=" + rowID);
		default:
			break;
		}

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, groupBy, having, sortOrder);

		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Open a read / write database to support the transaction.
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();

		// If this is a row URI, limit the deletion to the specified row.
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		default:
			break;
		}

		// To return the number of deleted items, you must specify a where
		// clause. To delete all rows and return a value, pass in "1".
		if (selection == null)
			selection = "1";

		// Execute the deletion.
		int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE,
				selection, selectionArgs);

		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);

		return deleteCount;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Open a read / write database to support the transaction.
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();

		// To add empty rows to your database by passing in an empty Content
		// Values
		// object, you must use the null column hack parameter to specify the
		// name of
		// the column that can be set to null.
		String nullColumnHack = null;

		// Insert the values into the table
		long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE, nullColumnHack,
				values);

		if (id > -1) {
			// Construct and return the URI of the newly inserted row.
			Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);

			// Notify any observers of the change in the data set.
			getContext().getContentResolver().notifyChange(insertedId, null);

			return insertedId;
		} else
			return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		// Open a read / write database to support the transaction.
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();

		// If this is a row URI, limit the deletion to the specified row.
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		default:
			break;
		}

		// Perform the update.
		int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, values,
				selection, selectionArgs);

		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);

		return updateCount;
	}

	// private class to get database :)

	public class MySQLiteOpenHelper extends SQLiteOpenHelper {

		public static final String DATABASE_NAME = "Storybook.db";
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_TABLE = "contact_data";

		public MySQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// SQL statement to create a new database.
		/*
		 * private static final String DATABASE_CREATE = "create table " +
		 * DATABASE_TABLE + " (" + KEY_ID +
		 * " integer primary key autoincrement, " + CONTACT + " text not null, "
		 * + CONTACT_ID + "text" + EVENT_TYPE + "text);";
		 */

		// Called when no database exists in disk and the helper class needs
		// to create a new one.
		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder createQuery = new StringBuilder();
			createQuery.append("CREATE TABLE \"" + DATABASE_TABLE + "\" (");
			createQuery.append("	    \"" + KEY_ID
					+ "\" INTEGER PRIMARY KEY AUTOINCREMENT,");
			createQuery.append("	    \"" + CONTACT + "\" TEXT,");
			createQuery.append("	    \"" + CONTACT_ID + "\" TEXT,");
			createQuery.append("	    \"" + EVENT_TYPE + "\" TEXT");
			createQuery.append(")");

			db.execSQL(createQuery.toString());
			Log.v(TAG_LOG, "database succesfully created");
		}

		// Called when there is a database version mismatch, meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");

			// Upgrade the existing database to conform to the new version.
			// Multiple
			// previous versions can be handled by comparing oldVersion and
			// newVersion
			// values.

			// The simplest case is to drop the old table and create a new one.
			db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(db);
		}

	}

}
