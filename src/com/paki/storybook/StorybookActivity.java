package com.paki.storybook;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StorybookActivity extends StoryBookStaticImport {
	
	
	Cursor cursor;
	List<String> ls = new ArrayList<String>();
	public static SQLiteDatabase db = StorybookContentProvider.myOpenHelper.getWritableDatabase();
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   
	    // Inflate your view
	    setContentView(R.layout.main);
	    
	    cursor = db.rawQuery("SELECT " + StorybookContentProvider.CONTACT + 
	    					 " FROM " + StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE ,
	    					 null);
	    

	   
	    int nameIdx = cursor.getColumnIndex(StorybookContentProvider.CONTACT);
	    while(cursor.moveToNext()){
	    	Log.v(StorybookContentProvider.TAG_LOG, "sono nell'oncreate!"+ cursor.getString(nameIdx));
	    	String name = cursor.getString(nameIdx);
	    	ls.add(name);
	    }
	    
	    ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ls);
	   
	    setListAdapter(aa);
	    
	    
	    getCalls();
	    getSms();
	}
	

}
