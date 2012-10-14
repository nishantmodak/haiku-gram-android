package com.kryptonlabs.haikugram;

import gueei.binding.Command;
import gueei.binding.DependentObservable;
import gueei.binding.app.BindingActivity;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.gueei.android.binding.tutorial.infiniteScroll.R;

public class InfiniteScrollActivityWithAsyncLoad extends BindingActivity {
	
		
	public IntegerObservable ItemsPerLoad = 
			new IntegerObservable(5);
	
	public ArrayListObservable<String> LocationList = 
			new ArrayListObservable<String>(String.class){
				@Override
				public void onLoad(int position) {
					super.onLoad(position);
					if (position>=Loaded.get()-1){
						loadMore();
					}
				}
	};
	
	public IntegerObservable Loaded =
			new IntegerObservable(0);
	
	public DependentObservable<Boolean> HasMore =
			new DependentObservable<Boolean>(Boolean.class){
				@Override
				public Boolean calculateValue(Object... args) throws Exception {
					return Loaded.get() < DATA.length;
				}
	};
	
	public BooleanObservable Loading = 
			new BooleanObservable(false);
	
	public Command LoadMoreItems = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			if (!Loading.get()) loadMore();
		}
	};
	
	private void loadMore(){
		if (Loading.get()) return;
		Loading.set(true);
		(new LoadMoreTask()).execute(ItemsPerLoad.get());
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMore();
        this.setAndBindRootView(R.layout.main, this);
    }
    
    private class LoadMoreTask extends AsyncTask<Integer, Integer, ArrayList<String>>{
		@Override
		protected ArrayList<String> doInBackground(Integer... arg0) {
			ArrayList<String> add = new ArrayList<String>();
			int loaded = Loaded.get();
			for(int i=0; i<arg0[0]; i++){
				if (DATA.length <= loaded) break;
				add.add(DATA[loaded]);
				loaded++;
			}
			// Delay a bit to simulate the loading
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return add;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			Loaded.set(Loaded.get() + result.size());
			LocationList.addAll(result);
			Loading.set(false);
		}

		@Override
		protected void onPreExecute() {
			Loading.set(true);
		}
    }
    
    private static String[] DATA = setHaiku();

	private static String[] setHaiku() {
		Log.d("1","called Haikufn");
		HttpURLConnection connection =null;
		Haiku[] resultObject = null;
		try {
			URL url = new URL("http://haikugram.herokuapp.com/haikus.json");
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
		Log.d("2","got result for Haikufn");
		Log.d("3","got result for Haikufn" + resultObject[1].body);
		String haikuBody[] = new String[resultObject.length] ;
		for(int i=0;i<resultObject.length;i++){
			
			haikuBody[i]=resultObject[i].body ;
		}
		Log.d("3","Returning haikuBody" + haikuBody[1]);
			return haikuBody;
		
			
	}
	private static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
			
	
	
			
}
	