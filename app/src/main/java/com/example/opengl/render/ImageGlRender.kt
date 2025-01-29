package com.example.opengl.render

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ImageGlRender : GLSurfaceView.Renderer {
    private var ptr: Long = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        ptr = createImageRender()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        resize(width, height, ptr)
    }

    override fun onDrawFrame(gl: GL10?) {
        draw(ptr)
    }

    /**
     * 需要在gl线程调用
     *
     * `glsView.queueEvent { imageGlRender.setImage(file.absolutePath) }`
     */
    fun setImage(path: String) {
        setImagePath(path, ptr)
    }

    private external fun createImageRender() : Long

    private external fun draw(ptr: Long)

    private external fun resize(width: Int, height: Int, ptr: Long)

    private external fun setImagePath(path: String, ptr: Long)
}