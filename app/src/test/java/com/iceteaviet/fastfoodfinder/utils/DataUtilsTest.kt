package com.iceteaviet.fastfoodfinder.utils


import org.assertj.core.api.Assertions.assertThat
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
        assertThat(getStoreType("shop_n_go")).isEqualTo(StoreType.TYPE_SHOP_N_GO)
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
        assertThat(getStoreSearchString(StoreType.TYPE_SHOP_N_GO)).isEqualTo("Shop and Go")
        assertThat(getStoreSearchString(StoreType.TYPE_FAMILY_MART)).isEqualTo("Family Mart")
        assertThat(getStoreSearchString(StoreType.TYPE_MINI_STOP)).isEqualTo("Mini Stop")
        assertThat(getStoreSearchString(-1)).isEqualTo("")
    }
}