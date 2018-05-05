package com.api.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

@Controller
public class GoogleCalController {

	private final static Log logger = LogFactory.getLog(GoogleCalController.class);
	private static final String APPLICATION_NAME = "";
	private static HttpTransport httpTransport;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static com.google.api.services.calendar.Calendar client;

	GoogleClientSecrets clientSecrets;
	GoogleAuthorizationCodeFlow flow;
	Credential credential;

	@Value("${google.client.client-id}")
	private String clientId;
	@Value("${google.client.client-secret}")
	private String clientSecret;
	@Value("${google.client.redirectUri}")
	private String redirectURI;

	private Set<Event> events = new HashSet<>();

	final DateTime date1 = new DateTime("2018-05-01T16:30:00.000+05:30");
	final DateTime date2 = new DateTime(new Date());

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	
	@RequestMapping(value = "/login/google", method = RequestMethod.POST)
	public ResponseEntity<String> updateEvent() throws IOException, GeneralSecurityException {
		
		Event event = new Event()
			    .setSummary("BEZZAOUI WALIDDDDDD 2018")
			    .setLocation("1000 RUE NOTRE DAME")
			    .setDescription("BLA BLA BLA BLA BLA");
		DateTime startDateTime = new DateTime("2018-05-06T09:00:00-07:00");
		EventDateTime start = new EventDateTime()
		    .setDateTime(startDateTime)
		    .setTimeZone("America/Los_Angeles");
		event.setStart(start);

		DateTime endDateTime = new DateTime("2018-05-07T17:00:00-07:00");
		EventDateTime end = new EventDateTime()
		    .setDateTime(endDateTime)
		    .setTimeZone("America/Los_Angeles");
		event.setEnd(end);

		String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
		event.setRecurrence(Arrays.asList(recurrence));

		EventAttendee[] attendees = new EventAttendee[] {
		    new EventAttendee().setEmail("walidrift@gmail.com"),
		    new EventAttendee().setEmail("walid.bezzaoui@hotmail.fr"),
		};
		event.setAttendees(Arrays.asList(attendees));

		EventReminder[] reminderOverrides = new EventReminder[] {
		    new EventReminder().setMethod("email").setMinutes(24 * 60),
		    new EventReminder().setMethod("popup").setMinutes(10),
		};
		Event.Reminders reminders = new Event.Reminders()
		    .setUseDefault(false)
		    .setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);
		
		String calendarId = "primary";
		TokenResponse response = new TokenResponse().setAccessToken("ya29.GluyBW-HAeBFYmP22i3Rhywun-4_LpKR-mQMet-fxHfj_vfst1eUGXhzXyIPdxWfg2uf6Ynm2ssy1XlbrWYRSb57wVgBHP6OM_zrx7JV8FN8n5pEAVfyORVmAOV3").setExpiresInSeconds((long) 3600).setRefreshToken("1/N8MI2-yqRYRs5HoNq67E7VySDm7u_xl-rAfa7HtOsI-9wiYW47FTt8XSHHg8o0De").setTokenType("Bearer");
		Details web = new Details();
		web.setClientId(clientId);
		web.setClientSecret(clientSecret);
		clientSecrets = new GoogleClientSecrets().setWeb(web);
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
				Collections.singleton(CalendarScopes.CALENDAR)).build();
		credential = flow.createAndStoreCredential(response, "userID");
		client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
		event = client.events().insert(calendarId, event).execute();
		System.out.printf("Event created: %s\n", event.getHtmlLink());
		return new ResponseEntity<>(event.getHtmlLink(), HttpStatus.OK);
		
	}
	@RequestMapping(value = "/login/google", method = RequestMethod.GET)
	public ResponseEntity<String> oauth2Callback() {
		com.google.api.services.calendar.model.Events eventList;
		String message;
		try {
			TokenResponse response = new TokenResponse().setAccessToken("ya29.GluyBW-HAeBFYmP22i3Rhywun-4_LpKR-mQMet-fxHfj_vfst1eUGXhzXyIPdxWfg2uf6Ynm2ssy1XlbrWYRSb57wVgBHP6OM_zrx7JV8FN8n5pEAVfyORVmAOV3").setExpiresInSeconds((long) 3600).setRefreshToken("1/N8MI2-yqRYRs5HoNq67E7VySDm7u_xl-rAfa7HtOsI-9wiYW47FTt8XSHHg8o0De").setTokenType("Bearer");
			Details web = new Details();
			web.setClientId(clientId);
			web.setClientSecret(clientSecret);
			clientSecrets = new GoogleClientSecrets().setWeb(web);
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
					Collections.singleton(CalendarScopes.CALENDAR)).build();
			credential = flow.createAndStoreCredential(response, "userID");
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
			Events events = client.events();
			eventList = events.list("primary").setTimeMin(date1).setTimeMax(date2).execute();
			message = eventList.getItems().toString();
			System.out.println("My:" + eventList.getItems());
		} catch (Exception e) {
			logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.");
			message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.";
		}

		System.out.println("cal message:" + message);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	public Set<Event> getEvents() throws IOException {
		return this.events;
	}

	private String authorize() throws Exception {
		AuthorizationCodeRequestUrl authorizationUrl;
		if (flow == null) {
			Details web = new Details();
	
			web.setClientId(clientId);
			web.setClientSecret(clientSecret);
			clientSecrets = new GoogleClientSecrets().setWeb(web);
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
					Collections.singleton(CalendarScopes.CALENDAR)).build();
		}
		authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
		System.out.println("cal authorizationUrl->" + authorizationUrl);
		return authorizationUrl.build();
	}
}