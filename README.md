# FRCScoutingApp
An Android app (written in kotlin) to help make scouting easier for all teams!

## Configurable
This app is completely configurable for your team. Simply head to the getData() function in [app/src/main/java/com/team5417/frcscouting/ScoutingActivity.kt](https://github.com/frc5417/FRCScoutingApp/blob/main/app/src/main/java/com/team5417/frcscouting/ScoutingActivity.kt).

### Requirements:
* MatchAndTeamNum() must be included, preferably first.
* Each data entry must have a unique id (should be as short as possible to minimize QR code size)

### Current Options:
* Header - Simply a separator line of static text.
* Number - A number with a label. Can be changed with buttons or with text entry. Has optional starting value, min, max, and step variables.
* Slider - Slider with configurable min, max, step, and starting values.
* Checkbox - Boolean value with optional starting value.
* Text - Text box with optional starting value.

Note: In the future, we hope to make configuring not require compiling like our Data Analyzer.

## Data Analyzer
Pairs with the https://github.com/frc5417/FRCScoutingAnalyzer desktop app to gather data in a centralized place and easily analyze it.

## How to Install
1. Download the latest apk from releases (or generate your own - see below) onto an Android phone.
2. Open the apk file (might have to enable developer settings).
3. Click "Install" when prompted.
4. Run the app!

## How to Build
1. Clone the repository
2. Open in Android Studio 2022
3. Build tab -> Generate APK(s)

## Data Transfer
Since wireless communications are disallowed [(E301)](https://www.firstinspires.org/sites/default/files/uploads/frc/EventRulesManual.pdf), this app generates QR codes that hold the data the scouters gather. It stores the data in text, not as images to increase the number of rounds it can hold before impacting performance.

## Features
### Auto Gather Team # to Scout
Will automatically set the team # once the match number has been set if the setting is enabled.

Requires:

* A Blue Alliance API Key [app/src/main/res/values/strings.xml](https://github.com/frc5417/FRCScoutingApp/blob/main/app/src/main/res/values/strings.xml)
* One TIme Wifi Connection
* Set Event & Team to Scout (e.g., Blue 1) in the Settings Menu

### Auto Increment Match Number
Will automatically incrememnt the match number after export.