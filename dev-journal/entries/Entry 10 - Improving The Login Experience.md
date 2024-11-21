# Purpose
There are two major issues I want to address about the state of the login experience:
- The user can log in, but they can't log out.
- The app always launches to the login screen, even if the user is already logged in.

# Major Changes
### Added button in SettingsFragment's PreferenceScreen to allow the user to disconnect Spotify account
- The following post helped me figure out how to add a button to the PreferenceScreen that SettingsFragment uses: https://stackoverflow.com/questions/5298370/how-to-add-a-button-to-a-preferencescreen
	- Hint - the button has to be treated like a preference
- Created a custom preference class specifically for the disconnect button. This allowed the utilization of the button's onClick rather than the preference's onClick
	- The preference's onClick was much easier to implement (no custom preference class required), but I wanted the user to have to click the button so the action is more clearly distanced from the other preferences.
### Replaced old authorization flow with the PKCE flow (Single Sign-In)
This is a change I did not expect to have to make when starting this leg of development. I had to completely overhaul the authorization flow with Spotify so the user won't have to log in every time they want to use the application.
- The old authorization flow utilized the Spotify Android SDK's auth library to easily allow the user to authorize their Spotify account with our app. Unfortunately, there does not appear to be a way to retrieve a "refresh token" through that library, meaning the user had to re-authorize every hour as the "access token" they received expires after only 1 hour. Access tokens are required to utilize most of the Spotify Web API's functionality.
- The PKCE flow (see: https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow) allows us to receive both access tokens and refresh tokens. The refresh tokens are later used to acquire fresh access tokens whenever we need to utilize the functionality of the Spotify Web API.
	- The following post helped me implement the "code verifier" and "code challenge" aspects of the PKCE flow in Java (even though the post uses Kotlin): https://stackoverflow.com/questions/68750229/how-to-impement-spotifys-authorization-code-with-pkce-in-kotlin
	- It should be noted that Spotify's auth library appears to still be necessary for proper functionality, although I'm not exactly sure why.
### Changed Activity flow based on Spotify connection status
- The app now launches directly to MainActivity, and checks for a Spotify refresh token. If the token is present, continue as usual (the user will only have a refresh token if they have authorized their account). If the token is not present, redirect to PrescreenActivity (prompting user to connect their Spotify account).

# Notes
- At the time of writing this, the app is currently written to refresh the access token when an authorization error occurs in a request to the Spotify Web API. This can also be done by creating a custom RetryPolicy that requests a new token before retrying the original request (say, to get a recommendation), but I chose to use nested callbacks instead because it is more predictable as the RetryPolicy method relies on the refresh request to finish before the original request hits the max number of retries. With that said, the RetryPolicy method is much cleaner code-wise, so I may implement it at a later time.
- Initial authorization requires the user's browser to open. An unfortunate side effect of the current implementation leaves an empty browser tab open, though I'm sure this can be fixed.
- Sometimes the SpotifyAppRemote fails to connect due to the connection request taking too long.
- (Not yet implemented) The history should be cleared upon logging out. Right now, it is useful to keep the history for testing purposes.