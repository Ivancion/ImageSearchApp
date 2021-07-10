package com.example.imagesearchapp.ui.details

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.imagesearchapp.R
import com.example.imagesearchapp.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.util.*


class DetailsFragment : Fragment(R.layout.fragment_details) {
    private val args by navArgs<DetailsFragmentArgs>()
    private lateinit var binding: FragmentDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            val photo = args.photo

            Glide.with(this@DetailsFragment)
                .load(photo.urls.full)
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        textViewCreator.isVisible = true
                        downloadButton.isVisible = true
                        textViewDescription.isVisible = photo.description != null
                        return false
                    }

                })
                .into(imageView)

            textViewDescription.text = photo.description

            val uri = Uri.parse(photo.user.attributionUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            textViewCreator.apply {
                text = "Photo by ${photo.user.username} on Unsplash"
                setOnClickListener{
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }
            downloadButton.setOnClickListener {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(this@DetailsFragment.context?.let { it1 ->
                            ContextCompat.checkSelfPermission(
                                it1, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(
                            this@DetailsFragment.context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        100)
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveImage()
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        saveImage()
                    }
                }
            }
        }
    }
    private fun saveImage() {
            val externalStorageState = Environment.getExternalStorageState()
            if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
                val storageDirectory = Environment.getExternalStorageDirectory().toString()
                val file = File(storageDirectory, "${System.currentTimeMillis()}.jpg")

                try {
                    val stream: OutputStream = FileOutputStream(file)
                    val bitmap = Glide.with(this@DetailsFragment)
                        .asBitmap()
                        .load(args.photo.urls.regular)
                        .submit()
                        .get()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    stream.close()

                    Snackbar.make(binding.root, "Image saved successfully", Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Snackbar.make(binding.root, "Unable to access the storage", Snackbar.LENGTH_SHORT).show()
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 100) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage()
            } else {
                Snackbar.make(binding.root, "Permission not granted, so image can't be saved in storage", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}