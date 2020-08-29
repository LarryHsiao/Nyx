# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**

# Needed by google-play-services when linking against an older platform version
-dontwarn com.google.android.gms.**

# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path

# Suppress notes on LicensingServices
-dontnote **.ILicensingService

# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe

-keep class org.h2.**{*;}
-keep class com.haibin.calendarview.**{*;}

-keep class com.larryhsiao.nyx.old.jot.JotMonthView { *; }
-keep class com.larryhsiao.nyx.old.jot.JotWeekView { *; }
-keep class com.larryhsiao.nyx.old.jot.SelectRangeMonthView { *; }
-keep class * extends com.haibin.calendarview.MonthView {*;}

-keep class androidx.exifinterface.media.ExifInterface { *; }

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception