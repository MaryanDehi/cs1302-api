package cs1302.api;

import java.util.*;

/** Tracks returned by search. */
public class Tracks {
    String href;
    float limit;
    String next;
    String previous;
    int total;
    ArrayList <Track> items = new ArrayList <> ();
} // Tracks
