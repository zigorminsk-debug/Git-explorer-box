# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   https://developer.android.com/studio/build/shrink-code.html

-dontnote androidx.**
-dontwarn androidx.**
-keep class com.gitexplorerbox.app.** { *; }
-keep class org.json.** { *; }
