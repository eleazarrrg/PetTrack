package com.pettrack.app.ui.community

import com.pettrack.app.data.repository.CommunityRepository
import com.pettrack.app.domain.model.PetStatus
import com.pettrack.app.fakes.FakeLocationSource
import com.pettrack.app.fakes.FakePetApi
import com.pettrack.app.fakes.FakeRpcApi
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val rpcApi = FakeRpcApi()
    private val petApi = FakePetApi()
    private val location = FakeLocationSource()

    private fun viewModel(): CommunityViewModel {
        val repo = CommunityRepository(rpcApi, petApi, mainRule.dispatcher)
        return CommunityViewModel(repo, location)
    }

    @Test
    fun onInit_usesPanamaCenter_andDefault5kmRadius() = runTest {
        viewModel()
        val req = rpcApi.lastNearbyRequest
        assertNotNull(req)
        assertEquals(8.98, req!!.lat, 0.0001)
        assertEquals(-79.52, req.lng, 0.0001)
        assertEquals(5000.0, req.radiusM, 0.0001)
    }

    @Test
    fun setRadiusAndStatus_mapToRequest() = runTest {
        val vm = viewModel()
        vm.setRadius(10)
        vm.setStatus(PetStatus.PERDIDA)
        val req = rpcApi.lastNearbyRequest!!
        assertEquals(10000.0, req.radiusM, 0.0001)
        assertEquals("perdida", req.status)
    }

    @Test
    fun useMyLocation_updatesCenterFromGps() = runTest {
        location.latLng = 9.10 to -79.90
        val vm = viewModel()
        vm.useMyLocation()
        assertEquals(9.10, vm.state.value.center.first, 0.0001)
        assertTrue(vm.state.value.usingMyLocation)
        assertEquals(9.10, rpcApi.lastNearbyRequest!!.lat, 0.0001)
    }
}
