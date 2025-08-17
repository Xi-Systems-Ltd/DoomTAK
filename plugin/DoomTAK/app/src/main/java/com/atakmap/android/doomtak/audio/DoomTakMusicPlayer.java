package com.atakmap.android.doomtak.audio;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

import cn.sherlock.com.sun.media.sound.SF2Soundbank;
import cn.sherlock.com.sun.media.sound.SoftSynthesizer;
import jp.kshoji.javax.sound.midi.InvalidMidiDataException;
import jp.kshoji.javax.sound.midi.MidiUnavailableException;
import jp.kshoji.javax.sound.midi.Receiver;
import jp.kshoji.javax.sound.midi.ShortMessage;

public class DoomTakMusicPlayer implements Closeable {
    private static final String TAG = "DoomTakMusicPlayer";
    private Receiver midiReceiver; // To receive MIDI messages
    private SoftSynthesizer synth;
    private Handler musicHandler = new Handler(Looper.getMainLooper());
    private static final int DOOM_MIDI_RATE = 140;
    private boolean isPlaying = false;

    // Native method to tick MIDI and return message
    private native int tickMidi();

    public DoomTakMusicPlayer(Context context) {
        setupSynthesizer(context);
    }

    // Set up the SoftSynthesizer with the SC-55 soundfont
    private void setupSynthesizer(Context context) {
        try {
            SF2Soundbank sf = new SF2Soundbank(context.getAssets().open("sc55.sf2"));
            synth = new SoftSynthesizer();
            synth.open();
            synth.loadAllInstruments(sf);
            midiReceiver = synth.getReceiver();
        } catch (IOException | MidiUnavailableException e) {
            Log.e("DoomTakMusicPlayer", e.toString());
        }
    }

    // Method to play MIDI sound through the synthesizer
    private void playMidiSound(byte[] msg, int length) {
        if (length > 0) {
            try {
                ShortMessage shortMessage = new ShortMessage();
                shortMessage.setMessage(msg[0], msg[1], msg[2]);
                midiReceiver.send(shortMessage, -1); // Send MIDI message to synthesizer
            } catch (InvalidMidiDataException e) {
                Log.e(TAG, "Invalid MIDI data: " + e.getMessage());
            }
        }
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
                    midiMsg[0] = (byte) (midiMessage & 0xFF);        // MIDI status byte
                    midiMsg[1] = (byte) ((midiMessage >> 8) & 0xFF); // MIDI data byte 1
                    midiMsg[2] = (byte) ((midiMessage >> 16) & 0xFF); // MIDI data byte 2
                    playMidiSound(midiMsg, midiMsg.length); // Play MIDI sound
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

    @Override
    public void close() {
        if (synth != null) {
            synth.close();
        }
    }
}
