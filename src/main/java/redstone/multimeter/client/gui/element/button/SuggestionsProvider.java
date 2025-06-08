package redstone.multimeter.client.gui.element.button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resource.Identifier;
import net.minecraft.util.registry.Registry;

public interface SuggestionsProvider {

	List<String> provide(String input);

	static SuggestionsProvider none() {
		return input -> Collections.emptyList();
	}

	static <V> SuggestionsProvider matching(Registry<String, V> registry, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isEmpty() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestMatching(input, registry.keySet());
			}
		};
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

	static <V> SuggestionsProvider resources(Registry<Identifier, V> registry, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isEmpty() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestResources(input, registry.keySet());
			}
		};
	}

	static SuggestionsProvider resources(Collection<Identifier> resources, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isEmpty() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestResources(input, resources);
			}
		};
	}

	static List<String> suggestMatching(String input, Collection<String> resources) {
		Set<String> suggestions = new TreeSet<>();

		for (String dimension : resources) {
			if (dimension.length() > input.length() && dimension.startsWith(input)) {
				suggestions.add(dimension);
			}
		}

		return new ArrayList<>(suggestions);
	}

	static List<String> suggestResources(String input, Collection<Identifier> resources) {
		Set<String> identifiers = new TreeSet<>();
		Set<String> namespaces = new TreeSet<>();
		Set<String> paths = new TreeSet<>();

		boolean hasSeparator = (input.indexOf(':') > 0);

		for (Identifier dimension : resources) {
			String identifier = dimension.toString();
			String namespace = dimension.getNamespace() + ":";
			String path = dimension.getPath();

			if (hasSeparator && identifier.length() > input.length() && identifier.startsWith(input)) {
				identifiers.add(identifier);
			}
			if (!hasSeparator && namespace.length() > input.length() && namespace.startsWith(input)) {
				namespaces.add(namespace);
			}
			if (!hasSeparator && path.length() > input.length() && path.startsWith(input)) {
				paths.add(path);
			}
		}

		List<String> suggestions = new ArrayList<>();

		if (namespaces.size() > 1) {
			suggestions.addAll(namespaces);
		}
		suggestions.addAll(paths);
		suggestions.addAll(identifiers);

		return suggestions;
	}
}
