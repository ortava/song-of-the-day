Well, I put this project on hold for quite a long time. I had other things to focus on, and I still do. I think I was trying to do too much unfamiliar work at once. 

From now on, I'm going to take a more free-form approach to the completion of this project. I started a prototype for the purpose of refreshing my knowledge on Android app development and for the purpose of learning how to implement Spotify's WebAPI. In its current form, it successfully authorizes the Spotify user, makes a call to the the Spotify WebAPI, and utilizes multiple activities and fragments.

Key points about the prototype so far:
- Has three sections in the form of fragments attached to the MainActivity
	- HomeFragment: Where the song of the day goes.
	- HistoryFragment: Where songs from previous days go.
	- SettingsFragment: Where settings options will be displayed.
- Uses Spotify Android SDK which includes:
	- spotify-app-remote: For playing tracks via the spotify app
	- auth-lib: For receiving the authorization token that is required for Spotify WebAPI calls
- Uses Volley to help make Spotify WebAPI calls.

At this point, I have my eye on a few major aspects before the prototype is in a good place:
- Create a decent layout for each fragment and some functionality to interact with the layout. 
- Find how how data for the history section of the app will be stored persistently.
- Figure out how settings will work and be stored.