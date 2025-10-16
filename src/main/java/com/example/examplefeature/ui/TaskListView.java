package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate())
                        .map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate()))
                .setHeader("Creation Date");
        taskGrid.setSizeFull();

        // Botão para gerar QR Code
        Button qrButton = new Button("Gerar QR Code", event -> {
            Task selectedTask = taskGrid.asSingleSelect().getValue();
            if (selectedTask == null) {
                Notification.show("Seleciona uma tarefa primeiro.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Monta o texto do QR code com descrição, data e criação
            String descriptionText = Optional.ofNullable(selectedTask.getDescription())
                    .filter(desc -> !desc.isBlank())
                    .orElse("Tarefa sem descrição");

            String dueDateText = Optional.ofNullable(selectedTask.getDueDate())
                    .map(date -> date.format(dateFormatter))
                    .orElse("Sem data");

            String creationDateText = dateTimeFormatter.format(selectedTask.getCreationDate());

            String text = descriptionText + "\n" +
                    "Prazo Limite: " + dueDateText + "\n" +
                    "Criada em: " + creationDateText;

            try {
                showQRCode(text);
            } catch (Exception e) {
                Notification.show("Erro ao gerar o QR code.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        qrButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn, qrButton)));
        add(taskGrid);
    }

    private void createTask() {
        try {
            taskService.createTask(description.getValue(), dueDate.getValue());
            taskGrid.getDataProvider().refreshAll();
            description.clear();
            dueDate.clear();
            Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Erro ao criar tarefa.", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showQRCode(String text) throws WriterException, IOException {
        // Gera o QR Code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        String base64Image = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
        Image qrImage = new Image("data:image/png;base64," + base64Image, "QR Code");
        qrImage.setWidth("250px");
        qrImage.setHeight("250px");

        // Cria layout dentro da notificação
        VerticalLayout layout = new VerticalLayout(qrImage);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(true);

        Notification notification = new Notification(layout);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(0); // Fica aberto até fechar manualmente
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);

        // Botão para fechar
        Button closeBtn = new Button("X", event -> notification.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        layout.add(closeBtn);
        layout.setAlignSelf(FlexComponent.Alignment.END, closeBtn);

        notification.open();
    }
}
