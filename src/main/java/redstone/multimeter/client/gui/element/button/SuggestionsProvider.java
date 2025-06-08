package redstone.multimeter.client.gui.element.button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.client.MultimeterClient;

public interface SuggestionsProvider {

	List<String> provide(String input);

	static SuggestionsProvider none() {
		return input -> Collections.emptyList();
	}

	static <T> SuggestionsProvider resources(ResourceKey<Registry<T>> key, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isBlank() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				Minecraft minecraft = MultimeterClient.MINECRAFT;
				RegistryAccess registries = minecraft.level.registryAccess();

				return suggestResources(input, registries.lookupOrThrow(key).keySet());
			}
		};
	}

	static SuggestionsProvider resources(Collection<ResourceLocation> resources, boolean suggestAllOnBlankInput) {
		return input -> {
			if (input.isBlank() && !suggestAllOnBlankInput) {
				return Collections.emptyList();
			} else {
				return suggestResources(input, resources);
			}
		};
	}

	private static List<String> suggestResources(String input, Collection<ResourceLocation> resources) {
		Set<String> identifiers = new TreeSet<>();
		Set<String> namespaces = new TreeSet<>();
		Set<String> paths = new TreeSet<>();

		boolean hasSeparator = (input.indexOf(':') > 0);

		for (ResourceLocation dimension : resources) {
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
