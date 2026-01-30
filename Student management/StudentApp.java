package com.example.studentmanagement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main application class for the Student Management System.
 * Place this file in: src/main/java/com/example/student_management/StudentApp.java
 */
public class StudentApp extends Application {

    private final ObservableList<Student> studentList = FXCollections.observableArrayList(
            new Student("Abdullah Al Mamun", "abdd@gmail.com"),
            new Student("Abdur Rahim", "ar@gmail.com"),
            new Student("Rajon Islam", "raj12@gmail.com")
    );

    private TextField nameField;
    private TextField emailField;
    private TableView<Student> tableView;
    private StackPane rootContainer;
    private VBox formContainer;
    private VBox tableContainer;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Details System");

        formContainer = createFormView();
        tableContainer = createTableView();
        tableContainer.setVisible(false);

        rootContainer = new StackPane(formContainer, tableContainer);
        rootContainer.setPadding(new Insets(40));

        Scene scene = new Scene(rootContainer, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createFormView() {
        VBox vbox = new VBox(30);
        vbox.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Enter Student Details");
        titleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 42));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Times New Roman", 32));
        nameField = new TextField();
        nameField.setFont(Font.font("Times New Roman", 24));
        nameField.setPrefWidth(400);

        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Times New Roman", 32));
        emailField = new TextField();
        emailField.setFont(Font.font("Times New Roman", 24));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);

        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnInsert = new Button("Insert");
        Button btnView = new Button("View");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");

        String btnStyle = "-fx-font-family: 'Times New Roman'; -fx-font-size: 24px; " +
                "-fx-background-color: #efefef; -fx-border-color: #767676; " +
                "-fx-border-radius: 4px; -fx-padding: 5 25 5 25;";

        for (Button b : new Button[]{btnInsert, btnView, btnUpdate, btnDelete}) {
            b.setStyle(btnStyle);
            b.setCursor(javafx.scene.Cursor.HAND);
        }

        btnInsert.setOnAction(e -> handleInsert());
        btnView.setOnAction(e -> showView(true));
        btnUpdate.setOnAction(e -> handleUpdate());
        btnDelete.setOnAction(e -> handleDelete());

        buttonBox.getChildren().addAll(btnInsert, btnView, btnUpdate, btnDelete);
        vbox.getChildren().addAll(titleLabel, grid, buttonBox);
        return vbox;
    }

    private VBox createTableView() {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.TOP_LEFT);

        Label actionLabel = new Label("Action: View");
        actionLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 38));

        tableView = new TableView<>();
        tableView.setItems(studentList);
        tableView.setPrefWidth(600);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 20px;");

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 20px;");

        tableView.getColumns().add(nameCol);
        tableView.getColumns().add(emailCol);

        Button btnBack = new Button("Back to Form");
        btnBack.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 20px;");
        btnBack.setOnAction(e -> showView(false));

        vbox.getChildren().addAll(actionLabel, tableView, btnBack);
        return vbox;
    }

    private void handleInsert() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) return;
        studentList.add(new Student(nameField.getText(), emailField.getText()));
        clearForm();
    }

    private void handleUpdate() {
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(emailField.getText())) {
                s.setName(nameField.getText());
                tableView.refresh();
                clearForm();
                return;
            }
        }
    }

    private void handleDelete() {
        studentList.removeIf(s -> s.getEmail().equalsIgnoreCase(emailField.getText()));
        clearForm();
    }

    private void showView(boolean showTable) {
        formContainer.setVisible(!showTable);
        tableContainer.setVisible(showTable);
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
    }

    public static class Student {
        private String name;
        private String email;
        public Student(String name, String email) { this.name = name; this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}