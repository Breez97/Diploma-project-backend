package com.breez.util;

import com.vaadin.flow.server.VaadinSession;

import java.util.UUID;

public class SessionUtils {

	private static final String SESSION_ID_ATTRIBUTE = "sessionId";

	public static String getOrCreateSessionId() {
		VaadinSession session = VaadinSession.getCurrent();
		String sessionId = session.getAttribute(SESSION_ID_ATTRIBUTE).toString();

		if (sessionId == null) {
			sessionId = UUID.randomUUID().toString();
			session.setAttribute(SESSION_ID_ATTRIBUTE, sessionId);
		}
		return sessionId;
	}

}
