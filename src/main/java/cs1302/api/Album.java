package cs1302.api;

import java.util.*;

/** Album info. This is used by Gson to create an object from the JSON response body. */
public class Album extends SuperItem {

    List <SpotImage> images = new ArrayList <> ();
    String label;

    /**
     * Gets image url specific to album object.
     * @return String if no image is found then {@code ApiApp.DEFAULT_IMG} is returned.
     * {@inheritDoc}
     */
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
