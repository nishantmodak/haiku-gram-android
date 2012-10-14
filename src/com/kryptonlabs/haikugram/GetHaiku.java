package com.kryptonlabs.haikugram;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;

public class GetHaiku extends AsyncTask<String, Void, Haiku[]>{

	private Haiku[] getResponse(String get_url)
	{
		HttpURLConnection connection =null;
		Haiku[] resultObject = null;
		try {
			URL url = new URL(get_url);
			disableConnectionReuseIfNecessary();
			connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			connection.setRequestProperty("Accept-Encoding","gzip");
			InputStreamReader iSReader = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"));
			BufferedReader reader = new BufferedReader(iSReader);
			String line;
			String result="";
			while ((line = reader.readLine()) != null) {
				result=result+line;
			}
			Gson gson = new Gson();
			resultObject= gson.fromJson(result, Haiku[].class);
			if (resultObject!=null){
				for (int index = 0; index < resultObject.length; index++)
					resultObject[index].body = resultObject[index].body;
			}
			reader.close();
			connection.disconnect();
		} catch (IOException e1) {
			e1.printStackTrace();
			if(connection!=null)
				connection.disconnect();
		}
		return resultObject;
	}

	private void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	@Override
	protected Haiku[] doInBackground(String... urls) {
		return getResponse("http://afternoon-shelf-8961.herokuapp.com/haikus.json");
	}
}
