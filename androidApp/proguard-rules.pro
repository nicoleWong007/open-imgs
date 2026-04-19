# OpenImgs ProGuard Rules

# SQLDelight
-keep class com.openimgs.shared.database.** { *; }
-keep class app.cash.sqldelight.** { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.openimgs.shared.**$$serializer { *; }
-keepclassmembers class com.openimgs.shared.** { *** Companion; }
-keepclasseswithmembers class com.openimgs.shared.** { kotlinx.serialization.KSerializer serializer(...); }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Coil
-dontwarn coil.**
