# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep JCIFS-NG classes
-keep class jcifs.** { *; }
-dontwarn jcifs.**

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Keep metadata extractor
-keep class com.drew.** { *; }
-dontwarn com.drew.**

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep data classes used with Gson
-keep class com.mybackup.smbsync.data.model.** { *; }
