package cs1302.api;

import java.util.*;

/** Track info. This is used by Gson to create an object from the JSON response body.*/
public class Track extends SuperItem {
    Album album;
    List <Artist> artists = new ArrayList <> ();

    /**
     * Gets image url specific to track object.
     * @return String if no image is found then {@code ApiApp.DEFAULT_IMG} is returned.
     * {@inheritDoc}
     */
    @Override
    public String getImageUrl() {
        String img = "";
        try {
            if (album.images.size() > 0) {
                img = album.images.get(0).url;
            } else {
                img = ApiApp.DEFAULT_IMG;
            } // if
        } catch (NullPointerException e) {
            System.out.println("Null image found.");
            // If image is null set it to default.
            img = ApiApp.DEFAULT_IMG;
        } // try
        return img;
    } // getImageUrl
} // track
