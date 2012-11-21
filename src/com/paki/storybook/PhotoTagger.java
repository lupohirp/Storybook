package com.paki.storybook;

import java.io.File;

import com.paki.storybook.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PhotoTagger extends Activity {

	ImageView imageView;
	LinearLayout tl;
	ScrollView sv;
	
	String currentDate;
	String Loc;
	String person = null;
	String OtherPers = null;
	ContentValues cv = new ContentValues();
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phototagger);
		imageView = (ImageView) findViewById(R.id.imageView1);
		tl=(LinearLayout) findViewById(R.id.Grid1);
		sv=(ScrollView) findViewById(R.id.Scroll1);
		
	}
	
	public void onStart(){
		super.onStart();
		String[] projection = new String[]{MediaStore.Images.ImageColumns._ID,
	            MediaStore.Images.ImageColumns.DATA,
	            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
	            MediaStore.Images.ImageColumns.DATE_TAKEN,
	            MediaStore.Images.ImageColumns.MIME_TYPE
	    };
	    final Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	                    projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
	    
	    final Uri uri;
	    if (cursor.moveToFirst()) {
	        
	        final String imageLocation = cursor.getString(1);
	        Log.v(StorybookContentProvider.TAG_LOG,"Image located at: " + imageLocation);
	        File imageFile = new File(imageLocation);
	        if (imageFile.exists()) {   // TODO: is there a better way to do this?
	        	Log.v(StorybookContentProvider.TAG_LOG, "l'immagine esiste");
	            final Bitmap bm = BitmapFactory.decodeFile(imageLocation);
	            uri = Uri.fromFile(imageFile);
	            imageView.setImageBitmap(bm);
	            //setContentView(imageView);
	            
	            final EditText ed = (EditText) findViewById(R.id.editText1);
	            ed.setText(null);
	            ed.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub

						 person = ed.getText().toString();
						
					}
				});
	            
	            final EditText ed2 = (EditText) findViewById(R.id.editText2);
	            ed2.setText(null);
	            ed2.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						currentDate= ed2.getText().toString();
						Log.v(StorybookContentProvider.TAG_LOG, currentDate);
						
					}
				});
	            
	           
	            
	            final EditText ed3 = (EditText) findViewById(R.id.editText3);
	            ed3.setText(null);
	            ed3.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						Loc= ed3.getText().toString();
					}
				});
	            
	            
	            
	            Button bt = (Button) findViewById(R.id.button1);
	            final EditText tv2 = new EditText(this);
	           
            	
	            bt.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
			
						TextView othText = new TextView(PhotoTagger.this);
						othText.setText("Iserisci un'altra persona");
						tv2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
	                            LayoutParams.WRAP_CONTENT, 1));
						tv2.setText(null);
						tl.addView(othText);
						tl.addView(tv2);
						
						tv2.setOnFocusChangeListener(new OnFocusChangeListener() {
							
							public void onFocusChange(View v, boolean hasFocus) {
								// TODO Auto-generated method stub

								 OtherPers= tv2.getText().toString();
								 Log.v(StorybookContentProvider.TAG_LOG, OtherPers);
							}
						});
						
					
					}
				});
	            
	            
	            
	            Button bt2 = (Button)findViewById(R.id.button2);
	           
	            bt2.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						boolean ver = SaveIntoDataBase(person, currentDate, Loc, uri);
						if(tv2.isShown())
								ver = SaveIntoDataBase(OtherPers, currentDate, Loc, uri);
						if(ver)
							finish();
					}
				});
	        }
	    } 
	}
	
	
	
	
	
	private boolean SaveIntoDataBase(String name,String date,String location, Uri uri){
		
		ContentValues cv = new ContentValues();
		
		if(name == null || date == null || location == null){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Devi riempire tutti i campi prima!!");
			dialog.setPositiveButton("Ok", null);
			AlertDialog dial = dialog.create();
			dial.show();
			return false;
		}
		
		cv.put(StorybookContentProvider.CONTACT, name);
		Log.v(StorybookContentProvider.TAG_LOG, "salvando" + name );  
		cv.put(StorybookContentProvider.DATE, date);
		Log.v(StorybookContentProvider.TAG_LOG, "salvando" + date );
		cv.put(StorybookContentProvider.EVENT_TYPE, "Foto");
		Log.v(StorybookContentProvider.TAG_LOG, "salvando" + location );
		cv.put(StorybookContentProvider.LOCATION, location);
		cv.put(StorybookContentProvider.URI, uri.toString());
		
		getContentResolver().insert(StorybookContentProvider.CONTENT_URI, cv);
			
		Log.v(StorybookContentProvider.TAG_LOG, "Inserito il tag nel database!!!");

		return true;
	}
	
}
