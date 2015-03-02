package it.android.rssclient;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.android.rssclient.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RssClientActivity extends Activity {
	
	      ArrayList<String> listItems=new ArrayList<String>();
          ArrayList<String> listRss=new ArrayList<String>();
          static String titolo=null;
          SharedPreferences pref; 
          SharedPreferences prefRss;
          private Intent sIntent; 
                   
          public boolean onCreateOptionsMenu(Menu menu) {  
        	super.onCreateOptionsMenu(menu);
           	menu.add("SetTimer");
        	menu.add("AddClass");
        	return true;}    
         
          public boolean onOptionsItemSelected(MenuItem item) {
        	 
        	 if (item.getTitle()=="AddClass"){
        		 Intent i = new Intent(RssClientActivity.this, AddClass.class);
        		 startActivityForResult(i, 0);	//lancio Activity per la gestione dell'immissione di un nuovo elemento			 
	 	 		 }
        	 else{
        		Intent i = new Intent (RssClientActivity.this, SetTimer.class);
        		startActivity(i);
              }
        	 return true;
         }
         
         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             //al termine dell'activity prendo il nome e il link rss immessi dall'utente e li salvo
        	 if (resultCode == RESULT_OK) {    		
         		String classe=data.getStringExtra("l"); 
         		String rss=data.getStringExtra("R");  
         		listRss.add(rss);
				listItems.add(classe);		
         	}  
         }
         
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        listItems.clear();
        listItems=GetPref(0);
        listRss.clear();
        listRss=GetPref(1);
        Intent intent = getIntent();//controllo se sono stato attivato da un intent esterno
        if (Intent.ACTION_VIEW.equals(intent.getAction())){
        	final String link=intent.getDataString();
        	setContentView(R.layout.addweb);
        	Button button = (Button) findViewById(R.id.sub); 
	    	button.setOnClickListener(new View.OnClickListener() {
 	             public void onClick(View v) {
 	            	 final String classe = ((TextView) findViewById(R.id.edit_web)).getText().toString();
 	            	 titolo=Carica(link);
 	            	 if(classe.isEmpty()){
 	            		Toast.makeText(getBaseContext(), "Error: Name or linkRss null", Toast.LENGTH_LONG).show();
 	            	 }
 	            	 else{
 	            	 if(titolo==null){
 	            		Toast.makeText(getBaseContext(), "Error: Rss not valid", Toast.LENGTH_LONG).show(); 
 	            	 }
 	            	 else{
 	            	 listItems.add(classe);
 	            	 listRss.add(link);
 	            	 Toast.makeText(getBaseContext(), "New Feed Rss was added", Toast.LENGTH_LONG).show();
 	            	 SetPref(listItems,0);
 	 			     SetPref(listRss,1);
 	 		         finish();}}}
 	             }); 
        }
        
        else
        if (listItems.isEmpty()){
			    	setContentView(R.layout.main);
			    	AlertDialog.Builder builder = new AlertDialog.Builder(RssClientActivity.this);
			    	 builder.setMessage("Your feed list is empty add a new rss manualy (press menù-AddClass) or add it by the web...").setCancelable(false);
		 		      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		 		      	public void onClick(DialogInterface dialog, int id) {
		 		      		dialog.cancel();
		 		      		  }
		 		      	});
		 		     AlertDialog alert = builder.create();
		 		      alert.show();}
        else{
        setContentView(R.layout.main);
        listItems.clear();
        listItems=GetPref(0);
        listRss.clear();
        listRss=GetPref(1);
        ListView lv = (ListView) findViewById(R.id.main);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
        lv.setClickable(true);
        int position = 0; 
        lv.setBackgroundColor((position & 1) == 1 ? Color.WHITE: Color.BLACK);
		ColorDrawable divcolor = new ColorDrawable(Color.WHITE);
		lv.setDivider(divcolor);
		lv.setDividerHeight(2); 
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

		    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int arg2, long arg3) {
		    	   AlertDialog.Builder builder = new AlertDialog.Builder(RssClientActivity.this);
                   
		 		      builder.setMessage("Are you sure you want to canc "+listItems.get(arg2)+" ?").setCancelable(false);
		 		      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		 		      	public void onClick(DialogInterface dialog, int id) {
		 		      		listRss.remove(arg2); //cancello nome e link dell'elemento  
		                    listItems.remove(arg2);
		                    onResume();}
		 		      		});

		 		      builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		 		      	public void onClick(DialogInterface dialog, int id) {
		 		      			dialog.cancel();
		 		      	}
		 		      });

		 		      AlertDialog alert = builder.create();
		 		      alert.show();
  		    return false;
		}
	});
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {		
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(isOnline()){		        
					Intent i = new Intent(RssClientActivity.this, RSS.class);
					i.putExtra("lrss",listRss.get(arg2));
					i.putExtra("indice",arg2);
					i.putExtra("dim", listItems.size());
					RssClientActivity.this.startActivity(i); }
					else{
						Toast.makeText(getBaseContext(),"Connection Failed",Toast.LENGTH_LONG).show();
					}}
                     });      
        }
       //lancio il service 
       sIntent = new Intent(this,MySer.class);
       startService(sIntent);
      
       }
	    
		public void onResume(){
			    super.onResume();
			    SetPref(listItems,0);
			    listItems.clear();
		        listItems=GetPref(0);
		        SetPref (listRss,1);
		        listRss.clear();
		        listRss=GetPref(1);
			    
			    Intent intent = getIntent();
			    //verifico se sono stato risvegliato da un intent esterno
			    if (Intent.ACTION_VIEW.equals(intent.getAction())){
			        	final String link=intent.getDataString();
			        	setContentView(R.layout.addweb);
			        	Button button = (Button) findViewById(R.id.sub); 
				    	button.setOnClickListener(new View.OnClickListener() {
			 	             public void onClick(View v) {
			 	            	 final String classe = ((TextView) findViewById(R.id.edit_web)).getText().toString();
			 	            	 titolo=Carica(link);
			 	            	 if(classe.isEmpty()||link.isEmpty()){
			 	            		Toast.makeText(getBaseContext(), "Error: Name or linkRss is null", Toast.LENGTH_LONG).show();
			 	            	 }
			 	            	 else{
			 	            	 if(titolo.isEmpty()){
			 	            		Toast.makeText(getBaseContext(), "Error: Rss not valid", Toast.LENGTH_LONG).show(); 
			 	            	 }
			 	            	 else{
			 	            	 listItems.add(classe);
			 	            	 listRss.add(link);
			 	            	 Toast.makeText(getBaseContext(), "New Feed Rss was added", Toast.LENGTH_LONG).show();
			 	            	 SetPref(listItems,0);
			 	 			     SetPref(listRss,1);
			 	 		         finish();}}}
			 	             }); 
			        }
			    
			    else
			    if (listItems.isEmpty()){
			    	setContentView(R.layout.main);
			    	AlertDialog.Builder builder = new AlertDialog.Builder(RssClientActivity.this);
			    	 builder.setMessage("Your feed list is empty add a new rss manualy (press menù-AddClass) or add it by the web...").setCancelable(false);
		 		      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		 		      	public void onClick(DialogInterface dialog, int id) {
		 		      		dialog.cancel();
		 		      		  }
		 		      	});
		 		     AlertDialog alert = builder.create();
		 		      alert.show();}
			    else{
		        setContentView(R.layout.main);
		        ListView lv = (ListView) findViewById(R.id.main);
			    lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
		        lv.setClickable(true);
		        int position = 0;
				lv.setBackgroundColor((position & 1) == 1 ? Color.WHITE: Color.BLACK);
				ColorDrawable divcolor = new ColorDrawable(Color.WHITE);
				lv.setDivider(divcolor);
				lv.setDividerHeight(2);      
				lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

		    	   public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int arg2, long arg3) {
		    		   AlertDialog.Builder builder = new AlertDialog.Builder(RssClientActivity.this);
                      
		 		      builder.setMessage("Are you sure you want to canc "+listItems.get(arg2)+" ?").setCancelable(false);
		 		      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		 		      	public void onClick(DialogInterface dialog, int id) {
		 		      		   listItems.remove(arg2);	
		 		               listRss.remove(arg2); //cancello nome e link dell'elemento  
		 		               onResume();}
		 		      	});

		 		      builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		 		      	public void onClick(DialogInterface dialog, int id) {
		 		      			dialog.cancel();
		 		      	}
		 		      });

		 		      AlertDialog alert = builder.create();
		 		      alert.show();
		 		     
		             return false;
		 		}
		 	});	
		        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		           		
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
						if(isOnline()){		        
						Intent i = new Intent(RssClientActivity.this, RSS.class);
						i.putExtra("lrss",listRss.get(arg2));
						i.putExtra("indice",arg2);
						i.putExtra("dim", listItems.size());
						RssClientActivity.this.startActivity(i); }
						else{
							Toast.makeText(getBaseContext(),"Connection Failed",Toast.LENGTH_LONG).show();
						}}
					});
		       }
		}
		
		  //setto le preferenze dei link rss e dei relativi nomi dati 
		  private void SetPref(ArrayList<String> lis, int x) { //x==0 listItems --- x==1 listRss
			  if (x==0){
				  String chiave="a";
				  
				  if (!lis.isEmpty()){
					    SharedPreferences.Editor editor = pref.edit();
				       	editor.clear();
			        	for(int i=0;i<lis.size();i++){
			    		editor.putString(chiave, lis.get(i));
			    		editor.commit();
			    		chiave=chiave+"a";}  }
				  else{
					  SharedPreferences.Editor editor = pref.edit();
					  editor.clear();
					  editor.putString("a", "Vuoto");
			    	  editor.commit();
				  }
			  }		  
			  else{
				  String chiaveR="r";
				  
				  if (!lis.isEmpty()){
					    SharedPreferences.Editor editor = prefRss.edit();
	 		    	   	editor.clear();
	 		        	for(int i=0;i<lis.size();i++){
	 		    		editor.putString(chiaveR, lis.get(i));
	 		    		editor.commit();
	 		    		chiaveR=chiaveR+"r";}
	 		        }
				  else{
					  SharedPreferences.Editor editor = prefRss.edit();
					  editor.clear();
					  editor.putString("r", "Vuoto");
			    	  editor.commit();
				  }}
			 }
		 
		  //carico i valori salvati nelle preferenze dei link rss e dei relativi nomi
		  private ArrayList<String> GetPref(int y){ //y==0 listItems ---- y==1 lisrRss
			  if (y==0){
				  		ArrayList<String> lis = new ArrayList<String>();
				  		String chiave="a";
				    	while(!(lis.contains("No option") || lis.contains("Vuoto"))){
				        pref = getSharedPreferences("example", Context.MODE_PRIVATE);
				        lis.add(pref.getString(chiave, "No option"));
				        chiave=chiave+"a";			       
				      }
				        chiave="a";
				       lis.remove("No option");
				       lis.remove("Vuoto");
				       return lis;		  
			  }			  
			  else{
				   ArrayList<String> lis = new ArrayList<String>();
				   String chiaveRss="r";
			    	while(!(lis.contains("No option") || lis.contains("Vuoto"))){
	 			        prefRss = getSharedPreferences("example2", Context.MODE_PRIVATE);
	 			        lis.add(prefRss.getString(chiaveRss, "No option"));
	 			        chiaveRss=chiaveRss+"r"; 
	 			       }
				        chiaveRss="r";
				        lis.remove("No option");
				        lis.remove("Vuoto");
	 		        	return lis;
	 			  }
			
		  }
		  
		  public boolean isOnline () {
              ConnectivityManager cm = ( ConnectivityManager ) this.getSystemService( Context.CONNECTIVITY_SERVICE );
              NetworkInfo ni = cm.getActiveNetworkInfo();
              if(ni == null)
                      return false;
              return ni.isConnected();
      }
		  
		  private static String Carica(String feed){
				
			    try {
			        
			        URL url = new URL(feed);
			        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //apro la connessione 
			         
			        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			            InputStream is = conn.getInputStream();
			            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			            DocumentBuilder db = dbf.newDocumentBuilder();
			            Document document = db.parse(is);
			            Element element = document.getDocumentElement();
			            NodeList nodeList = element.getElementsByTagName("item");//ricavo la struttura dove sono salvati i dati dei post
			             
			            if (nodeList.getLength() > 0) {
			            	    //carico il primo elemento e restituisco il suo titolo                
			                    Element entry = (Element) nodeList.item(0);
			                    Element _titleE       = (Element)entry.getElementsByTagName("title").item(0);
			                    titolo          = _titleE.getFirstChild().getNodeValue();
			                    }
			             }
			         } catch (Exception e) {
			        e.printStackTrace();
			      
			         }return titolo;
				}
		  }
