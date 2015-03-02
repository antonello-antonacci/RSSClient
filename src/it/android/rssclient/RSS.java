package it.android.rssclient;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.ArrayList;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RSS extends Activity  {
   
   String feedUrl =null;
   int indice=-1;//indice della classe che sto leggendo cioè listItems.get(indice)
   int size=0;//numero di elementi in listItems
   String titolo=null;
   ListView rssListView = null;
   SharedPreferences prefTitle;
   ArrayList<String> listTitoli=new ArrayList<String>();
   ArrayList<RSSItem> RSSItems = new ArrayList<RSSItem>();
   ArrayAdapter<RSSItem> array_adapter = null; 
    
   
   public boolean onCreateOptionsMenu(Menu menu) { 
     	super.onCreateOptionsMenu(menu);
      	menu.add("Refresh"); 
   	    return true;}  
   
   public boolean onOptionsItemSelected(MenuItem item) {
  	 
  	 if (item.getTitle()=="Refresh"){
  		 onCreate(null);	 
	 		 }        	 
  	 return true;
   }
   
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.list_item);
       final Intent i = getIntent();
       feedUrl=i.getStringExtra("lrss");//ricavo il link rss del quale l'utente vuole leggere le notizie
       indice=i.getIntExtra("indice",-1);
       size=i.getIntExtra("dim",0);
       refreshRSSList();//carico le notizie
       rssListView = (ListView) findViewById(R.id.rssListView);
       array_adapter = new ArrayAdapter<RSSItem>(this, android.R.layout.simple_list_item_1, RSSItems);
       rssListView.setAdapter(array_adapter);
       RSSItem Ind=array_adapter.getItem(0);//prendo la prima notizia
	   titolo=Ind.getTitle();//ricavo il titolo della prima notizia
	   listTitoli=GetTitle();
	   SetTitle(listTitoli);//setto il valore dei titoli che uso per verificare se ci sono dei nuovi post nel service
       rssListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 	@Override
		            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			        // TODO Auto-generated method stub
		            RSSItem prova=array_adapter.getItem(arg2);
		            String prova2=prova.getLink();
		        	Intent i =new Intent(RSS.this, Web.class);
		            i.putExtra("link", prova2);
		            startActivity(i);
		            }
	  });
      
}
    
   private void refreshRSSList() {
        
       ArrayList<RSSItem> newItems = RSSItem.getRSSItems(feedUrl);
       RSSItems.clear();
       RSSItems.addAll(newItems);
   }
   
   private void SetTitle(ArrayList<String> lis) {
		 
			  String chiave="a";
			  if (!lis.isEmpty()){
			       	SharedPreferences.Editor editor = prefTitle.edit();
		            editor.clear();
		        	for(int i=0;i<size;i++){
		        	if (i==indice){
		        		editor.putString(chiave, titolo);
			    		editor.commit();
			    		chiave=chiave+"a";
		        	}
		        	else{
		    		editor.putString(chiave, lis.get(i));
		    		editor.commit();
		    		chiave=chiave+"a";}  }
		  }	
     }
   
   private ArrayList<String> GetTitle(){ 
	   
			  		ArrayList<String> lis = new ArrayList<String>();
			  		String chiave="a";
			    	for(int i=0;i<size;i++){
			    	prefTitle = getSharedPreferences("exampleTit", Context.MODE_PRIVATE);
			        lis.add(prefTitle.getString(chiave, "No option"));
			        chiave=chiave+"a";			       
			      }
			        chiave="a";
			        return lis;		  
		  }	
}
