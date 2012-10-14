package com.kryptonlabs.haikugram;

import gueei.binding.Command;
import gueei.binding.DependentObservable;
import gueei.binding.app.BindingActivity;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.view.View;

import com.gueei.android.binding.tutorial.infiniteScroll.R;

public class InfiniteScrollActivity extends BindingActivity {
	
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
		
		ArrayList<String> add = new ArrayList<String>();
		int loaded = Loaded.get();
		for(int i=0; i<ItemsPerLoad.get(); i++){
			if (DATA.length <= loaded) break;
			add.add(DATA[loaded]);
			loaded++;
		}
		
		Loaded.set(loaded);
		
		// Batch adding will results less notification to view => better performance
		LocationList.addAll(add);
		
		Loading.set(false);
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMore();
        this.setAndBindRootView(R.layout.main, this);
    }
    
    private static final String[] DATA = setHaiku();

	private static String[] setHaiku() {
		GetHaiku downloader = new GetHaiku();
		downloader.execute();
		Haiku[] response = null;
		String[] haikuBody = null;
		
			try {
				response = downloader.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(response!=null){
				
				for(int i=0;i<response.length;i++){
					haikuBody[i]= response[i].body;
				}
		
	}
			return haikuBody;
	}
}