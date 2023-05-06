package cs1302.api;

/**
 * Represents a result in a response from the iTunes Search API. This is
 * used by Gson to create an object from the JSON response body.
 */
public class SpootifyItunesResult {
    String wrapperType;
    String kind;
    String artistName;
    String trackName;

    String collectionName;
    String artworkUrl100;
    String releaseDate;
    // the rest of the result is intentionally omitted since we don't use it
} // ItunesResult
