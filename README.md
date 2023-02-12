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

## Data Transfer
Since wireless communications are disallowed [(E301)](https://www.firstinspires.org/sites/default/files/uploads/frc/EventRulesManual.pdf), this app generates QR codes that hold the data the scouters gather. It stores the data in text, not as images to increase the number of rounds it can hold before impacting performance.
