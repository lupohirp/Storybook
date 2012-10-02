package com.paki.storybook;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StorybookActivity extends ListActivity {
	
	
	Cursor cursor;
	List<String> ls = new ArrayList<String>();
	public static SQLiteDatabase db = StorybookContentProvider.myOpenHelper.getWritableDatabase();
	public static int flag = 0;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   
	    // Inflate your view
	    setContentView(R.layout.main);
	    
	    cursor = db.rawQuery("SELECT " + StorybookContentProvider.CONTACT + 
	    					 " FROM " + StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE + 
	    					 " GROUP BY "  + StorybookContentProvider.CONTACT,
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
	    
	   
	   
	    if(flag==0){
	    Intent startActivityIntent = new Intent(this,StoryBookStaticImport.class);
	    startActivity(startActivityIntent);
	    }
	}
	

}
