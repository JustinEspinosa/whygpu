package textmode.curses.ui.components;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;

/**
 * A simple Panel that layouts the components in a column
 * @author justin
 *
 * @param <T>
 */
public class Panel<T extends Component> extends AbstractWindow<T> {

	public Panel(Curses cs,Position p, Dimension d) {
		super("",cs, p, d);
		setDecorated(false);
		clear();
		notifyDisplayChange();
	}

	@Override
	protected boolean isActive() {
		return false;
	}

	@Override
	protected synchronized void redraw() {
		clear();
	}

}
