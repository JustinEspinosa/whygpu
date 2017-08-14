package com.github.justinespinosa.textmode.curses;

import java.io.IOException;

import com.github.justinespinosa.textmode.curses.impl.DefaultCursesFactory;
import com.github.justinespinosa.textmode.curses.impl.TermInfoCursesFactory;
import com.github.justinespinosa.textmode.curses.net.SocketIO;
import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.term.termcap.TermType;

public abstract class CursesFactory {

    public abstract Curses createCurses(Terminal t);

    public abstract TermType createTermType(String ttname);

    public abstract Terminal createTerminal(String ttname) throws IOException;

    public abstract Terminal createTerminal(TermType tt) throws IOException;

    public abstract Terminal createTerminal(String ttname, SocketIO io) throws IOException;

    public abstract Terminal createTerminal(TermType tt, SocketIO io) throws IOException;

    public static CursesFactory getInstance() throws IOException {
        return new DefaultCursesFactory();
    }

    public static CursesFactory getInstance(String termCapFile) throws IOException {
        return new DefaultCursesFactory(termCapFile);
    }

    public static CursesFactory getTerminfoInstance(String termInfoFile) throws IOException{
            return new TermInfoCursesFactory(termInfoFile);
    }

}
