package com.iceteaviet.fastfoodfinder.ui.store.comment

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-17.
 */
class CommentPresenterTest {
    @Mock
    private lateinit var commentView: CommentContract.View

    private lateinit var commentPresenter: CommentPresenter

    @Mock
    private lateinit var dataManager: DataManager

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        commentPresenter = CommentPresenter(dataManager, TrampolineSchedulerProvider(), commentView)
    }

    @Test
    fun subscribeTest() {
        commentPresenter.subscribe()
    }

    @Test
    fun afterCommentTextChangedTest_emptyText() {
        commentPresenter.afterCommentTextChanged("")

        verify(commentView).setRemainCharCountText("140")
        verify(commentView).updateTextColor(false)
        verify(commentView).setPostButtonEnabled(false)
    }

    @Test
    fun afterCommentTextChangedTest_tooLongText() {
        commentPresenter.afterCommentTextChanged("Mockito has a very convenient way to inject mocks by using the @Mock annotation. Mockito has a very convenient way to inject mocks by using the @Mock annotation. ")

        verify(commentView).setRemainCharCountText("-22")
        verify(commentView).updateTextColor(true)
        verify(commentView).setPostButtonEnabled(false)
    }

    @Test
    fun afterCommentTextChangedTest_validData() {
        commentPresenter.afterCommentTextChanged("this is my comment")

        verify(commentView).setRemainCharCountText("122")
        verify(commentView).updateTextColor(false)
        verify(commentView).setPostButtonEnabled(true)
    }

    @Test
    fun onPostButtonClickTest_emptyText() {
        commentPresenter.onPostButtonClick("")

        verify(commentView).showCommentPostFailedWarning()
        verifyNoMoreInteractions(commentView)
    }

    @Test
    fun onPostButtonClickTest_tooLongText() {
        commentPresenter.onPostButtonClick("Mockito has a very convenient way to inject mocks by using the @Mock annotation. Mockito has a very convenient way to inject mocks by using the @Mock annotation. ")

        verify(commentView).showCommentPostFailedWarning()
        verifyNoMoreInteractions(commentView)
    }

    @Test
    fun onPostButtonClickTest_validText_nullUser() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(null)

        commentPresenter.onPostButtonClick("this is my comment")

        verify(commentView).showCommentPostFailedWarning()
        verifyNoMoreInteractions(commentView)
    }

    @Test
    fun onPostButtonClickTest_validText_validUser() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        val comment = Comment(USER_NAME, USER_PHOTO_URL, "this is my comment", "", System.currentTimeMillis())
        commentPresenter.onPostButtonClick("this is my comment")

        verify(commentView).exitWithResult(comment)
    }

    @Test
    fun onBackButtonClickTest_emptyText() {
        commentPresenter.onBackButtonClick("")

        verify(commentView).exit()
    }

    @Test
    fun onBackButtonClickTest_haveText() {
        commentPresenter.onBackButtonClick("this is my comment")

        verify(commentView).showCloseConfirmDialog()
    }

    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
    }
}