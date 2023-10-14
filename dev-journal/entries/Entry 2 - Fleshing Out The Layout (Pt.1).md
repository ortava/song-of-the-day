For the week of October 8 - October 13, 2023
### Summary
The goal this week was to get the layout of the prototype into a presentable state. I would say that this goal is about half-way complete, but I was able to implement some important foundational components of the layout on the History screen (scrolling list with RecyclerView) and the Settings screen (converted to built-in PreferenceFragmentCompat).

### History Screen
##### What was done:
- Added a RecyclerView
	- This required the creation of an Adapter class to build the views used by RecyclerView
##### What's missing:
- Add a button/link to each row that allows you to open the given song in Spotify
- Only display a row that has data to populate it

### Settings Screen
##### What was done:
- Added a preference layout (preferences.xml) with a PreferenceScreen
- Changed SettingsFragment from a Fragment into a PreferenceFragmentCompat (which is still a fragment, but specialized for app preferences)
##### What's missing:
- Proper representation of the settings-to-be

### Home Screen
##### What was done:
- Minor adjustments
##### What's missing:
- Home Screen is missing fundamental layout elements
