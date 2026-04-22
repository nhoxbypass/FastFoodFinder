package com.iceteaviet.fastfoodfinder.data.local.db.store.model

import com.google.common.truth.Truth.assertThat
import com.iceteaviet.fastfoodfinder.domain.model.Store
import org.junit.Test

class StoreEntityMappingTest {

    @Test
    fun toDomain_mapsEntityFieldsToDomain() {
        val entity = StoreEntity(
            id = 1, type = 2, title = "Circle K",
            address = "123 D1", latitude = 10.773996,
            longitude = 106.6898035, telephone = "0900000001"
        )

        val store = entity.toDomain()

        assertThat(store.id).isEqualTo(1)
        assertThat(store.type).isEqualTo(2)
        assertThat(store.title).isEqualTo("Circle K")
        assertThat(store.address).isEqualTo("123 D1")
        assertThat(store.lat).isWithin(0.0001).of(10.773996)
        assertThat(store.lng).isWithin(0.0001).of(106.6898035)
        assertThat(store.tel).isEqualTo("0900000001")
    }

    @Test
    fun toEntity_mapsDomainFieldsToEntity() {
        val store = Store(
            id = 1, title = "Circle K", address = "123 D1",
            lat = 10.773996, lng = 106.6898035,
            tel = "0900000001", type = 2
        )

        val entity = store.toEntity()

        assertThat(entity.id).isEqualTo(1)
        assertThat(entity.type).isEqualTo(2)
        assertThat(entity.title).isEqualTo("Circle K")
        assertThat(entity.address).isEqualTo("123 D1")
        assertThat(entity.latitude).isWithin(0.0001).of(10.773996)
        assertThat(entity.longitude).isWithin(0.0001).of(106.6898035)
        assertThat(entity.telephone).isEqualTo("0900000001")
    }

    @Test
    fun roundTrip_domainToEntityAndBack_preservesAllFields() {
        val original = Store(
            id = 42, title = "FamilyMart", address = "456 D2",
            lat = 10.78, lng = 106.70, tel = "0902", type = 3
        )

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }

    @Test
    fun toDomain_handlesDefaults() {
        val entity = StoreEntity()

        val store = entity.toDomain()

        assertThat(store.id).isEqualTo(0)
        assertThat(store.title).isEmpty()
        assertThat(store.lat).isWithin(0.001).of(0.0)
        assertThat(store.lng).isWithin(0.001).of(0.0)
        assertThat(store.tel).isEmpty()
        assertThat(store.type).isEqualTo(0)
    }

    @Test
    fun fieldNames_mapCorrectly_latToLatitude_lngToLongitude_telToTelephone() {
        val store = Store(
            id = 1, lat = 10.0, lng = 106.0, tel = "123", type = 0, title = "T", address = "A"
        )

        val entity = store.toEntity()

        assertThat(entity.latitude).isEqualTo(store.lat)
        assertThat(entity.longitude).isEqualTo(store.lng)
        assertThat(entity.telephone).isEqualTo(store.tel)
    }
}
