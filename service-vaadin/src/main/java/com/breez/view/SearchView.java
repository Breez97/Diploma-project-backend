package com.breez.view;

import com.breez.model.Response;
import com.breez.service.SearchService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route("/search")
@PageTitle("Search")
public class SearchView extends VerticalLayout {

	@Autowired
	private SearchService searchService;

	public SearchView() {
		addClassName("search-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		TextField inputField = new TextField();
		Button buttonSearch = new Button("Search", event -> {
			String inputText = inputField.getValue();
			if (StringUtils.isNotBlank(inputText)) {
				Response response = searchService.search(inputText);
				log.info(response.toString());
			}
		});

		add(new H1("Search View"), new HorizontalLayout(inputField, buttonSearch));
	}

}
