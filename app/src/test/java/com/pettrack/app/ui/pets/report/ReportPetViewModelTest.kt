package com.pettrack.app.ui.pets.report

import androidx.lifecycle.SavedStateHandle
import com.pettrack.app.data.repository.PetRepository
import com.pettrack.app.fakes.FakeLocationSource
import com.pettrack.app.fakes.FakePetApi
import com.pettrack.app.fakes.FakeRpcApi
import com.pettrack.app.fakes.FakeSessionStore
import com.pettrack.app.fakes.FakeStorageApi
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportPetViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val petApi = FakePetApi()
    private val rpcApi = FakeRpcApi()
    private val storageApi = FakeStorageApi()
    private val session = FakeSessionStore()
    private val location = FakeLocationSource()

    private fun viewModel(): ReportPetViewModel {
        val repo = PetRepository(petApi, rpcApi, storageApi, session, mainRule.dispatcher)
        return ReportPetViewModel(repo, location, SavedStateHandle())
    }

    @Test
    fun setLocation_setsPickedLatLng() = runTest {
        val vm = viewModel()
        vm.setLocation(8.42, -80.13)
        assertEquals(8.42, vm.state.value.latitude!!, 0.0001)
        assertEquals(-80.13, vm.state.value.longitude!!, 0.0001)
    }

    @Test
    fun setLocation_overridesPreviousGpsCapture() = runTest {
        location.latLng = 9.00 to -79.50
        val vm = viewModel()
        vm.captureLocation()
        vm.setLocation(7.10, -80.90)
        assertEquals(7.10, vm.state.value.latitude!!, 0.0001)
        assertEquals(-80.90, vm.state.value.longitude!!, 0.0001)
    }

    @Test
    fun captureLocation_whenGpsUnavailable_setsError() = runTest {
        location.latLng = null
        val vm = viewModel()
        vm.captureLocation()
        assertNull(vm.state.value.latitude)
        assertEquals("No se pudo obtener la ubicación GPS.", vm.state.value.error)
    }
}
