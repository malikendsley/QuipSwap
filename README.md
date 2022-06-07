# QuipSwap

QuipSwap is an image-sharing app where you can draw images to be shown directly on your friends' homescreens. Users can create an account, add their friends, and use the app's built in canvas system to draw pictures to send to their friends. They also have the option of saving the images for later. When configuring the companion widget for QuipSwap, users have the option of choosing which friend's Quips they would like to see.

![Light mode canvas screenshot](https://firebasestorage.googleapis.com/v0/b/quipswap.appspot.com/o/showcase-images%2Flight-mode-canvas.jpg?alt=media&token=aa7b3fcb-1d04-4593-b616-b0a57b05abab) &emsp; &emsp; ![Dark mode canvas screenshot](https://firebasestorage.googleapis.com/v0/b/quipswap.appspot.com/o/showcase-images%2Fdark-mode-canvas.jpg?alt=media&token=73a62e7e-1b3d-4bc5-9752-7fe7a777a29f)

^ The canvas in action

# Built With

### Native Android/AndroidX

QuipSwap was created for Android, written in Java and built with Gradle using the Android / AndroidX libraries.

The built-in canvas is a custom design adapted from the Paint class. Finger painting is implemented by overriding the touch events on a defined Paint View. The canvas has 6 colors, 2 brush types, erase, undo / redo, and clear capabilities. Users can also decide to save their drawing locally instead of sharing it with another user. This also doesn’t require that they be logged in, so users are free to demo the canvas system or forgo the social aspect entirely and have a simple drawing app.

The app uses various pieces of the Android library including Recycler views for high performance when displaying Quips, new AndroidX style AlertDialogs and Snackbars, as well as a custom centralized CRUD class to separate database access from UI logic. This also made maintenance and testing easier as database accesses were not scattered throughout the app.

The app uses an internal Alarm to keep the widgets on the home screen updated. The alarm is set up so that the Widgets containing Quips are only updated while the device is awake. The frequency of updates is configurable in the settings to cater to users concerned about battery. 

The app also uses BubbleShowCase-Android with modernizations and syntax error fixes (removing deprecated API usage, for example). These bubbles show a tutorial on first time startup or when help is needed.

### Firebase Authentication

Handles user registration, account setup, and security. When users are registered, their UID and username are also added to the database in a few places. Usernames are stored in an index in addition to in a profile in order to enable quickly checking whether a username is taken.

### Firebase Realtime Database

Maintains user records and metadata about Quips. Most documents in the RTDB are split into Public and Private portions, linked by a key if necessary. Quips are indexed on Recipient and Sender. Data is denormalized in a few ways to make reading scalable. A centralized CRUD system in the app mitigates the drawback of cumbersome writes and updates. Since data is rarely changed due to the nature of the app, this is a good tradeoff in my opinion.

### Firebase Storage

Contains bulk image data. According to Firebase, the URIs of each image is not guessable, so the data is accessible only by name and by authenticated users. The URI is stored in the Private half of each Quip. Images are compressed on the app according to the user's preferences before being uploaded. However, given the 6 color canvas setup and the simplicity of the images, their size will never be an issue except at extremely large scale. 

# About Me

My name is Malik Endsley, and I am a CS student attending Columbia University. You can learn more about me on my [LinkedIn](https://www.linkedin.com/in/malik-endsley) page.
