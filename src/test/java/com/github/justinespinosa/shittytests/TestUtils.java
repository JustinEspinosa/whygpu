package com.github.justinespinosa.shittytests;

import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.term.termcap.TermCap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

    public static char ESC = 27;

    public static Terminal createTestTerminal(ByteArrayOutputStream termScreen) throws IOException {
        TermCap capdb = new TermCap(TestUtils.class.getClassLoader().getResourceAsStream("termcap.src"));

        return new Terminal(capdb.getTermType("ansi"), new ByteArrayInputStream(new byte[0]),termScreen, null);
    }

    public static byte[] A(char ... p){
        byte[] r = new byte[p.length];
        for(int i=0;i<p.length;++i){
            r[i] = (byte)p[i];
        }
        return r;
    }

    public static byte[] M(byte[] ... a){
        int l = Arrays.stream(a).mapToInt(e -> e.length).sum();
        final byte[] dest = new byte[l];
        int o = 0;
        for(byte[] e : a){
            System.arraycopy(e,0,dest,o,e.length);
            o+=e.length;
        }
        return dest;
    }

    public static byte[] S(String str){
        char[] chars = new char[str.length()];
        str.getChars(0,str.length(),chars,0);
        return A(chars);
    }

    public static byte[] ASCn(int x){
        String str = String.valueOf(x);
        return S(str);
    }

    public static class ArrayBuilder{
        private List<byte[]> arrays = new ArrayList<>();
        public ArrayBuilder with(byte ... a){
            arrays.add(a);
            return this;
        }

        public ArrayBuilder with(byte a){
            arrays.add(new byte[]{a});
            return this;
        }

        public ArrayBuilder with(char ... a){
            arrays.add(A(a));
            return this;
        }

        public ArrayBuilder with(char a){
            arrays.add(A(a));
            return this;
        }

        public ArrayBuilder with(int a){
            arrays.add(ASCn(a));
            return this;
        }

        public ArrayBuilder with(String str){
            arrays.add(S(str));
            return this;
        }

        public byte[] build(){
            return TestUtils.M(arrays.toArray(new byte[][]{}));
        }
    }

}
