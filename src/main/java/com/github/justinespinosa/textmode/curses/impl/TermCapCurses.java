package com.github.justinespinosa.textmode.curses.impl;


import com.github.justinespinosa.textmode.curses.AbstractCurses;
import com.github.justinespinosa.textmode.curses.term.Terminal;

import java.io.IOException;


class TermCapCurses extends AbstractCurses {

    TermCapCurses(Terminal terminal) {
        super(terminal);
    }

    @Override
    public void initColor() throws IOException {
        getTerminal().writeCommand("Ic", 0);
    }


    @Override
    public void setStandout(boolean so) throws IOException {
        if (so)
            getTerminal().writeCommand("so", 0);
        else
            getTerminal().writeCommand("se", 0);
    }

    @Override
    public void setIntensity(boolean high) throws IOException {
        if (high)
            high();
        else
            low();
    }

    @Override
    public void high() throws IOException {
        getTerminal().writeCommand("md", 0);
    }

    @Override
    public void low() throws IOException {
        getTerminal().writeCommand("mh", 0);
    }

    @Override
    public void bColor(int c) throws IOException {
        if (c > -1) {
            if (getTerminal().canAnsiColor())
                getTerminal().writeCommand("AB", 0, c);
            else
                getTerminal().writeCommand("Sb", 0, c);
        }
    }

    @Override
    public void fColor(int c) throws IOException {
        if (c > -1) {
            if (getTerminal().canAnsiColor())
                getTerminal().writeCommand("AF", 0, c);
            else
                getTerminal().writeCommand("Sf", 0, c);

        }
    }

    @Override
    public void civis() throws IOException {
        getTerminal().writeCommand("vi", 0);
    }

    @Override
    public void cnorm() throws IOException {
        getTerminal().writeCommand("ve", 0);
    }

    @Override
    public void rmcup() throws IOException {
        getTerminal().writeCommand("te", getTerminal().getLines());
    }

    @Override
    public void smcup() throws IOException {
        getTerminal().writeCommand("ti", getTerminal().getLines());
    }

    @Override
    public void clear() throws IOException {
        getTerminal().writeCommand("cl", lines());
    }

    @Override
    public void printInStatus(String text) throws IOException {
        getTerminal().writeCommand("ts", 1);
        implPrint(text);
        getTerminal().flush();
        getTerminal().writeCommand("fs", 1);
    }

    @Override
    protected void implCursorAt(int line, int col) throws IOException {
        getTerminal().writeCommand("cm", 1, line, col);
    }


}
