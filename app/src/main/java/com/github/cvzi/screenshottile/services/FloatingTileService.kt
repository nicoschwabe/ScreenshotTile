package com.github.cvzi.screenshottile.services

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.viewbinding.BuildConfig
import com.github.cvzi.screenshottile.App
import com.github.cvzi.screenshottile.R
import com.github.cvzi.screenshottile.activities.NoDisplayActivity
import com.github.cvzi.screenshottile.services.ScreenshotAccessibilityService.Companion.openAccessibilitySettings


/**
 * Created by cuzi (cuzi@openmail.cc).
 */

@RequiresApi(Build.VERSION_CODES.P)
class FloatingTileService : TileService() {
    companion object {
        private const val TAG = "FloatingTileService"
    }

    private fun setState(newState: Int) {
        try {
            qsTile?.run {
                state = newState
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    qsTile.subtitle = getString(R.string.tile_floating_subtitle)
                }
                updateTile()
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "setState: IllegalStateException", e)
        } catch (e: NullPointerException) {
            Log.e(TAG, "setState: NullPointerException", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "setState: IllegalArgumentException", e)
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()
        if (BuildConfig.DEBUG) Log.v(TAG, "onTileAdded()")
        updateTileState()
    }

    override fun onStartListening() {
        super.onStartListening()
        if (BuildConfig.DEBUG) Log.v(TAG, "onStartListening()")
        updateTileState()
    }


    override fun onClick() {
        super.onClick()
        if (BuildConfig.DEBUG) Log.v(TAG, "onClick()")
        if (ScreenshotAccessibilityService.instance == null) {
            // Always enable if accessibility service is not yet running
            App.getInstance().prefManager.floatingButton = true
            openAccessibilitySettings(this)
        } else {
            // Toggle if accessibility service is running
            App.getInstance().prefManager.floatingButton =
                !App.getInstance().prefManager.floatingButton
            ScreenshotAccessibilityService.instance?.updateFloatingButton()
            startActivityAndCollapse(NoDisplayActivity.newIntent(this, false).apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            })
        }
        updateTileState()
    }

    /**
     * Set tile state according to settings and check if accessibility service is running
     */
    private fun updateTileState() {
        setState(
            if (App.getInstance().prefManager.floatingButton && ScreenshotAccessibilityService.instance != null) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
        )
    }

}
