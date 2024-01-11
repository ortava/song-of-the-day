### Summary
TrackService now gets more complete Track information from the Spotify Web API's GET request - ID, URI, Title, Album, and Artist. A number of smaller, unrelated changes were made, particularly with regards to code structure and cleanliness.

While making these changes, I noted some problems I feel that I may need to address at a later point in development. For now, I just want to get the basic functionality working for the app.

#### Unaddressed problems/concerns
TrackService Problems:
- Initializing the dataset for HistoryFragment is unsuccessful upon first creating the fragment (API calls aren't successful), but they work fine when clicking away and then back again (thus populating the HistoryFragment with the correct data).
- I'm not sure how to properly use the RequestQueue from the Volley library.
- There is no real error handling in the GET request.

Misc Problems:
- How do I store the track data for the history screen?
- Mixing responsibilities amongst activities and fragments
	- HomeFragment uses functions in MainActivity. This is in part because I don't want to have multiple instances of a TrackService among the different activities and fragments.