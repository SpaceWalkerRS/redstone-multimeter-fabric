package redstone.multimeter.client.gui.element.button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import redstone.multimeter.util.IdRegistry;

public interface SuggestionsProvider {

	List<String> provide(String input);

	static SuggestionsProvider none() {
		return input -> Collections.emptyList();
	}

	static SuggestionsProvider matching(Collection<String> resources, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isEmpty() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestMatching(input, resources);
			}
		};
	}

	static <V> SuggestionsProvider matching(IdRegistry registry, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isEmpty() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestMatching(input, registry.keySet());
			}
		};
	}

	static List<String> suggestMatching(String input, Collection<String> strings) {
		Set<String> start = new TreeSet<>();
		Set<String> anywhere = new TreeSet<>();

		for (String string : strings) {
			if (string.length() > input.length()) {
				if (string.startsWith(input)) {
					start.add(string);
				} else if (string.contains(input)) {
					anywhere.add(string);
				}
			}
		}

		List<String> suggestions = new ArrayList<>();

		suggestions.addAll(start);
		suggestions.addAll(anywhere);

		return suggestions;
	}
}
