package com.morozione.psychologyhelper.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val result = ByteArray(length.toInt())
    if (result.isNotEmpty()) {
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
    }
    return result
}

@OptIn(ExperimentalForeignApi::class)
private class ImagePickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val data = image?.let { UIImageJPEGRepresentation(it, 0.8) }
        data?.let { onImagePicked(it.toByteArray()) }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val delegate = remember { ImagePickerDelegate(onImagePicked) }
    return {
        val picker = UIImagePickerController()
        picker.delegate = delegate
        picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        val rootViewController: UIViewController? =
            UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, animated = true, completion = null)
    }
}
