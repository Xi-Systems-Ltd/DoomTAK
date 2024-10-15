package com.atakmap.android.doomtak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.atak.plugins.impl.PluginLayoutInflater;
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
    private final Button enterButton;
    private final Button escButton;
    private final JoystickView joystickView;
    private final ImageButton buttonInteract;
    private final ImageButton buttonShoot;
    private final GyroMouseListener gyroMouseListener;

    public native void mouseMove(int deltaX, int deltaY);

    public native void keyDown(int key);

    public native void keyUp(int key);

    public native void keyPress(int key);

    public native void joyButtonDown(int button);

    public native void joyButtonUp(int button);

    public native void joyButtonPress(int button);

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

        enterButton = dropdownView.findViewById(R.id.enter_button);
        enterButton.setOnClickListener(view -> {
            keyPress(13);
        });

        escButton = dropdownView.findViewById(R.id.esc_button);
        escButton.setOnClickListener(view -> {
            keyPress(27);
        });

        joystickView = dropdownView.findViewById(R.id.joystickView);
        joystickView.setJoystickListener(new JoystickView.JoystickListener() {
            @Override
            public void onJoystickDown() {
                // Press "strafe" joystick button.
                Log.d("JoystickListener","Joystick pressed.");
                joyButtonDown(1);
            }

            @Override
            public void onJoystickUp() {
                // Release "strafe" joystick button.
                Log.d("JoystickListener","Joystick unpressed.");
                joyButtonUp(1);
            }

            @Override
            public void onJoystickMoved(float xPercent, float yPercent) {
                Log.d("JoystickListener","Joystick moved " + xPercent + ", " + yPercent + ".");
                int xMovement = (int) (xPercent * 100);
                int yMovement = (int) (yPercent * 100);
                Log.d("JoystickListener","Joystick movement " + xMovement + ", " + yMovement + ".");
                joystick(xMovement, yMovement);
            }
        });

        buttonInteract = dropdownView.findViewById(R.id.buttonInteract);
        buttonInteract.setOnTouchListener((view, motionEvent) -> {
            switch ( motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("buttonAListener","Button A pressed.");
                    joyButtonDown(3);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("buttonAListener","Button A unpressed.");
                    joyButtonUp(3);
                    break;
            }
            return false;
        });

        buttonShoot = dropdownView.findViewById(R.id.buttonShoot);
        buttonShoot.setOnTouchListener((view, motionEvent) -> {
            switch ( motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("buttonAListener","Button A pressed.");
                    joyButtonDown(0);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("buttonAListener","Button A unpressed.");
                    joyButtonUp(0);
                    break;
            }
            return false;
        });

        gyroMouseListener = new GyroMouseListener(mapView.getContext(), 39.0);
        gyroMouseListener.setMouseMovementListener(this::mouseMove);
    }

    /**************************** PUBLIC METHODS *****************************/

    public void disposeImpl() {
        gyroMouseListener.stop();
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
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
        gyroMouseListener.stop();
    }
}
