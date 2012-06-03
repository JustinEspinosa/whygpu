package textmode.graphics.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Iterator;

import textmode.curses.lang.ColorChar;
import textmode.curses.ui.Color;
import textmode.curses.ui.ColorDepth;
import textmode.curses.ui.ColorPair;
import textmode.curses.ui.ColorTable;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.RGB;
import textmode.graphics.util.OccurenceTopList;

public class Bitmap{
	
	
	private BufferedImage pixelImage;
	private Dimension size;
	
	public Bitmap(BufferedImage image){
		pixelImage = image;
		size = new Dimension(image.getHeight(),image.getWidth());
	}
	
	public Bitmap(Dimension size){
		pixelImage = new BufferedImage(size.getLines(),size.getCols(),BufferedImage.TYPE_INT_RGB);
		this.size = size;
	}

	private void updateColor(WritableRaster raster,Position p,RGB error,double mult){
		if(!size.includes(p)) return;
		
		int c = p.getCol(), l = p.getLine();
		RGB oldcol = new RGB(raster.getPixel(c, l, new int[3]));
		RGB newcol = new RGB( oldcol.add(error.mult(mult)) );
		raster.setPixel(c, l, newcol.rgbBands());
	}
	
	private ColorChar getCharFor(WritableRaster raster,ColorTable<? extends Color> palette,Position p,Dimension d){
		OccurenceTopList<RGB> topList = new OccurenceTopList<RGB>();
		
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
		if(fg==null && bg==null)
			return new ColorChar(' ',new ColorPair( ColorDepth.colorFromRGB(new RGB(0,0,0), palette.depth()),
													ColorDepth.colorFromRGB(new RGB(255,255,255), palette.depth())));
		
		ColorPair col;
		if(fg==null)
			col = new ColorPair(ColorDepth.colorFromRGB(bg, palette.depth()),
								ColorDepth.colorFromRGB(bg, palette.depth()));
		else
			col = new ColorPair(ColorDepth.colorFromRGB(fg, palette.depth()),
								ColorDepth.colorFromRGB(bg, palette.depth()));
		
		char c =ASCIIPicture.getChar(	(((double)topList.getSecondCount()) /
										 ((double)topList.getFirstCount())) );
		
		return new ColorChar(c,col);
	}
	
	public ASCIIPicture ASCIIDither(Resolution r,ColorTable<? extends Color> palette){
		 ASCIIPicture destination = new ASCIIPicture(size, r);
		 
		 WritableRaster workingRaster = pixelImage.copyData(null);
		 Dimension part = r.toPixels(Dimension.UNITY);
		 
		 Iterator<Position> i = destination.size().iterator();
		 while(i.hasNext()){
			 Position p = i.next();
			 Position pp = r.toPixels(p);
			 destination.set(p,getCharFor(workingRaster, palette, pp, part)); 
		 }
		 
		 
		 return destination;
	}

}



