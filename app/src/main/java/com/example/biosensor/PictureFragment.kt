package com.example.biosensor

import androidx.fragment.app.Fragment
import android.os.Bundle
import com.example.biosensor.databinding.ActivityMainBinding
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import android.Manifest
import android.app.Activity

import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.content.Intent
import com.example.biosensor.databinding.PictureFragmentBinding

class PictureFragment: Fragment() {
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val PERMISSION_CODE_READ = 1001
    private val PERMISSION_CODE_WRITE = 1002

    var image_uri: Uri? = null
    private lateinit var binding : PictureFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.picture_fragment, container, false)
        binding.lifecycleOwner = this
        binding.captureButton.setOnClickListener{
            captureImage()
        }
        return binding.root
    }

    private fun captureImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(this.activity!!,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(this.activity!!,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Log.i("CODE", requestCode.toString())
            binding.salivaView.setImageURI(data?.data)

        }

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            Log.i("CODE", requestCode.toString())
            binding.salivaView.setImageURI(image_uri)
        }
    }
}