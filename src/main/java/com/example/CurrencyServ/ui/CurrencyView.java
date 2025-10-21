package com.example.CurrencyServ.ui;

import com.example.CurrencyServ.ForexService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;



@Route("forex")
@PageTitle("Intercâmbio de Moedas")
@Menu(order = 1, icon = "vaadin:exchange", title = "Intercâmbio de Moedas")
public class CurrencyView extends Main {
    public CurrencyView(@Autowired ForexService forexService) {
        TextField from = new TextField("De (ex: USD)");
        TextField to = new TextField("Para (ex: EUR)");
        NumberField amount = new NumberField("Valor");
        Button convert = new Button("Converter", e -> {
            try {
                double result = forexService.convert(from.getValue(), to.getValue(), amount.getValue());
                Notification.show("Resultado: " + result + " " + to.getValue());
            } catch (Exception ex) {
                Notification.show("Erro: " + ex.getMessage());
            }
        });
        add(from, to, amount, convert);
}
}
