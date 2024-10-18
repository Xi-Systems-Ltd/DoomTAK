package com.atakmap.android.doomtak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.doomtak.audio.DoomTakMusicPlayer;
import com.atakmap.android.doomtak.audio.DoomTakSoundPlayer;
import com.atakmap.android.doomtak.input.GyroMouseListener;
import com.atakmap.android.doomtak.plugin.R;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;

public class DoomTakDropDownReceiver extends DropDownReceiver implements
        DropDown.OnStateListener {

    public static final String TAG = DoomTakDropDownReceiver.class
            .getSimpleName();

    public static final String SHOW_PLUGIN = "com.atakmap.android.doomtak.SHOW_PLUGIN";
    private final View dropdownView;
    private final Context pluginContext;
    private GLSurfaceView glSurfaceView;
    private final JoystickView joystickView;
    private final ImageButton buttonInteract;
    private final ImageButton buttonShoot;
    private final Button escButton;
    private final Button button1;
    private final Button button2;
    private final Button button3;
    private final Button button4;
    private final Button button5;
    private final Button button6;
    private final Button button7;
    private final GyroMouseListener gyroMouseListener;
    private final DoomTakSoundPlayer doomTakSoundPlayer = new DoomTakSoundPlayer();
    private final DoomTakMusicPlayer doomTakMusicPlayer;

    public native void mouseMove(int deltaX, int deltaY);

    public native void keyDown(int key);

    public native void keyUp(int key);

    public native void joyButtonDown(int button);

    public native void joyButtonUp(int button);

    public native void joystick(int x, int y);

    @SuppressLint("ClickableViewAccessibility")
    public DoomTakDropDownReceiver(final MapView mapView,
                                   final Context context) {
        super(mapView);
        this.pluginContext = context;

        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        dropdownView = PluginLayoutInflater.inflate(context,
                R.layout.main_layout, null);

        joystickView = dropdownView.findViewById(R.id.joystickView);
        joystickView.setJoystickListener(new JoystickView.JoystickListener() {
            @Override
            public void onJoystickDown() {
                // Press "strafe" joystick button.
                Log.d("JoystickListener", "Joystick pressed.");
                joyButtonDown(1);
            }

            @Override
            public void onJoystickUp() {
                // Release "strafe" joystick button.
                Log.d("JoystickListener", "Joystick unpressed.");
                joyButtonUp(1);
            }

            @Override
            public void onJoystickMoved(float xPercent, float yPercent) {
                Log.d("JoystickListener", "Joystick moved " + xPercent + ", " + yPercent + ".");
                int xMovement = (int) (xPercent * 100);
                int yMovement = (int) (yPercent * 100);
                Log.d("JoystickListener", "Joystick movement " + xMovement + ", " + yMovement + ".");
                joystick(xMovement, yMovement);
            }
        });

        buttonInteract = dropdownView.findViewById(R.id.buttonInteract);
        buttonInteract.setOnTouchListener(getJoyButtonTouchListener(3));
        buttonShoot = dropdownView.findViewById(R.id.buttonShoot);
        buttonShoot.setOnTouchListener(getJoyButtonTouchListener(0));

        escButton = dropdownView.findViewById(R.id.esc_button);
        escButton.setOnTouchListener(getKeyTouchListener(27));

        button1 = dropdownView.findViewById(R.id.button1);
        button1.setOnTouchListener(getKeyTouchListener('1'));
        button2 = dropdownView.findViewById(R.id.button2);
        button2.setOnTouchListener(getKeyTouchListener('2'));
        button3 = dropdownView.findViewById(R.id.button3);
        button3.setOnTouchListener(getKeyTouchListener('3'));
        button4 = dropdownView.findViewById(R.id.button4);
        button4.setOnTouchListener(getKeyTouchListener('4'));
        button5 = dropdownView.findViewById(R.id.button5);
        button5.setOnTouchListener(getKeyTouchListener('5'));
        button6 = dropdownView.findViewById(R.id.button6);
        button6.setOnTouchListener(getKeyTouchListener('6'));
        button7 = dropdownView.findViewById(R.id.button7);
        button7.setOnTouchListener(getKeyTouchListener('7'));

        gyroMouseListener = new GyroMouseListener(mapView.getContext(), 39.0);
        gyroMouseListener.setMouseMovementListener(this::mouseMove);

        doomTakMusicPlayer = new DoomTakMusicPlayer(pluginContext);
    }

    @SuppressLint("ClickableViewAccessibility")
    private @NonNull View.OnTouchListener getJoyButtonTouchListener(int button) {
        return (View view, MotionEvent motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("joyButtonTouchListener", "Button " + button + " pressed.");
                    joyButtonDown(button);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("joyButtonTouchListener", "Button " + button + " unpressed.");
                    joyButtonUp(button);
                    break;
            }
            return false;
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    private @NonNull View.OnTouchListener getKeyTouchListener(int key) {
        return (View view, MotionEvent motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("keyTouchListener", "Key " + key + " pressed.");
                    keyDown(key);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("keyTouchListener", "Key " + key + " unpressed.");
                    keyUp(key);
                    break;
            }
            return false;
        };
    }

    /**************************** PUBLIC METHODS *****************************/

    public void disposeImpl() {
        gyroMouseListener.stop();
        doomTakSoundPlayer.stop();
        doomTakMusicPlayer.stopMusic();
        doomTakMusicPlayer.close();
    }

    /**************************** INHERITED METHODS *****************************/

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_PLUGIN)) {
            // Initialize the GLSurfaceView
            glSurfaceView = dropdownView.findViewById(R.id.opengl_surface_view);
            glSurfaceView.setEGLContextClientVersion(2); // Set OpenGL ES 2.0 context

            // Set the renderer to the native layer
            glSurfaceView.setRenderer(new DoomTakGLRenderer(pluginContext));

            doomTakSoundPlayer.start();
            doomTakMusicPlayer.startMusic();

            Log.d(TAG, "showing plugin drop down");
            showDropDown(dropdownView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false, this);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
        gyroMouseListener.start();
        doomTakMusicPlayer.startMusic();
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
        gyroMouseListener.stop();
        doomTakMusicPlayer.stopMusic();
    }
}
