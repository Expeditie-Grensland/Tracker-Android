package nl.expeditiegrensland.tracker.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.lang.ref.WeakReference

class DownloadImageTask(imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {
    private val imageViewReference: WeakReference<ImageView> = WeakReference(imageView)

    override fun doInBackground(vararg urls: String): Bitmap? =
            try {
                val inputStream = java.net.URL(urls[0]).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
                null
            }

    override fun onPostExecute(result: Bitmap?) {
        imageViewReference.get()?.run {
            setImageBitmap(result)
        }
    }
}