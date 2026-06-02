# ============================================================================
# GoService R8 / ProGuard rules
#
# AGP 8+ uses R8 in full mode by default. Most libraries (Room, WorkManager,
# Coroutines, Firebase, Yandex Ads, Compose) ship their own consumer rules via
# their artifacts, so this file only covers what R8 cannot infer on its own:
# reflection-driven kotlinx.serialization and type-safe Navigation routes.
# ============================================================================

# --- Crash reporting: keep readable stack traces -----------------------------
# The Crashlytics Gradle plugin uploads the mapping file to deobfuscate, but we
# still keep source/line attributes; -renamesourcefileattribute hides originals.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization ---------------------------------------------------
# Annotations must survive so the runtime can find generated serializers.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Keep the auto-generated `$serializer` and the `Companion.serializer()` of any
# @Serializable type (DTOs in :shared and the navigation routes below).
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
# Keep `INSTANCE.serializer()` for @Serializable objects (e.g. data objects).
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Jetpack Navigation Compose: type-safe routes ----------------------------
# Routes are @Serializable and resolved reflectively by Navigation; keep the
# whole route hierarchy so arg classes and their serializers are not stripped.
-keep class com.zrifapps.goservice.navigation.Screen { *; }
-keep class com.zrifapps.goservice.navigation.Screen$* { *; }

# --- WorkManager workers backing notifications -------------------------------
# WorkManager instantiates these by class name; its consumer rules cover the
# generic case, but these are notification-critical so we keep them explicitly.
-keep class com.zrifapps.goservice.core.notification.OdometerReminderWorker { *; }
-keep class com.zrifapps.goservice.core.notification.ReminderNotificationWorker { *; }
