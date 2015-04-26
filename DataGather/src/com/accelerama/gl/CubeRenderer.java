package com.accelerama.gl;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import com.accelerama.gl.geometry.Cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A {@link android.opengl.GLSurfaceView.Renderer} which holds and renders
 * a single {@link com.accelerama.gl.geometry.Cube}.
 *
 * @author Six
 * @since March 2, 2015
 */
public class CubeRenderer implements GLSurfaceView.Renderer {

    /**
     * The {@link com.accelerama.gl.geometry.Cube} which will be
     * rendered.
     */
    private Cube cube = new Cube();

    /**
     * Gets the {@link com.accelerama.gl.geometry.Cube} that is rendered
     * using this {@link com.accelerama.gl.CubeRenderer}.
     *
     * @return The {@link com.accelerama.gl.geometry.Cube} that is rendered
     */
    public Cube getCube() {
        return cube;
    }

    /** {@inheritDoc} */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClearDepthf(1f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
    }

    /** {@inheritDoc} */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = 1;
        }
        float aspect = (float)width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /** {@inheritDoc} */
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glScalef(2.5f,2.5f,2.5f);
        gl.glTranslatef(0f, 0f, -6f);

        cube.draw(gl);
    }
}
