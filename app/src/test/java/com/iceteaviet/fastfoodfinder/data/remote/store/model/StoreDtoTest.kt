package com.iceteaviet.fastfoodfinder.data.remote.store.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StoreDtoTest {

    @Test
    fun toDomain_mapsAllFields() {
        val dto = StoreDto(1, "Circle K", "123 D1", "10.773996", "106.6898035", "0900000001", 2)

        val store = dto.toDomain()

        assertThat(store.id).isEqualTo(1)
        assertThat(store.title).isEqualTo("Circle K")
        assertThat(store.address).isEqualTo("123 D1")
        assertThat(store.lat).isWithin(0.0001).of(10.773996)
        assertThat(store.lng).isWithin(0.0001).of(106.6898035)
        assertThat(store.tel).isEqualTo("0900000001")
        assertThat(store.type).isEqualTo(2)
    }

    @Test
    fun toDomain_parsesValidNumericStrings() {
        val dto = StoreDto(0, "", "", "0.0", "180.0", "", 0)

        val store = dto.toDomain()

        assertThat(store.lat).isWithin(0.001).of(0.0)
        assertThat(store.lng).isWithin(0.001).of(180.0)
    }

    @Test
    fun toDomain_fallsBackToZero_whenLatIsInvalid() {
        val dto = StoreDto(0, "", "", "not_a_number", "106.0", "", 0)

        val store = dto.toDomain()

        assertThat(store.lat).isWithin(0.001).of(0.0)
        assertThat(store.lng).isWithin(0.001).of(106.0)
    }

    @Test
    fun toDomain_fallsBackToZero_whenLngIsEmpty() {
        val dto = StoreDto(0, "", "", "10.5", "", "", 0)

        val store = dto.toDomain()

        assertThat(store.lat).isWithin(0.001).of(10.5)
        assertThat(store.lng).isWithin(0.001).of(0.0)
    }

    @Test
    fun toDomain_fallsBackToZero_whenBothInvalid() {
        val dto = StoreDto(0, "", "", "abc", "xyz", "", 0)

        val store = dto.toDomain()

        assertThat(store.lat).isWithin(0.001).of(0.0)
        assertThat(store.lng).isWithin(0.001).of(0.0)
    }

    @Test
    fun toDomain_defaultsTypeToZero() {
        val dto = StoreDto()

        val store = dto.toDomain()

        assertThat(store.type).isEqualTo(0)
    }
}
