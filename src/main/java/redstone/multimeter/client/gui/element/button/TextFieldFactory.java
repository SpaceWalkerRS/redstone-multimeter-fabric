package redstone.multimeter.client.gui.element.button;

public interface TextFieldFactory extends ButtonFactory {

	@Override
	TextField create(int width, int height);

}
