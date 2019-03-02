package com.android.artgallery.presentation.detailphoto

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import com.android.artgallery.R
import com.android.artgallery.databinding.ActivityPhotoDetailBinding
import com.android.artgallery.presentation.loadImageFull
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class PhotoDetailActivity : DaggerAppCompatActivity(), OnPhotoDetailCallback {

    private lateinit var activityPhotoDetailBinding: ActivityPhotoDetailBinding
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PhotoDetailViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PhotoDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityPhotoDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_detail)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        activityPhotoDetailBinding.photoDetailViewModel = viewModel

        val photoId = intent?.extras?.getLong(KEY_PHOTO_ID) ?: return
        viewModel.getDetail(photoId)
        viewModel.checkFavoriteStatus(photoId)

        viewModel.photoData.observe(this, Observer {
            activityPhotoDetailBinding.detailTitleTextView.setText(it?.title)
            activityPhotoDetailBinding.detailToolbarImageView.loadImageFull(it?.url)
        })


        viewModel.isFavorite.observe(this, Observer {
            if (it != true) {
                activityPhotoDetailBinding.detailFab.setImageResource(R.drawable.ic_star_empty_white_vector)
            } else {
                activityPhotoDetailBinding.detailFab.setImageResource(R.drawable.ic_star_full_vector)
            }
        })

        activityPhotoDetailBinding.detailFab.setOnClickListener {
            viewModel.updateFavoriteStatus()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = PhotoDetailActivity::class.java.name
        private val KEY_PHOTO_ID = "KEY_PHOTO_ID"
    }

}