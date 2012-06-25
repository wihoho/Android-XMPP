package app.tabsample;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a video stream
 *
 */
public class VideoStream {
	
	protected String mUrl;
	
	/**
	 * Construct a video stream from one of the strings obtained 
	 * 	from the "url_encoded_fmt_stream_map" parameter if the video_info 
	 * @param pStreamStr - one of the strings from "url_encoded_fmt_stream_map"
	 */
	public VideoStream(String pStreamStr){
		String[] lArgs=pStreamStr.split("&");
		Map<String,String> lArgMap = new HashMap<String, String>();
		for(int i=0; i<lArgs.length; i++){
			String[] lArgValStrArr = lArgs[i].split("=");
			if(lArgValStrArr != null){
				if(lArgValStrArr.length >= 2){
					lArgMap.put(lArgValStrArr[0], lArgValStrArr[1]);
				}
			}
		}
		mUrl = lArgMap.get("url");
	}
	public String getUrl(){
		return mUrl;
	}
}