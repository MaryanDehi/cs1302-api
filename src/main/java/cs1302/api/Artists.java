package cs1302.api;

import java.util.*;

/** Artists from search. This is used by Gson to create an object from the JSON response body. */
public class Artists {
    String href;
    float limit;
    String next;
    String previous;
    int total;
    List <Artist> items = new ArrayList <> ();
} // Artists
