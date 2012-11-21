package com.paki.storybook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class StorybookDisplayResult extends Activity{
	
	Cursor cursor;
	
	/*
	 *  Refers to passed objects
	 */
	
	String named;
	String dated;
	String loced;
	String evented;
	
	List<String> ls;
	ListView ls1;

	/*
	 * Costants for context menu
	 */
	
	final int CONTEXT_MENU_PERSON = 1;
	final int CONTEXT_MENU_DATE = 2;
	final int CONTEXT_MENU_LOCATION = 3;
	

	/**
	 *  Refer to Sotrybook database
	 */
	
	public static SQLiteDatabase db = StorybookContentProvider.myOpenHelper
			.getWritableDatabase();
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.displayresult);
		
		ls = new ArrayList<String>();
		ls1 = (ListView) findViewById(R.id.listView1);

		//creating context menu
		
		registerForContextMenu(ls1);
	
		//get objects passed from StorybookActivity
		
		named = getIntent().getExtras().getString("Name");
		dated = getIntent().getExtras().getString("Date");
		loced = getIntent().getExtras().getString("Location");
		evented = getIntent().getExtras().getString("Event");
		
		//Execute Query
	
        doQuery(named,dated,loced,evented);
		
	}
	
	//Overriding support function for context menu 
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        
		  
		  menu.add(Menu.NONE, CONTEXT_MENU_PERSON, Menu.NONE, "Contatto");
		  menu.add(Menu.NONE, CONTEXT_MENU_DATE, Menu.NONE, "Data");
		  menu.add(Menu.NONE, CONTEXT_MENU_LOCATION, Menu.NONE, "Luogo");
		  menu.setHeaderTitle("Esplora la mappa degli eventi per :");
		  
		 }
	
	@Override
	 public boolean onContextItemSelected(MenuItem item) {
	 
	      AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	      Long id = ls1.getAdapter().getItemId(info.position);
	 
	      switch (item.getItemId()) {
	              case CONTEXT_MENU_DATE:
	                    Log.v(StorybookContentProvider.TAG_LOG, "Data!!!!");
	                    StartExploreMap(CONTEXT_MENU_DATE);
	                   return(true);
	             case CONTEXT_MENU_LOCATION:
	                    Log.v(StorybookContentProvider.TAG_LOG, "Location!!!!");
	                    StartExploreMap(CONTEXT_MENU_LOCATION);
	                   return(true);
	             case CONTEXT_MENU_PERSON:
	            	 	StartExploreMap(CONTEXT_MENU_PERSON);
	            	    Log.v(StorybookContentProvider.TAG_LOG, "Person!!!!");
	            	   return(true);
	      }
	  return(super.onOptionsItemSelected(item));
	}

	/*
	 * Start Exploration Map
	 */
	
	private void StartExploreMap(int selected) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(StorybookDisplayResult.this,StorybookNavigateMap.class);
		intent.putExtra("Case", selected);
		startActivity(intent);
	}

	
	/**
	 * Private method to do the right query
	 * 
	 */
	
	private void doQuery(String named2, String dated2, String loced2,String evented2) {
		String query = null;
		
		if (!named2.equals("") && dated2.equals("") && loced2.equals("") && evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		else if (named2.equals("") && !dated2.equals("") && loced2.equals("") && evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.DATE + "=" + "'" + dated2 +"'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		else if (named2.equals("") && dated2.equals("") && !loced2.equals("") && evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		else if (named2.equals("") && dated2.equals("") && loced2.equals("") && !evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.EVENT_TYPE + "=" + "'" + evented2 + "'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		
		else if (!named2.equals("") && !dated2.equals("") && loced2.equals("") && evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"' AND " + 
					StorybookContentProvider.DATE + "=" + "'" + dated2 + "'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		} 	
		else if (!named2.equals("") && dated2.equals("") && !loced2.equals("") && evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"' AND " + 
					StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		} 
		else if (!named2.equals("") && dated2.equals("") && loced2.equals("") && !evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"' AND " + 
					StorybookContentProvider.EVENT_TYPE + "=" + "'" + evented2 + "'" ;
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		} 
		else if (!named2.equals("") && !dated2.equals("") && !loced2.equals("") && evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"' AND " + 
					StorybookContentProvider.DATE + "=" + "'" + dated2 + "' AND " + 
					StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "'";
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		else if (!named2.equals("") && !dated2.equals("") && loced2.equals("") && !evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"' AND " + 
					StorybookContentProvider.DATE + "=" + "'" + dated2 + "' AND " + 
					StorybookContentProvider.EVENT_TYPE + "=" + "'" + evented2 + "'";
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		else if (!named2.equals("") && dated2.equals("") && !loced2.equals("") && !evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + named2 +"' AND " + 
					StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "' AND " + 
					StorybookContentProvider.EVENT_TYPE + "=" + "'" + evented2 + "'";
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		else if (named2.equals("") && !dated2.equals("") && !loced2.equals("") && !evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.DATE + "=" + "'" + dated2 +"' AND " + 
					StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "' AND " + 
					StorybookContentProvider.EVENT_TYPE + "=" + "'" + evented2 + "'";
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		
		else if (!named2.equals("") && !dated2.equals("") && !loced2.equals("") && !evented2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.DATE + "=" + "'" + dated2 +"' AND " + 
					StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "' AND " + 
					StorybookContentProvider.EVENT_TYPE + "=" + "'" + evented2 + "' AND " +
					StorybookContentProvider.CONTACT + "=" + "'" + named2 + "'";
			
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		}
		
		else if (named2.equals("") && !dated2.equals("") && !loced2.equals("")){
			query = " SELECT * " +
					" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
					" WHERE " + StorybookContentProvider.DATE + "=" + "'" + dated2 +"' AND " + 
					StorybookContentProvider.LOCATION + "=" + "'" + loced2 + "'" ;
			cursor = db.rawQuery(query, null);
			populateListView(cursor);
		} 

}

	
	/*
	 *  Private Method to populate the results of the query with 
	 *  a Listview
	 */
	
	private void populateListView(Cursor curs) {
		// TODO Auto-generated method stub
		SimpleAdapter adapter;
		final List<HashMap<String, String>> fill;
		fill = new ArrayList<HashMap<String, String>>();
		
		
		final int nameIdx = cursor.getColumnIndex(StorybookContentProvider.CONTACT);
		final int dateIdx = cursor.getColumnIndex(StorybookContentProvider.DATE);
		final int evenIdx = cursor.getColumnIndex(StorybookContentProvider.EVENT_TYPE);
		final int locaIdx = cursor.getColumnIndex(StorybookContentProvider.LOCATION);
		final int uriIdx  = cursor.getColumnIndex(StorybookContentProvider.URI);

		String name = null;
		String date = null;
	    String event = null;
	    String location = null;

	    
		while (cursor.moveToNext()) {
			
			name = cursor.getString(nameIdx);
			date = cursor.getString(dateIdx);
			event = cursor.getString(evenIdx);
			location = cursor.getString(locaIdx);
	
			
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("item", name);
			map.put("subitem", event + " mentre eri a " + location + " il " + date + " ");
			fill.add(map);


		}
		
		// Declare the support Adapter
		
		adapter = new SimpleAdapter(this, fill, R.layout.displayresult, new String[]{"item", "subitem"}, new int[]{R.id.textView6, R.id.textView7});
		
		// Set the adapter to ListView and populate
		
		ls1.setAdapter(adapter);
		
		
		ls1.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("static-access")
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				// Get the item pressed and his elements
				
				HashMap<String, String> pr = fill.get(arg2);
				String name = pr.get("item");
				String surname = pr.get("subitem");
				
				// Calculate needed data searching in a string
				
				String event = surname.substring(0,surname.indexOf(" mentre "));
				String location = surname.substring(surname.indexOf(" mentre eri a ")+14,surname.indexOf(" il "));
				String date = surname.substring(surname.indexOf(" il ")+4,surname.length()-1);
				
				//Getting the URI. 
				
				String uri = null;
				
				
				String query = " SELECT * " +
						" FROM " +  StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT + 
						" WHERE " + StorybookContentProvider.CONTACT + "=" + "'" + name + "' AND " + 
						StorybookContentProvider.DATE + "=" + "'" + date + "' AND " + 
						StorybookContentProvider.EVENT_TYPE + "=" + "'" + event + "' AND " +
						StorybookContentProvider.LOCATION+ "=" + "'" + location + "'";
				
				Cursor cr = db.rawQuery(query, null);
				int uriindex = cr.getColumnIndex(StorybookContentProvider.URI);
				if(cr.moveToFirst()){
					uri = cr.getString(uriindex);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					if (event.equals("Foto"))
						intent.setDataAndType(Uri.parse(uri), "image/*");
					else
					intent.setData(Uri.parse(uri));
					startActivity(intent);
				}
			
				

				
			}
		});
	}
		
		
	}



