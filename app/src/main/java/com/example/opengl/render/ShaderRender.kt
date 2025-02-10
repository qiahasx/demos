package com.example.opengl.render

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ShaderRender : GLSurfaceView.Renderer {
    private external fun initOpenGL(): Long

    private external fun draw(pRender: Long)

    private external fun resize(pRender: Long, width: Int, height: Int)

    private external fun rotateAroundXAxis(pRender: Long, angle: Float)

    private external fun rotateAroundYAxis(pRender: Long, angle: Float)

    private external fun rotate(pRender: Long, xAngle: Float, yAngle: Float)

    private var pShader: Long = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        pShader = initOpenGL()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        resize(pShader, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        draw(pShader)
    }

    fun rotate(x: Float, y: Float) {
//        rotateAroundYAxis(pShader, -x)
//        rotateAroundXAxis(pShader, -y)
        rotate(pShader, -x, -y)
    }
}