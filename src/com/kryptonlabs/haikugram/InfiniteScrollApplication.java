package com.kryptonlabs.haikugram;

import gueei.binding.Binder;
import android.app.Application;

public class InfiniteScrollApplication extends Application {

	
	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
		
		}

}

