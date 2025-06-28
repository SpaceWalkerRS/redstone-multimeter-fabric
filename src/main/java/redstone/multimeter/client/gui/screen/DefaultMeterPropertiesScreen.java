package redstone.multimeter.client.gui.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.client.gui.element.BlockSelectorList;
import redstone.multimeter.client.gui.element.ScrollableList;
import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.SuggestionsMenu;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.client.gui.element.meter.MeterPropertyElement;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.client.meter.ClientMeterPropertiesManager;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.util.ColorUtils;

public class DefaultMeterPropertiesScreen extends RSMMScreen {

	private static final int SIDE_SPACING = 10;
	private static final int TOP_SPACING = 30;
	private static final int BOTTOM_SPACING = 38;
	private static final int BLOCK_LIST_TOP_BORDER = 6 + 2 * (Button.DEFAULT_HEIGHT + 2) + 2;
	private static final int BLOCK_LIST_BOTTOM_BORDER = 3 + 1 * (Button.DEFAULT_HEIGHT + 2) + 2;
	private static final int PROPERTIES_LIST_TOP_BORDER = 6;
	private static final int PROPERTIES_LIST_BOTTOM_BORDER = 3;

	private final ClientMeterPropertiesManager meterPropertiesManager;
	private final Map<ResourceLocation, EditableMeterProperties> defaults;
	private final Map<ResourceLocation, EditableMeterProperties> overrides;

	private Tab currentTab;
	private ResourceLocation currentBlock;

	private BlockSelectorList blockList;
	private ScrollableList propertiesList;
	private TextField searchbar;
	private Button add;
	private Button remove;
	private TextField create;

	protected DefaultMeterPropertiesScreen() {
		super(Texts.literal("Default Meter Properties"), true);

		this.meterPropertiesManager = this.client.getMeterPropertiesManager();
		this.defaults = new HashMap<>();
		this.overrides = new HashMap<>();

		for (Entry<ResourceLocation, MeterProperties> entry : this.meterPropertiesManager.getDefaults().entrySet()) {
			ResourceLocation key = entry.getKey();
			MeterProperties properties = entry.getValue();

			this.defaults.put(key, new EditableMeterProperties(properties));
		}
		for (Entry<ResourceLocation, MeterProperties> entry : this.meterPropertiesManager.getOverrides().entrySet()) {
			ResourceLocation key = entry.getKey();
			MeterProperties properties = entry.getValue();

			this.overrides.put(key, new EditableMeterProperties(properties));
		}
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean consumed = super.keyPress(keyCode, scanCode, modifiers) || blockList.keyPress(keyCode, scanCode, modifiers);

		if (!consumed) {
			if (remove.isActive() && keyCode == GLFW.GLFW_KEY_KP_SUBTRACT) {
				remove();
				consumed = true;
			} else if (add.isActive() && keyCode == GLFW.GLFW_KEY_KP_ADD) {
				add();
				consumed = true;
			}
		}

		return consumed;
	}

	@Override
	protected void initScreen() {
		int listWidth = (getWidth() - 3 * SIDE_SPACING) / 2;
		int listHeight = getHeight() - TOP_SPACING - BOTTOM_SPACING;

		int x = getX() + SIDE_SPACING;
		int y = getY() + TOP_SPACING;

		blockList = new BlockSelectorList(listWidth, listHeight, BLOCK_LIST_TOP_BORDER, BLOCK_LIST_BOTTOM_BORDER, key -> selectBlock(key));

		blockList.setSpacing(0);
		blockList.setDrawBackground(true);
		blockList.setBlockFilter(id -> id.toString().contains(searchbar.getValue()));
		blockList.setX(x);
		blockList.setY(y);

		x += 2;
		y += 2;
		int tabWidth = (listWidth - 3 * 2) / 2;

		Button defaultsTab = createTabButton(Tab.DEFAULTS, x, y, tabWidth, Button.DEFAULT_HEIGHT);
		Button overridesTab = createTabButton(Tab.OVERRIDES, x + (listWidth - 2) - (tabWidth + 2), y, tabWidth, Button.DEFAULT_HEIGHT);

		y += Button.DEFAULT_HEIGHT + 2;
		int searchbarWidth = (listWidth - 2 * 2) - (Button.DEFAULT_HEIGHT + 2);

		searchbar = new TextField(x, y, searchbarWidth, Button.DEFAULT_HEIGHT, Tooltips::empty, text -> blockList.update(), null);
		searchbar.setHint("search");
		Button clear = new BasicButton(x + searchbarWidth + 2, y, 20, Button.DEFAULT_HEIGHT, () -> Texts.literal("X"), Tooltips::empty, button -> {
			searchbar.clear();
			return true;
		});

		y = blockList.getY() + blockList.getTotalHeight() - (BLOCK_LIST_BOTTOM_BORDER - 3) + 2;

		add = new BasicButton(x, y, Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT, () -> Texts.literal("+").format(Formatting.GREEN), Tooltips::empty, button -> {
			add();
			return true;
		});
		remove = new BasicButton(x + (Button.DEFAULT_HEIGHT + 2), y, Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT, () -> Texts.literal("-").format(Formatting.RED), Tooltips::empty, button -> {
			remove();
			return true;
		});
		create = new TextField(x + 2 * (Button.DEFAULT_HEIGHT + 2), y, (listWidth - 2) - (4 + 2 * Button.DEFAULT_HEIGHT) - 2, Button.DEFAULT_HEIGHT, () -> nextBlockKey() == null ? Tooltips.line("That name is not valid or that block already has an override!") : Tooltips.empty(), text -> {
			ResourceLocation key = nextBlockKey();
			add.setActive(key != null && !key.getPath().isBlank());
		}, null);
		create.setHint("create");
		SuggestionsMenu blockSuggestions = create.setSuggestions(SuggestionsProvider.resources(Registries.BLOCK, true));

		x = getX() + getWidth() - listWidth - SIDE_SPACING;
		y = getY() + TOP_SPACING;

		propertiesList = new ScrollableList(listWidth, listHeight, PROPERTIES_LIST_TOP_BORDER, PROPERTIES_LIST_BOTTOM_BORDER);

		propertiesList.setDrawBackground(true);
		propertiesList.setX(x);
		propertiesList.setY(y);

		x = getX() + getWidth() / 2;
		y = getY() + getHeight() - (8 + Button.DEFAULT_HEIGHT);

		Button cancel = new BasicButton(x - (4 + Button.DEFAULT_WIDTH), y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiCancel, Tooltips::empty, button -> {
			close();
			return true;
		});
		Button done = new BasicButton(x + 4, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiDone, Tooltips::empty, button -> {
			save();
			close();
			return true;
		});

		addChild(blockSuggestions);

		addChild(blockList);
		addChild(propertiesList);
		addChild(defaultsTab);
		addChild(overridesTab);
		addChild(searchbar);
		addChild(clear);
		addChild(add);
		addChild(remove);
		addChild(create);
		addChild(cancel);
		addChild(done);

		selectTab(currentTab == null ? Tab.DEFAULTS : currentTab);
		selectBlock(null);
	}

	@Override
	protected boolean hasTransparentBackground() {
		return false;
	}

	private Button createTabButton(Tab tab, int x, int y, int width, int height) {
		return new BasicButton(x, y, width, height, () -> Texts.literal(tab.name), Tooltips::empty, button -> {
			selectTab(tab);
			selectBlock(null);
			return true;
		}) {

			@Override
			public boolean isActive() {
				return super.isActive() && currentTab != tab;
			}
		};
	}

	private void selectTab(Tab tab) {
		currentTab = tab;

		blockList.clear();
		searchbar.clear();

		switch (currentTab) {
		case DEFAULTS:
			blockList.setBottomBorder(PROPERTIES_LIST_BOTTOM_BORDER);
			blockList.add(defaults.keySet());
			break;
		case OVERRIDES:
			blockList.setBottomBorder(BLOCK_LIST_BOTTOM_BORDER);
			blockList.add(overrides.keySet());
			break;
		}

		blockList.update();

		add.setVisible(currentTab == Tab.OVERRIDES);
		remove.setVisible(currentTab == Tab.OVERRIDES);
		create.setVisible(currentTab == Tab.OVERRIDES);
		create.clear();
	}

	private void selectBlock(ResourceLocation key) {
		currentBlock = key;

		if (currentTab == Tab.OVERRIDES) {
			remove.setActive(currentBlock != null);
		}

		initPropertiesList();
	}

	private void initPropertiesList() {
		propertiesList.clear();
		EditableMeterProperties properties = getCurrentProperties();

		if (properties == null) {
			return;
		}

		boolean force = (currentTab != Tab.DEFAULTS);
		int totalWidth = propertiesList.getEffectiveWidth();
		int buttonWidth = totalWidth > 300 ? 150 : (totalWidth > 200 ? 100 : 50);

		if (force || properties.getName() != null) {
			MeterPropertyElement name = new MeterPropertyElement(totalWidth, buttonWidth, "Name");
			name.addControl("", (width, height) -> new TextField(0, 0, width, height, Tooltips::empty, text -> {
				properties.setName(text);
				name.update();
			}, () -> properties.name()));

			switch (currentTab) {
			case DEFAULTS:
				name.setActive(false);
				break;
			case OVERRIDES:
				name.setActive(properties.getName() != null);
				name.withToggle(on -> properties.setName(on ? "" : null));
				break;
			}

			propertiesList.add(name);
		}
		if (force || properties.getColor() != null) {
			MeterPropertyElement color = new MeterPropertyElement(totalWidth, buttonWidth, "Color");
			color.addControl("rgb", style -> style.withColor(properties.color()), (width, height) -> new TextField(0, 0, width, height, Tooltips::empty, text -> {
				try {
					properties.setColor(ColorUtils.fromRGBString(text));
					color.update();
				} catch (NumberFormatException e) {
				}
			}, () -> ColorUtils.toRGBString(properties.color())));
			color.addControl("red", style -> style.withColor(Formatting.RED), (width, height) -> new Slider(0, 0, width, height, () -> {
				int c = properties.color();
				int red = ColorUtils.getRed(c);

				return Texts.literal(String.valueOf(red));
			}, Tooltips::empty, value -> {
				int red = (int)Math.round(value * 0xFF);
				int c = ColorUtils.setRed(properties.color(), red);

				properties.setColor(c);
				color.update();
			}, () -> {
				int c = properties.color();
				int red = ColorUtils.getRed(c);

				return (double)red / 0xFF;
			}, 0xFF));
			color.addControl("blue", style -> style.withColor(Formatting.BLUE), (width, height) -> new Slider(0, 0, width, height, () -> {
				int c = properties.color();
				int blue = ColorUtils.getBlue(c);

				return Texts.literal(String.valueOf(blue));
			}, Tooltips::empty, value -> {
				int blue = (int)Math.round(value * 0xFF);
				int c = ColorUtils.setBlue(properties.color(), blue);

				properties.setColor(c);
				color.update();
			}, () -> {
				int c = properties.color();
				int blue = ColorUtils.getBlue(c);

				return (double)blue / 0xFF;
			}, 0xFF));
			color.addControl("green", style -> style.withColor(Formatting.GREEN), (width, height) -> new Slider(0, 0, width, height, () -> {
				int c = properties.color();
				int green = ColorUtils.getGreen(c);

				return Texts.literal(String.valueOf(green));
			}, Tooltips::empty, value -> {
				int green = (int)Math.round(value * 0xFF);
				int c = ColorUtils.setGreen(properties.color(), green);

				properties.setColor(c);
				color.update();
			}, () -> {
				int c = properties.color();
				int green = ColorUtils.getGreen(c);

				return (double)green / 0xFF;
			}, 0xFF));

			switch (currentTab) {
			case DEFAULTS:
				color.setActive(false);
				break;
			case OVERRIDES:
				color.setActive(properties.getColor() != null);
				color.withToggle(on -> properties.setColor(on ? 0xFFFFFF : null));
				break;
			}

			propertiesList.add(color);
		}
		if (force || properties.getMovable() != null) {
			MeterPropertyElement movable = new MeterPropertyElement(totalWidth, buttonWidth, "Movable");
			movable.addControl("", (width, height) -> new ToggleButton(0, 0, width, height, () -> properties.movable(), button -> {
				properties.toggleMovable();
				movable.update();
			}));

			switch (currentTab) {
			case DEFAULTS:
				movable.setActive(false);
				break;
			case OVERRIDES:
				movable.setActive(properties.getMovable() != null);
				movable.withToggle(on -> properties.setMovable(on ? true : null));
				break;
			}

			propertiesList.add(movable);
		}
		if (force || properties.getEventTypes() != null) {
			MeterPropertyElement eventTypes = new MeterPropertyElement(totalWidth, buttonWidth, "Event Types");
			for (EventType type : EventType.ALL) {
				eventTypes.addControl(type.getName(), (width, height) -> new ToggleButton(0, 0, width, height, () -> properties.hasEventType(type), button -> {
					properties.toggleEventType(type);
					eventTypes.update();
				}));
			}

			switch (currentTab) {
			case DEFAULTS:
				eventTypes.setActive(false);
				break;
			case OVERRIDES:
				eventTypes.setActive(properties.getEventTypes() != null);
				eventTypes.withToggle(on -> properties.setEventTypes(on ? 0 : null));
				break;
			}

			propertiesList.add(eventTypes);
		}

		propertiesList.update();
	}

	private EditableMeterProperties getCurrentProperties() {
		if (currentBlock == null) {
			return null;
		}

		switch (currentTab) {
		case DEFAULTS:
			return defaults.get(currentBlock);
		case OVERRIDES:
			return overrides.get(currentBlock);
		}

		return null;
	}

	private void add() {
		ResourceLocation key = nextBlockKey();

		if (key != null && overrides.putIfAbsent(key, new EditableMeterProperties()) == null) {
			selectTab(Tab.OVERRIDES);
			selectBlock(null);
		}
	}

	private void remove() {
		if (currentBlock != null && overrides.remove(currentBlock) != null) {
			selectTab(Tab.OVERRIDES);
			selectBlock(null);
		}
	}

	public ResourceLocation nextBlockKey() {
		try {
			String name = create.getValue();
			ResourceLocation key = ResourceLocation.parse(name);

			if (!overrides.containsKey(key)) {
				return key;
			}
		} catch (ResourceLocationException e) {
		}

		return null;
	}

	private void save() {
		meterPropertiesManager.update(overrides);
	}

	private enum Tab {

		DEFAULTS("defaults"),
		OVERRIDES("overrides");

		public final String name;

		private Tab(String name) {
			this.name = name;
		}
	}

	private class EditableMeterProperties extends MutableMeterProperties {

		public EditableMeterProperties() {
		}

		public EditableMeterProperties(MeterProperties properties) {
			fill(properties);
		}

		public String name() {
			String name = getName();
			return name == null ? "" : name;
		}

		public int color() {
			Integer color = getColor();
			return color == null ? 0xFFFFFF : color;
		}

		public boolean movable() {
			Boolean movable = getMovable();
			return movable == null ? true : movable;
		}

		public void toggleMovable() {
			setMovable(!movable());
		}

		public int eventTypes() {
			Integer eventTypes = getEventTypes();
			return eventTypes == null ? 0 : eventTypes;
		}

		@Override
		public boolean hasEventType(EventType type) {
			return (eventTypes() & type.flag()) != 0;
		}
	}
}
