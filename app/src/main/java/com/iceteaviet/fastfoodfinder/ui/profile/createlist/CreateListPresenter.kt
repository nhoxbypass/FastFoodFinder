package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class CreateListPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    schedulerProvider: SchedulerProvider,
        private val createListView: CreateListContract.View
) : BasePresenter<CreateListContract.Presenter>(schedulerProvider), CreateListContract.Presenter {


    @VisibleForTesting
    var iconId = R.drawable.ic_profile_list_1

    

    override fun subscribe() {
    }

    override fun onDoneButtonClick(name: String) {
        if (name.isEmpty()) {
            createListView.showEmptyNameWarning()
        } else {
            createListView.notifyWithResult(name, iconId)
        }
    }

    override fun onCancelButtonClick() {
        // TODO: Show close confirm dialog
        createListView.cancel()
    }

    override fun onListIconSelect(iconId: Int) {
        this.iconId = iconId

        createListView.updateSelectedIconUI(iconId)
    }
}