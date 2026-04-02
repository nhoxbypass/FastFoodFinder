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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.databinding.FragmentProfileBinding
import com.iceteaviet.fastfoodfinder.ui.custom.store.StoreListView
import com.iceteaviet.fastfoodfinder.ui.profile.cover.UpdateCoverImageDialog
import com.iceteaviet.fastfoodfinder.ui.profile.createlist.CreateListDialog
import com.iceteaviet.fastfoodfinder.utils.openListDetailActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()

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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storeListAdapter = UserStoreListAdapter()
        setupUI()
        setupEventListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.avatarPhotoUrl.isNotBlank()) {
                        Glide.with(requireActivity())
                            .load(state.avatarPhotoUrl)
                            .into(ivAvatarProfile)
                    }

                    binding.tvName.text = state.name.ifBlank { getString(R.string.unregistered_user) }
                    binding.tvEmail.text = state.email.ifBlank { getString(R.string.unregistered_email) }
                    
                    binding.tvNumberList.text = state.storeListCount
                    cvSavePlace.setCount(state.savedStoreCount.toString())
                    cvFavouritePlace.setCount(state.favouriteStoreCount.toString())

                    storeListAdapter?.setListPackets(state.userStoreLists)

                    when (val event = state.event) {
                        is ProfileEvent.Idle -> {}
                        is ProfileEvent.OpenLoginActivity -> {
                            openLoginActivity(requireActivity())
                            requireActivity().finish()
                            viewModel.markEventConsumed()
                        }
                        is ProfileEvent.ShowCreateNewListDialog -> {
                            showCreateNewListDialog()
                            viewModel.markEventConsumed()
                        }
                        is ProfileEvent.DismissCreateNewListDialog -> {
                            mDialogCreate?.dismiss()
                            viewModel.markEventConsumed()
                        }
                        is ProfileEvent.WarningListNameExisted -> {
                            Toast.makeText(context, R.string.list_name_already_exists, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is ProfileEvent.ShowGeneralErrorMessage -> {
                            // Empty originally
                            viewModel.markEventConsumed()
                        }
                        is ProfileEvent.OpenListDetail -> {
                            openListDetailActivity(requireActivity(), event.userStoreList, event.photoUrl)
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun showCreateNewListDialog() {
        mDialogCreate = CreateListDialog.newInstance()
        mDialogCreate?.show(parentFragmentManager, "")
        mDialogCreate?.setOnButtonClickListener(object : CreateListDialog.OnCreateListListener {
            override fun onCreateButtonClick(name: String, iconId: Int, dialog: CreateListDialog) {
                viewModel.onCreateNewList(name, iconId)
            }

            override fun onCancel(dialog: CreateListDialog) {
                dialog.dismiss()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvCreateNew -> {
                viewModel.onCreateNewListButtonClick()
                return
            }

            R.id.cv_saved_places -> {
                viewModel.onSavedListClick()
                return
            }

            R.id.cv_favourite_places -> {
                viewModel.onFavouriteListClick()
                return
            }

            R.id.btnUpdateCoverImage -> {
                mDialog?.show(parentFragmentManager, "")
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
                viewModel.onStoreListLongClick(position)
            }
        })

        storeListAdapter?.setOnItemClickListener(object : UserStoreListAdapter.OnItemClickListener {
            override fun onClick(listPacket: UserStoreList) {
                viewModel.onStoreListClick(listPacket)
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

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val item = menu.findItem(R.id.action_search)
        item.isVisible = false
    }

    companion object {
        fun newInstance(): ProfileFragment {
            val extras = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = extras
            return fragment
        }
    }
}