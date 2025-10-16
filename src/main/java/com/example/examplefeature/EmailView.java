// java
package com.example.examplefeature;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("email")
@PageTitle("Enviar Email")
public class EmailView extends VerticalLayout {

    private final MailService mailService;

    public EmailView(MailService mailService) {
        this.mailService = mailService;
        createUI();
    }

    private void createUI() {
        TextField to = new TextField("Para");
        TextField subject = new TextField("Assunto");
        TextArea message = new TextArea("Mensagem");
        to.setRequired(true);
        subject.setRequired(true);
        message.setWidthFull();

        Button send = new Button("Enviar");
        send.addClickListener(e -> {
            try {
                mailService.sendSimpleEmail(to.getValue(), subject.getValue(), message.getValue());
                Notification.show("Email enviado", 3000, Notification.Position.TOP_CENTER);
                to.clear(); subject.clear(); message.clear();
            } catch (Exception ex) {
                Notification.show("Erro ao enviar: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        FormLayout form = new FormLayout();
        form.add(to, subject, message);
        add(form, send);
        setPadding(true);
        setSizeFull();
    }
}
