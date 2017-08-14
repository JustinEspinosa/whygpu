package com.github.justinespinosa.textmode.curses;

import com.github.justinespinosa.textmode.curses.lang.ColorChar;
import com.github.justinespinosa.textmode.curses.lang.ColorString;
import com.github.justinespinosa.textmode.curses.lang.ColorStringBuilder;
import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.term.io.ConsoleInputStream;
import com.github.justinespinosa.textmode.curses.ui.*;
import com.github.justinespinosa.textmode.curses.ui.components.Component;
import com.github.justinespinosa.textmode.curses.ui.look.ColorManager;
import com.github.justinespinosa.textmode.curses.ui.look.ColorTheme;
import com.github.justinespinosa.textmode.curses.ui.util.CharacterScreenBuffer;

import java.io.IOException;
import java.util.Iterator;

public abstract class AbstractCurses implements Curses {
    private Terminal terminal;
    private ConsoleInputStream consoleInputStream;
    private CharacterScreenBuffer characterScreenBuffer;
    private int cursorLine = -1;
    private int cursorCol = -1;
    private ColorManager colorManager;


    protected AbstractCurses(Terminal terminal) {
        this.terminal = terminal;
        this.colorManager = ColorManager.createInstance();
        this.characterScreenBuffer = new CharacterScreenBuffer(new Dimension(terminal.getLines(), terminal.getCols()));
        //Allow curses to manage edition
        this.consoleInputStream = new ConsoleInputStream(terminal.getInputStream(), terminal.getOutputStream());
        terminal.replaceInputStream(consoleInputStream);
    }

    @Override
    public int numcolors() {
        return getTerminal().getNumColors();
    }

    @Override
    public Terminal getTerminal() {
        return terminal;
    }


    protected ConsoleInputStream getConsoleInputStream() {
        return consoleInputStream;
    }

    protected CharacterScreenBuffer getCharacterScreenBuffer() {
        return characterScreenBuffer;
    }

    protected int getCursorLine() {
        return cursorLine;
    }

    protected int getCursorCol() {
        return cursorCol;
    }

    @Override
    public ColorManager colors() {
        return colorManager;
    }

    @Override
    public void applyColorTheme(ColorTheme theme) {
        colorManager = theme.getColorManager();
    }

    @Override
    public void noecho() throws IOException {
        consoleInputStream.setEcho(false);
    }

    @Override
    public void echo() throws IOException {
        consoleInputStream.setEcho(true);
    }

    @Override
    public void noraw() throws IOException {
        consoleInputStream.setCanonical(true);
    }

    @Override
    public void raw() throws IOException {
        consoleInputStream.setCanonical(false);
    }

    @Override
    public void showWindow(Component w) throws IOException {
        drawColorCharArray(w.getContent(), w.getPosition());
    }

    //fill a text rectangle of defined characters + fore|back color as fast as possible
    @Override
    public synchronized void drawColorCharArray(ColorChar[][] contents, Position from) throws IOException {
        ColorStringBuilder bld = new ColorStringBuilder();
        boolean outputed = false;

        for (int line = 0; line < contents.length; line++) {

            for (int col = 0; col < contents[line].length; col++) {
                if (contents[line][col] != null)
                    bld.append(contents[line][col]);
            }

            if (bld.length() > 0)
                if (doubleBufferPrintAt(bld.toColorString(), line + from.getLine(), from.getCol(), false))
                    outputed = true;

            bld = new ColorStringBuilder();
        }

        if (outputed)
            getTerminal().flush();

    }

    @Override
    public void applyColorPair(ColorPair color) throws IOException {

        Color fore = ColorTable.convert(color.getForeColor(), ColorDepth.forNumCols(numcolors()));
        Color back = ColorTable.convert(color.getBackColor(), ColorDepth.forNumCols(numcolors()));

        bColor(back.index());
        fColor(fore.index());

        if (getTerminal().getNumColors() == 1)
            setStandout(color.getBackColor().index() != 0);

    }

    @Override
    public int lines() throws IOException {
        return getTerminal().getLines();
    }

    @Override
    public int cols() throws IOException {
        return getTerminal().getCols();
    }


    protected ColorPair writeCharAt(ColorChar c, Position at, ColorPair previous) throws IOException {
        if (!c.getColors().equals(previous))
            applyColorPair(c.getColors());
        cursorAt(at.getLine(), at.getCol());
        writeChar(c.getChr());
        return c.getColors();
    }

    protected void writeChar(char chr) throws IOException {
        getTerminal().writeChar(chr);
        if (cursorCol > -1 && cursorLine > -1) {
            cursorCol++;
            if (cursorCol > getTerminal().getCols()) {
                cursorCol = 0;
                if (cursorLine + 1 < getTerminal().getLines())
                    cursorLine++;
            }
        }
    }

    @Override
    public void cursorAt(int line, int col) throws IOException {
        if (cursorLine != line || cursorCol != col) {
            implCursorAt(line, col);
            cursorLine = line;
            cursorCol = col;
        }
    }

    @Override
    public synchronized void invalidateDoubleBuffering() {
        characterScreenBuffer.invalidate();
    }

    @Override
    public synchronized void redrawAllFromDoubleBuffer() throws IOException {

        ColorPair previous = new ColorPair(BaseColor.Undefined, BaseColor.Undefined);
        Iterator<Position> i = characterScreenBuffer.getSize().iterator();
        Position current;

        while (i.hasNext()) {
            previous = writeCharAt(characterScreenBuffer.get(current = i.next()), current, previous);
        }

    }

    @Override
    public synchronized boolean doubleBufferPrintAt(ColorString text, int line, int col, boolean flush) throws IOException {

        ColorPair lastColor = new ColorPair(BaseColor.Undefined, BaseColor.Undefined);
        boolean changeColor = false;
        boolean outputed = false;

        for (int i = 0; i < text.length(); i++) {
            ColorChar cchar = text.charAt(i);

            if (cchar != null) {
                changeColor = (!lastColor.equals(cchar.getColors()));

                Position p = new Position(line, col);
                if (characterScreenBuffer.set(p, cchar)) {

                    if (changeColor) {
                        applyColorPair(cchar.getColors());
                        lastColor = cchar.getColors();
                    }
                    cursorAt(line, col);
                    writeChar(cchar.getChr());
                    outputed = true;
                }
            }
            col++;
        }
        if (flush) {
            getTerminal().flush();
        }

        return outputed;
    }

    @Override
    public void wantsResizedNotification(TerminalResizedReceiver rcv) {
        getTerminal().addResizedReceiver(rcv);
    }


    protected void implPrint(String text) throws IOException {
        byte[] chrs = text.getBytes();
        for (int i = 0; i < chrs.length; i++) {
            writeChar((char) chrs[i]);
        }
    }

    @Override
    public void printAt(String text, int l, int c) throws IOException {
        cursorAt(l, c);
        implPrint(text);
        getTerminal().flush();
    }

    @Override
    public void print(String text) throws IOException {
        implPrint(text);
        getTerminal().flush();
    }

    @Override
    public void print(char c) throws IOException {
        writeChar(c);
        getTerminal().flush();
    }

    @Override
    public synchronized void resizeBuffer(int cols, int lines) {
        cursorLine = -1;
        cursorCol = -1;
        characterScreenBuffer = new CharacterScreenBuffer(new Dimension(lines, cols));
    }

    @Override
    public void flush() throws IOException {
        getTerminal().flush();
    }

    protected abstract void implCursorAt(int line, int col) throws IOException;
}
