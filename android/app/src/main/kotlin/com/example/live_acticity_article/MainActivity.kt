package com.example.live_acticity_article

import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.FlutterEngine
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat


class MainActivity: FlutterActivity() {
    private val CHANNEL = "flutterAndroidLiveActivity"
  
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        arrayOf()
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
      super.configureFlutterEngine(flutterEngine)
      MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
        call, result ->
        if (call.method == "startDelivery") {
            val args = call.arguments<Map<String, Any>>()
            val stage = args?.get("stage") as? Int
            val stagesCount = args?.get("stagesCount") as? Int

            
            if (stage != null && stagesCount != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LiveActivityManager(this@MainActivity).showNotification(stage, stagesCount)
                }
            }
            result.success("Notification displayed")
        } else if (call.method == "updateDeliveryStatus") {
            val args = call.arguments<Map<String, Any>>()
            val minutes = args?.get("minutesToDelivery") as? Int
            val stage = args?.get("stage") as? Int
            val stagesCount = args?.get("stagesCount") as? Int

            if (minutes != null && stage != null && stagesCount != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LiveActivityManager(this@MainActivity).updateNotification(minutes, stage, stagesCount)
                }
            }
            result.success("Notification updated")
        } else if (call.method == "finishDelivery") {
            val args = call.arguments<Map<String, Any>>()
            val stage = args?.get("stage") as? Int
            val stagesCount = args?.get("stagesCount") as? Int

            if (stage != null && stagesCount != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LiveActivityManager(this@MainActivity)
                        .finishDeliveryNotification(stage, stagesCount)
                }
            }
            result.success("Notification delivered")
        } else if (call.method == "endNotifications") { 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LiveActivityManager(this@MainActivity)
                    .endNotification()
            }
            result.success("Notification cancelled")
        }
      }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, permissions, 200)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LiveActivityManager(context).endNotification()
        }
    }
}

