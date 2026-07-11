package com.pettrack.app.core.common

import com.pettrack.app.BuildConfig

/** Public URL for an object in the public `pet-photos` bucket (loadable by Coil). */
fun publicPhotoUrl(storagePath: String): String =
    "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/pet-photos/$storagePath"
