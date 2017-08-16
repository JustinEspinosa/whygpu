package com.github.justinespinosa.textmode.curses;

import com.github.justinespinosa.textmode.curses.lang.ColorChar;
import com.github.justinespinosa.textmode.curses.lang.ColorString;
import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.ui.ColorPair;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.components.Component;
import com.github.justinespinosa.textmode.curses.ui.look.ColorManager;
import com.github.justinespinosa.textmode.curses.ui.look.ColorTheme;

import java.io.IOException;

public interface Curses {
    void initColor() throws IOException;

    int numcolors();

    void setStandout(boolean so) throws IOException;

    void setIntensity(boolean high) throws IOException;

    void high() throws IOException;

    void low() throws IOException;

    void bColor(int c) throws IOException;

    void fColor(int c) throws IOException;

    void civis() throws IOException;

    void cnorm() throws IOException;

    void noecho() throws IOException;

    void echo() throws IOException;

    void rmcup() throws IOException;

    void smcup() throws IOException;

    void noraw() throws IOException;

    void raw() throws IOException;

    void showWindow(Component w) throws IOException;

    //fill a text rectangle of defined characters + fore|back color as fast as possible
    void drawColorCharArray(ColorChar[][] contents, Position from) throws IOException;

    void applyColorPair(ColorPair color) throws IOException;

    int lines() throws IOException;

    int cols() throws IOException;

    void clear() throws IOException;

    void printInStatus(String text) throws IOException;

    void cursorAt(int line, int col) throws IOException;

    void cr() throws IOException;

    void sc() throws IOException;

    void rc() throws IOException;

    void cud1() throws IOException;

    void home() throws IOException;

    void invalidateDoubleBuffering();

    void redrawAllFromDoubleBuffer() throws IOException;

    boolean doubleBufferPrintAt(ColorString text, int line, int col, boolean flush) throws IOException;

    void wantsResizedNotification(TerminalResizedReceiver rcv);

    void printAt(String text, int l, int c) throws IOException;

    void print(String text) throws IOException;

    void print(char c) throws IOException;

    void resizeBuffer(int cols, int lines);

    void flush() throws IOException;

    Terminal getTerminal();

    ColorManager colors();

    void applyColorTheme(ColorTheme theme);
}