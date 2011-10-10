package com.davespanton.nutbar;

import roboguice.config.AbstractAndroidModule;
import roboguice.util.Ln;
import android.util.Log;

import com.davespanton.nutbar.service.binder.AccelerometerBinderBuilder;
import com.davespanton.nutbar.service.binder.GPSBinderBuilder;
import com.davespanton.nutbar.service.binder.StubAccelerometerBinderBuilder;
import com.davespanton.nutbar.service.binder.StubGPSBinderBuilder;
import com.davespanton.nutbar.service.connection.ListenerServiceConnection;
import com.davespanton.nutbar.service.connection.StubListenerServiceConnection;
import com.davespanton.nutbar.service.sensor.SensorChangeListener;
import com.davespanton.nutbar.service.sensor.StubSensorChangeMonitor;

public class NutbarTestModule extends AbstractAndroidModule {

	@Override 
	protected void configure() {
		bind(Ln.BaseConfig.class).toInstance(new NutbarLoggerConfig());
		bind(ListenerServiceConnection.class).toInstance(new StubListenerServiceConnection());
		bind(SensorChangeListener.class).toInstance(new StubSensorChangeMonitor());
		bind(GPSBinderBuilder.class).to(StubGPSBinderBuilder.class);
		bind(AccelerometerBinderBuilder.class).to(StubAccelerometerBinderBuilder.class);
	}

    static class NutbarLoggerConfig extends Ln.BaseConfig {
        public NutbarLoggerConfig() {
            super();
            this.packageName = "nutbar";
            this.minimumLogLevel = Log.VERBOSE;
            this.scope = "NUT";
        }
    }

}
