package app.tabsample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class YouTubeUtility {
	
	/**
	 * Retrieve the latest video in the specified playlist.
	 * @param pPlaylistId the id of the playlist for which to retrieve the latest video id
	 * @return the video id of the latest video, null if something goes wrong
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws FactoryConfigurationError
	 */
	public static String queryLatestPlaylistVideo(PlaylistId pPlaylistId)
			throws IOException, ClientProtocolException,
			FactoryConfigurationError {

		String lVideoId = null;

		HttpClient lClient = new DefaultHttpClient();
		
		HttpGet lGetMethod = new HttpGet(OpenYouTubePlayerActivity.YOUTUBE_PLAYLIST_ATOM_FEED_URL + 
										 pPlaylistId.getId()+"?v=2&max-results=50&alt=json");
		
		HttpResponse lResp = null;

		lResp = lClient.execute(lGetMethod);
		
		ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
		String lInfoStr = null;
		JSONObject lYouTubeResponse = null;
		
		try {
			
			lResp.getEntity().writeTo(lBOS);
			lInfoStr = lBOS.toString("UTF-8");
			lYouTubeResponse = new JSONObject(lInfoStr);
			
			 JSONArray lEntryArr = lYouTubeResponse.getJSONObject("feed").getJSONArray("entry");
			 JSONArray lLinkArr = lEntryArr.getJSONObject(lEntryArr.length()-1).getJSONArray("link");
			for(int i=0; i<lLinkArr.length(); i++){
				JSONObject lLinkObj = lLinkArr.getJSONObject(i);;
				String lRelVal = lLinkObj.optString("rel", null);
				if( lRelVal != null && lRelVal.equals("alternate")){
					
					String lUriStr = lLinkObj.optString("href", null);
					Uri lVideoUri = Uri.parse(lUriStr);
					lVideoId = lVideoUri.getQueryParameter("v");
					break;
				}
			}
		} catch (IllegalStateException e) {
			Log.i(YouTubeUtility.class.getSimpleName(), "Error retrieving content from YouTube", e);
		} catch (IOException e) {
			Log.i(YouTubeUtility.class.getSimpleName(), "Error retrieving content from YouTube", e);
		} catch(JSONException e){
			Log.i(YouTubeUtility.class.getSimpleName(), "Error retrieving content from YouTube", e);
		}

		return lVideoId;
	}

	
	/**
	 * Calculate the YouTube URL to load the video.  Includes retrieving a token that YouTube
	 * requires to play the video.
	 * 
	 * @param pYouTubeFmtQuality quality of the video.  17=low, 18=high
	 * @param bFallback whether to fallback to lower quality in case the supplied quality is not available
	 * @param pYouTubeVideoId the id of the video
	 * @return the url string that will retrieve the video
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws UnsupportedEncodingException
	 */
	public static String calculateYouTubeUrl(String pYouTubeFmtQuality, boolean pFallback,
			String pYouTubeVideoId) throws IOException,
			ClientProtocolException, UnsupportedEncodingException {

		String lUriStr = null;
		HttpClient lClient = new DefaultHttpClient();
		
		HttpGet lGetMethod = new HttpGet(OpenYouTubePlayerActivity.YOUTUBE_VIDEO_INFORMATION_URL + 
										 pYouTubeVideoId);
		
		HttpResponse lResp = null;

		lResp = lClient.execute(lGetMethod);
			
		ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
		String lInfoStr = null;
			
		lResp.getEntity().writeTo(lBOS);
		lInfoStr = new String(lBOS.toString("UTF-8"));
		
		String[] lArgs=lInfoStr.split("&");
		Map<String,String> lArgMap = new HashMap<String, String>();
		for(int i=0; i<lArgs.length; i++){
			String[] lArgValStrArr = lArgs[i].split("=");
			if(lArgValStrArr != null){
				if(lArgValStrArr.length >= 2){
					lArgMap.put(lArgValStrArr[0], URLDecoder.decode(lArgValStrArr[1]));
				}
			}
		}
		
		//Find out the URI string from the parameters
		
		//Populate the list of formats for the video
		String lFmtList = URLDecoder.decode(lArgMap.get("fmt_list"));
		ArrayList<Format> lFormats = new ArrayList<Format>();
		if(null != lFmtList){
			String lFormatStrs[] = lFmtList.split(",");
			
			for(String lFormatStr : lFormatStrs){
				Format lFormat = new Format(lFormatStr);
				lFormats.add(lFormat);
			}
		}
		
		//Populate the list of streams for the video
		String lStreamList = lArgMap.get("url_encoded_fmt_stream_map");
		if(null != lStreamList){
			String lStreamStrs[] = lStreamList.split(",");
			ArrayList<VideoStream> lStreams = new ArrayList<VideoStream>();
			for(String lStreamStr : lStreamStrs){
				Log.i("URL", lStreamStr);
				VideoStream lStream = new VideoStream(lStreamStr);
				lStreams.add(lStream);
			}	
			
			//Search for the given format in the list of video formats
			// if it is there, select the corresponding stream
			// otherwise if fallback is requested, check for next lower format
			int lFormatId = Integer.parseInt(pYouTubeFmtQuality);
			
			Format lSearchFormat = new Format(lFormatId);
			while(!lFormats.contains(lSearchFormat) && pFallback ){
				int lOldId = lSearchFormat.getId();
				int lNewId = getSupportedFallbackId(lOldId);
				
				if(lOldId == lNewId){
					break;
				}
				lSearchFormat = new Format(lNewId);
			}
			
			int lIndex = lFormats.indexOf(lSearchFormat);
			Log.i("index", String.valueOf(lIndex));
			if(lIndex >= 0){
				VideoStream lSearchStream = lStreams.get(lIndex);
				lUriStr = lSearchStream.getUrl();
				Log.i("lUriStr", lUriStr);
			}
			
		}		
		//Return the URI string. It may be null if the format (or a fallback format if enabled)
		// is not found in the list of formats for the video
		//return "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/03000201004F3D14416BF601D5830CAEEB4145-B33B-495E-D4CC-74AF1701A872?K=a589efebad1db9212410e7b2,k2:116b7712c80837037";
		return lUriStr;
	}

	public static boolean hasVideoBeenViewed(Context pCtxt, String pVideoId) {
		SharedPreferences lPrefs = PreferenceManager.getDefaultSharedPreferences(pCtxt);

		String lViewedVideoIds = lPrefs.getString("com.keyes.screebl.lastViewedVideoIds", null);
		
		if(lViewedVideoIds == null){
			return false;
		}
		
		String[] lSplitIds =lViewedVideoIds.split(";");  
		if(lSplitIds == null || lSplitIds.length == 0){
			return false;
		}
		
		for(int i=0; i<lSplitIds.length; i++){
			if(lSplitIds[i] != null && lSplitIds[i].equals(pVideoId)){
				return true;
			}
		}
		
		return false;

	}
	
	public static void markVideoAsViewed(Context pCtxt, String pVideoId){
		
		SharedPreferences lPrefs = PreferenceManager.getDefaultSharedPreferences(pCtxt);

		if(pVideoId == null){
			return;
		}
		
		String lViewedVideoIds = lPrefs.getString("com.keyes.screebl.lastViewedVideoIds", null);

		if(lViewedVideoIds == null){
			lViewedVideoIds = "";
		}
		
		String[] lSplitIds =lViewedVideoIds.split(";");  
		if(lSplitIds == null){
			lSplitIds = new String[]{};
		}
		
		// make a hash table of the ids to deal with duplicates
		Map<String, String> lMap = new HashMap<String, String>();
		for(int i=0; i<lSplitIds.length; i++){
			lMap.put(lSplitIds[i], lSplitIds[i]);
		}
		
		// recreate the viewed list
		String lNewIdList = "";
		Set<String> lKeys = lMap.keySet();
		Iterator<String> lIter = lKeys.iterator();
		while(lIter.hasNext()){
			String lId = lIter.next();
			if( ! lId.trim().equals("")){
				lNewIdList += lId + ";";
			}
		}
		
		// add the new video id
		lNewIdList += pVideoId + ";";
		
		Editor lPrefEdit = lPrefs.edit();
		lPrefEdit.putString("com.keyes.screebl.lastViewedVideoIds", lNewIdList);
		lPrefEdit.commit();
		
	}
	
	public static int getSupportedFallbackId(int pOldId){
		final int lSupportedFormatIds[] = {13,  //3GPP (MPEG-4 encoded) Low quality 
										  17,  //3GPP (MPEG-4 encoded) Medium quality 
										  18,  //MP4  (H.264 encoded) Normal quality
										  22,  //MP4  (H.264 encoded) High quality
										  37   //MP4  (H.264 encoded) High quality
										  };
		int lFallbackId = pOldId;
		for(int i = lSupportedFormatIds.length - 1; i >= 0; i--){
			if(pOldId == lSupportedFormatIds[i] && i > 0){
				lFallbackId = lSupportedFormatIds[i-1];
			}			
		}
		return lFallbackId;
	}
}
