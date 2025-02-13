//
// Created by zhengmc01 on 2025/2/13.
//

#include "Render.h"

#ifndef OPENGL_TRAINSTIONRENDER_H
#define OPENGL_TRAINSTIONRENDER_H

#endif //OPENGL_TRAINSTIONRENDER_H

class TransitionRender : public Render {
public:
    TransitionRender() {
        init();
    }
    void init() override;

    void draw() override;
};