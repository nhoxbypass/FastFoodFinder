package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class CreateListPresenter : BasePresenter<CreateListContract.Presenter>, CreateListContract.Presenter {

    private val createListView: CreateListContract.View

    @VisibleForTesting
    var iconId = R.drawable.ic_profile_list_1

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, createListView: CreateListContract.View) : super(dataManager, schedulerProvider) {
        this.createListView = createListView
    }

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