package com.atakmap.android.doomtak;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.atakmap.coremap.log.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class DoomTakGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "DoomTakGLRenderer";
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureBuffer;

    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 position;" +
                    "attribute vec2 texCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "    gl_Position = position;" +
                    "    vTexCoord = texCoord;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec2 vTexCoord;" +
                    "uniform sampler2D texture;" +
                    "void main() {" +
                    "    gl_FragColor = texture2D(texture, vTexCoord);" +
                    "}";

    private static final float[] QUAD_VERTICES = {
            -1.0f, 1.0f,  // top left
            -1.0f, -1.0f, // bottom left
            1.0f, 1.0f,   // top right
            1.0f, -1.0f   // bottom right
    };

    private static final float[] TEXTURE_COORDS = {
            0.0f, 0.0f,  // top left
            0.0f, 1.0f,  // bottom left
            1.0f, 0.0f,  // top right
            1.0f, 1.0f   // bottom right
    };

    public native void doomUpdate();
    public native byte[] getFramebuffer(int channels);

    public DoomTakGLRenderer() {
        // Allocate buffers for vertices and texture coordinates.
        ByteBuffer vb = ByteBuffer.allocateDirect(QUAD_VERTICES.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(QUAD_VERTICES);
        vertexBuffer.position(0);

        ByteBuffer tb = ByteBuffer.allocateDirect(TEXTURE_COORDS.length * 4);
        tb.order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer();
        textureBuffer.put(TEXTURE_COORDS);
        textureBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Initialize OpenGL settings.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Load shaders and create a program.
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        // Get attribute and uniform locations.
        int positionHandle = GLES20.glGetAttribLocation(program, "position");
        int texCoordHandle = GLES20.glGetAttribLocation(program, "texCoord");
        int textureHandle = GLES20.glGetUniformLocation(program, "texture");

        // Enable vertex and texture coordinate arrays.
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        // Set vertex attributes.
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        // Create and bind texture.
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // Set texture parameters.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Adjust viewport based on surface dimensions.
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the screen.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Update DOOM game state.
        doomUpdate();

        // Get the framebuffer from native code (assuming 3 channels for RGB).
        byte[] framebuffer = getFramebuffer(3);

        // Update the texture with the new framebuffer data.
        ByteBuffer buffer = ByteBuffer.wrap(framebuffer);
        // DOOM default screen dimensions.
        int screenWidth = 320;
        int screenHeight = 200;
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, screenWidth, screenHeight, 0,
                GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, buffer);

        // Draw the quad.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + type + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }
}
