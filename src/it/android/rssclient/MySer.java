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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class MySer extends Service {

	private static final int SIMPLE_NOTIFICATION_ID = 0;
	ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listRss=new ArrayList<String>();
    ArrayList<String>listTitoli=new ArrayList<String>();
    SharedPreferences prefTitle;
	private RefThread thread; 
	SharedPreferences timer;
    SharedPreferences pref;
    SharedPreferences prefRss;
    long tslice=0; //timer aggiornamento post
    static String titolo=null;
	
	
	
	public void onCreate(){
		super.onCreate();
		Log.d("SERVICE_TAG","MyService started");
		timer = getSharedPreferences("tim", Context.MODE_PRIVATE);
		tslice=timer.getInt("TSlice", 600000); //carico il valore del timer dalle shared preferences, 10 minuti di default
		listItems=GetPref(0);
		listRss=GetPref(1);
		thread=new RefThread();
		thread.start();
		
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SERVICE_TAG","MyService started");
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	class RefThread extends Thread {
    	
    	private boolean running;
    	  	
    	public RefThread() {
    		super("RefThread");
    		running=false;
    	}
    	
    	public void run() {
    		while(running) {
    			try{Thread.sleep(tslice);} catch (InterruptedException e){}
    			if(isOnline()){
    			listTitoli.clear();
    			listTitoli=GetTitle();
    			if (!listTitoli.isEmpty()){
    				for(int i=0;i<listTitoli.size();i++){
    				String tmp=Carica(listRss.get(i)); //scarico il titolo dell'utlima notizia della classe in listItems.get(i) con link rss in listRss.get(i)
    				String tmp2=listTitoli.get(i);//prendo il titolo della notizia più recente vista dall'utente
    				if (!tmp2.equals(tmp)){
    					//se sono diversi allora ci sono dei nuovi post,avviso l'utente tramite notifica
    					Notification notification = new Notification(R.drawable.ic_launcher, "New Post At "+listItems.get(i), System.currentTimeMillis());
    					notification.flags |= Notification.FLAG_AUTO_CANCEL;
    					notification.defaults|=Notification.DEFAULT_VIBRATE;
    					notification.defaults|=Notification.DEFAULT_SOUND;
    					long[] vibrate={0,100,200,300};
    					notification.vibrate = vibrate;
    					Intent intent = new Intent(MySer.this,RSS.class);
    					intent.putExtra("lrss", listRss.get(i));
    					intent.putExtra("indice", i);
    					intent.putExtra("dim",listItems.size());
    					PendingIntent pIntent = PendingIntent.getActivity(MySer.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    					notification.setLatestEventInfo(MySer.this, tmp , "New Post At "+listItems.get(i), pIntent);
    					NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    					notificationManager.notify(SIMPLE_NOTIFICATION_ID, notification);
    				}
    			}}}
    			Log.d("THREAD","RUNNING");
    			}
    	}
    	
    	public void start() {
    		running=true;
    		super.start();
    	}
    	
    	
    	public void reset() {
    		running=false;
    	}
    
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	  
	  private ArrayList<String> GetPref(int y){ //y==0 listItems ---- y==1 lisrRss
		  if (y==0){
			  		ArrayList<String> lis = new ArrayList<String>();
			  		String chiave="a";
			    	while(!lis.contains("No option")){
			        pref = getSharedPreferences("example", Context.MODE_PRIVATE);
			        lis.add(pref.getString(chiave, "No option"));
			        chiave=chiave+"a";			       
			      }
			        chiave="a";
			       lis.remove("No option");
			       return lis;		  
		  }			  
		  else{
			   ArrayList<String> lis = new ArrayList<String>();
			   String chiaveRss="r";
			   while(!lis.contains("No option")){
			        prefRss = getSharedPreferences("example2", Context.MODE_PRIVATE);
			        lis.add(prefRss.getString(chiaveRss, "No option"));
			        chiaveRss=chiaveRss+"r"; 
			       }
			        chiaveRss="r";
		        	lis.remove("No option");
		        	return lis;
			     }
		  }
	  
	  private ArrayList<String> GetTitle(){ 
			 
	  		ArrayList<String> lis = new ArrayList<String>();
	  		String chiave="a";
	    	for(int i=0;i<listRss.size();i++){
	        prefTitle = getSharedPreferences("exampleTit", Context.MODE_PRIVATE);
	        lis.add(prefTitle.getString(chiave, "No option"));
	        chiave=chiave+"a";			       
	        }
	        chiave="a";
	        return lis;		  
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
	  
	  public boolean isOnline () {
          ConnectivityManager cm = ( ConnectivityManager ) this.getSystemService( Context.CONNECTIVITY_SERVICE );
          NetworkInfo ni = cm.getActiveNetworkInfo();
          if(ni == null)
                  return false;
          return ni.isConnected();
  }
     }
