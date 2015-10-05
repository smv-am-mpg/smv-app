# smv-app
[![Build Status](https://travis-ci.org/smv-am-mpg/smv-app.svg?branch=master)](https://travis-ci.org/smv-am-mpg/smv-app)

# Prerequisites
- Android SDK Manager
- Gradle
- Git
- Build Tools Version : 23.0.1
- Sdk : 23

# Android Studio
Yeah, some how clone this project from git and then import it ...

# Eclipse 
1. Import -> Git -> Projects from Git -> Clone URI -> (URI is https://github.com/smv-am-mpg/smv-app.git) -> Next -> Next -> (Directory should be in your working-directory) -> Next -> Cancel
2. Import -> Gradle -> Gradle Project -> select the directory you used above -> Build Model -> Finish
3. Import the support-v4, appcompat-v7 and design lib as described [here](https://developer.android.com/tools/support-library/setup.html#libs-with-res)
4. The design lib needs special care: Properties (of design project) -> Java Build Path -> Libraries -> Add Jars... -> android-support-v7-appcompat -> libs -> android-support-v4.jar and android-support-v7-appcompat.jar
5. Now click "Order and Export" -> Uncheck android-support-v4.jar and android-support-v7-appcompat.jar
6. You should be done :)

# Using git
You are not allowed to directly push commits to the master branch!
You have to create a Pull Request, so that we can review it.

Ask intrigus, how to do that :)
