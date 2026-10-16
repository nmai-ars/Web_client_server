package WebClient;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebClient extends Application {

    private WebEngine webEngine;
    private TextField addressField;
    private Label statusLabel;
    private ProgressBar progressBar;

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        webEngine = webView.getEngine();

        Button backButton = new Button("←");
        Button forwardButton = new Button("→");
        Button reloadButton = new Button("↻");
        Button homeButton = new Button("Home");
        Button searchButton = new Button("Search");

        addressField = new TextField();
        addressField.setPromptText("Nhap tu khoa hoac dia chi website");

        statusLabel = new Label("San sang");
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(4);

        HBox navigationBar = new HBox(
                8,
                backButton,
                forwardButton,
                reloadButton,
                homeButton,
                addressField,
                searchButton
        );

        navigationBar.setAlignment(Pos.CENTER);
        navigationBar.setPadding(new Insets(10));
        HBox.setHgrow(addressField, Priority.ALWAYS);

        VBox topPanel = new VBox(
                0,
                navigationBar,
                progressBar
        );

        BorderPane root = new BorderPane();
        root.setTop(topPanel);
        root.setCenter(webView);
        root.setBottom(statusLabel);

        BorderPane.setMargin(statusLabel, new Insets(6, 10, 6, 10));

        backButton.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(-1);
            }
        });

        forwardButton.setOnAction(e -> {
            int current = webEngine.getHistory().getCurrentIndex();
            int size = webEngine.getHistory().getEntries().size();

            if (current < size - 1) {
                webEngine.getHistory().go(1);
            }
        });

        reloadButton.setOnAction(e -> webEngine.reload());

        homeButton.setOnAction(e -> loadGoogle());

        searchButton.setOnAction(e -> search());

        addressField.setOnAction(e -> search());

        webEngine.locationProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        addressField.setText(newValue);
                    }
                }
        );

        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        statusLabel.setText("Tai trang thanh cong");
                    } else if (newState == Worker.State.RUNNING) {
                        statusLabel.setText("Dang tai trang...");
                    } else if (newState == Worker.State.FAILED) {
                        statusLabel.setText(
                                "Tai trang that bai. Hay kiem tra Internet."
                        );
                    }
                }
        );

        progressBar.progressProperty().bind(
                webEngine.getLoadWorker().progressProperty()
        );

        Scene scene = new Scene(root, 1100, 720);

        stage.setTitle("Google Web Client - JavaFX");
        stage.setScene(scene);
        stage.show();

        loadGoogle();
    }

    private void loadGoogle() {
        webEngine.load("https://www.google.com/");
    }

    private void search() {
        String input = addressField.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        if (input.startsWith("https://")
                || input.startsWith("http://")) {
            webEngine.load(input);
        } else if (input.contains(".")
                && !input.contains(" ")) {
            webEngine.load("https://" + input);
        } else {
            String keyword = java.net.URLEncoder.encode(
                    input,
                    java.nio.charset.StandardCharsets.UTF_8
            );

            webEngine.load(
                    "https://www.google.com/search?q=" + keyword
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
