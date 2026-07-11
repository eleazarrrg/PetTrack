package com.pettrack.app.data.remote.api

import com.pettrack.app.data.remote.dto.PetDto
import com.pettrack.app.data.remote.dto.PetInsertDto
import com.pettrack.app.data.remote.dto.PetPatchDto
import com.pettrack.app.data.remote.dto.PetPhotoDto
import com.pettrack.app.data.remote.dto.PetPhotoInsert
import com.pettrack.app.data.remote.dto.PetPutDto
import com.pettrack.app.data.remote.dto.SightingDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/** Scalar columns of the `pets` table (excludes the geography column, which PostgREST
 *  would return as EWKB hex). Reads use the `pets_geo` view which adds latitude/longitude. */
private const val PET_SCALARS =
    "id,owner_id,name,species,breed,approx_age,color,size,distinguishing_marks,has_collar_chip,chip_number,status,lost_at"

interface PetApi {

    @GET("rest/v1/pets_geo")
    suspend fun getPets(
        @Query("owner_id") ownerIdEq: String? = null,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
    ): List<PetDto>

    @GET("rest/v1/pets_geo")
    suspend fun getPet(
        @Query("id") idEq: String,
        @Query("select") select: String = "*",
    ): List<PetDto>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/pets")
    suspend fun createPet(
        @Body body: PetInsertDto,
        @Query("select") select: String = PET_SCALARS,
    ): List<PetDto>

    // Full-row replace — genuine HTTP PUT (single-row upsert on the id filter).
    @Headers("Prefer: return=representation")
    @PUT("rest/v1/pets")
    suspend fun replacePet(
        @Query("id") idEq: String,
        @Body body: PetPutDto,
        @Query("select") select: String = PET_SCALARS,
    ): List<PetDto>

    // Partial update (e.g. status toggle).
    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/pets")
    suspend fun updatePet(
        @Query("id") idEq: String,
        @Body body: PetPatchDto,
        @Query("select") select: String = PET_SCALARS,
    ): List<PetDto>

    @DELETE("rest/v1/pets")
    suspend fun deletePet(@Query("id") idEq: String): Response<Unit>

    @POST("rest/v1/pet_photos")
    suspend fun addPhoto(@Body body: PetPhotoInsert): Response<Unit>

    @GET("rest/v1/pet_photos")
    suspend fun getPhotos(
        @Query("pet_id") petIdEq: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "is_primary.desc,created_at.asc",
    ): List<PetPhotoDto>

    @GET("rest/v1/sightings_geo")
    suspend fun getSightings(
        @Query("pet_id") petIdEq: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "sighted_at.desc",
    ): List<SightingDto>
}
