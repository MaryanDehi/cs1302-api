package cs1302.api;

import java.net.http.*;
import java.net.*;
import java.net.http.HttpResponse.BodyHandlers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
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
import javafx.scene.control.ContentDisplay;
import javafx.event.*;
import java.io.IOException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import java.util.*;
import javafx.animation.*;
import javafx.util.*;


/**
 * Lets users search for a artist, album or track and recieve recommendations based off
 * their choice.
 */
public class ApiApp extends Application {

    /** URL used for testing testing purposes. */
    String searchUrl;

    /** Grid width and height. */
    private final int numCols = 5;
    private final int numRows = 4;

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
    private HBox searchBar;
    /** Holds progress bar and description label. */
    private HBox progressHbox;
    private Label instructionLabel;
    private Label searchLabel;
    private ComboBox<String> dropDownMenu;
    /** Containers for all the images. */
    private ImageView[][] frameList;
    /** Organizes the imageview. */
    private GridPane grid;


    private TextField textField;
    private ToggleButton playPauseButton;
    private Button getImagesButton;
    private ProgressBar progressBar;
    private Label iTunesLabel;

    /** String list of all non repeated image urls. */
    private Set<String> uniqueUrlSet;
    /** List of images that are downloaded. */
    private List<Image> downloadedImagesList;
    /** List of displayed images. */
    private List<Image> displayedImagesList;

    /** A default image which loads when the application starts. */
    private static final String DEFAULT_IMG = "resources/default.png";
    /** Default dimensions for images. */
    private double defHEIGHT;
    private double defWIDTH;

    private static final String CLIENT_ID = "d30a7cd0bf96458d844fde3d1b4364b9";
    private static final String CLIENT_SECRET = "6468f80b34ed472c9605a151a97e48ce";
    private static final String AUTH_URL = "https://accounts.spotify.com/api/token";

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
        this.iTunesLabel =  new Label("Images provided by iTunes Search API. ");
        this.searchLabel = new Label("Search: ");
        this.searchBar = new HBox();
        this.grid = new GridPane();
        this.frameList = new ImageView[4][5];
        this.playPauseButton = new ToggleButton("Play");
        this.textField = new TextField("jack johnson");
        this.getImagesButton = new Button("Get Images");
        this.dropDownMenu = new ComboBox<>();
        this.progressBar = new ProgressBar(0);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        // feel free to modify this method
        root.getChildren().addAll(vbox);
        vbox.getChildren().addAll(searchBar,instructionLabel, grid, progressHbox);
        searchBar.getChildren().addAll(playPauseButton, searchLabel, textField, dropDownMenu,
            getImagesButton);
        searchBar.setFillHeight(true);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressHbox.getChildren().addAll(progressBar, iTunesLabel);
        // gets default image and sets default height and width.
        Image defaultImage = new Image("file:" + DEFAULT_IMG);
        defHEIGHT = defaultImage.getHeight();
        defWIDTH = defaultImage.getWidth();
        // Defines all the options for the dropdown menu.
        ObservableList<String> options =
            FXCollections.observableArrayList(
                "movie",
                "music",
                "musicVideo",
                "audiobook",
                "shortFilm",
                "tvShow",
                "software",
                "ebook",
                "all"
                );
        dropDownMenu.setItems(options);
        // Makes the default value for dropDown menu
        dropDownMenu.setValue("music");
        uniqueUrlSet = new HashSet<>();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                frameList[row][col] = new ImageView(defaultImage);
            } // for
        } // for
        loadImageGrid();
        this.playPauseButton.setDisable(true);
        EventHandler<ActionEvent> handler = event -> play();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        EventHandler<ActionEvent> mouseClickHandler = (ActionEvent e) -> {
            timeline.pause(); // pauses Play button
            this.loadImages(e); // loads images
        };
        getImagesButton.setOnAction(mouseClickHandler);
        playPauseButton.setOnAction(event -> {
            if (playPauseButton.isSelected()) {
                Platform.runLater(() -> playPauseButton.setText("Pause"));
                timeline.play();
            } else {
                Platform.runLater(() -> playPauseButton.setText("Play"));
                timeline.pause(); // pause playing.
            } // if
        }); // setOnAction
    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.root);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("GalleryApp!");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));
    } // start

    /** {@inheritDoc} */
    @Override
    public void stop() {
        System.out.println("Stop method called.");
    } // stop


    /**
     * Replaces a Image of a random ImageView with a non displayed one.
     */
    private void play() {
        Random random = new Random();
        // Sets a random index for the ImageView grid which will be the one to be replaced.
        int randomRow = (int) Math.round(random.nextDouble() * 3);
        int randomCol = (int) Math.round(random.nextDouble() * 4);
        // Initiall random index of the downloaded image to replace the one in the imageView.
        int newImageIndex = (int) Math.round(random.nextDouble() * (downloadedImagesList.size() -
            1));
        // Keeps regenerating a random index until it one that isn't already displayed.
        while (displayedImagesList.contains(downloadedImagesList.get(newImageIndex))) {
            newImageIndex = (int) Math.round(random.nextDouble() * (downloadedImagesList.size() -
                1));

        } // while
        // Replaces that imageview image with the new one from the downloaded image list and removes
        // the replaced image from the displayed images list.
        Image temp = frameList[randomRow][randomCol].getImage();
        frameList[randomRow][randomCol].setImage(downloadedImagesList.get(newImageIndex));
        displayedImagesList.add(downloadedImagesList.get(newImageIndex));
        displayedImagesList.remove(temp);
    } // play

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
     * Sets imgView with image given from url if it's valid.
     * @param event source event
     */
    private void loadImages(ActionEvent event) {
        beginLoading();
        Runnable searchTask = () -> {


            updateLabel("Getting Images...");
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .post()
                    .build();

                searchUrl = "https://itunes.apple.com/search?term=" +
                    URLEncoder.encode(textField.getText())
                    + "&limit=200&media=" + dropDownMenu.getValue();;
                HttpRequest request = HttpRequest.newBuilder()
                    .GET() // optional since default type of request is GET
                    .uri(new URI(searchUrl))
                    .build();
                // Send the request to get an HttpResponse
                HttpResponse<String> response = HTTP_CLIENT
                    .<String>send(request, BodyHandlers.ofString());
                // Check the status code of the response
                if (response.statusCode() != 200) {
                    throw new IOException("HTTP " + response.statusCode());
                } // if
                // Convert response JSON to Java objects.
                String body = response.body();
                ItunesResponse itunesResponse = GSON.fromJson(body, ItunesResponse.class);
                uniqueUrlSet.clear();
                // Adds all unique urls to a list.
                for (int i = 0; i < itunesResponse.results.length ;i++) {
                    uniqueUrlSet.add(itunesResponse.results[i].artworkUrl100);
                } // for
                // Checks if there is suffiient amount of usable images.
                if (uniqueUrlSet.size() < 21) {
                    throw new IllegalArgumentException(uniqueUrlSet.size() + " distinct results " +
                        "found but 21 or more are needed.");
                } // if
                // If above conditions are met then next code can run to download and
                // display images.
                setProgress(0);
                downloadedImagesList = new ArrayList<>();
                displayedImagesList = new ArrayList<>();
                // If unique url condition is met then images are downloaded and the first 20 in the
                // list are displayed..
                downloadImageList();
                setProgress(0.99);
                setImages();
                setProgress(1.0);
                updateLabel(searchUrl);
            } catch (URISyntaxException | IOException | IllegalArgumentException |
                InterruptedException e) {
                alertError(e);
                Platform.runLater(() -> {
                    updateLabel("Last attempt to get images failed..."); });
            } // try
            endLoading();
        }; // searchTask
        runNow(searchTask);
    } // loadImage

    /** Sets all the ImageViews in the gridpanes with downloaded images. */
    private void setImages() {
        int count = 0;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // sets each imageview with a downloaded image.
                setFrameImage(row, col, downloadedImagesList.get(count));
                displayedImagesList.add(downloadedImagesList.get(count));
                count++;
            } // for
        } // for
    } // setImages

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
        this.getImagesButton.setDisable(true);
        this.playPauseButton.setDisable(true);
        this.playPauseButton.setText("Play");
    } // beginLoading

    /** Sets buttons status after loadImage is finished. */
    private void endLoading() {
        Platform.runLater(() -> {
            this.getImagesButton.setDisable(false);
            playPauseButton.setSelected(false);
            if (downloadedImagesList != null && downloadedImagesList.size() > 0) {
                playPauseButton.setDisable(false);
            } // if
        });
    } // endLoading

    /**
     * Downloads all the images and stores them in a list.
     */
    private void downloadImageList() throws IOException {
        int i = 0;
        for (String artworkUrl100 : uniqueUrlSet) {
            // makes images from the urls and adds them to a list.
            Image newImg = new Image(artworkUrl100, defHEIGHT, defWIDTH, false, false);
            if (newImg.isError()) {
                throw new IOException(newImg.getException());
            } // if
            downloadedImagesList.add(newImg);
            setProgress((0.99) * i / uniqueUrlSet.size());
            i++;
        } // for
    } // downloadImageList

    /**
     * Loads an image to a specific ImageView on the grid.
     * @param row is the row of the ImageView.
     * @param col is colomn of the ImageView.
     * @param image is the image to be added to the ImageView.
     */
    private void setFrameImage(int row, int col, Image image) {
        Platform.runLater(() -> frameList[row][col].setImage(image));
    } // setFrameImage

    /**
     * Creates a gridpane of 4 by 5 ImageViews.
     */
    private void loadImageGrid() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                grid.add(frameList[row][col], col, row);
            } // for
        } // for
    } // loadImageGrid

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
     */
    private static void getAuthToken() throws IOException, InterruptedException {

        String tokenURL = "https://accounts.spotify.com/api/token";

        String formatted = clientId + ":" + clientSecret;
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
        System.out.println(response.body());

    }

} // ApiApp
