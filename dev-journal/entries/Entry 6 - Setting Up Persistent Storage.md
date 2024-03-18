# Purpose
I want to set up persistent storage in order to save previous track recommendations and to retrieve them for HistoryFragment's dataset.

# Changes
### Added Room Database
After weighing the [options that Android suggests for persistent storage](https://developer.android.com/training/data-storage) I decided to go with the [Room library](https://developer.android.com/training/data-storage/room) which provides an abstraction layer over an SQLite database.

I am only storing 30-50 "Tracks" where each track contains a number of strings: Title, Artist, Spotify ID, Spotify URI, Cover Art URL. This isn't a lot of data, but it was recommended in the guide to use a database for data with more than two columns.
### Utilizing Room Database
- Upon receiving the Song of the Day recommendation, insert the track information into the database.
- Get list of tracks from the database when populating the History dataset.
- Added a new int id field to use as the primary key for the database. The former id field (which was a String representing the song's ID according to Spotify) is now called spotifyId.
	- As the new int id is auto-incrementing and rows would be added every day, I wondered if there would be an issue when running into the max value for an int. But the max value of an int is 2,147,483,647, and there are only 36,525 days in 100 years, so I think it will be fine. I could also use an unsigned int or a BIGINT if we ever discover how to stay alive for millions of years.
### Refactoring - Added new folder called db
Moved AppDatabase, Track, TrackDAO, and TrackService to a new folder called db.
### Refactoring - TrackService now mediates database connections
Previously, TrackService's only purpose was to speak with Spotify's WebAPI. Now, TrackService also uses a TrackDAO object to mediate database operations by handling business rules (like ensuring that the number of rows in the database does not exceed a certain number when inserting a track). This change was made to minimize duplication of code and to simply code, but it has introduced what seems to be a multi-purpose class. I am thinking of separating TrackService into two classes, one for db mediation and one for Spotify WebAPI communication. I'm not sure what to call the new class, but TrackService seems like a proper name for a database mediator class.