package com.atakmap.android.doomtak.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class DoomTakSoundPlayer {
    private AudioTrack audioTrack;
    private Thread soundThread;
    private boolean isPlaying = false;

    // Native method to get the sound buffer
    private native short[] getSoundBuffer(int length);

    public void start() {
        int sampleRate = 11025;
        int bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );

        audioTrack.play();
        isPlaying = true;

        // Start sound thread
        soundThread = new Thread(() -> {
            while (isPlaying) {
                int bufferLen = 512 * 2 * 2; // 512 samples, 2 bytes per sample, 2 channels
                short[] buffer = getSoundBuffer(bufferLen);

                if (buffer != null) {
                    audioTrack.write(buffer, 0, buffer.length);
                }
            }
        });
        soundThread.start();
    }

    public void stop() {
        isPlaying = false;
        if (soundThread != null) {
            try {
                soundThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
        }
    }
}
