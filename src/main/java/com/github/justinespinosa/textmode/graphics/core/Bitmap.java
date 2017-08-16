package com.github.justinespinosa.textmode.graphics.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.justinespinosa.textmode.curses.lang.ColorChar;
import com.github.justinespinosa.textmode.curses.ui.*;
import com.github.justinespinosa.textmode.graphics.util.OccurenceTopList;

import static com.github.justinespinosa.textmode.curses.ui.RGB.BLACK;
import static com.github.justinespinosa.textmode.curses.ui.RGB.WHITE;
import static com.github.justinespinosa.textmode.graphics.core.ASCIIGlyphs.Corner.*;

public class Bitmap{

	private BufferedImage pixelImage;
	private Dimension size;
	private boolean useShape = false;

	public Bitmap(BufferedImage image){
		pixelImage = image;
		size = new Dimension(image.getHeight(),image.getWidth());
	}
	
	public Bitmap(Dimension size){
		pixelImage = new BufferedImage(size.getLines(),size.getCols(),BufferedImage.TYPE_INT_RGB);
		this.size = size;
	}

    public boolean isUseShape() {
        return useShape;
    }

    public void setUseShape(boolean useShape) {
        this.useShape = useShape;
    }

    private void updateColor(WritableRaster raster, Position p, RGB error, double mult){
		if(!size.includes(p)) {
            return;
        }
		
		int c = p.getCol(), l = p.getLine();
		RGB oldcol = new RGB(raster.getPixel(c, l, new int[3]));
		RGB newcol = new RGB(oldcol.add(error.mult(mult)));
		raster.setPixel(c, l, newcol.rgbBands());
	}

    private Map<ASCIIGlyphs.Corner, Double> computeCornerPercentages(RGB fg, ColorTable<? extends Color> palette, WritableRaster raster, Position p, Dimension d) {
        int middleCols = d.getCols() / 2;
        int middleLines = d.getLines() / 2;

        Map<ASCIIGlyphs.Corner, Rectangle> areas = new HashMap<>();
        areas.put(UL, new Rectangle(p, p.translate(middleLines, middleCols)));
        areas.put(UR, new Rectangle(p.horizontal(middleCols), p.translate(middleLines, d.getCols())));
        areas.put(DR, new Rectangle(p.translate(middleLines, middleCols), p.translate(d.getLines(), d.getCols())));
        areas.put(DL, new Rectangle(p.vertical(middleLines), p.translate(d.getLines(), middleCols)));

        Map<ASCIIGlyphs.Corner, Double> corners = new HashMap<>();
        for (ASCIIGlyphs.Corner corner : ASCIIGlyphs.Corner.values()) {
            Rectangle area = areas.get(corner);

            Iterator<Position> i = area.getDimension().iterator(area.getPosition());
            int pixelCount = 0;
            int fgCount = 0;
            while (i.hasNext()) {
                Position current = i.next();


                RGB color = palette.findNearestIndex(new RGB(raster.getPixel(current.getCol(), current.getLine(), new int[4])));
                ++pixelCount;
                if (fg == color) {
                    ++fgCount;
                }
            }

            corners.put(corner, (double)fgCount / (double)pixelCount);
        }


        return corners;
    }
	
	private ColorChar getCharFor(WritableRaster raster,ColorTable<? extends Color> palette,Position p,Dimension d){
		OccurenceTopList<RGB> topList = new OccurenceTopList<>();

        Iterator<Position> i = d.iterator(p);
		while(i.hasNext()){
		    Position curr = i.next();
			int c = curr.getCol(), l = curr.getLine();

			RGB oldcol = new RGB(raster.getPixel(c, l, new int[4]));
			RGB newcol = palette.findNearestIndex(oldcol);
			RGB error  = new RGB(oldcol,newcol);
			topList.plusOne(newcol);
			
			updateColor(raster,curr.right(),error, 7./16.);
			Position nextLine = curr.down();
			updateColor(raster,nextLine.right(),error, 3./16.);
			updateColor(raster,nextLine,error, 5./16.);
			updateColor(raster,nextLine.left(),error, 1./16.);
		}
		
		RGB bg = topList.getFirst();
		RGB fg = topList.getSecond();

		if(fg==null) {
            fg = WHITE;
		}

		if(bg==null){
		    bg = BLACK;
        }
		
		ColorPair col = new ColorPair(fg, bg, palette.depth());
		char c;
		double percFG = ASCIIGlyphs.percentForeground(topList);
		if(useShape){
            Map<ASCIIGlyphs.Corner, Double> corners = computeCornerPercentages(fg, palette, raster, p, d);
		    c = ASCIIGlyphs.getCharForZone(percFG, corners);
        }else {
            c = ASCIIGlyphs.getCharForPercent(percFG);
        }
        return new ColorChar(c,col);
	}
	
	public ASCIIPicture ASCIIDither(Resolution r,ColorTable<? extends Color> palette){
		 ASCIIPicture destination = new ASCIIPicture(size, r);
		 
		 WritableRaster workingRaster = pixelImage.copyData(null);
		 Dimension part = r.toPixels(Dimension.UNITY);
		 
		 for(Position p :  destination.size()){
			 Position pp = r.toPixels(p);
			 destination.set(p,getCharFor(workingRaster, palette, pp, part)); 
		 }
		 
		 return destination;
	}

}



