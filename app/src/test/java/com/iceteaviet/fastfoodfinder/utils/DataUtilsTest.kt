package com.iceteaviet.fastfoodfinder.utils


import com.google.common.truth.Truth.assertThat
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import org.junit.Test

class DataUtilsTest {
    @Test
    fun isValidUserUid_true() {
        assertThat(isValidUserUid("abcxyz")).isTrue()
        assertThat(isValidUserUid("1abc5xyz")).isTrue()
    }

    @Test
    fun isValidUserUid_false() {
        assertThat(isValidUserUid("")).isFalse()
        assertThat(isValidUserUid("null")).isFalse()
    }

    @Test
    fun isValidEmail_true() {
        assertThat(isValidEmail("abcxyz@gmail.com")).isTrue()
        assertThat(isValidEmail("1abc5xyz@yahoo.com.vn")).isTrue()
        assertThat(isValidEmail("a@iceteaviet.com")).isTrue()
    }

    @Test
    fun isValidEmail_false() {
        assertThat(isValidEmail("")).isFalse()
        assertThat(isValidEmail("null")).isFalse()
        assertThat(isValidEmail("abczyx@gmail")).isFalse()
        assertThat(isValidEmail("abcxyc@")).isFalse()
        assertThat(isValidEmail("@gmail.com")).isFalse()
    }

    @Test
    fun isValidPassword_true() {
        assertThat(isValidPassword("somepassword")).isTrue()
        assertThat(isValidPassword("Str0n9passwOrd$")).isTrue()
        assertThat(isValidPassword("a@iceteaviet.com")).isTrue()
    }

    @Test
    fun isValidPassword_false() {
        assertThat(isValidPassword("")).isFalse()
        assertThat(isValidPassword("null")).isFalse()
        assertThat(isValidPassword("1234567")).isFalse()
        assertThat(isValidPassword("1")).isFalse()
    }

    @Test
    fun getFakeCommentsTest() {
        assertThat(getFakeComments()).isNotNull()
        assertThat(getFakeComments()).isNotEmpty()
    }

    @Test
    fun getFakeCommentTest() {
        assertThat(getFakeComment()).isNotNull()
    }

    @Test
    fun getFakeStoreListTest() {
        assertThat(getFakeStoreList()).isNotNull()
        assertThat(getFakeStoreList()).isNotEmpty()
    }

    @Test
    fun getFakeUserStoreListsTest() {
        assertThat(getFakeUserStoreLists()).isNotNull()
        assertThat(getFakeUserStoreLists()).isNotEmpty()
    }

    @Test
    fun getStoreTypeTest() {
        assertThat(getStoreType("circle_k")).isEqualTo(StoreType.TYPE_CIRCLE_K)
        assertThat(getStoreType("mini_stop")).isEqualTo(StoreType.TYPE_MINI_STOP)
        assertThat(getStoreType("family_mart")).isEqualTo(StoreType.TYPE_FAMILY_MART)
        assertThat(getStoreType("bsmart")).isEqualTo(StoreType.TYPE_BSMART)
        assertThat(getStoreType("711")).isEqualTo(StoreType.TYPE_7_ELEVEN)
    }

    @Test
    fun getStoreTypeTest_invalidInput() {
        assertThat(getStoreType("")).isEqualTo(StoreType.TYPE_CIRCLE_K)
        assertThat(getStoreType(null)).isEqualTo(StoreType.TYPE_CIRCLE_K)
    }

    @Test
    fun getStoreSearchStringTest() {
        assertThat(getStoreSearchString(StoreType.TYPE_CIRCLE_K)).isEqualTo("Circle K")
        assertThat(getStoreSearchString(StoreType.TYPE_BSMART)).isEqualTo("B'smart")
        assertThat(getStoreSearchString(StoreType.TYPE_7_ELEVEN)).isEqualTo("7-Eleven")
        assertThat(getStoreSearchString(StoreType.TYPE_FAMILY_MART)).isEqualTo("Family Mart")
        assertThat(getStoreSearchString(StoreType.TYPE_MINI_STOP)).isEqualTo("Mini Stop")
        assertThat(getStoreSearchString(-1)).isEqualTo("")
    }

    @Test
    fun getStoreNameByKeyTest() {
        assertThat(getStoreNameByKey(KEY_CIRCLE_K)).isEqualTo("Cirle K")
        assertThat(getStoreNameByKey(KEY_BSMART)).isEqualTo("B’s mart")
        assertThat(getStoreNameByKey(KEY_FAMILY_MART)).isEqualTo("Family mart")
        assertThat(getStoreNameByKey(KEY_MINI_STOP)).isEqualTo("Ministop")
        assertThat(getStoreNameByKey(KEY_711)).isEqualTo("7-Eleven")

        assertThat(getStoreNameByKey(null)).isEmpty()
        assertThat(getStoreNameByKey("")).isEmpty()
        assertThat(getStoreNameByKey("-1")).isEmpty()
    }

    @Test
    fun `getRandomInt returns value within range`() {
        val min = 10
        val max = 20
        repeat(100) {
            val result = getRandomInt(min, max)
            assertThat(result).isAtLeast(min)
            assertThat(result).isAtMost(max)
        }
    }

    @Test
    fun `getRandomLong returns different values`() {
        val result1 = getRandomLong()
        val result2 = getRandomLong()
        val result3 = getRandomLong()

        // Not guaranteed but very likely
        assertThat(result1).isNotEqualTo(result2)
        assertThat(result2).isNotEqualTo(result3)
    }

    @Test
    fun `getRandomInt returns min when min equals max`() {
        val minMax = 42
        val result = getRandomInt(minMax, minMax)
        assertThat(result).isEqualTo(minMax)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getRandomInt throws if min greater than max`() {
        getRandomInt(5, 3)
    }

    companion object {
        const val KEY_CIRCLE_K = "circle_k"
        const val KEY_MINI_STOP = "mini_stop"
        const val KEY_FAMILY_MART = "family_mark"
        const val KEY_BSMART = "bsmart"
        const val KEY_711 = "711"

    }
}