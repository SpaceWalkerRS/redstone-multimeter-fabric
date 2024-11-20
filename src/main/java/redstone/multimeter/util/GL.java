package redstone.multimeter.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

public class GL {

	private static boolean separateBlend;

	public static void init() {
		separateBlend = GLContext.getCapabilities().OpenGL14;
	}

	public static void blendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		if (separateBlend) {
			GL14.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
		} else {
			GL11.glBlendFunc(sfactorRGB, dfactorRGB);
		}
	}
}
