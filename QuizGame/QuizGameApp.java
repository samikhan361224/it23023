package com.example.quizgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizGameApp extends Application {
    private Stage primaryStage;
    private String playerName;
    private List<Question> quizQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    // Database Connection String
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("JavaFX Tech Quiz");

        initializeDatabase();
        showWelcomeScene();
        primaryStage.show();
    }

    // --- SCENE 1: WELCOME ---
    private void showWelcomeScene() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Welcome to the Quiz!");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter your name");
        nameInput.setMaxWidth(200);

        Button startBtn = new Button("Start Quiz");
        Button exitBtn = new Button("Exit");

        startBtn.setOnAction(e -> {
            playerName = nameInput.getText().trim();
            if (!playerName.isEmpty()) {
                loadQuizData();
                showQuizScene();
            } else {
                showAlert("Error", "Please enter your name to start.");
            }
        });

        exitBtn.setOnAction(e -> Platform.exit());

        layout.getChildren().addAll(title, nameInput, startBtn, exitBtn);
        primaryStage.setScene(new Scene(layout, 400, 300));
    }

    // --- SCENE 2: QUIZ ---
    private void showQuizScene() {
        if (currentQuestionIndex >= quizQuestions.size()) {
            saveScore();
            showResultScene();
            return;
        }

        Question q = quizQuestions.get(currentQuestionIndex);
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));

        Label progressLabel = new Label("Question " + (currentQuestionIndex + 1) + " of 5");
        Label questionLabel = new Label(q.getText());
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ToggleGroup optionsGroup = new ToggleGroup();
        RadioButton rbA = new RadioButton("A) " + q.getA());
        RadioButton rbB = new RadioButton("B) " + q.getB());
        RadioButton rbC = new RadioButton("C) " + q.getC());
        RadioButton rbD = new RadioButton("D) " + q.getD());

        rbA.setToggleGroup(optionsGroup);
        rbB.setToggleGroup(optionsGroup);
        rbC.setToggleGroup(optionsGroup);
        rbD.setToggleGroup(optionsGroup);

        Button nextBtn = new Button(currentQuestionIndex == 4 ? "Submit" : "Next");

        nextBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
            if (selected != null) {
                String answer = selected.getText().substring(0, 1); // Get 'A', 'B', etc.
                if (answer.equals(q.getCorrectAnswer())) score++;

                currentQuestionIndex++;
                showQuizScene();
            } else {
                showAlert("Selection Required", "Please select an answer.");
            }
        });

        layout.getChildren().addAll(progressLabel, questionLabel, rbA, rbB, rbC, rbD, nextBtn);
        primaryStage.setScene(new Scene(layout, 500, 400));
    }

    // --- SCENE 3: RESULT ---
    private void showResultScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Label finishLabel = new Label("Quiz Completed!");
        finishLabel.setStyle("-fx-font-size: 22px;");

        Label scoreLabel = new Label(playerName + ", your score is: " + score + " / 5");
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button restartBtn = new Button("Play Again");
        Button exitBtn = new Button("Exit");

        restartBtn.setOnAction(e -> {
            currentQuestionIndex = 0;
            score = 0;
            showWelcomeScene();
        });
        exitBtn.setOnAction(e -> Platform.exit());

        layout.getChildren().addAll(finishLabel, scoreLabel, restartBtn, exitBtn);
        primaryStage.setScene(new Scene(layout, 400, 300));
    }

    // --- DATABASE LOGIC ---
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create tables
            stmt.execute("CREATE TABLE IF NOT EXISTS questions (id INTEGER PRIMARY KEY AUTOINCREMENT, question_text TEXT, option_a TEXT, option_b TEXT, option_c TEXT, option_d TEXT, correct_answer TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS scores (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT, score INTEGER, played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Check if seeding is needed
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM questions");
            if (rs.next() && rs.getInt(1) == 0) {
                seedQuestions(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedQuestions(Connection conn) throws SQLException {
        String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 20; i++) {
                pstmt.setString(1, "Question " + i + ": What is the result of 10 + " + i + "?");
                pstmt.setString(2, String.valueOf(10 + i));
                pstmt.setString(3, "99");
                pstmt.setString(4, "0");
                pstmt.setString(5, "Unknown");
                pstmt.setString(6, "A");
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void loadQuizData() {
        quizQuestions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Randomly select 5 questions
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions ORDER BY RANDOM() LIMIT 5");
            while (rs.next()) {
                quizQuestions.add(new Question(
                        rs.getString("question_text"),
                        rs.getString("option_a"), rs.getString("option_b"),
                        rs.getString("option_c"), rs.getString("option_d"),
                        rs.getString("correct_answer")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveScore() {
        String sql = "INSERT INTO scores (player_name, score) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner Class for Question Data
    static class Question {
        private String text, a, b, c, d, correct;
        public Question(String text, String a, String b, String c, String d, String correct) {
            this.text = text; this.a = a; this.b = b; this.c = c; this.d = d; this.correct = correct;
        }
        public String getText() { return text; }
        public String getA() { return a; }
        public String getB() { return b; }
        public String getC() { return c; }
        public String getD() { return d; }
        public String getCorrectAnswer() { return correct; }
    }
}
