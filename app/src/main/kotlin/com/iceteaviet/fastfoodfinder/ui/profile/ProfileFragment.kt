package com.iceteaviet.fastfoodfinder.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.custom.store.StoreListView
import com.iceteaviet.fastfoodfinder.ui.profile.ListDetailActivity.Companion.KEY_USER_PHOTO_URL
import com.iceteaviet.fastfoodfinder.ui.profile.ListDetailActivity.Companion.KEY_USER_STORE_LIST
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*


class ProfileFragment : Fragment(), View.OnClickListener {
    lateinit var ivAvatarProfile: ImageView
    lateinit var cvSavePlace: StoreListView
    lateinit var cvFavouritePlace: StoreListView
    lateinit var btnCreateNew: CardView

    private var mDialog: DialogUpdateCoverImage? = null
    private var mDialogCreate: DialogCreateNewList? = null
    private var mAdapter: UserStoreListAdapter? = null
    private var defaultList: MutableList<UserStoreList> = ArrayList() // Default store list (saved, favourite) that every user have
    private var listName: ArrayList<String> = ArrayList()

    private lateinit var dataManager: DataManager


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        dataManager = App.getDataManager()
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupEventListeners()
        loadCurrentUserData()
    }

    override fun onResume() {
        super.onResume()
        // Invalid auth token -> go to login screen
        if (!dataManager.isSignedIn())
            openLoginActivity(activity!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvCreateNew -> {
                mDialogCreate = DialogCreateNewList.newInstance(listName)
                mDialogCreate!!.show(fragmentManager!!, "")
                mDialogCreate!!.setOnButtonClickListener(object : DialogCreateNewList.OnCreateListListener {
                    override fun onButtonClick(name: String, iconId: Int) {
                        val currentUser = dataManager.getCurrentUser()
                        val id = currentUser!!.getUserStoreLists().size //New id = current size
                        val list = UserStoreList(id, ArrayList(), iconId, name)
                        mAdapter!!.addListPacket(list)
                        currentUser.addStoreList(list)
                        dataManager.getRemoteUserDataSource().updateStoreListForUser(currentUser.uid, currentUser.getUserStoreLists())
                        tvNumberList!!.text = String.format("(%s)", mAdapter!!.itemCount.toString())
                    }
                })
                return
            }

            R.id.cv_saved_places -> {
                sendToDetailListActivity(defaultList[UserStoreList.ID_SAVED])
                return
            }

            R.id.cv_favourite_places -> {
                sendToDetailListActivity(defaultList[UserStoreList.ID_FAVOURITE])
                return
            }

            R.id.btnUpdateCoverImage -> {
                mDialog!!.show(fragmentManager!!, "")
                btnUpdateCoverImage!!.visibility = View.GONE
                return
            }
        }
    }

    private fun setupEventListeners() {
        btnCreateNew.setOnClickListener(this)
        cvSavePlace.setOnClickListener(this)
        cvFavouritePlace.setOnClickListener(this)
        btnUpdateCoverImage.setOnClickListener(this)

        mAdapter!!.setOnItemLongClickListener(object : UserStoreListAdapter.OnItemLongClickListener {
            override fun onClick(position: Int) {
                val currentUser = dataManager.getCurrentUser()
                tvNumberList!!.text = String.format("(%s)", mAdapter!!.itemCount.toString())
                currentUser!!.removeStoreList(position)
                dataManager.getRemoteUserDataSource().updateStoreListForUser(currentUser.uid, currentUser.getUserStoreLists())
            }
        })

        mAdapter!!.setOnItemClickListener(object : UserStoreListAdapter.OnItemClickListener {
            override fun onClick(listPacket: UserStoreList) {
                sendToDetailListActivity(listPacket)
            }
        })

        mDialog!!.setOnButtonClickListener(object : DialogUpdateCoverImage.OnButtonClickListener {
            override fun onOkClick(Id: Int, bmp: Bitmap?) {
                if (Id != 0)
                    if (Id == -1) {
                        ivCoverImage!!.setImageBitmap(bmp)
                    } else {
                        ivCoverImage!!.setImageResource(Id)
                    }

                btnUpdateCoverImage!!.visibility = View.VISIBLE
            }

            override fun onCancelClick() {
                btnUpdateCoverImage!!.visibility = View.VISIBLE
            }
        })
    }

    private fun setupUi() {
        ivAvatarProfile = iv_profile_avatar
        cvSavePlace = cv_saved_places
        cvFavouritePlace = cv_favourite_places
        btnCreateNew = cvCreateNew

        mAdapter = UserStoreListAdapter()
        val mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvListPacket!!.adapter = mAdapter
        rvListPacket!!.layoutManager = mLayoutManager

        mDialog = DialogUpdateCoverImage.newInstance()

        tvName!!.setText(R.string.unregistered_user)
        tvEmail!!.setText(R.string.unregistered_email)
    }

    fun loadStoreLists() {
        val currentUser = dataManager.getCurrentUser()
        for (i in 0 until currentUser!!.getUserStoreLists().size) {
            if (i <= 2) {
                defaultList.add(currentUser.getUserStoreLists()[i])
            } else {
                mAdapter!!.addListPacket(currentUser.getUserStoreLists()[i])
            }
        }
        tvNumberList!!.text = String.format("(%s)", mAdapter!!.itemCount.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val item = menu.findItem(R.id.action_search)
        item.isVisible = false
    }

    private fun loadCurrentUserData() {
        val uid = dataManager.getCurrentUserUid()
        if (!isValidUserUid(uid))
            return

        dataManager.getRemoteUserDataSource().getUser(uid)
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        dataManager.setCurrentUser(user)
                        Glide.with(context!!)
                                .load(user.photoUrl)
                                .into(ivAvatarProfile)
                        tvName!!.text = user.name
                        tvEmail!!.text = user.email
                        loadStoreLists()
                        for (i in 0 until user.getUserStoreLists().size) {
                            listName.add(user.getUserStoreLists()[i].listName)
                        }

                        fillStoreListPreviewData(user)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun fillStoreListPreviewData(user: User) {
        cvSavePlace.setCount(user.getSavedStoreList().getStoreIdList()!!.size.toString())
        cvFavouritePlace.setCount(user.getFavouriteStoreList().getStoreIdList()!!.size.toString())
    }

    fun sendToDetailListActivity(userStoreList: UserStoreList) {
        val intent = Intent(context, ListDetailActivity::class.java)
        intent.putExtra(KEY_USER_PHOTO_URL, dataManager.getCurrentUser()!!.photoUrl)
        intent.putExtra(KEY_USER_STORE_LIST, userStoreList)
        startActivity(intent)
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
