package com.iceteaviet.fastfoodfinder.ui.store

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.core.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.databinding.ActivityStoreDetailBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.store.comment.CommentActivity
import com.iceteaviet.fastfoodfinder.ui.store.comment.CommentActivity.Companion.KEY_COMMENT
import com.iceteaviet.fastfoodfinder.utils.REQUEST_LOCATION
import com.iceteaviet.fastfoodfinder.utils.isLocationPermissionGranted
import com.iceteaviet.fastfoodfinder.utils.makeNativeCall
import com.iceteaviet.fastfoodfinder.utils.openRoutingActivity
import com.iceteaviet.fastfoodfinder.utils.requestLocationPermission
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreDetailActivity : BaseActivity() {

    private val viewModel: StoreDetailViewModel by viewModels()

    private lateinit var binding: ActivityStoreDetailBinding

    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var ivBackdrop: ImageView
    private lateinit var rvContent: RecyclerView

    private var adapter: StoreDetailAdapter? = null

    override val layoutId: Int
        get() = R.layout.activity_store_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoreDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.injectLocationManager(GoogleLocationManager.getInstance())

        setupUI()
        setupEventHandlers()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start(isLocationPermissionGranted(this))
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    collapsingToolbar.title = state.title
                    adapter?.setIsSignedIn(state.isSignedIn)

                    if (state.comments.isNotEmpty()) {
                        adapter?.setComments(state.comments.toMutableList())
                    }

                    when (state.event) {
                        is StoreDetailEvent.Idle -> {}
                        is StoreDetailEvent.RequestLocationPermission -> {
                            requestLocationPermission(this@StoreDetailActivity)
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowCannotGetLocationMessage -> {
                            Toast.makeText(this@StoreDetailActivity, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.AddStoreComment -> {
                            adapter?.addComment(state.event.comment)
                            binding.appbar.setExpanded(false)
                            viewModel.clearAddCommentEventConsumed()
                        }
                        is StoreDetailEvent.ScrollToCommentList -> {
                            rvContent.scrollToPosition(3)
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowCommentEditorView -> {
                            startActivityForResult(Intent(this@StoreDetailActivity, CommentActivity::class.java), RC_ADD_COMMENT)
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.StartCallIntent -> {
                            makeNativeCall(this@StoreDetailActivity, state.event.tel)
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowInvalidPhoneNumbWarning -> {
                            Toast.makeText(this@StoreDetailActivity, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowMapRoutingView -> {
                            openRoutingActivity(this@StoreDetailActivity, state.event.store, state.event.mapsDirection)
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.Exit -> {
                            finish()
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowStoreAddedToFavMessage -> {
                            Toast.makeText(this@StoreDetailActivity, R.string.fav_stores_added, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowGeneralErrorMessage -> {
                            Toast.makeText(this@StoreDetailActivity, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowInvalidStoreLocationWarning -> {
                            Toast.makeText(this@StoreDetailActivity, R.string.error_invalid_store_location, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                        is StoreDetailEvent.ShowLoginRequestToast -> {
                            Toast.makeText(applicationContext, getString(R.string.str_login_request), Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == RC_ADD_COMMENT && data != null) {
            val comment = data.getParcelableExtra(KEY_COMMENT) as Comment?
            viewModel.onAddNewComment(comment)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onLocationPermissionGranted()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.onBackButtonClick()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        collapsingToolbar = binding.collapsingToolbar
        ivBackdrop = binding.backdrop
        rvContent = binding.content

        adapter = StoreDetailAdapter()
        rvContent.adapter = adapter
        rvContent.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Glide.with(this)
            .load(R.drawable.detail_sample_circlekcover)
            .apply(RequestOptions().centerCrop())
            .into(ivBackdrop)
    }

    private fun setupEventHandlers() {
        adapter?.setListener(object : StoreDetailAdapter.StoreActionListener {
            override fun onCommentButtonClick() {
                viewModel.onCommentButtonClick()
            }

            override fun onCallButtonClick() {
                viewModel.onCallButtonClick()
            }

            override fun onNavigationButtonClick() {
                viewModel.onNavigationButtonClick()
            }

            override fun onAddToFavButtonClick() {
                viewModel.onAddToFavButtonClick()
            }

            override fun onSaveButtonClick() {
                viewModel.onSaveButtonClick()
            }

        })
    }

    companion object {
        const val KEY_STORE = "key_store"
        const val RC_ADD_COMMENT = 113
    }
}
