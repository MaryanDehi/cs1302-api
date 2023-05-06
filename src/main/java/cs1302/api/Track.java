package cs1302.api;

import java.util.*;

/** Track info.*/
public class Track extends SuperItem {
    Album album;
    ArrayList <Artist> artists = new ArrayList <> ();
    // String href;
    // String id;
    // String name;
    // float popularity;
    // String uri;

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
