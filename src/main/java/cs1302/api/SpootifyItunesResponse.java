package cs1302.api;

/**
 * Represents a response from the iTunes Search API. This is used by Gson to
 * create an object from the JSON response body.
 */
public class SpootifyItunesResponse {
    int resultCount;
    SpootifyItunesResult[] results;
} // ItunesResponse
