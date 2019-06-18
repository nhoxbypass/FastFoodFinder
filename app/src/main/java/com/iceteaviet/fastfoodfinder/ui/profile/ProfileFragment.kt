package com.iceteaviet.fastfoodfinder.ui.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.custom.store.StoreListView
import com.iceteaviet.fastfoodfinder.ui.profile.cover.UpdateCoverImageDialog
import com.iceteaviet.fastfoodfinder.ui.profile.createlist.CreateListDialog
import com.iceteaviet.fastfoodfinder.utils.openListDetailActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*

// TODO: Check fragment lifecycle to support go to login screen when auth token invalid
class ProfileFragment : Fragment(), ProfileContract.View, View.OnClickListener {
    override lateinit var presenter: ProfileContract.Presenter

    lateinit var ivAvatarProfile: CircleImageView
    lateinit var cvSavePlace: StoreListView
    lateinit var cvFavouritePlace: StoreListView
    lateinit var btnCreateNew: CardView

    private var mDialog: UpdateCoverImageDialog? = null
    private var mDialogCreate: CreateListDialog? = null
    private var storeListAdapter: UserStoreListAdapter? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storeListAdapter = UserStoreListAdapter()
        setupUI()
        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvCreateNew -> {
                presenter.onCreateNewListButtonClick()
                return
            }

            R.id.cv_saved_places -> {
                presenter.onSavedListClick()
                return
            }

            R.id.cv_favourite_places -> {
                presenter.onFavouriteListClick()
                return
            }

            R.id.btnUpdateCoverImage -> {
                mDialog?.show(fragmentManager!!, "")
                btnUpdateCoverImage.visibility = View.GONE
                return
            }
        }
    }

    private fun setupEventListeners() {
        btnCreateNew.setOnClickListener(this)
        cvSavePlace.setOnClickListener(this)
        cvFavouritePlace.setOnClickListener(this)
        btnUpdateCoverImage.setOnClickListener(this)

        storeListAdapter?.setOnItemLongClickListener(object : UserStoreListAdapter.OnItemLongClickListener {
            override fun onLongClick(position: Int) {
                presenter.onStoreListLongClick(position)
            }
        })

        storeListAdapter?.setOnItemClickListener(object : UserStoreListAdapter.OnItemClickListener {
            override fun onClick(listPacket: UserStoreList) {
                presenter.onStoreListClick(listPacket)
            }
        })

        mDialog?.setOnButtonClickListener(object : UpdateCoverImageDialog.OnButtonClickListener {
            override fun onOkClick(selectedImage: Drawable?) {
                if (selectedImage != null)
                    ivCoverImage.setImageDrawable(selectedImage)

                btnUpdateCoverImage.visibility = View.VISIBLE
            }

            override fun onCancelClick() {
                btnUpdateCoverImage.visibility = View.VISIBLE
            }
        })
    }

    private fun setupUI() {
        ivAvatarProfile = iv_profile_avatar
        cvSavePlace = cv_saved_places
        cvFavouritePlace = cv_favourite_places
        btnCreateNew = cvCreateNew

        val mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvListPacket.adapter = storeListAdapter
        rvListPacket.layoutManager = mLayoutManager

        mDialog = UpdateCoverImageDialog.newInstance()

        tvName.setText(R.string.unregistered_user)
        tvEmail.setText(R.string.unregistered_email)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val item = menu.findItem(R.id.action_search)
        item.isVisible = false
    }

    override fun openLoginActivity() {
        openLoginActivity(activity!!)
        activity!!.finish()
    }

    override fun loadAvatarPhoto(photoUrl: String) {
        Glide.with(activity!!)
                .load(photoUrl)
                .into(ivAvatarProfile)
    }

    override fun setName(name: String) {
        tvName.text = name
    }

    override fun setEmail(email: String) {
        tvEmail.text = email
    }

    override fun setStoreListCount(storeCount: String) {
        tvNumberList.text = storeCount
    }

    override fun setSavedStoreCount(size: Int) {
        cvSavePlace.setCount(size.toString())
    }

    override fun setFavouriteStoreCount(size: Int) {
        cvFavouritePlace.setCount(size.toString())
    }

    override fun setUserStoreLists(userStoreLists: List<UserStoreList>) {
        storeListAdapter?.setListPackets(userStoreLists)
    }

    override fun addUserStoreList(list: UserStoreList) {
        storeListAdapter?.addListPacket(list)
    }

    override fun showCreateNewListDialog() {
        mDialogCreate = CreateListDialog.newInstance()
        mDialogCreate?.show(fragmentManager!!, "")
        mDialogCreate?.setOnButtonClickListener(object : CreateListDialog.OnCreateListListener {
            override fun onCreateButtonClick(name: String, iconId: Int, dialog: CreateListDialog) {
                presenter.onCreateNewList(name, iconId)
            }

            override fun onCancel(dialog: CreateListDialog) {
                dialog.dismiss()
            }
        })
    }

    override fun dismissCreateNewListDialog() {
        mDialogCreate?.dismiss()
    }

    override fun warningListNameExisted() {
        Toast.makeText(context, R.string.list_name_already_exists, Toast.LENGTH_SHORT).show()
    }

    override fun openListDetail(userStoreList: UserStoreList, photoUrl: String) {
        openListDetailActivity(activity!!, userStoreList, photoUrl)
    }

    override fun showGeneralErrorMessage() {

    }

    companion object {
        fun newInstance(): ProfileFragment {
            val extras = Bundle()
            val fragment = ProfileFragment()
            fragment.presenter = ProfilePresenter(App.getDataManager(), App.getSchedulerProvider(), fragment)
            fragment.arguments = extras
            return fragment
        }
    }
}
