# Purpose:
I want to get recommendations from Spotify WebAPI and make the utilization of results more predictable.

# Changes:
### Added ability to get recommendations from Spotify WebAPI
The GET recommendations reference helped: https://developer.spotify.com/documentation/web-api/reference/get-recommendations

In its current form, TrackService only uses music genres as seeds when asking Spotify WebAPI for recommendations. In the future, I intend to make use of the many other options listed in the reference, particularly having to do with the various qualities of the recommendation such as liveness, energy, and popularity.
### Removed HistoryFragment's reliance on MainActivity methods
In doing this, I also changed the way that HistoryFragment's dataset is made. It's not in its final state in this regard. I believe I will need to set up persistent storage of previous track data before the dataset construction is as intended.
### Removed HomeFragment's reliance on MainActivity methods
The SpotifyAppRemote is now located in HomeFragment. There are a few things I want to note about this:
- The good:
	- The functionality has not changed.
	- HomeFragment does not rely on MainActivity (no longer breaking the Single Responsibility Principle)
- The questionable:
	- The remote disconnects when the user moves to a new fragment, and reconnects each time the user moves back to the HomeFragment. I don't know if this should be a concern.
	- HomeFragment is getting to be a rather large class. I am wondering if it is possible to move some of these methods to a helper/utility class of some sort.

# Summary:
- The application receives recommendations from Spotify WebAPI.
- Views are populated with data from API requests without having to switch back and forth between fragments.
- Fragments no longer rely on methods that were previously located in MainActivity (adhering more closely to the Single Responsibility Principle).