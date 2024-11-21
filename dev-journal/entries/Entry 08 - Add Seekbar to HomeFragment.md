# Purpose
Add a Seekbar to HomeFragment in order to display/control the recommended track's progress as it plays.

# Changes
### Added Seekbar to HomeFragment
- Added duration to Track objects (to set Seekbar's max value)
- Added functionality to scrub through track (by changing Seekbar's value)
- Added labels for each end of the seekbar
- Added continuous updates to seekbar's value as track plays
	- Does not: only run thread if track is playing. I don't know how big of a deal this is, and it doesn't seem to cause any functional issues, but I feel it would make sense to only let the thread run when the track is actively playing.

# Updated HomeFragment UI
![[HomeFragment UI Jul22.png]]