package com.macieandrz.securitycamera.data.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ClearCacheWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
   private val inputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir

    override fun doWork(): Result {

        val files = inputDirectory.listFiles()

        files?.forEach { file ->
            if (file.name.startsWith("photo_") && file.name.endsWith(".jpg")) {
                file.delete()
            }
        }
        Log.d("DEBUG", "Cache cleared")
        return Result.success()
    }
}
