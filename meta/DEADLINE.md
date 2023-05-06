# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice looking HTML.

## Part 1: App Description

This app allows users to search for tracks, artists or albums from Spotify through the Spotify API.
This app allows users to search for tracks, artists or albums from Spotify through the Spotify API. Then they can get additional information
about a result(artist, album or track) from the iTunes API.
Spotify API: https://developer.spotify.com/documentation/web-api
iTunes API: https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/index.html

How the iTunes API knows what information to bring back is because when the user clicks on the more info button next to a result of interest,
a url for the iTunes API is generated based on the type and name of that result from the Spotify API. That url will return different things based on the
type of the item of interest: artist, album or track.
If the item is an artist, then iTunes API will return a list of albums under that artist’s name.
If the item is an album, then the iTunes API will return a list of songs within the album.
If the item is a track, then the iTunes API will return the song image and the artist.

Repository: https://github.com/HeartyFeels/cs1302-api.git

## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

I have learned how to use accessTokens and what Oauth2 is. I had to overcome the obstacle of requesting a refresh token to use the Spotify API.
Spotify Authorization Process: https://developer.spotify.com/documentation/web-api/concepts/authorization
I also learned how to use a Tableview and add a button to its column, which was much more complex than I thought.

## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

If I could start the project over from scratch I would first chart out my json object class and see what attributes are shared across them
so I can make a superclass much earlier in the process and save a lot more time and code. Then I would work on getting the iTunes results
 to be more accurate by filtering the results based on name and artist.
