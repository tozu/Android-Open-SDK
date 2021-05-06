package com.streamaxia.opensdkdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.streamaxia.android.CameraPreview;
import com.streamaxia.android.StreamaxiaPublisher;
import com.streamaxia.android.handlers.EncoderHandler;
import com.streamaxia.android.handlers.RecordHandler;
import com.streamaxia.android.handlers.RtmpHandler;
import com.streamaxia.android.utils.Size;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StreamActivity
        extends AppCompatActivity
        implements
        RecordHandler.RecordListener,
        EncoderHandler.EncodeListener,
        RtmpHandler.RtmpListener {

    private final String TAG = this.getClass().getSimpleName();

    // Set default values for the streamer
    public final static String streamaxiaStreamName = "nx96HyMGijj";

    // The view that displays the camera feed
    @BindView(R.id.preview)
    CameraPreview mCameraView;
    @BindView(R.id.chronometer)
    Chronometer mChronometer;
    @BindView(R.id.start_stop)
    TextView startStopTextView;
    @BindView(R.id.state_text)
    TextView stateTextView;

    private StreamaxiaPublisher mPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stream);

        ButterKnife.bind(this);
        hideStatusBar();

        mPublisher = new StreamaxiaPublisher(mCameraView, this);

        // Listeners
        mPublisher.setEncoderHandler(new EncoderHandler(this));

        // DOES NOT work (logs are omitted)
        // RTMPListenerImpl rtmpListener = new RTMPListenerImpl();
        // mPublisher.setRtmpHandler(new RtmpHandler(rtmpListener));

        // DOES work (logs are shown)
        mPublisher.setRtmpHandler(new RtmpHandler(this));

        // todo(tobias): test to separate Handler(s) instead

        mPublisher.setRecordEventHandler(new RecordHandler(this));

        // Setup Streaming
        mPublisher.setFramerate(30);
        mPublisher.setKeyframeInterval(5);

        mCameraView.startCamera();

        setStreamerDefaultValues();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            stopStreaming();
            stopChronometer();
            startStopTextView.setText("START");
        } else {
            Intent intent = new Intent(this, SplashscreenActivity.class);
            startActivity(intent);
            Toast.makeText(this, "You need to grant persmissions in order to begin streaming.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mCameraView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stopCamera();
        mPublisher.stopPublish();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.start_stop)
    public void startStopStream() {
        if (startStopTextView.getText().toString().toLowerCase().equals("start")) {
            startStopTextView.setText("STOP");
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mPublisher.startPublish("rtmp://eu-west.streamify.io/broadcast/" + streamaxiaStreamName);
            takeSnapshot();
        } else {
            startStopTextView.setText("START");
            stopChronometer();
            mPublisher.stopPublish();
        }
    }

    private void stopStreaming() {
        mPublisher.stopPublish();
    }

    private void takeSnapshot() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCameraView.takeSnapshot(new CameraPreview.SnapshotCallback() {
                    @Override
                    public void onSnapshotTaken(Bitmap image) {
                        //Do something with the snapshot
                    }
                });
            }
        }, 5000);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.setScreenOrientation(newConfig.orientation);
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setStreamerDefaultValues() {
        // Set one of the available resolutions
        List<Size> sizes = mPublisher.getSupportedPictureSizes(getResources().getConfiguration().orientation);
        Size resolution = sizes.get(0);
        mPublisher.setVideoOutputResolution(resolution.width, resolution.height, this.getResources().getConfiguration().orientation);
    }

    @SuppressLint("SetTextI18n")
    private void setStatusMessage(final String msg) {
        runOnUiThread(() -> stateTextView.setText("[" + msg + "]"));
    }

    /*
     * EncoderHandler implementation
     * You can use these callbacks to get events from the streamer
     * */

    @Override
    public void onNetworkWeak() {

    }

    @Override
    public void onNetworkResume() {

    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }


    /*
     * RecordHandler implementation
     * */

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String s) {

    }

    @Override
    public void onRecordFinished(String s) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }


    @Override
    public void onRtmpConnecting(String s) {
        Log.e(TAG, "onRtmpConnecting: " + s);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRtmpConnected(String s) {
        // setStatusMessage(s);
        // startStopTextView.setText("STOP");
        Log.e(TAG, "onRtmpConnecting: " + s);
    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {
        // setStatusMessage("STOPPED");
        Log.e(TAG, "onRtmpStopped");
    }

    @Override
    public void onRtmpDisconnected() {
        // setStatusMessage("Disconnected");
        Log.e(TAG, "onRtmpDisconnected");
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.e(TAG, "onRtmpVideoFpsChanged: " + fps);
    }

    @Override
    public void onRtmpVideoBitrateChanged(double videoBitrate) {
        Log.e(TAG, "onRtmpVideoBitrateChanged: " + videoBitrate);
    }

    @Override
    public void onRtmpAudioBitrateChanged(double audioBitrate) {
        Log.e(TAG, "onRtmpAudioBitrateChanged: " + audioBitrate);
    }

    @Override
    public void onRtmpBitrateChanged(double bitrate) {
        Log.e(TAG, "onRtmpBitrateChanged: " + bitrate);
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        // handleException(e);
        Log.e(TAG, "onRtmpSocketException: " + e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        // handleException(e);
        Log.e(TAG, "onRtmpIOException: " + e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        // handleException(e);
        Log.e(TAG, "onRtmpIllegalArgumentException: " + e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        // handleException(e);
        Log.e(TAG, "onRtmpIllegalStateException: " + e);
    }

    @Override
    public void onRtmpAuthenticationg(String s) {
        Log.e(TAG, "onRtmpAuthenticationg: " + s);
    }

    private void stopChronometer() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.stop();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
        } catch (Exception e1) {
            // Ignore
        }
    }
}
