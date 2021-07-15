package bg.forcar.mobile.utils;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import bg.forcar.mobile.R;

public class ShaderUtils {

    public static Shader getTextShader(Context context, @ColorRes Integer startColor, @ColorRes Integer endColor, TextView textView) {
        // From left to right gradient
        return new LinearGradient(0, 0, textView.getPaint().measureText(textView.getText().toString()), 0,
                new int[]{context.getColor(startColor), context.getColor(endColor)},
                null, Shader.TileMode.CLAMP);
    }
}
