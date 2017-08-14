package com.github.justinespinosa.textmode.curses.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.github.justinespinosa.textmode.curses.term.Terminal;
import org.junit.Assert;
import org.junit.Test;
import sun.util.resources.cldr.es.CurrencyNames_es;

import static com.github.justinespinosa.shittytests.TestUtils.*;


public class TestCurses {


    @Test
    public void testCurses() throws IOException {
        ByteArrayOutputStream termScreen = new ByteArrayOutputStream(1118);
        Terminal term = createTestTerminal(termScreen);

        TermCapCurses crs = new TermCapCurses(term);

        crs.clear();
        for (int j = 0; j < 8; j++) {
            crs.bColor(j);
            for (int i = 0; i < 8; i++) {
                crs.fColor(i);
                crs.printAt("Hello", i, j * 6);
            }
        }

        ArrayBuilder b = new ArrayBuilder();
        b.with(ESC,'[','H',ESC,'[','J'); // Clear
        for (int j = 0; j < 8; j++) {
            b.with(ESC, '[','4').with(j).with('m');   //fg
            for (int i = 0; i < 8; i++) {
                b.with(ESC, '[','3').with(i).with('m'); //bg
                b.with(ESC, '[').with(i+1).with(';').with((j*6)+1).with('H'); //cursor
                b.with("Hello");
            }
        }

        Assert.assertArrayEquals(b.build(), termScreen.toByteArray());
    }
}
