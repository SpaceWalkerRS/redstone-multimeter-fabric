package redstone.multimeter.client.gui.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import redstone.multimeter.RedstoneMultimeterMod;

public class BuiltInTranslations {

	public static void apply(Map<String, String> storage, List<String> langs) {
		for (String lang : langs) {
			apply(storage, lang);
		}
	}

	private static void apply(Map<String, String> storage, String lang) {
		String path = resourcePath(lang);
		InputStream is = BuiltInTranslations.class.getResourceAsStream(path);

		if (is == null) {
			return;
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;

			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty() || line.charAt(0) == '#') {
					continue;
				}

				String[] parts = line.split("[=]");

				if (parts.length != 2) {
					RedstoneMultimeterMod.LOGGER.warn("ignoring invalid built-in RSMM translation \'" + line + "\'");
					continue;
				}

				String key = parts[0].trim();
				String value = parts[1].trim();

				storage.put(key, unescape(value));
			}
		} catch (Exception e) {
			RedstoneMultimeterMod.LOGGER.warn("unable to load RSMM's built-in translations for " + lang, e);
		}
	}

	private static String unescape(String s) {
		StringBuilder sb = new StringBuilder();

		boolean escaped = false;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (escaped) {
				if (c == 'n') {
					c = '\n';
				}

				escaped = false;
			} else if (c == '\\') {
				escaped = true;
			}

			if (!escaped) {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static String resourcePath(String lang) {
		return "/assets/" + RedstoneMultimeterMod.NAMESPACE + "/lang/" + lang + ".lang";
	}
}
