package it.android.rssclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetTimer extends Activity{
    
	SharedPreferences timer;
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.settimer);
    	timer = getSharedPreferences("tim", Context.MODE_PRIVATE);
    	Button button = (Button) findViewById(R.id.timid);
    	button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click
            	 int tim=0;
            	 tim = ((TextView) findViewById(R.id.editText1)).getInputType();
            	 if (tim>0){
            		 tim=tim*60000;
            		 SharedPreferences.Editor editor = timer.edit();
 		             editor.clear();
 		             editor.putInt("TSlice", tim);
		    		 editor.commit();
		    	 	 Intent sIntent = new Intent(getBaseContext(),MySer.class);
		    		 stopService(sIntent);
		    		 startService(sIntent);
            		 Toast.makeText(getBaseContext(), "New value of timer was saved", Toast.LENGTH_LONG).show();
            		 finish();
            		 }
            	 else{
            		 Toast.makeText(getBaseContext(), "Error: the value of timer is <= of 0", Toast.LENGTH_LONG).show();
            	 
            	 }	}		
         });
    	
	
	}
}
