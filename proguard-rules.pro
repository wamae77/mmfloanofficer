# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn java.awt.**,javax.activation.**,java.beans.**

-dontwarn android.support.**
-dontwarn xxdroid.support.**

-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}

-keep class com.newpos.** { *; }
-keep class com.android.newpos.** { *; }
-keep class cn.desert.** { *; }

-keep class com.desert.** { *; }

-keepclassmembers class com.eclectics.agency.nbkpos.remote.ApiConnection { *; }


-keep class cz.msebera.android.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.loopj.** { *; }

-keepclassmembers class com.dom925.xxxx {
   public *;
}

-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}
-dontwarn javax.mail.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.octo.android.robospice.SpiceService
-dontwarn android.support.v4.**


