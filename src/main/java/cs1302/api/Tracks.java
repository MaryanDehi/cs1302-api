package cs1302.api;

import java.util.*;

/** Tracks returned by search. */
public class Tracks {
    private String href;
    private float limit;
    private String next;
    private float offset;
    private String previous;
    private float total;
    ArrayList <Track> items = new ArrayList <> ();
} // Tracks
