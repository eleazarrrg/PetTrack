package com.pettrack.app.fakes

import com.pettrack.app.data.remote.api.PetApi
import com.pettrack.app.data.remote.dto.PetDto
import com.pettrack.app.data.remote.dto.PetInsertDto
import com.pettrack.app.data.remote.dto.PetPatchDto
import com.pettrack.app.data.remote.dto.PetPhotoDto
import com.pettrack.app.data.remote.dto.PetPhotoInsert
import com.pettrack.app.data.remote.dto.PetPutDto
import com.pettrack.app.data.remote.dto.SightingDto
import retrofit2.Response

class FakePetApi : PetApi {
    var pets: List<PetDto> = emptyList()
    var photos: List<PetPhotoDto> = emptyList()
    var sightings: List<SightingDto> = emptyList()
    val deletedIds = mutableListOf<String>()

    override suspend fun getPets(ownerIdEq: String?, select: String, order: String): List<PetDto> = pets
    override suspend fun getPet(idEq: String, select: String): List<PetDto> = pets
    override suspend fun createPet(body: PetInsertDto, select: String): List<PetDto> = pets
    override suspend fun replacePet(idEq: String, body: PetPutDto, select: String): List<PetDto> = pets
    override suspend fun updatePet(idEq: String, body: PetPatchDto, select: String): List<PetDto> = pets
    override suspend fun deletePet(idEq: String): Response<Unit> {
        deletedIds.add(idEq)
        return Response.success(Unit)
    }
    override suspend fun addPhoto(body: PetPhotoInsert): Response<Unit> = Response.success(Unit)
    override suspend fun getPhotos(petIdEq: String, select: String, order: String): List<PetPhotoDto> = photos
    override suspend fun getSightings(petIdEq: String, select: String, order: String): List<SightingDto> = sightings
}
