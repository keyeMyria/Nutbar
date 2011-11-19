package com.davespanton.nutbar.alarms;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.davespanton.nutbar.alarms.LocationAlarm;
import com.google.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Application;
import android.location.LocationManager;

import com.davespanton.nutbar.injected.InjectedTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowLocationManager;

@RunWith(InjectedTestRunner.class)
public class LocationAlarmTest {

	@Inject
    private LocationAlarm sut;

	private ShadowLocationManager shadow;
	
	@Before
	public void setup() {
		shadow = (ShadowLocationManager) shadowOf((LocationManager) Robolectric.application.getSystemService(Application.LOCATION_SERVICE));
	}
	
	@After
	public void tearDown() {
		sut = null;
		shadow = null;
	}
	
	@Test
	public void shouldAddListenerOnStartListening() {
		sut.startListening();
		assertTrue(shadow.getRequestLocationUpdateListeners().contains(sut));
	}
	
	@Test
	public void shouldRemoveListenerOnStopListening() {
		sut.startListening();
		assertTrue(shadow.getRequestLocationUpdateListeners().contains(sut));
		sut.stopListening();
		assertFalse(shadow.getRequestLocationUpdateListeners().contains(sut));
	}
	
	@Test
	public void shouldReturnIsListeningWhenListening() {
		sut.startListening();
		assertTrue(sut.isListening());
	}
	
	@Test
	public void shouldReturnIsNotListeningWhenNotListening() {
		sut.startListening();
		sut.stopListening();
		assertFalse(sut.isListening());
	}
}