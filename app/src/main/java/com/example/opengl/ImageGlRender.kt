package com.example.opengl

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ImageGlRender : GLSurfaceView.Renderer {
    private external fun initOpenGL()

    private external fun draw()

    private external fun resize(width: Int, height: Int)

    private external fun setImagePath(path: String)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        initOpenGL()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        resize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        draw()
    }

    /**
     * 需要在gl线程调用
     *
     * `glsView.queueEvent { imageGlRender.setImage(file.absolutePath) }`
     */
    fun setImage(path: String) {
        setImagePath(path)
    }
}