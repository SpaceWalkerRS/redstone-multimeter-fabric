package redstone.multimeter.client.gui.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.minecraft.locale.LanguageManager;
import net.minecraft.text.Formatting;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.BlockListElement;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.client.gui.element.meter.MeterPropertyElement;
import redstone.multimeter.client.meter.ClientMeterPropertiesManager;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.util.ColorUtils;

public class DefaultMeterPropertiesScreen extends RSMMScreen {

	private final ClientMeterPropertiesManager meterPropertiesManager;
	private final Map<String, EditableMeterProperties> defaults;
	private final Map<String, EditableMeterProperties> overrides;

	private Tab currentTab;
	private String currentBlock;

	private BlockListElement blockList;
	private ScrollableListElement propertiesList;
	private TextField searchbar;
	private IButton add;
	private IButton remove;
	private TextField create;

	protected DefaultMeterPropertiesScreen(MultimeterClient client) {
		super(client, "Default Meter Properties", true);

		this.meterPropertiesManager = this.client.getMeterPropertiesManager();
		this.defaults = new HashMap<>();
		this.overrides = new HashMap<>();

		for (Entry<String, MeterProperties> entry : this.meterPropertiesManager.getDefaults().entrySet()) {
			String key = entry.getKey();
			MeterProperties properties = entry.getValue();

			this.defaults.put(key, new EditableMeterProperties(properties));
		}
		for (Entry<String, MeterProperties> entry : this.meterPropertiesManager.getOverrides().entrySet()) {
			String key = entry.getKey();
			MeterProperties properties = entry.getValue();

			this.overrides.put(key, new EditableMeterProperties(properties));
		}
	}

	@Override
	public boolean keyPress(int keyCode) {
		boolean consumed = super.keyPress(keyCode) || blockList.keyPress(keyCode);

		if (!consumed) {
			if (remove.isActive() && keyCode == Keyboard.KEY_SUBTRACT) {
				remove();
				consumed = true;
			} else if (add.isActive() && keyCode == Keyboard.KEY_ADD) {
				add();
				consumed = true;
			}
		}

		return consumed;
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void initScreen() {
		Keyboard.enableRepeatEvents(true);

		int spacing = 10;
		int top = 10 + 3 * (IButton.DEFAULT_HEIGHT + 2);
		int bottom = 18 + 2 * (IButton.DEFAULT_HEIGHT + 2);

		int half = (getWidth() - 3 * spacing) / 2;

		int x = getX() + spacing;
		int y = getY();

		blockList = new BlockListElement(client, half, getHeight(), top, bottom, key -> selectBlock(key));

		blockList.setSpacing(0);
		blockList.setDrawBackground(true);
		blockList.setBlockFilter(id -> id.toString().contains(searchbar.getText()));
		blockList.setX(x);
		blockList.setY(y);

		y += IButton.DEFAULT_HEIGHT + 4;
		int width = (half / 2) - 2;

		IButton defaultsTab = createTabButton(Tab.DEFAULTS, x, y, width, IButton.DEFAULT_HEIGHT);
		IButton overridesTab = createTabButton(Tab.OVERRIDES, x + half - width, y, width, IButton.DEFAULT_HEIGHT);

		y += IButton.DEFAULT_HEIGHT + 2;
		width = half - (IButton.DEFAULT_HEIGHT + 2);

		searchbar = new TextField(client, x, y, width, IButton.DEFAULT_HEIGHT, () -> Tooltip.EMPTY, text -> blockList.update(), null);
		IButton clear = new Button(client, x + width + 2, y, 20, IButton.DEFAULT_HEIGHT, () -> "X", () -> Tooltip.EMPTY, button -> {
			searchbar.clear();
			return true;
		});

		y = getY() + getHeight() - (bottom - 6);

		add = new Button(client, x, y, IButton.DEFAULT_HEIGHT, IButton.DEFAULT_HEIGHT, () -> Formatting.GREEN + "+", () -> Tooltip.EMPTY, button -> {
			add();
			return true;
		});
		remove = new Button(client, x + (IButton.DEFAULT_HEIGHT + 2), y, IButton.DEFAULT_HEIGHT, IButton.DEFAULT_HEIGHT, () -> Formatting.RED + "-", () -> Tooltip.EMPTY, button -> {
			remove();
			return true;
		});
		create = new TextField(client, x + 2 * (IButton.DEFAULT_HEIGHT + 2), y, half - (4 + 2 * IButton.DEFAULT_HEIGHT), IButton.DEFAULT_HEIGHT, () -> nextBlockKey() == null ? Tooltip.of("That name is not valid or that block already has an override!") : Tooltip.EMPTY, text -> {
			String key = nextBlockKey();
			add.setActive(key != null && !key.trim().isEmpty());
		}, null);

		top -= 2 * (IButton.DEFAULT_HEIGHT + 2);
		bottom -= (IButton.DEFAULT_HEIGHT + 2);
		x = getX() + half + 2 * spacing;
		y = getY();

		propertiesList = new ScrollableListElement(client, half, getHeight(), top, bottom);

		propertiesList.setDrawBackground(true);
		propertiesList.setX(x);
		propertiesList.setY(y);

		x = getX() + getWidth() / 2;
		y = getY() + getHeight() - (8 + IButton.DEFAULT_HEIGHT);

		IButton cancel = new Button(client, x - (4 + IButton.DEFAULT_WIDTH), y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> LanguageManager.getInstance().translate("gui.cancel"), () -> Tooltip.EMPTY, button -> {
			close();
			return true;
		});
		IButton done = new Button(client, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> LanguageManager.getInstance().translate("gui.done"), () -> Tooltip.EMPTY, button -> {
			save();
			close();
			return true;
		});

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

	private IButton createTabButton(Tab tab, int x, int y, int width, int height) {
		return new Button(client, x, y, width, height, () -> tab.name, () -> Tooltip.EMPTY, button -> {
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
			blockList.add(defaults.keySet());
			break;
		case OVERRIDES:
			blockList.add(overrides.keySet());
			break;
		}

		blockList.update();

		add.setActive(false);
		remove.setActive(false);
		create.setActive(currentTab == Tab.OVERRIDES);
		create.clear();
	}

	private void selectBlock(String key) {
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
			MeterPropertyElement name = new MeterPropertyElement(client, totalWidth, buttonWidth, "Name");
			name.addControl("", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> {
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
			MeterPropertyElement color = new MeterPropertyElement(client, totalWidth, buttonWidth, "Color");
			color.addControl("rgb", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> {
				try {
					properties.setColor(ColorUtils.fromRGBString(text));
					color.update();
				} catch (NumberFormatException e) {
				}
			}, () -> ColorUtils.toRGBString(properties.color())));
			color.addControl("red", text -> Formatting.RED + text, (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
				int c = properties.color();
				int red = ColorUtils.getRed(c);

				return String.valueOf(red);
			}, () -> Tooltip.EMPTY, value -> {
				int red = (int)Math.round(value * 0xFF);
				int c = ColorUtils.setRed(properties.color(), red);

				properties.setColor(c);
				color.update();
			}, () -> {
				int c = properties.color();
				int red = ColorUtils.getRed(c);

				return (double)red / 0xFF;
			}, 0xFF));
			color.addControl("blue", text -> Formatting.BLUE + text, (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
				int c = properties.color();
				int blue = ColorUtils.getBlue(c);

				return String.valueOf(blue);
			}, () -> Tooltip.EMPTY, value -> {
				int blue = (int)Math.round(value * 0xFF);
				int c = ColorUtils.setBlue(properties.color(), blue);

				properties.setColor(c);
				color.update();
			}, () -> {
				int c = properties.color();
				int blue = ColorUtils.getBlue(c);

				return (double)blue / 0xFF;
			}, 0xFF));
			color.addControl("green", text -> Formatting.GREEN + text, (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
				int c = properties.color();
				int green = ColorUtils.getGreen(c);

				return String.valueOf(green);
			}, () -> Tooltip.EMPTY, value -> {
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
			MeterPropertyElement movable = new MeterPropertyElement(client, totalWidth, buttonWidth, "Movable");
			movable.addControl("", (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> properties.movable(), button -> {
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
			MeterPropertyElement eventTypes = new MeterPropertyElement(client, totalWidth, buttonWidth, "Event Types");
			for (EventType type : EventType.ALL) {
				eventTypes.addControl(type.getName(), (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> properties.hasEventType(type), button -> {
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
		String key = nextBlockKey();

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

	public String nextBlockKey() {
		String key = create.getText();

		if (!overrides.containsKey(key)) {
			return key;
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
