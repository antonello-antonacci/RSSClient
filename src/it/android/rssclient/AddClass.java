package it.android.rssclient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AddClass extends Activity  {

	 static String titolo=null;
	
	 public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.add);
	       	final Intent get = getIntent();
	        	       
	    	Button button = (Button) findViewById(R.id.buttonid);
	    	button.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	                 // Perform action on click
	            	 
	            	 final String classe = ((TextView) findViewById(R.id.et_name)).getText().toString();
	            	 final String link1 = ((TextView) findViewById(R.id.et_lastName)).getText().toString();
	            	 if (link1.length()==0 || classe.length()==0){
	            		 Toast.makeText(getBaseContext(), "Error: Name or linkRss is null", Toast.LENGTH_LONG).show();
	            		 }
	            	 else{
	            	 String x=null;	 
	            	 x=Carica(link1);
	            	 if(x==null){Toast.makeText(getBaseContext(), "Error: Rss not valid", Toast.LENGTH_LONG).show();}
	            	 else{
	            	 get.putExtra("l", classe);
	            	 get.putExtra("R",link1);
	            	 setResult(RESULT_OK,get);
	            	 Toast.makeText(getBaseContext(), "Inserimento effettuato", Toast.LENGTH_LONG).show();
	            	 finish();
	            	 }	}	}	
	         });
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
