package com.github.justinespinosa.shittytests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.term.termcap.TermCap;
import org.junit.Assert;
import org.junit.Test;

import static com.github.justinespinosa.shittytests.TestUtils.A;
import static com.github.justinespinosa.shittytests.TestUtils.ESC;
import static com.github.justinespinosa.shittytests.TestUtils.createTestTerminal;


public class TestClear {

    @Test
    public void testClear() throws IOException {
        ByteArrayOutputStream termScreen = new ByteArrayOutputStream(6);
        Terminal term = createTestTerminal(termScreen);

        term.writeCommand("cl", 24);

        byte[] screenData = termScreen.toByteArray();

        //ansi cl = \E[H\E[J
        Assert.assertArrayEquals(A(ESC,'[','H',ESC,'[','J'), screenData);
    }
}
