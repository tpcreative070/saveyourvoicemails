package co.tpcreative.saveyourvoicemails.ui.trim

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.domain.models.MimeTypeFile
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivityTrimBinding
import co.tpcreative.trimmerlibrary.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*


class TrimAct : BaseActivity() , MarkerView.MarkerListener,
WaveformView.WaveformListener{

    lateinit var binding: ActivityTrimBinding
    val viewModel : TrimViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()))
    }

    companion object {
        const val AUDIO_URL_EXTRA = "audio_url_extra"
        const val AUDIO_TITLE_EXTRA = "audio_title_extra"
    }

    private var mLoadingLastUpdateTime: Long = 0
    private var mLoadingKeepGoing = false
    private var mRecordingKeepGoing = false
    private var mFinishActivity = false
    private var mAlertDialog: AlertDialog? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mSoundFile: SoundFile? = null
    private var mFile: File? = null
    private var mFilename: String? = null
    private var mTitleName : String? = null
    private var mWaveformView: WaveformView? = null
    private var mStartMarker: MarkerView? = null
    private var mEndMarker: MarkerView? = null
    private var mStartText: TextView? = null
    private var mEndText: TextView? = null
    private var mInfo: TextView? = null
    private var mInfoContent: String? = null
    private var mPlayButton: ImageButton? = null
    private var mRewindButton: ImageButton? = null
    private var mFfwdButton: ImageButton? = null
    private var mKeyDown = false
    private var mCaption = ""
    private var mWidth = 0
    private var mMaxPos = 0
    private var mStartPos = 0
    private var mEndPos = 0
    private var mStartVisible = false
    private var mEndVisible = false
    private var mLastDisplayedStartPos = 0
    private var mLastDisplayedEndPos = 0
    private var mOffset = 0
    private var mOffsetGoal = 0
    private var mFlingVelocity = 0
    private var mPlayStartMsec = 0
    private var mPlayEndMsec = 0
    private var mHandler: Handler? = null
    private var mIsPlaying = false
    private var mPlayer: SamplePlayer? = null
    private var mTouchDragging = false
    private var mTouchStart = 0f
    private var mTouchInitialOffset = 0
    private var mTouchInitialStartPos = 0
    private var mTouchInitialEndPos = 0
    private var mWaveformTouchStartMsec: Long = 0
    private var mDensity = 0f
    private var mMarkerLeftInset = 0
    private var mMarkerRightInset = 0
    private var mMarkerTopOffset = 0
    private var mMarkerBottomOffset = 0

    private var mLoadSoundFileThread: Thread? = null
    private var mRecordAudioThread: Thread? = null
    private var mSaveSoundFileThread: Thread? = null

    // Result codes
    private val REQUEST_CODE_CHOOSE_CONTACT = 1

    //
    // Public methods and protected overrides
    //

    //
    // Public methods and protected overrides
    //
    /** Called when the activity is first created.  */
    override fun onCreate(icicle: Bundle?) {
      log("EditActivity OnCreate")
        super.onCreate(icicle)

        binding = ActivityTrimBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initUI()

        mPlayer = null
        mIsPlaying = false
        mAlertDialog = null
        mProgressDialog = null
        mLoadSoundFileThread = null
        mRecordAudioThread = null
        mSaveSoundFileThread = null
        val intent = intent

        // If the Ringdroid media select activity was launched via a
        // GET_CONTENT intent, then we shouldn't display a "saved"
        // message when the user saves, we should just return whatever
        // they create.

        mFilename = intent.getStringExtra(AUDIO_URL_EXTRA)
        mTitleName = intent.getStringExtra(AUDIO_TITLE_EXTRA)
        mSoundFile = null
        mKeyDown = false
        mHandler = Handler()
        loadGui()
        mHandler!!.postDelayed(mTimerRunnable, 100)
        loadFromFile()
    }

    private fun closeThread(thread: Thread?) {
        if (thread != null && thread.isAlive) {
            try {
                thread.join()
            } catch (e: InterruptedException) {
            }
        }
    }

    /** Called when the activity is finally destroyed.  */
    override fun onDestroy() {
        log("EditActivity OnDestroy")
        mLoadingKeepGoing = false
        mRecordingKeepGoing = false
        closeThread(mLoadSoundFileThread)
        closeThread(mRecordAudioThread)
        closeThread(mSaveSoundFileThread)
        mLoadSoundFileThread = null
        mRecordAudioThread = null
        mSaveSoundFileThread = null
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        if (mAlertDialog != null) {
            mAlertDialog!!.dismiss()
            mAlertDialog = null
        }
        if (mPlayer != null) {
            if (mPlayer!!.isPlaying || mPlayer!!.isPaused) {
                mPlayer?.stop()
            }
            mPlayer?.release()
            mPlayer = null
        }
        super.onDestroy()
    }

    /** Called with an Activity we started with an Intent returns.  */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        dataIntent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
        log("EditActivity onActivityResult")
        if (requestCode == REQUEST_CODE_CHOOSE_CONTACT) {
            // The user finished saving their ringtone and they're
            // just applying it to a contact.  When they return here,
            // they're done.
            finish()
            return
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        log("EditActivity onConfigurationChanged")
        val saveZoomLevel: Int = mWaveformView!!.zoomLevel
        super.onConfigurationChanged(newConfig)
        loadGui()
        mHandler!!.postDelayed({
            mStartMarker!!.requestFocus()
            markerFocus(mStartMarker)
            mWaveformView?.zoomLevel = saveZoomLevel
            mWaveformView?.recomputeHeights(mDensity)
            updateDisplay()
        }, 500)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_options, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_save).isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                onSave()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            onPlay(mStartPos)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //
    // WaveformListener
    //

    //
    // WaveformListener
    //
    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another redraw.
     */
    override fun waveformDraw() {
        mWidth = mWaveformView!!.measuredWidth
        if (mOffsetGoal != mOffset && !mKeyDown) updateDisplay() else if (mIsPlaying) {
            updateDisplay()
        } else if (mFlingVelocity != 0) {
            updateDisplay()
        }
    }

    override fun waveformTouchStart(x: Float) {
        mTouchDragging = true
        mTouchStart = x
        mTouchInitialOffset = mOffset
        mFlingVelocity = 0
        mWaveformTouchStartMsec = getCurrentTime()
    }

    override fun waveformTouchMove(x: Float) {
        mOffset = trap((mTouchInitialOffset + (mTouchStart - x)).toInt())
        updateDisplay()
    }

    override fun waveformTouchEnd() {
        mTouchDragging = false
        mOffsetGoal = mOffset
        val elapsedMsec = getCurrentTime() - mWaveformTouchStartMsec
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                val seekMsec: Int = mWaveformView!!.pixelsToMillisecs(
                    (mTouchStart + mOffset).toInt()
                )
                if (seekMsec in mPlayStartMsec until mPlayEndMsec
                ) {
                    mPlayer?.seekTo(seekMsec)
                } else {
                    handlePause()
                }
            } else {
                onPlay((mTouchStart + mOffset).toInt())
            }
        }
    }

    override fun waveformFling(vx: Float) {
        mTouchDragging = false
        mOffsetGoal = mOffset
        mFlingVelocity = (-vx).toInt()
        updateDisplay()
    }

    override fun waveformZoomIn() {
        mWaveformView!!.zoomIn()
        mStartPos = mWaveformView!!.start
        mEndPos = mWaveformView!!.end
        mMaxPos = mWaveformView!!.maxPos()
        mOffset = mWaveformView!!.offset
        mOffsetGoal = mOffset
        updateDisplay()
    }

    override fun waveformZoomOut() {
        mWaveformView!!.zoomOut()
        mStartPos = mWaveformView!!.start
        mEndPos = mWaveformView!!.end
        mMaxPos = mWaveformView!!.maxPos()
        mOffset = mWaveformView!!.offset
        mOffsetGoal = mOffset
        updateDisplay()
    }

    //
    // MarkerListener
    //

    //
    // MarkerListener
    //
    override fun markerDraw() {}

    override fun markerTouchStart(marker: MarkerView?, x: Float) {
        mTouchDragging = true
        mTouchStart = x
        mTouchInitialStartPos = mStartPos
        mTouchInitialEndPos = mEndPos
    }

    override fun markerTouchMove(marker: MarkerView?, x: Float) {
        val delta = x - mTouchStart
        if (marker === mStartMarker) {
            mStartPos = trap((mTouchInitialStartPos + delta).toInt())
            mEndPos = trap((mTouchInitialEndPos + delta).toInt())
        } else {
            mEndPos = trap((mTouchInitialEndPos + delta).toInt())
            if (mEndPos < mStartPos) mEndPos = mStartPos
        }
        updateDisplay()
    }

    override fun markerTouchEnd(marker: MarkerView?) {
        mTouchDragging = false
        if (marker === mStartMarker) {
            setOffsetGoalStart()
        } else {
            setOffsetGoalEnd()
        }
    }

    override fun markerLeft(marker: MarkerView?, velocity: Int) {
        mKeyDown = true
        if (marker === mStartMarker) {
            val saveStart = mStartPos
            mStartPos = trap(mStartPos - velocity)
            mEndPos = trap(mEndPos - (saveStart - mStartPos))
            setOffsetGoalStart()
        }
        if (marker === mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity)
                mEndPos = mStartPos
            } else {
                mEndPos = trap(mEndPos - velocity)
            }
            setOffsetGoalEnd()
        }
        updateDisplay()
    }

    override fun markerRight(marker: MarkerView?, velocity: Int) {
        mKeyDown = true
        if (marker === mStartMarker) {
            val saveStart = mStartPos
            mStartPos += velocity
            if (mStartPos > mMaxPos) mStartPos = mMaxPos
            mEndPos += mStartPos - saveStart
            if (mEndPos > mMaxPos) mEndPos = mMaxPos
            setOffsetGoalStart()
        }
        if (marker === mEndMarker) {
            mEndPos += velocity
            if (mEndPos > mMaxPos) mEndPos = mMaxPos
            setOffsetGoalEnd()
        }
        updateDisplay()
    }

    override fun markerEnter(marker: MarkerView?) {}

    override fun markerKeyUp() {
        mKeyDown = false
        updateDisplay()
    }

    override fun markerFocus(marker: MarkerView?) {
        mKeyDown = false
        if (marker === mStartMarker) {
            setOffsetGoalStartNoUpdate()
        } else {
            setOffsetGoalEndNoUpdate()
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler!!.postDelayed({ updateDisplay() }, 100)
    }

    //
    // Internal methods
    //

    //
    // Internal methods
    //
    /**
     * Called from both onCreate and onConfigurationChanged
     * (if the user switched layouts)
     */
    private fun loadGui() {
        // Inflate our UI from its XML layout description.
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mDensity = metrics.density
        mMarkerLeftInset = (46 * mDensity).toInt()
        mMarkerRightInset = (48 * mDensity).toInt()
        mMarkerTopOffset = (10 * mDensity).toInt()
        mMarkerBottomOffset = (10 * mDensity).toInt()
        mStartText = binding.starttext
        mStartText!!.addTextChangedListener(mTextWatcher)
        mEndText = binding.endtext
        mEndText!!.addTextChangedListener(mTextWatcher)
        mPlayButton = binding.play
        mPlayButton!!.setOnClickListener(mPlayListener)
        mRewindButton = binding.rew
        mRewindButton!!.setOnClickListener(mRewindListener)
        mFfwdButton = binding.ffwd
        mFfwdButton!!.setOnClickListener(mFfwdListener)
        val markStartButton = binding.markStart
        markStartButton.setOnClickListener(mMarkStartListener)
        val markEndButton = binding.markEnd
        markEndButton.setOnClickListener(mMarkEndListener)
        enableDisableButtons()
        mWaveformView = binding.waveform
        mWaveformView?.setListener(this)
        mInfo = binding.info
        mInfo!!.text = mCaption
        mMaxPos = 0
        mLastDisplayedStartPos = -1
        mLastDisplayedEndPos = -1
        if (mSoundFile != null && !mWaveformView!!.hasSoundFile()) {
            mWaveformView?.setSoundFile(mSoundFile)
            mWaveformView?.recomputeHeights(mDensity)
            mMaxPos = mWaveformView!!.maxPos()
        }
        mStartMarker = binding.startmarker
        mStartMarker?.setListener(this)
        mStartMarker?.alpha = 1f
        mStartMarker?.isFocusable = true
        mStartMarker?.isFocusableInTouchMode = true
        mStartVisible = true
        mEndMarker = findViewById<View>(R.id.endmarker) as MarkerView
        mEndMarker?.setListener(this)
        mEndMarker?.alpha = 1f
        mEndMarker?.isFocusable = true
        mEndMarker?.isFocusableInTouchMode = true
        mEndVisible = true
        updateDisplay()
    }

    private fun loadFromFile() {
        mFile = File(mFilename)
        title = mTitleName
        mLoadingLastUpdateTime = getCurrentTime()
        mLoadingKeepGoing = true
        mFinishActivity = false
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog!!.setTitle(R.string.progress_dialog_loading)
        mProgressDialog!!.setCancelable(true)
        mProgressDialog!!.setOnCancelListener {
            mLoadingKeepGoing = false
            mFinishActivity = true
        }
        mProgressDialog!!.show()
        val listener: SoundFile.ProgressListener = object : SoundFile.ProgressListener {
            override fun reportProgress(fractionComplete: Double): Boolean {
                val now = getCurrentTime()
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog!!.progress = (mProgressDialog!!.max * fractionComplete).toInt()
                    mLoadingLastUpdateTime = now
                }
                return mLoadingKeepGoing
            }
        }

        // Load the sound file in a background thread
        mLoadSoundFileThread = object : Thread() {
            override fun run() {
                try {
                    mSoundFile = SoundFile.create(mFile!!.absolutePath, listener)
                    if (mSoundFile == null) {
                        mProgressDialog!!.dismiss()
                        val name = mFile!!.name.toLowerCase(Locale.ROOT)
                        val components = name.split("\\.".toRegex()).toTypedArray()
                        val err: String
                        if (components.size < 2) {
                            err = resources.getString(
                                R.string.no_extension_error
                            )
                        } else {
                            err = resources.getString(
                                R.string.bad_extension_error
                            ) + " " +
                                    components[components.size - 1]
                        }
                        val runnable =
                            Runnable { showFinalAlert(Exception(), err) }
                        mHandler!!.post(runnable)
                        return
                    }
                    mPlayer = SamplePlayer(mSoundFile!!)
                } catch (e: Exception) {
                    mProgressDialog!!.dismiss()
                    e.printStackTrace()
                    mInfoContent = e.toString()
                    runOnUiThread { mInfo!!.text = mInfoContent }
                    val runnable =
                        Runnable { showFinalAlert(e, resources.getText(R.string.read_error)) }
                    mHandler!!.post(runnable)
                    return
                }
                mProgressDialog!!.dismiss()
                if (mLoadingKeepGoing) {
                    val runnable = Runnable { finishOpeningSoundFile() }
                    mHandler!!.post(runnable)
                } else if (mFinishActivity) {
                   finish()
                }
            }
        }
        mLoadSoundFileThread?.start()
    }

    private fun finishOpeningSoundFile() {
        mWaveformView?.setSoundFile(mSoundFile)
        mWaveformView?.recomputeHeights(mDensity)
        mMaxPos = mWaveformView!!.maxPos()
        mLastDisplayedStartPos = -1
        mLastDisplayedEndPos = -1
        mTouchDragging = false
        mOffset = 0
        mOffsetGoal = 0
        mFlingVelocity = 0
        resetPositions()
        if (mEndPos > mMaxPos) mEndPos = mMaxPos
        mCaption = mSoundFile?.filetype.toString() + ", " +
                mSoundFile?.sampleRate + " Hz, " +
                mSoundFile?.avgBitrateKbps + " kbps, " +
                formatTime(mMaxPos) + " " +
                resources.getString(R.string.time_seconds)
        mInfo!!.text = mCaption
        updateDisplay()
    }

    @Synchronized
    private fun updateDisplay() {
        if (mIsPlaying) {
            val now: Int = mPlayer!!.currentPosition
            val frames: Int = mWaveformView!!.millisecsToPixels(now)
            mWaveformView!!.setPlayback(frames)
            setOffsetGoalNoUpdate(frames - mWidth / 2)
            if (now >= mPlayEndMsec) {
                handlePause()
            }
        }
        if (!mTouchDragging) {
            var offsetDelta: Int
            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80
                } else {
                    mFlingVelocity = 0
                }
                mOffset += offsetDelta
                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2
                    mFlingVelocity = 0
                }
                if (mOffset < 0) {
                    mOffset = 0
                    mFlingVelocity = 0
                }
                mOffsetGoal = mOffset
            } else {
                offsetDelta = mOffsetGoal - mOffset
                if (offsetDelta > 10) offsetDelta /= 10 else if (offsetDelta > 0) offsetDelta =
                    1 else if (offsetDelta < -10) offsetDelta /= 10 else if (offsetDelta < 0) offsetDelta = -1 else offsetDelta = 0
                mOffset += offsetDelta
            }
        }
        mWaveformView?.setParameters(mStartPos, mEndPos, mOffset)
        mWaveformView?.invalidate()
        mStartMarker?.contentDescription = resources.getText(R.string.start_marker).toString() + " " +
                formatTime(mStartPos)
        mEndMarker?.contentDescription = (resources.getText(R.string.end_marker).toString() + " " +
                formatTime(mEndPos))
        var startX = mStartPos - mOffset - mMarkerLeftInset
        if (startX + mStartMarker!!.width >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler!!.postDelayed({
                    mStartVisible = true
                    mStartMarker?.alpha = 1f
                }, 0)
            }
        } else {
            if (mStartVisible) {
                mStartMarker?.alpha = 0f
                mStartVisible = false
            }
            startX = 0
        }
        var endX: Int = mEndPos - mOffset - mEndMarker!!.width + mMarkerRightInset
        if (endX + mEndMarker!!.width >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler!!.postDelayed({
                    mEndVisible = true
                    mEndMarker!!.alpha = 1f
                }, 0)
            }
        } else {
            if (mEndVisible) {
                mEndMarker!!.alpha = 0f
                mEndVisible = false
            }
            endX = 0
        }
        var params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            startX,
            mMarkerTopOffset,
            -mStartMarker!!.width,
            -mStartMarker!!.height
        )
        mStartMarker!!.layoutParams = params
        params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            endX,
            mWaveformView!!.measuredHeight - mEndMarker!!.height - mMarkerBottomOffset,
            -mStartMarker!!.width,
            -mStartMarker!!.height
        )
        mEndMarker!!.layoutParams = params
    }

    private val mTimerRunnable: Runnable = object : Runnable {
        override fun run() {
            // Updating an EditText is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos &&
                !mStartText!!.hasFocus()
            ) {
                mStartText!!.text = formatTime(mStartPos)
                mLastDisplayedStartPos = mStartPos
            }
            if (mEndPos != mLastDisplayedEndPos &&
                !mEndText!!.hasFocus()
            ) {
                mEndText!!.text = formatTime(mEndPos)
                mLastDisplayedEndPos = mEndPos
            }
            mHandler!!.postDelayed(this, 100)
        }
    }

    private fun enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton!!.setImageResource(android.R.drawable.ic_media_pause)
            mPlayButton!!.contentDescription = resources.getText(R.string.stop)
        } else {
            mPlayButton!!.setImageResource(android.R.drawable.ic_media_play)
            mPlayButton!!.contentDescription = resources.getText(R.string.play)
        }
    }

    private fun resetPositions() {
        mStartPos = mWaveformView!!.secondsToPixels(0.0)
        mEndPos = mWaveformView!!.secondsToPixels(15.0)
    }

    private fun trap(pos: Int): Int {
        if (pos < 0) return 0
        return if (pos > mMaxPos) mMaxPos else pos
    }

    private fun setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2)
    }

    private fun setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2)
    }

    private fun setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2)
    }

    private fun setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2)
    }

    private fun setOffsetGoal(offset: Int) {
        setOffsetGoalNoUpdate(offset)
        updateDisplay()
    }

    private fun setOffsetGoalNoUpdate(offset: Int) {
        if (mTouchDragging) {
            return
        }
        mOffsetGoal = offset
        if (mOffsetGoal + mWidth / 2 > mMaxPos) mOffsetGoal = mMaxPos - mWidth / 2
        if (mOffsetGoal < 0) mOffsetGoal = 0
    }

    private fun formatTime(pixels: Int): String {
        return if (mWaveformView != null && mWaveformView!!.isInitialized) {
            formatDecimal(mWaveformView!!.pixelsToSeconds(pixels))
        } else {
            ""
        }
    }

    private fun formatDecimal(x: Double): String {
        var xWhole = x.toInt()
        var xFrac = (100 * (x - xWhole) + 0.5).toInt()
        if (xFrac >= 100) {
            xWhole++ //Round up
            xFrac -= 100 //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10 //we need a fraction that is 2 digits long
            }
        }
        return if (xFrac < 10) "$xWhole.0$xFrac" else "$xWhole.$xFrac"
    }

    @Synchronized
    private fun handlePause() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer?.pause()
        }
        mWaveformView?.setPlayback(-1)
        mIsPlaying = false
        enableDisableButtons()
    }

    @Synchronized
    private fun onPlay(startPosition: Int) {
        if (mIsPlaying) {
            handlePause()
            return
        }
        if (mPlayer == null) {
            // Not initialized yet
            return
        }
        try {
            mPlayStartMsec = mWaveformView!!.pixelsToMillisecs(startPosition)
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView!!.pixelsToMillisecs(mStartPos)
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView!!.pixelsToMillisecs(mMaxPos)
            } else {
                mPlayEndMsec = mWaveformView!!.pixelsToMillisecs(mEndPos)
            }
            mPlayer?.setOnCompletionListener(object : SamplePlayer.OnCompletionListener {
                override fun onCompletion() {
                    handlePause()
                }
            })
            mIsPlaying = true
            mPlayer?.seekTo(mPlayStartMsec)
            mPlayer?.start()
            updateDisplay()
            enableDisableButtons()
        } catch (e: Exception) {
            showFinalAlert(e, R.string.play_error)
            return
        }
    }

    /**
     * Show a "final" alert dialog that will exit the activity
     * after the user clicks on the OK button.  If an exception
     * is passed, it's assumed to be an error condition, and the
     * dialog is presented as an error, and the stack trace is
     * logged.  If there's no exception, it's a success message.
     */
    private fun showFinalAlert(e: Exception?, message: CharSequence) {
        val title: CharSequence
        if (e != null) {
            log("Error: $message")
            log((getStackTrace(e)))
            title = resources.getText(R.string.alert_title_failure)
            setResult(RESULT_CANCELED, Intent())
        } else {
            log("Success: $message")
            title = resources.getText(R.string.alert_title_success)
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                R.string.alert_ok_button
            ) { dialog, whichButton -> finish() }
                .setCancelable(false)
            .show()
    }

    private fun showFinalAlert(e: Exception, messageResourceId: Int) {
        showFinalAlert(e, resources.getText(messageResourceId))
    }

    private fun makeRingtoneFilename(title: CharSequence, extension: String): String {
        return Utils.createTrimFile(title.toString() + System.currentTimeMillis(), extension)
    }

    fun saveRingtone(title: CharSequence) {
        val startTime: Double = mWaveformView!!.pixelsToSeconds(mStartPos)
        val endTime: Double = mWaveformView!!.pixelsToSeconds(mEndPos)
        val startFrame: Int = mWaveformView!!.secondsToFrames(startTime)
        val endFrame: Int = mWaveformView!!.secondsToFrames(endTime)
        val duration = (endTime - startTime + 0.5).toInt()

        // Create an indeterminate progress dialog
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setTitle(R.string.progress_dialog_saving)
        mProgressDialog!!.isIndeterminate = true
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()

        // Save the sound file in a background thread
        mSaveSoundFileThread = object : Thread() {
            override fun run() {
                // Try AAC first.
                val outPath = makeRingtoneFilename(title, ".mp3")
                val outFile = File(outPath)
                try {
                    // Write the new file
                    mSoundFile!!.WriteFile(outFile, startFrame, endFrame - startFrame)
                } catch (e: Exception) {
                    // log the error and try to create a .wav file instead
                    if (outFile.exists()) {
                        outFile.delete()
                    }
                    val writer = StringWriter()
                    e.printStackTrace(PrintWriter(writer))
                    log("Error: Failed to create $outPath")
                    log(writer.toString())
                }
                mProgressDialog!!.dismiss()
                val finalOutPath: String = outPath
                val runnable = Runnable {
                    afterSavingRingtone(
                        title,
                        finalOutPath,
                        duration
                    )
                }
                mHandler!!.post(runnable)
            }
        }
        mSaveSoundFileThread?.start()
    }

    private fun afterSavingRingtone(
        title: CharSequence,
        outPath: String,
        duration: Int
    ) {
        log("Duration : $duration")
       log("afterSavingRingtone $outPath")
        val mFile = File(outPath)
        val mimeTypeFile: MimeTypeFile? = Utils.mediaTypeSupport()[mFile.extension]
        mimeTypeFile?.name = title.toString() + mimeTypeFile?.extension
        val importFiles = ImportFilesModel(mimeTypeFile, outPath, 0, false, Utils.getUUId())
        importingData(importFiles,title.toString())
    }

    private fun onSave() {
        if (mIsPlaying) {
            handlePause()
        }
        enterTrimTitle(mTitleName!!)
    }

    private val mPlayListener: View.OnClickListener = View.OnClickListener { onPlay(mStartPos) }

    private val mRewindListener: View.OnClickListener = View.OnClickListener {
        if (mIsPlaying) {
            var newPos: Int = mPlayer!!.currentPosition - 5000
            if (newPos < mPlayStartMsec) newPos = mPlayStartMsec
            mPlayer!!.seekTo(newPos)
        } else {
            mStartMarker!!.requestFocus()
            markerFocus(mStartMarker)
        }
    }

    private val mFfwdListener: View.OnClickListener = View.OnClickListener {
        if (mIsPlaying) {
            var newPos: Int = 5000 + mPlayer!!.currentPosition
            if (newPos > mPlayEndMsec) newPos = mPlayEndMsec
            mPlayer!!.seekTo(newPos)
        } else {
            mEndMarker!!.requestFocus()
            markerFocus(mEndMarker)
        }
    }

    private val mMarkStartListener: View.OnClickListener = View.OnClickListener {
        if (mIsPlaying) {
            mStartPos = mWaveformView!!.millisecsToPixels(
                mPlayer!!.currentPosition
            )
            updateDisplay()
        }
    }

    private val mMarkEndListener: View.OnClickListener = View.OnClickListener {
        if (mIsPlaying) {
            mEndPos = mWaveformView!!.millisecsToPixels(
                mPlayer!!.currentPosition
            )
            updateDisplay()
            handlePause()
        }
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int, before: Int, count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {
            if (mStartText!!.hasFocus()) {
                try {
                    mStartPos = mWaveformView!!.secondsToPixels(
                        mStartText!!.text.toString().toDouble()
                    )
                    updateDisplay()
                } catch (e: NumberFormatException) {
                }
            }
            if (mEndText!!.hasFocus()) {
                try {
                    mEndPos = mWaveformView!!.secondsToPixels(
                        mEndText!!.text.toString().toDouble()
                    )
                    updateDisplay()
                } catch (e: NumberFormatException) {
                }
            }
        }
    }

    private fun getCurrentTime(): Long {
        return System.nanoTime() / 1000000
    }

    private fun getStackTrace(e: Exception): String {
        val writer = StringWriter()
        e.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }
}