# So Much Simplicity (SMS)
<img src="https://github.com/dhruvika/LVTexting/blob/master/images/smsLogo8.png" width="20%">
This app was written in Java and developed using Android Studio, which is the IDE that we recommend all users and developers make use of when adding updating/adding this app to a device. SMS is currently only available for Android devices, and there are presently no plans to build an iOS version of it. As such, SMS can only be download to Android devices running version 4.0.3 (API 15) or higher.

## Installation on Device

We plan on getting (SMS) onto the Google Play store for easy download and installation, but until then, the only option for installation on a device is to download and build the project in Android Studio, and then download the app to your device, after making the phone a development device and turning on USB-debugging.

To install (SMS) on your Android device you must do the following:
1. Install Android Studio a compatible computer. Android Studio can be downloaded [here](https://developer.android.com/studio/ "Android Studio")
2. Download this repository (SMS) as a ZIP file
3. Extract the downloaded ZIP file into a folder
4. In Android Studio, go to `File -> New -> Import Project` and import the folder containing the downloaded ZIP file contents. This should open the project in Android Studio
5. Follow the instructions [here](https://developer.android.com/studio/run/device "Run apps on a hardware device") to setups your device for development and run the app on your device.

The app must be set as your default SMS app in order access features such as sending deleting messages, so when prompted to do so, tap "Yes". With that, (SMS) should be fully functional as your SMS app.

Be sure to set SMS as your default texting app in order to avail of all of the built-in features (including clearing a chat, getting notifications, and making proper use of the read/unread token).

Troubleshooting Advice:
Ensure that the USB cable you are using to connect your phone to your laptop is not broken. Ensure that your phone is on while trying to download the app to it, and make sure you indicate that you trust the computer attempting to connect to your device, on the phone's screen. If the device does not show up in Android Studio, try unplugging and then replugging the device. If the problem persists, unplug and replug the device while simultaneously restarting Android Studio.

## How to Use Guide

The app has a couple of different screens that can be accessed and used. They are explained here:

### Dashboard Screen
<img src="https://github.com/dhruvika/LVTexting/blob/master/images/dashboardInverted.png" width="40%">

The dashboard contains a list of the current conversations in the inbox. Each row has a white dot if there are unread messages in the conversation, otherwise nothing. It also contains the name of the person the conversation is with if they are in the user's contacts otherwise their phone number. Finally, the row contains the date time of the most recent communications. The dashboard is sorted such that all unread messages are ordered before all read messages. Within the unread and read sections, each message is ordered based on recency.

To delete any conversation, a user can long press on the corresponding row in the dashboard and then click on the Delete button that pops up under the row. Finally, the dashboard also supports push notifications. On receiving a text, a notification will pop up on the screen. On clicking the notification, the user will be directed to the dashboard where they can select the unread messages they want to read. If the user is on the dashboard when the notification is received, the dashboard will auto-refresh displaying the newly received messages on top.

Pressing the readaloud button on the top right of the screen will read aloud the names and dates of the different conversations, beginning with unread messages and then continuing to read messages. While the conversations are being read, pressing the GO button will redirect the app to the conversation that is being read at that time.

### Conversation Screen
<img src="https://github.com/dhruvika/LVTexting/blob/master/images/conversationInverted.png" width="40%">
The conversation screen is the portion of the app that represents the actual contents of the messages exchanged between the user and their contact. As such, SMS is able to parse conversations between the user and any US number, at the moment. In order to add support for numbers with non +1 country codes, a developer would have to add in cases to support checking all possible ways a phone number can be represented in that country.

SMS then is able to query the phone's built-in message database, and displays all messages that the user sent and received from this contact. All messages are separated using a blank line, and all user sent messages are right justified, while all received messages are left justified. All messages are wrapped, such that even if they take up more than one row, the entire message will be represented.

Messages can be scrolled through either discretely (using the up and down arrows) or continuously (by dragging a finger up and down). By tapping on a message once, the timestamp associated with that message appears. With another tap, the timestamp disappears. Additionally, in order to copy the contents of a message, the user can long-press on a message, and it will be added to their clipboard, ready to paste.

At the bottom of the conversation screen, the user can input a message and send it off to the current contact. In the event that the user is composing a new SMS, a field in the center-top of the screen will appear, where the user can type in a contact name, or a number, to send the message off to. Finally, in the top-left of the screen, the user can call the contact.

Pressing the readaloud button on the top right of the screen will read aloud all of the messages that are present on the screen, distinguishing between messages that are sent and received, with a "you said" before all messages you sent, and a "they said" before all messages you received.

### Settings Screen
<img src="https://github.com/dhruvika/LVTexting/blob/master/images/settingsInverted.png" width="40%">
Features a text preview in the top third of the screen and a number of settings in the bottom two thirds of the screen. Each setting has an "Increase" button on the right and a "Decrease" button on the left, with an up and down facing arrow respectively. These will increase or decrease the relevant setting, changing the apps global settings, as well as the text preview on the screen. Pressing the readaloud button on the top right of the screen will read aloud all of the available settings in order.

### Search Screen
<img src="https://github.com/dhruvika/LVTexting/blob/master/images/searchInverted.png" width="40%">
Features a search bar and search button. Pressing the search button will fill the screen with conversations containing the query input in the search bar.

## Development Guide

To get started developing SMS, all you need to do is follow steps 1-4 in Installation on Device. Now you're ready to start developing!

For those interested in developing the app, but unsure where to begin, [here](https://developer.android.com/guide/slices/getting-started "Android Getting Started") is Android's Getting Started page, where you can get started with Android app development.

Once you have downloaded Android Studio, you need to import the Github repository into the environment. Follow the steps below:
1. In Github click the "Clone or download" button of the project 
2. Download the ZIP file and unzip it. 
3. In Android Studio Go to File -> New Project -> Import Project and select the newly unzipped folder -> press OK.

<img src="https://github.com/dhruvika/LVTexting/blob/master/images/android.png" width="80%">

<img src="https://github.com/dhruvika/LVTexting/blob/master/images/android.png" width="60%">




