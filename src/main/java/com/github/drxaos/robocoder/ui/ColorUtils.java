package com.github.drxaos.robocoder.ui;

import org.jbox2d.common.Color3f;

public class ColorUtils {

    public static Color3f fade(Color3f color3f, float k) {
        return new Color3f(color3f.x * k, color3f.y * k, color3f.z * k);
    }

}
