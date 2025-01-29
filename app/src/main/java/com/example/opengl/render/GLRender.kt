package com.example.opengl.render

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRender : GLSurfaceView.Renderer {
    private external fun initOpenGL()

    private external fun draw()

    private external fun resize(width: Int, height: Int)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        initOpenGL()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        resize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        draw()
    }
}