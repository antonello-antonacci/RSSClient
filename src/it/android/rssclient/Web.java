package it.android.rssclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web extends Activity {
	
		String link=null;
		WebView webView=null;
		
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	      setContentView(R.layout.web);
	      Intent i=getIntent();
	      link=i.getStringExtra("link");
	      webView=(WebView)findViewById(R.id.webView);
	      if(savedInstanceState!=null){
	    	webView.restoreState(savedInstanceState);  
	      }
	      else{
	      final ProgressDialog progressDialog = new ProgressDialog(Web.this);
	      progressDialog.setMessage("Loading ...");
	      progressDialog.setCancelable(false);
	      progressDialog.show();
	      webView.setWebViewClient(new WebViewClient() {
	    	  @Override
	    	  public void onPageFinished(WebView view, String url) {
	    	  super.onPageFinished(view, url);
	    	  progressDialog.hide();
	    	  }
	    	  });
	      webView.loadUrl(link);  
	      WebSettings webSettings = webView.getSettings();
          webSettings.setJavaScriptEnabled(true);
          webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
          webSettings.setSupportZoom(true);
         
	  }
	  }
	  protected void onSaveInstanceState(Bundle savedInstanceState) {
		 webView.saveState(savedInstanceState);
		}

	public boolean onCreateOptionsMenu(Menu menu) {
     	super.onCreateOptionsMenu(menu);
        menu.add("Share"); 
     	return true;} 
	  
	      public boolean onOptionsItemSelected(MenuItem item) {
	      Intent i = new Intent(Intent.ACTION_SEND);
	      i.setType("text/plain");
	      i.putExtra(Intent.EXTRA_TEXT, link);
	      startActivity(Intent.createChooser(i, "Share"));
		return false;}
	      }
