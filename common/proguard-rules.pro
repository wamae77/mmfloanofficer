# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
#  public <init>(...);
#}

-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
  public <init>(...);
}

-assumenosideeffects class android.util.Log {
  public static *** d(...);
  public static *** w(...);
  public static *** v(...);
  public static *** i(...);
  public static *** e(...);
}


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
 -keep class * extends com.applozic.mobicommons.json.JsonMarker {
     !static !transient <fields>;
 }

 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
     !static !transient <fields>;
 }

 #GSON Config
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class com.google.gson.** { *; }

# androidX rules
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
##---------------End: proguard configuration for Gson  ----------

-keep public class com.google.android.gms.location.** { public *; }

#Keep the annotated things annotated
-keepattributes *Annotation*

#Keep the dagger annotation classes themselves
-keep interface dagger.*,javax.inject.*

#Keep the Modules intact
-keep @dagger.Module class *

#-Keep the fields annotated with @Inject of any class that is not deleted.
-keepclassmembers class * {
  @javax.inject.* <fields>;
}

#-Keep the names of classes that have fields annotated with @Inject and the fields themselves.
-keepclasseswithmembernames class * {
  @javax.inject.* <fields>;
}

# Keep the generated classes by dagger-compile
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection

-keep class androidx.datastore.*.** {*;}
-keep class res.drawable.**{*;}

-keep public class com.google.gson.** {
  public protected *;
}

-keep class retrofit.** { *; }

-keep class com.android.tools.r8.internal.mX.** { *; }
