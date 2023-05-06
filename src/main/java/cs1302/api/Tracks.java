package cs1302.api;

import java.util.*;

/**
 * Tracks returned by search. This is used by Gson to create an object from the
 * JSON response body.
 */
public class Tracks {
    String href;
    float limit;
    String next;
    String previous;
    int total;
    List <Track> items = new ArrayList <> ();
} // Tracks
