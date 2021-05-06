package com.streamaxia.opensdkdemo.listeners;

import android.annotation.SuppressLint;
import android.util.Log;

import com.streamaxia.android.handlers.RtmpHandler;

import java.io.IOException;
import java.net.SocketException;

public class RTMPListenerImpl implements RtmpHandler.RtmpListener {

    private final String TAG = RTMPListenerImpl.class.getSimpleName();

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

}
