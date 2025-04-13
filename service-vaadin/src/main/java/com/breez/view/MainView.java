package com.breez.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/")
public class MainView extends VerticalLayout {

	public MainView() {
		Button buttonSearch = new Button("Search", event -> {
			UI.getCurrent().navigate("/search");
		});
		add(new H1("Main View"), buttonSearch);
	}

}
