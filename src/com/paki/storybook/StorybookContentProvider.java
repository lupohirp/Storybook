package com.paki.storybook;

import java.util.HashMap;



import android.annotation.SuppressLint;
import android.app.SearchManager;
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

	
	/*
	 *  Constants for Contact Table 
	 */
	
	public static final String KEY_ID = "_id";
	public static final String CONTACT = "contact";
	public static final String EVENT_TYPE = "event_type";
	public static final String URI = "uri";
	public static final String DATE = "date";
	public static final String LOCATION = "location";
	
	
	  // The name and column index of each column in your database.
	  // These should be descriptive.
	  public static final String KEY_COLUMN_1_NAME = "KEY_COLUMN_1_NAME";
	
	
	
	public static final String TAG_LOG = "DATAMANAGERACTIVITY";

	public static MySQLiteOpenHelper myOpenHelper;

	  /**
	   * Listing 8-30: Detecting search suggestion requests in Content Providers
	   */
	  private static final int ALLROWS = 1;
	  private static final int SINGLE_ROW = 2;
	  private static final int SEARCH = 3;

	  private static final UriMatcher uriMatcher;

	  static {
	   uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	   uriMatcher.addURI("com.paad.skeletondatabaseprovider", 
	                     "elements", ALLROWS);
	   uriMatcher.addURI("com.paad.skeletondatabaseprovider", 
	                     "elements/#", SINGLE_ROW);
	   
	   uriMatcher.addURI("com.paad.skeletondatabaseprovider",
	     SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
	   uriMatcher.addURI("com.paad.skeletondatabaseprovider",
	     SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
	   uriMatcher.addURI("com.paad.skeletondatabaseprovider",
	     SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
	   uriMatcher.addURI("com.paad.skeletondatabaseprovider",
	     SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);
	  }

	  /**
	   * Listing 8-31: Returning the correct MIME type for search results
	   */
	  @Override
	  public String getType(Uri uri) {
	    // Return a string that identifies the MIME type
	    // for a Content Provider URI
	    switch (uriMatcher.match(uri)) {
	      case ALLROWS: 
	        return "vnd.android.cursor.dir/vnd.paad.elemental";
	      case SINGLE_ROW: 
	        return "vnd.android.cursor.item/vnd.paad.elemental";
	      case SEARCH : 
	        return SearchManager.SUGGEST_MIME_TYPE;
	      default: 
	        throw new IllegalArgumentException("Unsupported URI: " + uri);
	    }
	  }

	  /**
	   * Listing 8-32: Creating a projection for returning search suggestions
	   */
	  public static final String KEY_SEARCH_COLUMN = KEY_COLUMN_1_NAME;
	    
	  private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;
	  static {
	    SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
	    SEARCH_SUGGEST_PROJECTION_MAP.put(
	      "_id", KEY_ID + " AS " + "_id");
	    SEARCH_SUGGEST_PROJECTION_MAP.put(
	      SearchManager.SUGGEST_COLUMN_TEXT_1,
	      KEY_SEARCH_COLUMN + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
	    SEARCH_SUGGEST_PROJECTION_MAP.put(
	      SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, KEY_ID + 
	      " AS " + "_id");
	  }
	  
	  /**
	   * Listing 8-33: Returning search suggestions for a query
	   */
	  @Override
	  public Cursor query(Uri uri, String[] projection, String selection,
	      String[] selectionArgs, String sortOrder) {
	    // Open a read-only database.
	    SQLiteDatabase db = myOpenHelper.getWritableDatabase();

	    // Replace these with valid SQL statements if necessary.
	    String groupBy = null;
	    String having = null;
	    
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE_CONTACT);
	    
	    // If this is a row query, limit the result set to the passed in row.
	    switch (uriMatcher.match(uri)) {
	      case SINGLE_ROW : 
	        String rowID = uri.getPathSegments().get(1);
	        queryBuilder.appendWhere(KEY_ID + "=" + rowID);
	        break;
	      case SEARCH : 
	        String query = uri.getPathSegments().get(1);
	        queryBuilder.appendWhere(KEY_SEARCH_COLUMN + 
	          " LIKE \"%" + query + "%\"");
	        queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP); 
	        break;
	      default: break;
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
	      case SINGLE_ROW : 
	        String rowID = uri.getPathSegments().get(1);
	        selection = KEY_ID + "=" + rowID
	            + (!TextUtils.isEmpty(selection) ? 
	              " AND (" + selection + ')' : "");
	      default: break;
	    }
	    
	    // To return the number of deleted items you must specify a where
	    // clause. To delete all rows and return a value pass in "1".
	    if (selection == null)
	      selection = "1";
	  
	    // Execute the deletion.
	    int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE_CONTACT, selection, selectionArgs);
	    
	    // Notify any observers of the change in the data set.
	    getContext().getContentResolver().notifyChange(uri, null);
	    
	    return deleteCount;
	  }
	  
	  @Override
	  public Uri insert(Uri uri, ContentValues values) {
	    // Open a read / write database to support the transaction.
	    SQLiteDatabase db = myOpenHelper.getWritableDatabase();
	    
	    // To add empty rows to your database by passing in an empty Content Values object
	    // you must use the null column hack parameter to specify the name of the column 
	    // that can be set to null.
	    String nullColumnHack = null;
	    
	    // Insert the values into the table
	    long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE_CONTACT, 
	        nullColumnHack, values);
	    
	    if (id > -1) {
	      // Construct and return the URI of the newly inserted row.
	      Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
	      
	      // Notify any observers of the change in the data set.
	      getContext().getContentResolver().notifyChange(insertedId, null);
	      
	      return insertedId;
	    }
	    else
	      return null;
	  }
	  
	  @Override
	  public int update(Uri uri, ContentValues values, String selection,
	    String[] selectionArgs) {
	    
	    // Open a read / write database to support the transaction.
	    SQLiteDatabase db = myOpenHelper.getWritableDatabase();
	    
	    // If this is a row URI, limit the deletion to the specified row.
	    switch (uriMatcher.match(uri)) {
	      case SINGLE_ROW : 
	        String rowID = uri.getPathSegments().get(1);
	        selection = KEY_ID + "=" + rowID
	            + (!TextUtils.isEmpty(selection) ? 
	              " AND (" + selection + ')' : "");
	      default: break;
	    }
	  
	    // Perform the update.
	    int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE_CONTACT, 
	      values, selection, selectionArgs);
	    
	    // Notify any observers of the change in the data set.
	    getContext().getContentResolver().notifyChange(uri, null);
	    
	    return updateCount;
	  }

	  
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


	// private class to get database :)

	public class MySQLiteOpenHelper extends SQLiteOpenHelper {

		public static final String DATABASE_NAME = "Storybook.db";
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_TABLE_CONTACT = "contact_data";
 
		
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
			createQuery.append("CREATE TABLE \"" + DATABASE_TABLE_CONTACT + "\" (");
			createQuery.append("	    \"" + KEY_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT,");
			createQuery.append("	    \"" + CONTACT + "\" TEXT,");
			createQuery.append("	    \"" + EVENT_TYPE + "\" TEXT,");
			createQuery.append("	    \"" + URI + "\" TEXT,");
			createQuery.append("	    \"" + DATE + "\" DATE,");
			createQuery.append("	    \"" + LOCATION + "\" TEXT");
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
			db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_CONTACT);
			// Create a new one.
			onCreate(db);
		}

	}

}
