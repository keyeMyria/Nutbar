package com.davespanton.nutbar.service;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import com.davespanton.nutbar.R;
import com.davespanton.nutbar.alarms.LocationAlarm;
import com.davespanton.nutbar.alarms.SMSSendingAlarm;
import com.davespanton.nutbar.alarms.StubLocationAlarm;
import com.davespanton.nutbar.alarms.StubSMSSendingAlarm;
import com.davespanton.nutbar.alarms.factory.SMSSendingAlarmFactory;
import com.davespanton.nutbar.injected.InjectedTestRunner;
import com.google.inject.Inject;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

@RunWith(InjectedTestRunner.class)
public class AlarmServiceTest {

	private static final int EXPECTED_TRIP_COUNT = 1;

    @Inject
	private AlarmService alarmService;
	
	private SMSSendingAlarm smsAlarm;

    @Inject
    private SMSSendingAlarmFactory smsAlarmFactory;

    @Inject
    private LocationAlarm locationAlarm;
	
	@Before
	public void setup() {
        alarmService.onCreate();
        smsAlarm = smsAlarmFactory.create(alarmService);
	}
	
	@After
	public void tearDown() {
		alarmService = null;
		smsAlarm = null;
	}

    @Test
    public void shouldTripSmsSendingAlarmOnCorrectStartCommand() {
		startServiceWithAlarmTripAction();
        assertEquals(EXPECTED_TRIP_COUNT, ((StubSMSSendingAlarm) smsAlarm).getTripCount());
	}

    private void startServiceWithAlarmTripAction() {
        Intent i = new Intent(Robolectric.application.getString(R.string.alarm_service_trip));
		alarmService.onStartCommand(i, 0, 0);
    }

    @Test
    public void shouldResetAlarmsOnCorrectStartCommand() {
        startServiceWithAlarmTripAction();

        Intent i = new Intent(Robolectric.application.getString(R.string.alarm_service_reset));
        alarmService.onStartCommand(i, 0, 0);
        assertFalse(locationAlarm.isListening());
        assertFalse(smsAlarm.isListening());
    }

    @Test
    public void shouldResetLocationAlarmOnDestroy() {
        startServiceWithAlarmTripAction();

        alarmService.onDestroy();
        assertFalse(locationAlarm.isListening());
    }

    @Test
    public void shouldAddListenerToLocationAlarmOnCreate() {
        alarmService.onCreate();
        assertNotNull(((StubLocationAlarm) locationAlarm).getOnLocationChangeLocationListener());
    }

    @Test
    public void shouldTripLocationsAlarmOnCorrectStartCommand() {
        startServiceWithAlarmTripAction();
        assertEquals(EXPECTED_TRIP_COUNT, ((StubLocationAlarm) locationAlarm).getTripCount());
    }

    @Test
    public void shouldBroadcastLocationUpdatesInExpectedFormat() {
        String provider = LocationManager.GPS_PROVIDER;
        Double lat = 50d;
        Double lng = 0d;
        changeLocation(provider, lat, lng);

        Intent i = Robolectric.shadowOf(alarmService).peekNextStartedService();

        String[] xmppBodyElements = parseXmppMessageFromIntentWithExtra(i);

        assertEquals(Double.toString(lat), xmppBodyElements[0]);
        assertEquals(Double.toString(lng), xmppBodyElements[1]);
        assertEquals(provider, xmppBodyElements[2]);
    }

    private void changeLocation(String provider, Double lat, Double lng) {
        Location loc = new Location(provider);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        locationAlarm.onLocationChanged(loc);
    }

    private String[] parseXmppMessageFromIntentWithExtra(Intent broadcast) {
        String xmppMessage = broadcast.getExtras().getString(alarmService.getString(R.string.send_xmpp_extra));
        String xmppBody = xmppMessage.split(" ")[1];
        return xmppBody.split(",");
    }
}
