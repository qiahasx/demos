package com.example.opengl.render

import android.opengl.GLSurfaceView
import com.example.common.BaseApp
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ShaderRender : GLSurfaceView.Renderer {
    private external fun initOpenGL(imagePath: String): Long

    private external fun draw(pRender: Long)

    private external fun resize(pRender: Long, width: Int, height: Int)

    private external fun rotate(pRender: Long, xAngle: Float, yAngle: Float)

    private var pShader: Long = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val imagePath = BaseApp.instance.getExternalFilesDir("image")?.absolutePath.toString()
        pShader = initOpenGL(imagePath)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        resize(pShader, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        draw(pShader)
    }

    fun rotate(x: Float, y: Float) {
        rotate(pShader, -x, -y)
    }
}