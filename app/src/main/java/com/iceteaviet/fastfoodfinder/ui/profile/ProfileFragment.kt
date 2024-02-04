package com.iceteaviet.fastfoodfinder.ui.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.databinding.FragmentProfileBinding
import com.iceteaviet.fastfoodfinder.ui.custom.store.StoreListView
import com.iceteaviet.fastfoodfinder.ui.profile.cover.UpdateCoverImageDialog
import com.iceteaviet.fastfoodfinder.ui.profile.createlist.CreateListDialog
import com.iceteaviet.fastfoodfinder.utils.openListDetailActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Check fragment lifecycle to support go to login screen when auth token invalid
class ProfileFragment : Fragment(), ProfileContract.View, View.OnClickListener {
    override lateinit var presenter: ProfileContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: FragmentProfileBinding

    lateinit var ivAvatarProfile: CircleImageView
    lateinit var cvSavePlace: StoreListView
    lateinit var cvFavouritePlace: StoreListView
    lateinit var btnCreateNew: CardView

    private var mDialog: UpdateCoverImageDialog? = null
    private var mDialogCreate: CreateListDialog? = null
    private var storeListAdapter: UserStoreListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                mDialog?.show(requireFragmentManager(), "")
                binding.btnUpdateCoverImage.visibility = View.GONE
                return
            }
        }
    }

    private fun setupEventListeners() {
        btnCreateNew.setOnClickListener(this)
        cvSavePlace.setOnClickListener(this)
        cvFavouritePlace.setOnClickListener(this)
        binding.btnUpdateCoverImage.setOnClickListener(this)

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
                    binding.ivCoverImage.setImageDrawable(selectedImage)

                binding.btnUpdateCoverImage.visibility = View.VISIBLE
            }

            override fun onCancelClick() {
                binding.btnUpdateCoverImage.visibility = View.VISIBLE
            }
        })
    }

    private fun setupUI() {
        ivAvatarProfile = binding.ivProfileAvatar
        cvSavePlace = binding.cvSavedPlaces
        cvFavouritePlace = binding.cvFavouritePlaces
        btnCreateNew = binding.cvCreateNew

        val mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvListPacket.adapter = storeListAdapter
        binding.rvListPacket.layoutManager = mLayoutManager

        mDialog = UpdateCoverImageDialog.newInstance()

        binding.tvName.setText(R.string.unregistered_user)
        binding.tvEmail.setText(R.string.unregistered_email)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val item = menu.findItem(R.id.action_search)
        item.isVisible = false
    }

    override fun openLoginActivity() {
        openLoginActivity(requireActivity())
        requireActivity().finish()
    }

    override fun loadAvatarPhoto(photoUrl: String) {
        Glide.with(requireActivity())
            .load(photoUrl)
            .into(ivAvatarProfile)
    }

    override fun setName(name: String) {
        binding.tvName.text = name
    }

    override fun setEmail(email: String) {
        binding.tvEmail.text = email
    }

    override fun setStoreListCount(storeCount: String) {
        binding.tvNumberList.text = storeCount
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
        mDialogCreate?.show(requireFragmentManager(), "")
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
        openListDetailActivity(requireActivity(), userStoreList, photoUrl)
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