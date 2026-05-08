# ── Stack traces ──────────────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Kotlin ────────────────────────────────────────────────────────────────────
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Metadata { *; }

# ── kotlinx.serialization ─────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

-keep,includedescriptorclasses class com.cvp.app.**$$serializer { *; }
-keepclassmembers class com.cvp.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.cvp.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all @Serializable annotated classes
-keep @kotlinx.serialization.Serializable class * { *; }

# ── MapLibre Maps SDK ─────────────────────────────────────────────────────────
-keep class org.maplibre.** { *; }
-dontwarn org.maplibre.**

# ── Koin ──────────────────────────────────────────────────────────────────────
-keep class org.koin.** { *; }
-keepnames class * { @org.koin.core.annotation.* *; }

# Prevent R8 from removing Koin module objects declared as `val` in top-level files
-keepclassmembers class com.cvp.app.di.** { *; }

# ── Jetpack Compose ───────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── DataStore ─────────────────────────────────────────────────────────────────
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# ── Timber ────────────────────────────────────────────────────────────────────
# Timber.d/e/w are stripped from release builds only if explicitly desired.
# We keep Timber classes but Timber.DebugTree is not planted in release (see CvpApplication).
-keep class timber.log.Timber { *; }
