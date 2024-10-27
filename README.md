
# Photo Edit App

This project is a photo editing application built using **Kotlin** and **Firebase** for storage. It integrates with the [PhotoEditor SDK](https://github.com/burhanrashid52/PhotoEditor) to provide a range of editing features and Firebase for seamless image upload and storage.

## Features

1. **Capture Photos**
   - Allows users to capture photos directly from the app.

2. **View Image Gallery**
   - Displays a list of all saved images for easy access and management.

3. **Basic Photo Editing**
   - Provides essential editing tools like crop, rotate, filter, and more for basic photo enhancements.

4. **Firebase Storage Integration**
   - Enables users to upload and store images in Firebase for secure, scalable storage.

5. **Customizable Functionality**
   - The app is designed to be extensible, allowing further features and improvements based on user ideas or requirements.

## Installation and Setup

1. Clone the repository:

   ```bash git clone https://github.com/T04N/photo_edit

   ```
2. Open the project in Android Studio.

3. Set up Firebase:
   - Register the app with Firebase and download the `google-services.json` file.
   - Place `google-services.json` in the `app` directory.
   - Follow Firebase's setup instructions for connecting to the Android project.

4. Add PhotoEditor SDK:
   - Include the PhotoEditor SDK in your `build.gradle` file.

5. Build and run the project.

## Usage

1. **Capture a Photo**: Use the camera feature to capture a new photo.
2. **View Gallery**: Access the gallery to view all previously saved images.
3. **Edit a Photo**: Select an image to enter the editor and apply basic edits.
4. **Upload to Firebase**: Save your edited photo to Firebase Storage for easy retrieval and sharing.

