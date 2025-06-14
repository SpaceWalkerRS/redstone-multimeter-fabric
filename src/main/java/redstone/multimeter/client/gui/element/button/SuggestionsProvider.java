package redstone.multimeter.client.gui.element.button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resource.Identifier;
import net.minecraft.util.registry.MappedRegistry;

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

	static <V> SuggestionsProvider matching(MappedRegistry<String, V> registry, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isEmpty() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestMatching(input, registry.keySet());
			}
		};
	}

	static <V> SuggestionsProvider resources(MappedRegistry<Identifier, V> registry, boolean suggestAllOnBlankInput) {
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

	static List<String> suggestResources(String input, Collection<Identifier> resources) {
		Set<String> identifiersStart = new TreeSet<>();
		Set<String> identifiersAnywhere = new TreeSet<>();
		Set<String> namespacesStart = new TreeSet<>();
		Set<String> namespacesAnywhere = new TreeSet<>();

		boolean hasSeparator = (input.indexOf(':') > 0);

		for (Identifier dimension : resources) {
			String identifier = dimension.toString();
			String namespace = dimension.getNamespace() + ":";
			String path = dimension.getPath();

			if (hasSeparator && identifier.length() > input.length()) {
				if (identifier.startsWith(input)) {
					identifiersStart.add(identifier);
				}
			}
			if (!hasSeparator && namespace.length() > input.length()) {
				if (namespace.startsWith(input)) {
					namespacesStart.add(namespace);
				} else if (namespace.contains(input)) {
					namespacesAnywhere.add(namespace);
				}
			}
			if (!hasSeparator && path.length() > input.length()) {
				if (path.startsWith(input)) {
					identifiersStart.add(identifier);
				} else if (path.contains(input)) {
					identifiersAnywhere.add(identifier);
				}
			}
		}

		List<String> suggestions = new ArrayList<>();

		suggestions.addAll(namespacesStart);
		suggestions.addAll(namespacesAnywhere);
		suggestions.addAll(identifiersStart);
		suggestions.addAll(identifiersAnywhere);

		return suggestions;
	}
}
