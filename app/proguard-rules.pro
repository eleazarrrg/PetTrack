# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class ** {
    @kotlinx.serialization.Serializable <methods>;
}
-keep,includedescriptorclasses class com.pettrack.app.**$$serializer { *; }
-keepclassmembers class com.pettrack.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.pettrack.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature, Exceptions

# osmdroid
-dontwarn org.osmdroid.**
