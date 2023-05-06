package cs1302.api;

import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.beans.property.*;

/** Represents a human readable format of a result from a search.*/
public class CustomResult {
    ImageView image;
    SimpleStringProperty name;
    SimpleStringProperty id;
    SimpleStringProperty href;
    SimpleStringProperty type;
    private static final String DEFAULT_IMG = "file:resources/default.png";

    /**
     * Constructs a CustomResult.
     * @param imgUrl represents a url to a image.
     * @param name is the name of the result.
     * @param id is the id of the result.
     * @param href is the url to the item.
     * @param type item type.
     */
    public CustomResult(String imgUrl, String name, String id, String href, String type) {
        try {
            this.image = new ImageView(new Image(imgUrl, 80, 80, false, false));
        } catch (IllegalArgumentException e) {
            image = new ImageView(new Image(DEFAULT_IMG, 80, 80, false, false));
        } // try
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleStringProperty(id);
        this.href = new SimpleStringProperty(href);
        this.type = new SimpleStringProperty(type);
    } // CustumResult constructor

    /**
     * Returns item's type.
     * @return String item type.
     */
    public String getType() {
        return type.get();
    } // getType

    /**
     * Returns item's name.
     * @return String item name.
     */
    public String getName() {
        return name.get();
    } // getName

    /**
     * Returns item's image.
     * @return ImageView item image.
     */
    public ImageView getImage() {
        return image;
    } // getImage

    /**
     * Returns item's id.
     * @return String item id.
     */
    public String getId() {
        return id.get();
    } // getId

    /**
     * Returns item's href.
     * @return String item href.
     */
    public String getHref() {
        return href.get();
    } // href
} // CustumResult
