# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/justinliu/Maaii/Android/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
# }

#====================================================
# Config
#====================================================
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Use this to turn off/on code obfuscation
#-dontobfuscate

-keepattributes *Annotation*

# Used to retain line numbers for stack traces
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Retain Keep annotation so that they are still around during obfuscation phase
-keep,allowoptimization,allowobfuscation @interface proguard.annotation.*

###############################
#          Enumeration        #
###############################
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

###############################
#          Parcelable         #
###############################
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

###############################
#         Serializable        #
###############################
-keep class * implements java.io.Serializable {
    public void set*(***);
    public *** get*();
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

###############################
#            Native           #
###############################
-keepclasseswithmembernames class * {
    native <methods>;
}

###############################
#            Android          #
###############################
-keep class **.R$* { *; }
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep class android.support.v7.widget.SearchView { *; }

###############################
#          Google Play        #
###############################
-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}
-keep,includedescriptorclasses public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

###############################
#   M800PhoneVerificationSDK  #
###############################
-keep class com.m800.phoneverification.api.** {
    <fields>;
}

###############################
#            M800SDK          #
###############################
-dontwarn sun.misc.Unsafe
-dontwarn ch.qos.logback.core.net.*
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn com.maaii.account.*

-keep class com.m800.sdk.*M800** {
    <fields>;
    <methods>;
}
-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }

-keepclasseswithmembers class com.m800.msme.android.M800AndroidIntegration { public *; }
-keepclasseswithmembernames class media5.m5t.sce.demo.CSceDemoLibrary { public *; }
-keep class com.m800.msme.jni.** { *; }
-keep class org.webrtc.** { *; }

-keepattributes Signature
-keep,includedescriptorclasses class org.jivesoftware.smack.sasl.** { public *; }
-keep,includedescriptorclasses class de.measite.smack.AndroidDebugger {  public *; }
-keep,includedescriptorclasses class org.jivesoftware.smack.maaii.MaaiiAndroidDebugger { public *; }
-keep,allowoptimization,allowobfuscation @interface com.fasterxml.jackson.annotation.*
-keep class com.fasterxml.jackson.** { *; }

###############################
#    Demo app dependencies    #
###############################

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Android support
-dontwarn **CompatHoneycomb
-dontwarn android.support.design.**
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-keep public class * extends android.support.v4.app.Fragment
