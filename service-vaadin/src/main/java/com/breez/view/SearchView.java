package com.breez.view;

import com.breez.service.HttpClientService;
import com.breez.util.SessionUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Route("/search")
@PageTitle("Search")
public class SearchView extends VerticalLayout {

	private static final Logger logger = LoggerFactory.getLogger(SearchView.class);

	private final HttpClientService httpClientService;
	private final HttpClient httpClient;

	@Autowired
	public SearchView(HttpClientService httpClientService, HttpClient httpClient) {
		this.httpClientService = httpClientService;
		this.httpClient = httpClient;
		viewConfiguration();
	}

	private void viewConfiguration() {
		addClassName("search-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		TextField wildberriesInputField = new TextField();
		Button buttonSearchWildberries = new Button("Search WB", event -> {
			String inputText = wildberriesInputField.getValue();
			String sessionId = SessionUtils.getOrCreateSessionId();
			if (StringUtils.isNotBlank(inputText)) {
				try {
					HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create("http://localhost:8082/api/v1/wildberries?title=" + inputText))
							.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
							.header("Session-Id", sessionId)
							.GET()
							.build();
					HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
					String responseBody = response.body();
					logger.info(responseBody);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

		add(new H1("Search View"), new HorizontalLayout(wildberriesInputField, buttonSearchWildberries));
	}

}
