package cs1302.api;

import java.util.*;

/** Albums from search. This is used by Gson to create an object from the JSON response body.*/
public class Albums {
    String href;
    float limit;
    String next;
    String previous;
    int total;
    List <Album> items = new ArrayList <Album> ();
} // Albums
