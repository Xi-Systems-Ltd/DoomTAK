Welcome to the ATAK SDK Developer Guide.  Please see ATAK_Plugin_Development_Guide.pdf


The SDK is branched as expected by the autobuilder.   For example if you would like to 
build against 3.12, you would need to checkout the maintenance-3.12 branch.   The braches 
have easily identifiable markings and correspond with the released versions of ATAK.

**WARNING ADDITIONAL STEPS**
This repository now makes use of git-lfs and you will need to enable that capability by installing 
[git-lfs](https://github.com/git-lfs/git-lfs/wiki/Installation)

Please note that the ability to download a zip snapshot to a snapshot should not be used because of this bug -
[bug desc](https://gitlab.com/gitlab-org/gitlab-foss/issues/14261)


In Android Studio - remember to select your appropriate build variant. default buildVariant otherwise you will build ausDebug by default.   This is because Android Studio alphabetically sorts the flavors and builds the first one by default.    This feature can be found under Build->Select Build Variants.
If you are having trouble building flavors, please delete the .iml files, the .idea directory after you have closed out of the project.  Then reimport it.   This seems to be an issue in the 3.5 series of Android Studio.

min/target SDK
===

At this time there are no requirements that a plugin needs to target the same minSDK as ATAK.    Please choose a min/target that best fits for your deployment.

Espresso Testing
===

All plugins will shortly be able to make use of espresso testing.   In order to accomplish this, the espresso folder needs to be added to the root of your plugin folder.   The build.gradle file in the PluginTemplate has been modified to work with this directory to build and execute the espresso tests.

These tests can then be run:

     ./gradlew connected<flavor>DebugAndroidTest


   civ being the most common

     ./gradlew connectedCivDebugAndroidTest

In order to provide a better chance for succesful Espresso testing - it is encourage to disable animations on your device or virtual machine.  This can be accomplished by executing the following:

adb shell settings put global window_animation_scale 0 && adb shell settings put global transition_animation_scale 0 && adb shell settings put global animator_duration_scale 0 

Additionally, espresso test internals are tagged as ATAK_ESPRESSO_TEST in the log files.

Lambda
===

Recently it has been observed that although lambdas work just fine on a debug build, they do 
not work with a release build due to the proguard obfuscation.


