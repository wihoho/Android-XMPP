package app.tabsample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Sample extends Activity {
	private ListView videosList;
	private ArrayAdapter<String> videosArray;
	
  @Override
  protected void onCreate(Bundle pSavedInstanceState) {
    super.onCreate(pSavedInstanceState);
    setContentView(R.layout.sample);
    videosList = (ListView) findViewById(R.id.in);
	videosArray = new ArrayAdapter<String>(this, R.layout.list);
	videosArray.setNotifyOnChange(true);
	
	videosArray.add("iUoREG3F6XY");
	videosArray.add("nGeKSiCQkPw");
	videosArray.add("GI6CfKcMhjY");
	videosArray.add("_JmA2ClUvUY");
	videosArray.add("QH2-TGUlwu4");
	videosArray.add("khCokQt--l4");
	videosArray.add("tLPZmPaHme0");
	videosArray.add("xG0wi1m-89o");
	videosArray.add("kfVsfOSbJY0");
	videosArray.add("kfVsfOSbJY0");
	
	videosList.setAdapter(videosArray);
	
	videosList.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
	      // When clicked, show a toast with the TextView text
	      Log.i("Select Video", videosArray.getItem(position));
	      String videoId = videosArray.getItem(position);
	      //...........Update the clicked video ID
//	      Temp app = (Temp)getApplicationContext();
//	      app.setID(videoId);
//	      Log.i("Record the Video ID", videoId);
	      //...........end of updating
	      Temp.setID(videoId);
	      Log.i("Record the Video ID", videoId);
	      
	        if(videoId == null || videoId.trim().equals("")){
	          return;
	        }
	        
	        Intent lVideoIntent = new Intent(null, Uri.parse("ytv://"+videoId.trim()), Sample.this, OpenYouTubePlayerActivity.class);
	        startActivity(lVideoIntent);
	       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.youtube.com/watch?v=cxLG2wtE7TM")));
	    }
	  });
    
  }
}
