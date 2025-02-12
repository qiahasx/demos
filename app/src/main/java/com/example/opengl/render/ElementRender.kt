package com.example.opengl.render

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ElementRender(val mode: Int) : GLSurfaceView.Renderer {
    private var pRender: Long = 0
    private external fun initOpenGL(mode: Int): Long

    private external fun draw(pRender: Long)

    private external fun resize(pRender: Long, width: Int, height: Int)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        pRender = initOpenGL(mode)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        resize(pRender, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        draw(pRender)
    }
}