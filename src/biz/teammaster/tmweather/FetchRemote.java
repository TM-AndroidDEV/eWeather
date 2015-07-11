/* This class responsible for fetching the weather data from the OpenWeatherMap API */

/* HttpURLConnection class to make remote request
 * BufferReader to read the API's response
 * JSON data field col value if the request was success
 *  */

package biz.teammaster.tmweather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import biz.teammaster.eweather.R;

import android.content.Context;
import android.util.Log;

public class FetchRemote {
	private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

	public static JSONObject getJSON(Context context,
			String city) {
		try {
			URL url = new URL(String.format(OPEN_WEATHER_MAP_API,
					city));
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.addRequestProperty("x-api-key",
					context.getString(R.string.open_weather_maps_app_id));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuffer json = new StringBuffer(1024);
			String tmp = "";
			while ((tmp = reader.readLine()) != null)
				json.append(tmp).append("\n");
			reader.close();

			JSONObject data = new JSONObject(json.toString());

			// This value will be 404 if the request was not successful
			if (data.getInt("cod") != 200) {
				return null;
			}

			return data;
		} catch (Exception e) {
			return null;
		}
	}
	public static JSONObject getJSONString(Context context,String cityName){
		try {
			return new JSONObject(requestForString(context, cityName));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	private static String requestForString(Context context,String cityName){
		HttpURLConnection connection=null;
		String response="";
		try {
			
			
			//initialize parameter if not null
			
			
			//load url
			URL url=new URL(String.format(OPEN_WEATHER_MAP_API,cityName));
			//HttpURLConnection is an abstract therefore we need to get from URL class
			connection=(HttpURLConnection)url.openConnection();
			
			
			
			
			//set Header Property such as parameters, content-type, content-encoding...
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	       
	        connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
	        
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setUseCaches(false);
	        
	        connection.setReadTimeout(36000);
	        connection.setConnectTimeout(36000);
	        
	        //get response stream
	        InputStream is=connection.getInputStream();
	        BufferedReader bf=new BufferedReader(new InputStreamReader(is, "utf-8"));
	        StringBuilder sb=new StringBuilder();
	        String line=null;
	        while((line=bf.readLine())!=null){
	        	sb.append(line);
	        }
	        response=sb.toString();
	        Log.e("Connection Response-->","<--"+response+"-->");
	        bf.close();
	        is.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("error",e.getMessage()+"0000");
		}finally{
			connection.disconnect();
			
		}
		return response;
		
	}
}
