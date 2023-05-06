package cs1302.api;

import java.util.*;

/** Album info. */
public class Album extends SuperItem {
    // String href;
    // String id;
    ArrayList <SpotImage> images = new ArrayList <> ();
//    String name;
    ArrayList <String> genres = new ArrayList <> ();
    String label;
    //  float popularity;

    @Override
    public String getImageUrl() {
        String img = "";
        try {
            if (images.size() > 0) {
                img = images.get(0).url;
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

} // Album
