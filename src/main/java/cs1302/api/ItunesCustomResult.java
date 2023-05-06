package cs1302.api;

import javafx.scene.image.*;
import javafx.beans.property.*;

/** Respresents the information protrayed on the more Info Scene. */
public class ItunesCustomResult {
    ImageView image;
    SimpleStringProperty artistName;
    SimpleStringProperty name;
    private static final String DEFAULT_IMG = "file:resources/default.png";

    /**
     * Constructs a ItunesCustomResult.
     * @param imgUrl represents a url to a image.
     * @param artistName represents the name of the artist.
     * @param name represents the name of the song or album.
     */
    public ItunesCustomResult(String imgUrl, String artistName, String name) {
        try {
            this.image = new ImageView(new Image(imgUrl, 80, 80, false, false));
        } catch (IllegalArgumentException e) {
            this.image = new ImageView(new Image(DEFAULT_IMG, 80, 80, false, false));
        } // try

        this.name = new SimpleStringProperty(name);
        this.artistName = new SimpleStringProperty(artistName);
    } // ItunesCustomResult constructor

    /**
     * Returns name of item, song or album name.
     * @return String.
     */
    public String getName() {
        return name.get();
    } // getName

    /**
     * Returns name of artist of item.
     * @return String artist name.
     */
    public String getArtistName() {
        return artistName.get();
    } // getArtis

    /**
     * Returns image of item.
     * @return ImageView image.
     */
    public ImageView getImage() {
        return image;
    } // getImage

} // ItunesCustomResult
