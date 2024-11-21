## External Context
I graduated with a Bachelor's Degree in Computer Science in May 2022. I'm now searching for a job as an entry-level software engineer. Job searching takes much higher priority over the development of this project, so don't expect this project to be developed as if I was working on it full-time.

___

## What is this project?
Song of the Day - SOTD (working title) is a mobile app that serves one song recommendation per day to the user. The app integrates with a streaming service API to get relevant user data and generate song recommendations that fit the user. As the user continues using SOTD, their recommendations will improve. The user can also adjust settings in the SOTD app to alter their future song recommendations.

It's possible that the plan will change to serve more than one song per day, or at least allow the ability to refresh your song recommendation. Whatever the case, there will be a limitation on the number of songs you can be recommended per day.

The app is planned to exist in separate Android and iOS versions, both with Spotify and Apple Music integration. However, due to the circumstances surrounding this project, the initial focus will be to create an Android app that integrates with Spotify.

## Why create this project?
* **As a developer**
My main goal with this project is to improve in areas of software development that I am particularly weak in. Therefore, I am focusing on **developer documentation, testing, automation, and general organization skills in the dev-ops realm**. I've also never made a full-fledged **mobile app** or **integrated an API**. All of this experience would be nice to have for my resume, job interviews, and general ability as a developer.

* **As a user**
I want a simple app that helps me develop my taste as a music listener. While Spotify is a great place to discover new music, especially for those who know where to look, sometimes I don't want to have to look. What if the song found me? I'd like to receive a push notification that lets me know that I have a new song that might be for me. If I like the song, I can explore that artist on Spotify and possibly find more music to save or put into a playlist. In my experience, one good song can cause a domino effect of discovery.

___

## Work so far
I worked on the foundation of this project about three weeks prior to writing this journal entry. Here's what I recall about what I've done so far:

### Set up a Kanban Board (Trello)
##### *Why?*
I wanted a tool to help me visually keep track of tasks. Although I am working on this project alone, it's useful to be able to easily create tasks, categorize and prioritize them, then complete them.

##### *What did I do?*
I successfully set up the Kanban board and completed tasks regarding planning and set-up.
The available lists are:
* **Backlog** - For tasks that are still being conceptualized.
* **To Do** - For tasks that are ready to be worked on. Prioritize tasks at the top of the list.
* **Doing** - For tasks that are currently being worked on.
* **Testing** - For programming tasks that may be done, but need to pass a testing process first.
* **Done** - For tasks that are complete.
* **Design [Complete]** - A place for completed design document tasks attached with the corresponding document and research tasks containing the results of the research.

##### *Where did I struggle?*
I need a better sense of how long it will take me to complete a task when setting its due date.

### Functional Requirements Document (FRD)
##### *Why?*
I've learned about FRDs in school, but never had to formally make one, so I wanted to practice. They are useful for developing a more complete vision of your concept.

##### *What did I do?*
I did some research online and followed guides/templates.

* ["Functional requirements examples and templates" - JamaSoftware](https://www.jamasoftware.com/requirements-management-guide/writing-requirements/functional-requirements-examples-and-templates)
* ["What is a Functional Requirements Document (FRD) I Introduction & Tips" - BusinessAnalystMentor](https://businessanalystmentor.com/functional-requirements-document/)
* ["Introduction to Gathering Requirements and Creating Use Cases" - CodeMag](https://www.codemag.com/article/0102061/Introduction-to-Gathering-Requirements-and-Creating-Use-Cases)

The result is the first version of an FRD for this project. Version 1.0 of the FRD includes sections for Project Description, Background, Purpose, Scope, Points of Contact, Functional Requirements, and Non-functional Requirements.

##### *Where did I struggle?*
Writing requirements, differentiating between functional and non-functional requirements, completeness of requirements. I think it's very likely that v1.0 is missing multiple functional and non-functional requirements that should be documented. 

Also, v1.0 seems to be barebones compared to the templates I've seen. This is partly because I didn't know how to write all of the sections described in the templates. Instead of trying to do all of it correctly in one go, I decided to focus mainly on gathering the requirements.

### Use Case Document
##### *Why?*
My reasons for creating the Use Case Document are similar to my reasons for creating the FRD. I wanted to practice formal developer documentation. Writing these use cases also helped me understand how the app will look from the user's point of view.

##### *What did I do?*
I did some research online and followed guides/templates.

* ["Use Cases" - Usability](https://www.usability.gov/how-to-and-tools/methods/use-cases.html)
* ["Introduction to Gathering Requirements and Creating Use Cases" - CodeMag](https://www.codemag.com/article/0102061/Introduction-to-Gathering-Requirements-and-Creating-Use-Cases)

I followed CodeMag's template pretty closely. It describes use cases through text with the following sections for each use case:

* **Overview** - What scenario is described in this use case?
* **Actors** - What actors are involved in the scenario? (users, external systems/parties)
* **Pre-conditions** - What must be true before this scenario can take place?
* **Scenario** - The step-by-step process by which the scenario plays out. For each step in the scenario, an actor must make an action (stimulus) that the software reacts to.
* **Post-conditions** - What must be true after this scenario takes place?
* **Exceptions** - What will cause the scenario to fail to complete?
* **Dependencies and Relations** - What use cases does this use case depend on or relate to?

##### *Where did I struggle?*
Like with the requirements document, I think I am likely missing use cases that ought to be documented. There are seven use cases described in this document, which seems like a low amount to me, but I didn't see any other use cases to write.

Perhaps my greatest struggle was writing the scenario actions and reactions. I wrote actions like "User presses the 'Settings' button," and reactions like, "System navigates to the Settings view," but I'm not sure if I'm supposed to be giving names to controls and such in the use cases. Should I even be describing how the user navigates to the settings menu? 

Finally, writing the use case for a song being automatically recommended based on a timer was hard for me. It's basically *the* reason a user will use the app, but the user doesn't have to do anything to receive a recommendation. Could I consider a timer to be an actor? That's what I chose to do, but It's possible that this scenario shouldn't be a use case in the first place.

___

## What's next?
Next, I will be making a Class Diagram. That's another type of documentation that I want to practice, and it will be my last for this project. Otherwise, I only intend to update the design documentation I've already made as the project progresses.

___
Look forward to the next entry!
(It will be a lot shorter. Less catching up to do!)