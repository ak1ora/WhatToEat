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

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends dagger.hilt.android.HiltApplication

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class *
-keepclassmembers @kotlinx.serialization.Serializable class * { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }

# Coil
-keep class coil.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }

# Prevent obfuscation of model classes
-keep class com.example.whattoeat.domain.model.** { *; }
-keep class com.example.whattoeat.data.api.dto.** { *; }
-keep class com.example.whattoeat.data.db.** { *; }