package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

/**
 * A simple Panel that layouts the components in a column
 * @author justin
 *
 * @param <T>
 */
public class Panel<T extends Component> extends AbstractWindow<T> {

	public Panel(Curses cs,Position p, Dimension d) {
		super("",cs, p, d);
		setColor(ColorDefaults.getDefaultColor(ColorType.WINDOW,curses()));
		clear();
		notifyDisplayChange();
	}

	@Override
	protected boolean isActive() {
		return false;
	}

}
