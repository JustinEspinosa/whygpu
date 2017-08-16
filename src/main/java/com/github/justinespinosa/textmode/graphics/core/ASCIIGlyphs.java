package com.github.justinespinosa.textmode.graphics.core;

import com.github.justinespinosa.textmode.curses.ui.*;
import com.github.justinespinosa.textmode.graphics.util.OccurenceTopList;

import java.util.*;

import static com.github.justinespinosa.textmode.graphics.core.ASCIIGlyphs.Corner.*;
import static com.github.justinespinosa.textmode.graphics.core.ASCIIGlyphs.Shape.*;

public class ASCIIGlyphs {

    private static final double THRESHOLD = 0.4;
    private static final char[] GLYPHS_PCT = {' ', '.', ',', '-', '+', '*', '%', '$', '#'};

    enum Corner {
        UL, UR, DR, DL
    }

    enum Shape {
        UP(new char[]{'"'}, new Corner[]{UL, UR}),
        DOWN(new char[]{'_'}, new Corner[]{DL, DR}),
        LEFT(new char[]{'['}, new Corner[]{UL, DL}),
        RIGHT(new char[]{']'}, new Corner[]{UR, DR}),
        CORNER_UL(new char[]{'`'}, new Corner[]{UL}),
        CORNER_UR(new char[]{'\''}, new Corner[]{UR}),
        CORNER_DR(new char[]{'.'}, new Corner[]{DR}),
        CORNER_DL(new char[]{','}, new Corner[]{DL}),
        BORDER_UL(new char[]{'F'}, new Corner[]{UL, DL, UR}),
        BORDER_UR(new char[]{'T'}, new Corner[]{UR, DR, UL}),
        BORDER_DR(new char[]{'J'}, new Corner[]{DR, DL, UR}),
        BORDER_DL(new char[]{'L'}, new Corner[]{DL, DR, UL}),
        NEUTRAL(GLYPHS_PCT, Corner.values());

        private char[] glyphList;
        private List<Corner> corners;

        Shape(char[] glyphList, Corner[] corners) {
            this.glyphList = glyphList;
            this.corners = Arrays.asList(corners);
        }

        private char[] getGlyphList() {
            return glyphList;
        }

        private boolean hasCorner(Corner corner) {
            return corners.contains(corner);
        }
    }

    private static char getCharForPercent(char[] glyphList, double percent) {
        if (percent > 1) {
            percent = 1;
        }
        int index = (int) Math.floor(((double) (glyphList.length - 1)) * percent);
        return glyphList[index];
    }

    public static char getCharForPercent(double percFG) {
        return getCharForPercent(GLYPHS_PCT, percFG);
    }


    public static double percentForeground(OccurenceTopList<RGB> cornerList) {
        return (double) cornerList.getSecondCount() / (double) (cornerList.getFirstCount()+cornerList.getSecondCount());
    }

    private static boolean threshold(double value) {
        return value >= THRESHOLD;
    }

    private static boolean shapeMatchesThresholds(Shape s, Map<Corner, Double> perc) {
        boolean result = true;
        for (Corner corner : Corner.values()) {
            if (s.hasCorner(corner)) {
                result = result && threshold(perc.get(corner));
            } else {
                result = result && (!threshold(perc.get(corner)));
            }
        }
        return result;
    }

    private static Shape chooseShape(Map<Corner, Double> perc) {
        for (Shape searchShape : Shape.values()) {
            if (shapeMatchesThresholds(searchShape, perc)) {
                return searchShape;
            }
        }

        return NEUTRAL;
    }

    public static char getCharForShape(Shape shape, double percFG) {
        return getCharForPercent(shape.getGlyphList(), percFG);
    }

    public static char getCharForZone(double percFG, Map<Corner, Double> perc) {
        Shape shape = chooseShape(perc);
        return getCharForShape(shape, percFG);
    }


}
