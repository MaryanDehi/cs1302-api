package cs1302.api;

import java.util.*;

/** Artist info.*/
public class Artist extends SuperItem {
    ArrayList <String> genres = new ArrayList <> ();
    //String id;
    //String href
    ArrayList <SpotImage> images = new ArrayList <> ();
    //String name;
    //float popularity;
    //String type;
    //String uri;

    @Override
    public String getImageUrl() {
        String imgUrl = "";
        try {
            if (images.size() > 0) {
                imgUrl = images.get(0).url;
            } else {
                imgUrl = ApiApp.DEFAULT_IMG;
            } // if
        } catch (NullPointerException e) {
            System.out.println("Null image found.");
            // If image is null set it to default.
            imgUrl = ApiApp.DEFAULT_IMG;
        } // try
        return imgUrl;
    } // getImageUrl

} // artist
