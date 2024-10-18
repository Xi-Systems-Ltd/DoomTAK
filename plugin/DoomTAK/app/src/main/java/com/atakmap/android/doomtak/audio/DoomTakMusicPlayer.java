package com.atakmap.android.doomtak.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

public class DoomTakMusicPlayer {
    private static final String TAG = "DoomTakMusicPlayer";
    private MidiManager midiManager;
    private MidiReceiver midiReceiver; // To receive MIDI messages
    private Handler musicHandler = new Handler(Looper.getMainLooper());
    private static final int DOOM_MIDI_RATE = 140; // 140 ticks per second
    private boolean isPlaying = false;

    // Native method to tick MIDI and return message
    private native int tickMidi();

    public DoomTakMusicPlayer(Context context) {
        midiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
        midiReceiver = new MyMidiReceiver(); // Create your MIDI receiver
        discoverMidiDevices();
    }

    // Custom MIDI receiver to handle incoming MIDI messages
    private class MyMidiReceiver extends MidiReceiver {
        @Override
        public void onSend(byte[] msg, int offset, int count, long timestamp) {
            // Process MIDI message to play sound
            playMidiSound(msg, count);
        }
    }

    // Discover MIDI devices (optional for receiving messages)
    private void discoverMidiDevices() {
        MidiDeviceInfo[] infos = midiManager.getDevices();
        for (MidiDeviceInfo info : infos) {
            // You can add any necessary MIDI device connection logic here
        }
    }

    // Method to play MIDI sound through the synthesizer
    private void playMidiSound(byte[] msg, int length) {
        if (length > 0) {
            // Send MIDI message to synthesizer
            if ((msg[0] & 0xF0) == 0x90) { // Note On message
                int note = msg[1]; // Note number
                int velocity = msg[2]; // Velocity
                playNote(note, velocity); // Call playNote method
            }
        }
    }

    // Method to play a MIDI note
    private void playNote(int note, int velocity) {
        int sampleRate = 44100; // Standard sample rate
        int duration = 500; // Duration of the note in ms

        // Create an AudioTrack instance for playback
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, sampleRate * duration / 1000,
                AudioTrack.MODE_STATIC);

        // Generate a sine wave for the note frequency
        byte[] soundData = new byte[sampleRate * duration / 1000];
        double frequency = 440 * Math.pow(2, (note - 69) / 12.0); // Convert MIDI note to frequency
        for (int i = 0; i < soundData.length; i++) {
            soundData[i] = (byte) (Math.sin(2 * Math.PI * frequency * (i / (double) sampleRate)) * 127);
        }

        audioTrack.write(soundData, 0, soundData.length);
        audioTrack.play();
    }

    private Runnable musicRunnable = new Runnable() {
        @Override
        public void run() {
            int midiMessage;

            // Synchronize audio and music update to avoid race conditions
            synchronized (this) {
                while ((midiMessage = tickMidi()) != 0) {
                    // Prepare MIDI message bytes
                    byte[] midiMsg = new byte[3];
                    midiMsg[0] = (byte)(midiMessage & 0xFF);        // MIDI status byte
                    midiMsg[1] = (byte)((midiMessage >> 8) & 0xFF); // MIDI data byte 1
                    midiMsg[2] = (byte)((midiMessage >> 16) & 0xFF);// MIDI data byte 2
                    try {
                        midiReceiver.send(midiMsg, 0, midiMsg.length, -1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // Schedule next tick
            if (isPlaying) {
                musicHandler.postDelayed(this, 1000 / DOOM_MIDI_RATE);
            }
        }
    };

    public void startMusic() {
        isPlaying = true;
        musicHandler.post(musicRunnable);
    }

    public void stopMusic() {
        isPlaying = false;
        musicHandler.removeCallbacks(musicRunnable);
    }
}
