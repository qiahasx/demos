package com.example.opengl

import com.example.common.ui.ButtonItemBean
import com.example.common.util.startActivity
import com.example.opengl.ElementActivity.Companion.selectElement
import com.example.opengl.TransitionActivity.Companion.navTransition

val SELECT_OPENGL_DEMO = listOf(
    ButtonItemBean(R.string.image_gl_render, R.string.image_gl_render_info) { context, _ ->
        context.startActivity(ImageActivity::class.java)
    },
    ButtonItemBean(R.string.cube_gl_render, R.string.cube_gl_render_info) { context, _ ->
        context.startActivity(CubeActivity::class.java)
    },
    ButtonItemBean(R.string.element_gl_render, R.string.element_gl_render_info) { _, mainViewModel ->
        mainViewModel.showBottomSheet(SELECT_ELEMENT_DEMO)
    },
    ButtonItemBean(R.string.transition_render, R.string.transition_render_info) { _, mainViewModel ->
        mainViewModel.showBottomSheet(SELECT_TRANSITION_DEMO)
    }
)

val SELECT_ELEMENT_DEMO = listOf(
    ButtonItemBean(R.string.gl_points, R.string.gl_points_info) { context, _ ->
        context.selectElement(ElementActivity.GL_POINTS)
    },
    ButtonItemBean(R.string.gl_lines, R.string.gl_lines_info) { context, _ ->
        context.selectElement(ElementActivity.GL_LINES)
    },
    ButtonItemBean(R.string.gl_line_loop, R.string.gl_line_loop_info) { context, _ ->
        context.selectElement(ElementActivity.GL_LINE_LOOP)
    },
    ButtonItemBean(R.string.gl_line_strip, R.string.gl_line_strip_info) { context, _ ->
        context.selectElement(ElementActivity.GL_LINE_STRIP)
    },
    ButtonItemBean(R.string.gl_triangles, R.string.gl_triangles_info) { context, _ ->
        context.selectElement(ElementActivity.GL_TRIANGLES)
    },
    ButtonItemBean(R.string.gl_triangle_strip, R.string.gl_triangle_strip_info) { context, _ ->
        context.selectElement(ElementActivity.GL_TRIANGLE_STRIP)
    },
    ButtonItemBean(R.string.gl_triangle_fan, R.string.gl_triangle_fan_info) { context, _ ->
        context.selectElement(ElementActivity.GL_TRIANGLE_FAN)
    }
)

val SELECT_TRANSITION_DEMO = listOf(
    ButtonItemBean(R.string.transition_slide, R.string.transition_slide_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.SLIDE)
    },
    ButtonItemBean(R.string.transition_wipe, R.string.transition_wipe_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.LINEAR_WIPE)
    },
    ButtonItemBean(R.string.transition_radial, R.string.transition_radial_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.RADIAL_UNFOLD)
    },
    ButtonItemBean(R.string.transition_fade, R.string.transition_fade_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.FADE)
    },
    ButtonItemBean(R.string.transition_rotating, R.string.transition_rotating_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.ROTATING)
    },
    ButtonItemBean(R.string.transition_blur, R.string.transition_blur_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.ZOOM_BLUR)
    },
    ButtonItemBean(R.string.transition_burn, R.string.transition_burn_info) { context, _ ->
        context.navTransition(TransitionActivity.TransitionMode.BURN)
    },
)



