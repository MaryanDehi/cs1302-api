package cs1302.api;

/**
 * Super class that contains shared attributes of the Artist, Album and Track class.
 */
public abstract class SuperItem {
    String href;
    String id;
    String name;
    String type;
    int popularity;
    String uri;

    /**
     * Returns the image url for item.
     * @return String image url.
     */
    public abstract String getImageUrl();


} // SuperItem
