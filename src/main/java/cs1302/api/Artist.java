package cs1302.api;

import java.util.*;

/** Artist info. This is used by Gson to create an object from the JSON response body.*/
public class Artist extends SuperItem {
    List <String> genres = new ArrayList <> ();
    List <SpotImage> images = new ArrayList <> ();

    /**
     * Gets image url specific to artist object.
     * @return String if no image is found then {@code ApiApp.DEFAULT_IMG} is returned.
     * {@inheritDoc}
     */
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
