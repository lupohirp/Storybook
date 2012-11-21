package com.paki.storybook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class StorybookActivity extends Activity {

	Cursor cursor;
	
	CheckBox namech;
	CheckBox datech;
	CheckBox locch;
	CheckBox eventch;
	
	EditText nameed;
	EditText dateed;
	EditText loced;
	EditText evented;

	
	
	public static SQLiteDatabase db = StorybookContentProvider.myOpenHelper
			.getWritableDatabase();
	public static int flag = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate your view
		setContentView(R.layout.main);

		//if (flag == 0) {
			//Intent startActivityIntent = new Intent(this,
			//		StoryBookStaticImport.class);
			//startActivity(startActivityIntent);
		//}
		
		Intent startServiceIntent = new Intent(this,
				StorybookDynamicImport.class);
		startService(startServiceIntent);
		
		namech = (CheckBox) findViewById(R.id.checkBox1);
		datech = (CheckBox) findViewById(R.id.checkBox2);
		locch = (CheckBox) findViewById(R.id.checkBox3);
		eventch = (CheckBox) findViewById(R.id.checkBox4);
		
		
		nameed = (EditText) findViewById(R.id.editText1);
		dateed = (EditText) findViewById(R.id.editText2);
		loced = (EditText) findViewById(R.id.editText3);
		evented = (EditText) findViewById(R.id.editText4);
		
		/*
		 *  Callback for First checkbox
		 */
		
		namech.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				if(isChecked){
					AlertDialog.Builder builder = new AlertDialog.Builder(StorybookActivity.this);
					final AutoCompleteTextView edchoose = new AutoCompleteTextView(StorybookActivity.this);
					final ArrayAdapter<String> aa = new ArrayAdapter<String>(StorybookActivity.this, android.R.layout.simple_dropdown_item_1line, GetNamesFromDb());
					edchoose.setAdapter(aa);
					edchoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							String selected = (String) aa.getItem(arg2);
							edchoose.setText(selected);
							
							
						}
					});
					builder.setMessage("Inserisci il nome da cercare");
					builder.setView(edchoose);
					builder.setPositiveButton("Ok", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							nameed.setText(edchoose.getText().toString());
							
						
							
						}
					});
					
					AlertDialog dialog = builder.create();
					dialog.show();
					
					
				}
				else{
					nameed.setText(null);
				}
					
				
			}
		});
		
		/*
		 * End of first callback
		 * 
		 */
		
		
		/*
		 * Callback for second checkbox
		 */
		
		datech.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					AlertDialog.Builder builder = new AlertDialog.Builder(StorybookActivity.this);
					final AutoCompleteTextView edchoose = new AutoCompleteTextView(StorybookActivity.this);
					final ArrayAdapter<String> aa = new ArrayAdapter<String>(StorybookActivity.this, android.R.layout.simple_dropdown_item_1line, GetDatesFromDb());
					edchoose.setAdapter(aa);
					edchoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							String selected = (String) aa.getItem(arg2);
							edchoose.setText(selected);
							
							
						}
					});
					
					builder.setMessage("Inserisci la data da cercare (in formato gg/mm/aaaa)");
					builder.setView(edchoose);
					builder.setPositiveButton("Ok", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							dateed.setText(edchoose.getText().toString());
							
						
							
						}
					});
					
					AlertDialog dialog = builder.create();
					dialog.show();
					
					
				}
				else{
					dateed.setText(null);
				}
					
				}
			
		});
		
		
		/*
		 * End of second callback
		 */
		
		
		
		
		
		/*
		 * Start callback for third checkbox
		 * 
		 */
	
		locch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					AlertDialog.Builder builder = new AlertDialog.Builder(StorybookActivity.this);
					final AutoCompleteTextView edchoose = new AutoCompleteTextView(StorybookActivity.this);
					final ArrayAdapter<String> aa = new ArrayAdapter<String>(StorybookActivity.this, android.R.layout.simple_dropdown_item_1line, GetLocsFromDb());
					edchoose.setAdapter(aa);
					edchoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							String selected = (String) aa.getItem(arg2);
							edchoose.setText(selected);
							
							
						}
					});

					builder.setMessage("Inserisci la località da cercare");
					builder.setView(edchoose);
					builder.setPositiveButton("Ok", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							loced.setText(edchoose.getText().toString());
							
						
							
						}
					});
					
					AlertDialog dialog = builder.create();
					dialog.show();
					
					
				}
				else{
					loced.setText(null);
				}
			}
		});
		
		
		
		/*
		 * end of third callback
		 */
	
		
		
		/*
		 * Start fourth callback
		 */
		
		eventch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				if(isChecked){
					final CharSequence[] event = {"Chiamata ricevuta","Chiamata effettuata", "Sms inviato", "Sms ricevuto", "Foto"};
				    AlertDialog.Builder alert = new AlertDialog.Builder(StorybookActivity.this);
				    alert.setTitle("Seleziona Evento");
				    final String display = null;
				    alert.setSingleChoiceItems(event,-1, new DialogInterface.OnClickListener(){
				    	
				        public void onClick(DialogInterface dialog, int which) 
				        {
				            
							if(event[which]=="Chiamata ricevuta")
				            {
				                
				                evented.setText("Chiamata ricevuta");
				            }
				            else if (event[which]=="Chiamata effettuata")
				            {
				                
				                evented.setText("Chiamata effettuata");
				                
				            }
				            else if(event[which]== "Sms inviato")
				            {
				            	
				            	evented.setText("Sms inviato");
				            	
				            }
				            else if (event[which]== "Sms ricevuto")
				            {
				            	
				            	evented.setText("Sms ricevuto");
				            }
				            else if (event[which]== "Foto")
				            {
				            	
				            	evented.setText("Foto");
				            	
				            }
				        }
				    });
				    
				    alert.setPositiveButton("Ok", null);
				    AlertDialog dial = alert.create();
				    dial.show();	
					
				}else{
					evented.setText("");
				}
			
			}
		});

		Button searchbt = (Button) findViewById(R.id.findbutton);
		
		searchbt.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayResults(nameed,dateed,loced,evented);
				
			}
			
		});
	

	}

	protected List<String> GetLocsFromDb() {
		// TODO Auto-generated method stub
		ArrayList<String> toReturn = new ArrayList<String>();
		cursor = db.rawQuery("SELECT " + StorybookContentProvider.LOCATION
				+ " FROM "
				+ StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT
				+ " GROUP BY " + StorybookContentProvider.LOCATION, null);
		int LocIdx = cursor.getColumnIndex(StorybookContentProvider.LOCATION);
		while (cursor.moveToNext()) {
			String Location = cursor.getString(LocIdx);
			toReturn.add(Location);
		}
		
		return toReturn;
	}

	protected List<String> GetDatesFromDb() {
		// TODO Auto-generated method stub
		ArrayList<String> toReturn = new ArrayList<String>();
		cursor = db.rawQuery("SELECT " + StorybookContentProvider.DATE
				+ " FROM "
				+ StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT
				+ " GROUP BY " + StorybookContentProvider.DATE, null);
		int dateIdx = cursor.getColumnIndex(StorybookContentProvider.DATE);
		while (cursor.moveToNext()) {
			String date = cursor.getString(dateIdx);
			toReturn.add(date);
		}
		
		return toReturn;
	}
	

	protected List<String> GetNamesFromDb() {
		ArrayList<String> toReturn = new ArrayList<String>();
		cursor = db.rawQuery("SELECT " + StorybookContentProvider.CONTACT
				+ " FROM "
				+ StorybookContentProvider.MySQLiteOpenHelper.DATABASE_TABLE_CONTACT
				+ " GROUP BY " + StorybookContentProvider.CONTACT, null);
		int nameIdx = cursor.getColumnIndex(StorybookContentProvider.CONTACT);
		while (cursor.moveToNext()) {
			Log.v(StorybookContentProvider.TAG_LOG, "sono nell'oncreate!"
					+ cursor.getString(nameIdx));
			String name = cursor.getString(nameIdx);
			toReturn.add(name);
		}
		
		return toReturn;
	}

	protected void displayResults(EditText nameed2,
			EditText dateed2, EditText loced2,EditText evented2) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(StorybookActivity.this,StorybookDisplayResult.class);
		intent.putExtra("Name", nameed2.getText().toString());
		intent.putExtra("Date", dateed2.getText().toString());
		intent.putExtra("Location", loced2.getText().toString());
		intent.putExtra("Event", evented2.getText().toString());
		
		startActivity(intent);
		
		
		
	}
	
	

	

	

		
	}

