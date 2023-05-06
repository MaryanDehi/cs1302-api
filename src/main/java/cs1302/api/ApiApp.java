package cs1302.api;

import java.net.http.*;
import java.net.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
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
import javafx.scene.*;
import javafx.animation.*;
import javafx.util.*;
import com.google.gson.FieldNamingPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

/**
 * Lets users search for a artist, album or track from Spotify Websearch API and get to see
 * additional info about a result provided by Itunes API.
 */
public class ApiApp extends Application {

    /** A default image which loads when there is no image with result. */
    public static final String DEFAULT_IMG = "file:resources/default.png";

    /** Oauth2 credentials for Spotify API. */
    private static final String CLIENT_ID = "d30a7cd0bf96458d844fde3d1b4364b9";
    private static final String CLIENT_SECRET = "6468f80b34ed472c9605a151a97e48ce";
    private static final String AUTH_URL = "https://accounts.spotify.com/api/token";

    /** Limit for number of results from Spotify API. */
    private static final String SEARCH_NUMBER_LIMIT = "20";

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
    private Scene scene; // This is the main scene

    /** Holds the most recent type of search performed.*/
    private String searchType;

    /** Holds the search bar, play/pause button, dropdown menu and get Images button. */
    private HBox searchArea;

    /** Holds description label. */
    private HBox labelHBox;

    private Label instructionLabel;
    private Label searchLabel;
    private ComboBox<String> dropDownMenu;

    /** Search result display table. */
    private TableView<CustomResult> table;
    private TableColumn<CustomResult, ImageView> imageCol;
    private TableColumn<CustomResult, String> nameCol;
    private TableColumn<CustomResult,String> actionCol;

    private TextField textField;
    private Button searchButton;
    private Label spotifyLabel;
    private Label itunesLabel;
    private Label itunesLabelAlbum;

    /** Scene that shows extra info of a selected item. */
    private Scene albumArtistScene;
    private Scene songInfoScene;
    private HBox root;
    private VBox vbox;
    private HBox songButtonLabel;

    /** Info Scene elements. */
    private BorderPane songBorderPane;
    private VBox albumArtistRoot;

    /** Table to display Album and Artist info.*/
    private TableView<ItunesCustomResult> infoTable;
    private TableColumn<ItunesCustomResult, ImageView> infoImageCol;
    private TableColumn<ItunesCustomResult, String> infoNameCol;
    private TableColumn<ItunesCustomResult, String> infoArtistCol;

    /** Info view elements. */
    private ImageView infoImage;
    private Button backButton;
    private Button tableBackButton;
    private Text titleText;

    /** Oauth2 verification token for Spotify API. */
    private String accessToken;
    private SpotifySearchResponse searchResponse;
    private ObservableList<CustomResult>resultList;

    /**
     * Constructs a {@code ApiApp} object.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;
        this.albumArtistScene = null;
        this.root = new HBox();
        this.vbox = new VBox(8);
        this.songInfoScene = null;
        this.albumArtistRoot = new VBox();
        this.songBorderPane = new BorderPane();
        this.infoTable = new TableView<>();
        this.infoImageCol = new TableColumn<>("Image");
        this.infoNameCol = new TableColumn<>("Name");
        this.infoArtistCol = new TableColumn<>("Artist");
        this.infoTable.getColumns().addAll(infoImageCol, infoNameCol, infoArtistCol);
        infoImageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        infoImageCol.prefWidthProperty().bind(infoTable.widthProperty().divide(6));
        infoNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        infoNameCol.prefWidthProperty().bind(infoTable.widthProperty().divide(3));
        infoArtistCol.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        infoArtistCol.prefWidthProperty().bind(infoTable.widthProperty().divide(3));
        this.labelHBox = new HBox();
        this.infoImage = new ImageView(new Image(DEFAULT_IMG, 300, 300, true, true));
        this.titleText = new Text("");
        this.backButton = new Button("Back");
        this.backButton.setOnAction(event -> {
            switchToMainScene();
        });
        this.tableBackButton = new Button("Back");
        this.tableBackButton.setOnAction(event -> {
            switchToMainScene();
        });
        this.instructionLabel = new Label("Type in a term,"
            + " select a media type, then click the button.");
        this.spotifyLabel =  new Label("Results provided by Spotify Search API. ");
        this.itunesLabel = new Label("Result provided by iTunes API");
        this.itunesLabelAlbum = new Label("Result provided by iTunes API");
        this.searchLabel = new Label("Search: ");
        this.songButtonLabel = new HBox(8);

        this.searchArea = new HBox();
        this.textField = new TextField("the blonde");
        this.searchButton = new Button("Search");
        this.dropDownMenu = new ComboBox<>();
        this.table = new TableView<>();
        imageCol = new TableColumn<>("Image");
        nameCol = new TableColumn<>("Name");
        actionCol = new TableColumn<>("More Info");
        table.getColumns().addAll(imageCol, nameCol, actionCol);
        imageCol.prefWidthProperty().bind(table.widthProperty().divide(4)); // w * 1/4
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        nameCol.prefWidthProperty().bind(table.widthProperty().divide(2)); // w * 1/2
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        actionCol.prefWidthProperty().bind(table.widthProperty().divide(4)); // w * 1/4
        actionCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        Callback<TableColumn<CustomResult, String>, TableCell<CustomResult, String>> cellFactory =
            createCellFactory();
        actionCol.setCellFactory(cellFactory);
        HBox.setHgrow(vbox,  Priority.ALWAYS);
        HBox.setHgrow(textField, Priority.ALWAYS);
    } // ApiApp

    /**
     * Initializes the scenes layouts.
     * {@inheritDoc}
     */
    @Override
    public void init() {
        try {
            // Gets a accessToken to be used for Spotify API.
            accessToken = getAuthToken();
        } catch (Exception e) {
            System.err.println("fail to get auth token:" + e.getMessage());
            System.exit(0);
        } // catch
        albumArtistRoot.getChildren().addAll(itunesLabelAlbum, infoTable, this.tableBackButton);

        songButtonLabel.getChildren().addAll(itunesLabel, backButton);
        songBorderPane.setTop(titleText);
        songBorderPane.setMargin(titleText, new Insets(30,12,30,12));
        songBorderPane.setCenter(infoImage);
        songBorderPane.setBottom(songButtonLabel);
        songBorderPane.setAlignment(titleText, Pos.CENTER);
        songBorderPane.setAlignment(songButtonLabel, Pos.CENTER);
        songBorderPane.setMargin(songButtonLabel, new Insets(12,12,30,12));
        root.getChildren().addAll(vbox);
        vbox.getChildren().addAll(searchArea,instructionLabel, table, labelHBox);

        searchArea.getChildren().addAll(searchLabel, textField, dropDownMenu,
            searchButton);
        searchArea.setFillHeight(true);
        labelHBox.getChildren().add(spotifyLabel);

        // Defines all the options for the dropdown menu.
        ObservableList<String> options =
            FXCollections.observableArrayList(
                "artist",
                "album",
                "track"
                );

        dropDownMenu.setItems(options);
        // Makes the default value for dropDown menu
        dropDownMenu.setValue("track");

        EventHandler<ActionEvent> mouseClickHandler = (ActionEvent e) -> {
            this.loadResults(e); // loads results.
        };
        searchButton.setOnAction(mouseClickHandler);
    } // init

    /**
     * Initializes the stage. Is the main entry point to Application.
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.root, 500, 500);
        // sets default root for scene, which is songGrid.
        this.songInfoScene = new Scene(this.songBorderPane, 500, 500);
        this.albumArtistScene = new Scene(this.albumArtistRoot, 500, 500);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("SpootyTunes App");
        this.stage.setScene(this.scene);
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));
    } // start

    /**
     * Is called when application should stop.
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        System.out.println("Stop method called.");
    } // stop

    /**
     * Updates the extra information and switches to it.
     * @param result specifies which item to get the info from.
     */
    private void switchInfoScene(CustomResult result) {
        try {
            String searchUrl = generateItunesUrl(result.getName(), result.getType());

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
            SpootifyItunesResponse itunesResponse = GSON.fromJson(body,
                 SpootifyItunesResponse.class);
            final int statusCode = response.statusCode();
            if (statusCode != 200) {
                throw new IOException("response status code not 200:" + statusCode);
            } // if

            if (searchType.equals("album") || searchType.equals("artist")) {
                displayAlbumArtistView(itunesResponse);
            } else {
                displaySongInfo(itunesResponse.results[0]);
            } // if
        } catch (Exception e) {
            alertError(e);
        } // catch
    } // switchInfoScene

    /**
     * Display the album/artist result table with info from Itunes API.
     * @param response contains the items from Itunes.
     */
    private void displayAlbumArtistView(SpootifyItunesResponse response) {
        infoTable.setItems(createInfoList(response.results));
        this.stage.setScene(this.albumArtistScene);
    } // displayAlbumArtistView

    /**
     * Displays the song info from Itunes API.
     * @param result contains the items from Itunes.
     */
    private void displaySongInfo(SpootifyItunesResult result) {
        infoImage.setImage(new Image(result.artworkUrl100, 300, 300, true, true));
        titleText.setText(result.trackName + " by " + result.artistName);
        this.stage.setScene(this.songInfoScene);
    } // displaySongInfo

    /**
     * Converts SpootifyItunesResult to ItunesCustomResult objects and returns a list of them.
     * @param results results from Itunes api.
     * @return ObservableList the list of ItunesCustomResult.
     */
    private ObservableList<ItunesCustomResult> createInfoList (SpootifyItunesResult[] results) {
        ObservableList<ItunesCustomResult> list = FXCollections.observableArrayList();
        for (int i = 0; i < results.length; i++) {
            String name = results[i].trackName;
            String artistName = results[i].artistName;
            String imgUrl = results[i].artworkUrl100;
            if (searchType.equals("artist")) {
                name = results[i].collectionName;
            } // if
            ItunesCustomResult item = new ItunesCustomResult(imgUrl, artistName, name);
            list.add(item);
        } // for
        return list;
    } // createInfoList

    /**
     * Generates a Itunes search Url based on name and type of item from table.
     * @param searchTerm The name of the item.
     * @param type The item type.
     * @return String the complete Url.
     */
    private String generateItunesUrl(String searchTerm, String type) {
        String entity = "";
        String attribute = "";
        int searchLimit = 20;
        if (type.equals("track")) {
            entity = "entity=song&";
            attribute = "attribute=songTerm";
            searchLimit = 1;
        } else if (type.equals("album")) {
            entity = "entity=song&";
            attribute = "attribute=albumTerm";
        } else {
            entity = "entity=album&";
            attribute = "attribute=artistTerm";
        } // if
        String url = "https://itunes.apple.com/search?term=" +
            URLEncoder.encode(searchTerm)
            + "&media=music&" + entity + attribute + "&limit=" + searchLimit;
        return url;
    } //

    /**
     * Switches scene to search Scene.
     */
    private void switchToMainScene() {
        this.stage.setScene(this.scene);
    } // switchToMainScene

    /**
     * Creates the moreinfo button and assigns it to the table column.
     * @return Callback the cellfactory
     */
    private Callback<TableColumn<CustomResult,String>, TableCell<CustomResult,
           String>> createCellFactory() {
        Callback<TableColumn<CustomResult, String>, TableCell<CustomResult, String>> cellFactory =
            new Callback<TableColumn<CustomResult, String>, TableCell<CustomResult, String>>() {
                    @Override
                    public TableCell<CustomResult, String> call(final TableColumn<CustomResult
                        , String> param) {
                        final TableCell<CustomResult, String> cell = new TableCell<CustomResult,
                            String>() {
                            final Button btn = new Button("More Info");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        CustomResult result = getTableView()
                                            .getItems().get(getIndex());
                                        switchInfoScene(result);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    } // createCellFactory

    /**
     * Displays search results.
     * @param event source event
     */
    private void loadResults(ActionEvent event) {
        beginLoading();
        Runnable searchTask = () -> {
            try {
                searchResponse = searchSpotifyData();
                searchType = dropDownMenu.getValue();
                Runnable b = () -> table.setItems(createList(searchResponse));
                runNow(b);
                table.setItems(resultList);
            } catch ( IOException | IllegalArgumentException |
                InterruptedException e) {
                alertError(e);
            } // try

            endLoading();
        }; // searchTask
        runNow(searchTask);
    } // loadImage


    /**
     * Creates a ArrayList of CustomResult objects that contain basic info of each result.
     * @param searchResponse search response from Spotify API.
     * @return ObservableList Search result.
     */
    private ObservableList<CustomResult> createList(SpotifySearchResponse searchResponse) {
        ObservableList<CustomResult> list = FXCollections.observableArrayList();

        if (dropDownMenu.getValue().equals("track")) {
            for (int i = 0; i < searchResponse.tracks.items.size(); i++) {
                SuperItem item = searchResponse.tracks.items.get(i);
                list.add(convertItemToCustom(item));
            } // for
        } else if (dropDownMenu.getValue().equals("artist")) {
            for (int i = 0; i < searchResponse.artists.items.size(); i++) {
                SuperItem item = searchResponse.artists.items.get(i);
                list.add(convertItemToCustom(item));
            } // for
        } else {
            for (int i = 0; i < searchResponse.albums.items.size(); i++) {
                SuperItem item = searchResponse.albums.items.get(i);
                list.add(convertItemToCustom(item));
            } // for
        } // if
        return list;
    } // searchTrack

    /**
     * Converts Spotify item to CustomResult.
     * @param item to be converted.
     * @return CustomResult the class that SuperItem is converted to.
     */
    private CustomResult convertItemToCustom(SuperItem item) {
        String img = item.getImageUrl();
        String name = item.name;
        String id = item.id;
        String href = item.href;
        String type = item.type;
        return new CustomResult(img, name, id, href, type);
    } // convertItemToCustom

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
            TextArea text = new TextArea("Exception: "
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

        String spotifyTokenURL = AUTH_URL;

        String spotifyFormatted = CLIENT_ID + ":" + CLIENT_SECRET;
        String encoded = Base64.getEncoder().encodeToString((spotifyFormatted).getBytes());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(spotifyTokenURL))
                .headers("Content-Type", "application/x-www-form-urlencoded", "Authorization",
                    "Basic " + encoded)
                .POST(BodyPublishers.ofString("grant_type=client_credentials")).build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

        // Convert response JSON to Java objects.
        String body = response.body();
        AuthorizationResponse authResponse = GSON.fromJson(body, AuthorizationResponse.class);
        return authResponse.getAccessToken();
    }


    /**
     * Sends a request to the Spotify API and returns its response.
     * @return SpotifySearchResponse the search result from search API.
     */
    private SpotifySearchResponse searchSpotifyData() throws IOException, InterruptedException {
        String term = URLEncoder.encode(textField.getText());
        String spotifyURL = "https://api.spotify.com/v1/search?q=" + term + "&type=" +
            dropDownMenu.getValue() + "&limit=" + SEARCH_NUMBER_LIMIT;

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
        SpotifySearchResponse spotResponse =  GSON.fromJson(response.body().trim(),
            SpotifySearchResponse.class);
        return spotResponse;
    } // SearchDate

} // ApiApp
