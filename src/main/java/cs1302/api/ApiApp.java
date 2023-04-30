package cs1302.api;

import java.net.http.*;
import java.net.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.image.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.event.*;
import java.io.IOException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import java.util.*;
import javafx.animation.*;
import javafx.util.*;
import com.google.gson.FieldNamingPolicy;

/**
 * Lets users search for a artist, album or track and recieve recommendations based off
 * their choice.
 */
public class ApiApp extends Application {

    /** URL used for testing testing purposes. */
    String searchUrl;


    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    private Stage stage;
    private Scene scene;
    private HBox root;
    private VBox vbox;
    /** Holds the search bar, play/pause button, dropdown menu and get Images button. */
    private HBox searchArea;
    /** Holds progress bar and description label. */
    private HBox progressHbox;
    private Label instructionLabel;
    private Label searchLabel;
    private ComboBox<String> dropDownMenu;

    /** Containers for all the images. */
    private TableView<CustomResult> table;
    private TableColumn<CustomResult, ImageView> imageCol;
    private TableColumn<CustomResult, String> nameCol;
    private TableColumn<CustomResult,Button> buttonCol;


    private TextField textField;
    private Button searchButton;
    private ProgressBar progressBar;
    private Label spotifyLabel;

    /** A default image which loads when the application starts. */
    private static final String DEFAULT_IMG = "resources/default.png";
    /** Default dimensions for images. */
    private double defHEIGHT;
    private double defWIDTH;

    private static final String CLIENT_ID = "d30a7cd0bf96458d844fde3d1b4364b9";
    private static final String CLIENT_SECRET = "6468f80b34ed472c9605a151a97e48ce";
    private static final String AUTH_URL = "https://accounts.spotify.com/api/token";
    private String accessToken;
    /**
     * Constructs a {@code ApiApp} object}.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;
        this.root = new HBox();
        this.vbox = new VBox(8);
        this.progressHbox = new HBox();
        this.instructionLabel = new Label("Type in a term,"
            + " select a media type, then click the button.");
        this.spotifyLabel =  new Label("Results provided by Spotify Search API. ");
        this.searchLabel = new Label("Search: ");
        this.searchArea = new HBox();
        this.textField = new TextField("the blonde");
        this.searchButton = new Button("Search");
        this.dropDownMenu = new ComboBox<>();
        this.progressBar = new ProgressBar(0);
        this.table = new TableView<>();
        imageCol = new TableColumn("Image");
        nameCol = new TableColumn("Name");
        buttonCol = new TableColumn();
        table.getColumns().addAll(imageCol, nameCol, buttonCol);
        imageCol.prefWidthProperty().bind(table.widthProperty().divide(4)); // w * 1/4
        nameCol.prefWidthProperty().bind(table.widthProperty().divide(2)); // w * 1/2
        buttonCol.prefWidthProperty().bind(table.widthProperty().divide(4)); // w * 1/4
        HBox.setHgrow(progressBar, Priority.ALWAYS);
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        try {
            accessToken = getAuthToken();
        } catch (Exception e) {
            System.err.println("fail to get auth token:" + e.getMessage());
            System.exit(0);
        } // catch


        root.getChildren().addAll(vbox);
        vbox.getChildren().addAll(searchArea,instructionLabel, table, progressHbox);
        searchArea.getChildren().addAll(searchLabel, textField, dropDownMenu,
            searchButton);
        searchArea.setFillHeight(true);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressHbox.getChildren().addAll(progressBar, spotifyLabel);

        // gets default image and sets default height and width.
        Image defaultImage = new Image("file:" + DEFAULT_IMG);
        defHEIGHT = defaultImage.getHeight();
        defWIDTH = defaultImage.getWidth();
        // Defines all the options for the dropdown menu.
        ObservableList<String> options =
            FXCollections.observableArrayList(
                "artist",
                "album",
                "track"
                );
        dropDownMenu.setItems(options);
        // Makes the default value for dropDown menu
        dropDownMenu.setValue("artist");

        EventHandler<ActionEvent> mouseClickHandler = (ActionEvent e) -> {
            this.loadResults(e); // loads results.
        };
        searchButton.setOnAction(mouseClickHandler);
    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.root, 720, 1280);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("Spootify");
        this.stage.setScene(this.scene);
        this.stage.setHeight(720);
        this.stage.setWidth(1280);
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));
    } // start

    /** {@inheritDoc} */
    @Override
    public void stop() {
        System.out.println("Stop method called.");
    } // stop


    /**
     * Sets the progress on progress bar.
     * @param progress amount of progress to be set.
     */
    private void setProgress(final double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    } // setProgress

    /**
     * Updates the instruction label text to the supplied text.
     * @param label the new text for the label
     */
    private void updateLabel(String label) {
        Platform.runLater(() -> instructionLabel.setText(label));
    } // updateLabel

    /**
     * Displays search results.
     * @param event source event
     */
    private void loadResults(ActionEvent event) {
        beginLoading();
        Runnable searchTask = () -> {
            updateLabel("Getting results...");
            try {
                SpotifySearchResponse searchResponse = searchData();
                if (dropDownMenu.getValue().equals("track")) {
                    System.out.println(searchResponse.tracks.items.get(0).name);
                    System.out.println(searchResponse.tracks.items.get(0).artists.get(0).name);
                    System.out.println(searchResponse.tracks.items.get(0).album.images
                        .get(0).url);
                } else if (dropDownMenu.getValue().equals("artist")) {
                    System.out.println(searchResponse.artists);
                } else {
                    System.out.println(searchResponse.albums);
                } // if

            } catch ( IOException | IllegalArgumentException |
                InterruptedException e) {
                alertError(e);
                Platform.runLater(() -> {
                    updateLabel("Last attempt to get images failed..."); });
            } // try
            endLoading();
        }; // searchTask

        runNow(searchTask);
    } // loadImage


    /**
     * Creates a thread and start running it.
     * @param target is the task to be run in the thread.
     */
    private static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } // runNow

    /** Load buttons. */
    private void beginLoading () {
        this.searchButton.setDisable(true);
    } // beginLoading

    /** Sets buttons status after loadImage is finished. */
    private void endLoading() {
        Platform.runLater(() -> {
            this.searchButton.setDisable(false);
        });
    } // endLoading

    /**
     * Show a modal error alert based on {@code cause}.
     * @param cause a {@link java.lang.Throwable Throwable} that caused the alert
     */
    public void alertError(Throwable cause) {
        Runnable alertTask = () -> {
            TextArea text = new TextArea("URI: " + searchUrl + "\n\n\nException: "
                + cause.toString());
            text.setEditable(false);
            Alert alert = new Alert(AlertType.ERROR);
            alert.getDialogPane().setContent(text);
            alert.setResizable(false);
            alert.showAndWait();
        }; // alertTask
        Platform.runLater(alertTask);
    } // alertError

    /**
     * Spotify uses Client Credentials flow for server-to-server authentication. Client ID and
     * Client secret are sent in HTTP header to the authorization endpoint to get a refreshed
     * token.
     * @throws IOException
     * @return String Refresh Token
     */
    private String getAuthToken() throws IOException, InterruptedException {

        String tokenURL = AUTH_URL;

        String formatted = CLIENT_ID + ":" + CLIENT_SECRET;
        String encoded = Base64.getEncoder().encodeToString((formatted).getBytes());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenURL))
                .headers("Content-Type", "application/x-www-form-urlencoded", "Authorization",
                    "Basic " + encoded)
                .POST(BodyPublishers.ofString("grant_type=client_credentials")).build();

        System.out.println(request.headers());

        HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

        System.out.println(response.statusCode());
//        System.out.println(response.body());

        // Convert response JSON to Java objects.
        String body = response.body();
        AuthorizationResponse authResponse = GSON.fromJson(body, AuthorizationResponse.class);
        System.out.println("Token:" + authResponse.getAccessToken());
        return authResponse.getAccessToken();

    }

    /**
     * brubh
     * @return SpotifySearchResponse the search result from search API.
     */
    private SpotifySearchResponse searchData() throws IOException, InterruptedException {
        String term = URLEncoder.encode(textField.getText());
        String spotifyURL = "https://api.spotify.com/v1/search?q=" + term + "&type=" +
            dropDownMenu.getValue() + "&limit=2";

        HttpRequest searchRequest = HttpRequest.newBuilder()
                .uri(URI.create(spotifyURL))
                .headers("Authorization",
                    "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(searchRequest,
            BodyHandlers.ofString());

        final int statusCode = response.statusCode();
        if (statusCode != 200) {
            throw new IOException("response status code not 200:" + statusCode);
        } // if
        System.out.println(response.body().trim());
        SpotifySearchResponse searchResponse =  GSON.fromJson(response.body().trim(),
            SpotifySearchResponse.class);
        return searchResponse;
    }


} // ApiApp
