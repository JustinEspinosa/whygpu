package textmode.curses.ui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;


public class ColorTable<T extends Color> extends ArrayList<RGB>{

	public static ColorTable<AnsiColor8> AnsiColor8 = new ColorTable<AnsiColor8>(AnsiColor8.class);
	public static ColorTable<AnsiColor16> AnsiColor16 = new ColorTable<AnsiColor16>(AnsiColor16.class);
	public static ColorTable<XTermColor256> XTermColor256 = new ColorTable<XTermColor256>();

	private static EnumMap<ColorDepth,ColorTable<?>> tables = new EnumMap<ColorDepth,ColorTable<?>>(ColorDepth.class);
	
	static{
		AnsiColor8.add(new RGB(0x00,0x00,0x00,0));
		AnsiColor8.add(new RGB(0x80,0x00,0x00,1));
		AnsiColor8.add(new RGB(0x00,0x80,0x00,2));
		AnsiColor8.add(new RGB(0x80,0x80,0x00,3));
		AnsiColor8.add(new RGB(0x00,0x00,0x80,4));
		AnsiColor8.add(new RGB(0x80,0x00,0x80,5));
		AnsiColor8.add(new RGB(0x00,0x80,0x80,6));
		AnsiColor8.add(new RGB(0xc0,0xc0,0xc0,7));
		
		AnsiColor16.add(new RGB(0x00,0x00,0x00,0));
		AnsiColor16.add(new RGB(0x80,0x00,0x00,1));
		AnsiColor16.add(new RGB(0x00,0x80,0x00,2));
		AnsiColor16.add(new RGB(0x80,0x80,0x00,3));
		AnsiColor16.add(new RGB(0x00,0x00,0x80,4));
		AnsiColor16.add(new RGB(0x80,0x00,0x80,5));
		AnsiColor16.add(new RGB(0x00,0x80,0x80,6));
		AnsiColor16.add(new RGB(0xc0,0xc0,0xc0,7));
		AnsiColor16.add(new RGB(0x80,0x80,0x80,8));
		AnsiColor16.add(new RGB(0xff,0x00,0x00,9));
		AnsiColor16.add(new RGB(0x00,0xff,0x00,10));
		AnsiColor16.add(new RGB(0xff,0xff,0x00,11));
		AnsiColor16.add(new RGB(0x00,0x00,0xff,12));
		AnsiColor16.add(new RGB(0xff,0x00,0xff,13));
		AnsiColor16.add(new RGB(0x00,0xff,0xff,14));
		AnsiColor16.add(new RGB(0xff,0xff,0xff,15));
		
		XTermColor256.add(new RGB(0x00,0x00,0x00,0));
		XTermColor256.add(new RGB(0x80,0x00,0x00,1));
		XTermColor256.add(new RGB(0x00,0x80,0x00,2));
		XTermColor256.add(new RGB(0x80,0x80,0x00,3));
		XTermColor256.add(new RGB(0x00,0x00,0x80,4));
		XTermColor256.add(new RGB(0x80,0x00,0x80,5));
		XTermColor256.add(new RGB(0x00,0x80,0x80,6));
		XTermColor256.add(new RGB(0xc0,0xc0,0xc0,7));
		XTermColor256.add(new RGB(0x80,0x80,0x80,8));
		XTermColor256.add(new RGB(0xff,0x00,0x00,9));
		XTermColor256.add(new RGB(0x00,0xff,0x00,10));
		XTermColor256.add(new RGB(0xff,0xff,0x00,11));
		XTermColor256.add(new RGB(0x00,0x00,0xff,12));
		XTermColor256.add(new RGB(0xff,0x00,0xff,13));
		XTermColor256.add(new RGB(0x00,0xff,0xff,14));
		XTermColor256.add(new RGB(0xff,0xff,0xff,15));
		XTermColor256.add(new RGB(0x00,0x00,0x00,16));
		XTermColor256.add(new RGB(0x00,0x00,0x5f,17));
		XTermColor256.add(new RGB(0x00,0x00,0x87,18));
		XTermColor256.add(new RGB(0x00,0x00,0xaf,19));
		XTermColor256.add(new RGB(0x00,0x00,0xdf,20));
		XTermColor256.add(new RGB(0x00,0x00,0xff,21));
		XTermColor256.add(new RGB(0x00,0x5f,0x00,22));
		XTermColor256.add(new RGB(0x00,0x5f,0x5f,23));
		XTermColor256.add(new RGB(0x00,0x5f,0x87,24));
		XTermColor256.add(new RGB(0x00,0x5f,0xaf,25));
		XTermColor256.add(new RGB(0x00,0x5f,0xdf,26));
		XTermColor256.add(new RGB(0x00,0x5f,0xff,27));
		XTermColor256.add(new RGB(0x00,0x87,0x00,28));
		XTermColor256.add(new RGB(0x00,0x87,0x5f,29));
		XTermColor256.add(new RGB(0x00,0x87,0x87,30));
		XTermColor256.add(new RGB(0x00,0x87,0xaf,31));
		XTermColor256.add(new RGB(0x00,0x87,0xdf,32));
		XTermColor256.add(new RGB(0x00,0x87,0xff,33));
		XTermColor256.add(new RGB(0x00,0xaf,0x00,34));
		XTermColor256.add(new RGB(0x00,0xaf,0x5f,35));
		XTermColor256.add(new RGB(0x00,0xaf,0x87,36));
		XTermColor256.add(new RGB(0x00,0xaf,0xaf,37));
		XTermColor256.add(new RGB(0x00,0xaf,0xdf,38));
		XTermColor256.add(new RGB(0x00,0xaf,0xff,39));
		XTermColor256.add(new RGB(0x00,0xdf,0x00,40));
		XTermColor256.add(new RGB(0x00,0xdf,0x5f,41));
		XTermColor256.add(new RGB(0x00,0xdf,0x87,42));
		XTermColor256.add(new RGB(0x00,0xdf,0xaf,43));
		XTermColor256.add(new RGB(0x00,0xdf,0xdf,44));
		XTermColor256.add(new RGB(0x00,0xdf,0xff,45));
		XTermColor256.add(new RGB(0x00,0xff,0x00,46));
		XTermColor256.add(new RGB(0x00,0xff,0x5f,47));
		XTermColor256.add(new RGB(0x00,0xff,0x87,48));
		XTermColor256.add(new RGB(0x00,0xff,0xaf,49));
		XTermColor256.add(new RGB(0x00,0xff,0xdf,50));
		XTermColor256.add(new RGB(0x00,0xff,0xff,51));
		XTermColor256.add(new RGB(0x5f,0x00,0x00,52));
		XTermColor256.add(new RGB(0x5f,0x00,0x5f,53));
		XTermColor256.add(new RGB(0x5f,0x00,0x87,54));
		XTermColor256.add(new RGB(0x5f,0x00,0xaf,55));
		XTermColor256.add(new RGB(0x5f,0x00,0xdf,56));
		XTermColor256.add(new RGB(0x5f,0x00,0xff,57));
		XTermColor256.add(new RGB(0x5f,0x5f,0x00,58));
		XTermColor256.add(new RGB(0x5f,0x5f,0x5f,59));
		XTermColor256.add(new RGB(0x5f,0x5f,0x87,60));
		XTermColor256.add(new RGB(0x5f,0x5f,0xaf,61));
		XTermColor256.add(new RGB(0x5f,0x5f,0xdf,62));
		XTermColor256.add(new RGB(0x5f,0x5f,0xff,63));
		XTermColor256.add(new RGB(0x5f,0x87,0x00,64));
		XTermColor256.add(new RGB(0x5f,0x87,0x5f,65));
		XTermColor256.add(new RGB(0x5f,0x87,0x87,66));
		XTermColor256.add(new RGB(0x5f,0x87,0xaf,67));
		XTermColor256.add(new RGB(0x5f,0x87,0xdf,68));
		XTermColor256.add(new RGB(0x5f,0x87,0xff,69));
		XTermColor256.add(new RGB(0x5f,0xaf,0x00,70));
		XTermColor256.add(new RGB(0x5f,0xaf,0x5f,71));
		XTermColor256.add(new RGB(0x5f,0xaf,0x87,72));
		XTermColor256.add(new RGB(0x5f,0xaf,0xaf,73));
		XTermColor256.add(new RGB(0x5f,0xaf,0xdf,74));
		XTermColor256.add(new RGB(0x5f,0xaf,0xff,75));
		XTermColor256.add(new RGB(0x5f,0xdf,0x00,76));
		XTermColor256.add(new RGB(0x5f,0xdf,0x5f,77));
		XTermColor256.add(new RGB(0x5f,0xdf,0x87,78));
		XTermColor256.add(new RGB(0x5f,0xdf,0xaf,79));
		XTermColor256.add(new RGB(0x5f,0xdf,0xdf,80));
		XTermColor256.add(new RGB(0x5f,0xdf,0xff,81));
		XTermColor256.add(new RGB(0x5f,0xff,0x00,82));
		XTermColor256.add(new RGB(0x5f,0xff,0x5f,83));
		XTermColor256.add(new RGB(0x5f,0xff,0x87,84));
		XTermColor256.add(new RGB(0x5f,0xff,0xaf,85));
		XTermColor256.add(new RGB(0x5f,0xff,0xdf,86));
		XTermColor256.add(new RGB(0x5f,0xff,0xff,87));
		XTermColor256.add(new RGB(0x87,0x00,0x00,88));
		XTermColor256.add(new RGB(0x87,0x00,0x5f,89));
		XTermColor256.add(new RGB(0x87,0x00,0x87,90));
		XTermColor256.add(new RGB(0x87,0x00,0xaf,91));
		XTermColor256.add(new RGB(0x87,0x00,0xdf,92));
		XTermColor256.add(new RGB(0x87,0x00,0xff,93));
		XTermColor256.add(new RGB(0x87,0x5f,0x00,94));
		XTermColor256.add(new RGB(0x87,0x5f,0x5f,95));
		XTermColor256.add(new RGB(0x87,0x5f,0x87,96));
		XTermColor256.add(new RGB(0x87,0x5f,0xaf,97));
		XTermColor256.add(new RGB(0x87,0x5f,0xdf,98));
		XTermColor256.add(new RGB(0x87,0x5f,0xff,99));
		XTermColor256.add(new RGB(0x87,0x87,0x00,100));
		XTermColor256.add(new RGB(0x87,0x87,0x5f,101));
		XTermColor256.add(new RGB(0x87,0x87,0x87,102));
		XTermColor256.add(new RGB(0x87,0x87,0xaf,103));
		XTermColor256.add(new RGB(0x87,0x87,0xdf,104));
		XTermColor256.add(new RGB(0x87,0x87,0xff,105));
		XTermColor256.add(new RGB(0x87,0xaf,0x00,106));
		XTermColor256.add(new RGB(0x87,0xaf,0x5f,107));
		XTermColor256.add(new RGB(0x87,0xaf,0x87,108));
		XTermColor256.add(new RGB(0x87,0xaf,0xaf,109));
		XTermColor256.add(new RGB(0x87,0xaf,0xdf,110));
		XTermColor256.add(new RGB(0x87,0xaf,0xff,111));
		XTermColor256.add(new RGB(0x87,0xdf,0x00,112));
		XTermColor256.add(new RGB(0x87,0xdf,0x5f,113));
		XTermColor256.add(new RGB(0x87,0xdf,0x87,114));
		XTermColor256.add(new RGB(0x87,0xdf,0xaf,115));
		XTermColor256.add(new RGB(0x87,0xdf,0xdf,116));
		XTermColor256.add(new RGB(0x87,0xdf,0xff,117));
		XTermColor256.add(new RGB(0x87,0xff,0x00,118));
		XTermColor256.add(new RGB(0x87,0xff,0x5f,119));
		XTermColor256.add(new RGB(0x87,0xff,0x87,120));
		XTermColor256.add(new RGB(0x87,0xff,0xaf,121));
		XTermColor256.add(new RGB(0x87,0xff,0xdf,122));
		XTermColor256.add(new RGB(0x87,0xff,0xff,123));
		XTermColor256.add(new RGB(0xaf,0x00,0x00,124));
		XTermColor256.add(new RGB(0xaf,0x00,0x5f,125));
		XTermColor256.add(new RGB(0xaf,0x00,0x87,126));
		XTermColor256.add(new RGB(0xaf,0x00,0xaf,127));
		XTermColor256.add(new RGB(0xAF,0x00,0xDF,128));
		XTermColor256.add(new RGB(0xaf,0x00,0xff,129));
		XTermColor256.add(new RGB(0xaf,0x5f,0x00,130));
		XTermColor256.add(new RGB(0xaf,0x5f,0x5f,131));
		XTermColor256.add(new RGB(0xaf,0x5f,0x87,132));
		XTermColor256.add(new RGB(0xaf,0x5f,0xaf,133));
		XTermColor256.add(new RGB(0xaf,0x5f,0xdf,134));
		XTermColor256.add(new RGB(0xaf,0x5f,0xff,135));
		XTermColor256.add(new RGB(0xaf,0x87,0x00,136));
		XTermColor256.add(new RGB(0xaf,0x87,0x5f,137));
		XTermColor256.add(new RGB(0xaf,0x87,0x87,138));
		XTermColor256.add(new RGB(0xaf,0x87,0xaf,139));
		XTermColor256.add(new RGB(0xaf,0x87,0xdf,140));
		XTermColor256.add(new RGB(0xaf,0x87,0xff,141));
		XTermColor256.add(new RGB(0xaf,0xaf,0x00,142));
		XTermColor256.add(new RGB(0xaf,0xaf,0x5f,143));
		XTermColor256.add(new RGB(0xaf,0xaf,0x87,144));
		XTermColor256.add(new RGB(0xaf,0xaf,0xaf,145));
		XTermColor256.add(new RGB(0xaf,0xaf,0xdf,146));
		XTermColor256.add(new RGB(0xaf,0xaf,0xff,147));
		XTermColor256.add(new RGB(0xaf,0xdf,0x00,148));
		XTermColor256.add(new RGB(0xaf,0xdf,0x5f,149));
		XTermColor256.add(new RGB(0xaf,0xdf,0x87,150));
		XTermColor256.add(new RGB(0xaf,0xdf,0xaf,151));
		XTermColor256.add(new RGB(0xaf,0xdf,0xdf,152));
		XTermColor256.add(new RGB(0xaf,0xdf,0xff,153));
		XTermColor256.add(new RGB(0xaf,0xff,0x00,154));
		XTermColor256.add(new RGB(0xaf,0xff,0x5f,155));
		XTermColor256.add(new RGB(0xaf,0xff,0x87,156));
		XTermColor256.add(new RGB(0xaf,0xff,0xaf,157));
		XTermColor256.add(new RGB(0xaf,0xff,0xdf,158));
		XTermColor256.add(new RGB(0xaf,0xff,0xff,159));
		XTermColor256.add(new RGB(0xdf,0x00,0x00,160));
		XTermColor256.add(new RGB(0xdf,0x00,0x5f,161));
		XTermColor256.add(new RGB(0xdf,0x00,0x87,162));
		XTermColor256.add(new RGB(0xdf,0x00,0xaf,163));
		XTermColor256.add(new RGB(0xdf,0x00,0xdf,164));
		XTermColor256.add(new RGB(0xdf,0x00,0xff,165));
		XTermColor256.add(new RGB(0xdf,0x5f,0x00,166));
		XTermColor256.add(new RGB(0xdf,0x5f,0x5f,167));
		XTermColor256.add(new RGB(0xdf,0x5f,0x87,168));
		XTermColor256.add(new RGB(0xdf,0x5f,0xaf,169));
		XTermColor256.add(new RGB(0xdf,0x5f,0xdf,170));
		XTermColor256.add(new RGB(0xdf,0x5f,0xff,171));
		XTermColor256.add(new RGB(0xdf,0x87,0x00,172));
		XTermColor256.add(new RGB(0xdf,0x87,0x5f,173));
		XTermColor256.add(new RGB(0xdf,0x87,0x87,174));
		XTermColor256.add(new RGB(0xdf,0x87,0xaf,175));
		XTermColor256.add(new RGB(0xdf,0x87,0xdf,176));
		XTermColor256.add(new RGB(0xdf,0x87,0xff,177));
		XTermColor256.add(new RGB(0xdf,0xaf,0x00,178));
		XTermColor256.add(new RGB(0xdf,0xaf,0x5f,179));
		XTermColor256.add(new RGB(0xdf,0xaf,0x87,180));
		XTermColor256.add(new RGB(0xdf,0xaf,0xaf,181));
		XTermColor256.add(new RGB(0xdf,0xaf,0xdf,182));
		XTermColor256.add(new RGB(0xdf,0xaf,0xff,183));
		XTermColor256.add(new RGB(0xdf,0xdf,0x00,184));
		XTermColor256.add(new RGB(0xdf,0xdf,0x5f,185));
		XTermColor256.add(new RGB(0xdf,0xdf,0x87,186));
		XTermColor256.add(new RGB(0xdf,0xdf,0xaf,187));
		XTermColor256.add(new RGB(0xdf,0xdf,0xdf,188));
		XTermColor256.add(new RGB(0xdf,0xdf,0xff,189));
		XTermColor256.add(new RGB(0xdf,0xff,0x00,190));
		XTermColor256.add(new RGB(0xdf,0xff,0x5f,191));
		XTermColor256.add(new RGB(0xdf,0xff,0x87,192));
		XTermColor256.add(new RGB(0xdf,0xff,0xaf,193));
		XTermColor256.add(new RGB(0xdf,0xff,0xdf,194));
		XTermColor256.add(new RGB(0xdf,0xff,0xff,195));
		XTermColor256.add(new RGB(0xff,0x00,0x00,196));
		XTermColor256.add(new RGB(0xff,0x00,0x5f,197));
		XTermColor256.add(new RGB(0xff,0x00,0x87,198));
		XTermColor256.add(new RGB(0xff,0x00,0xaf,199));
		XTermColor256.add(new RGB(0xff,0x00,0xdf,200));
		XTermColor256.add(new RGB(0xff,0x00,0xff,201));
		XTermColor256.add(new RGB(0xff,0x5f,0x00,202));
		XTermColor256.add(new RGB(0xff,0x5f,0x5f,203));
		XTermColor256.add(new RGB(0xff,0x5f,0x87,204));
		XTermColor256.add(new RGB(0xff,0x5f,0xaf,205));
		XTermColor256.add(new RGB(0xff,0x5f,0xdf,206));
		XTermColor256.add(new RGB(0xff,0x5f,0xff,207));
		XTermColor256.add(new RGB(0xff,0x87,0x00,208));
		XTermColor256.add(new RGB(0xff,0x87,0x5f,209));
		XTermColor256.add(new RGB(0xff,0x87,0x87,210));
		XTermColor256.add(new RGB(0xff,0x87,0xaf,211));
		XTermColor256.add(new RGB(0xff,0x87,0xdf,212));
		XTermColor256.add(new RGB(0xff,0x87,0xff,213));
		XTermColor256.add(new RGB(0xff,0xaf,0x00,214));
		XTermColor256.add(new RGB(0xff,0xaf,0x5f,215));
		XTermColor256.add(new RGB(0xff,0xaf,0x87,216));
		XTermColor256.add(new RGB(0xff,0xaf,0xaf,217));
		XTermColor256.add(new RGB(0xff,0xaf,0xdf,218));
		XTermColor256.add(new RGB(0xff,0xaf,0xff,219));
		XTermColor256.add(new RGB(0xff,0xdf,0x00,220));
		XTermColor256.add(new RGB(0xff,0xdf,0x5f,221));
		XTermColor256.add(new RGB(0xff,0xdf,0x87,222));
		XTermColor256.add(new RGB(0xff,0xdf,0xaf,223));
		XTermColor256.add(new RGB(0xff,0xdf,0xdf,224));
		XTermColor256.add(new RGB(0xff,0xdf,0xff,225));
		XTermColor256.add(new RGB(0xff,0xff,0x00,226));
		XTermColor256.add(new RGB(0xff,0xff,0x5f,227));
		XTermColor256.add(new RGB(0xff,0xff,0x87,228));
		XTermColor256.add(new RGB(0xff,0xff,0xaf,229));
		XTermColor256.add(new RGB(0xff,0xff,0xdf,230));
		XTermColor256.add(new RGB(0xff,0xff,0xff,231));
		XTermColor256.add(new RGB(0x08,0x08,0x08,232));
		XTermColor256.add(new RGB(0x12,0x12,0x12,233));
		XTermColor256.add(new RGB(0x1c,0x1c,0x1c,234));
		XTermColor256.add(new RGB(0x26,0x26,0x26,235));
		XTermColor256.add(new RGB(0x30,0x30,0x30,236));
		XTermColor256.add(new RGB(0x3a,0x3a,0x3a,237));
		XTermColor256.add(new RGB(0x44,0x44,0x44,238));
		XTermColor256.add(new RGB(0x4e,0x4e,0x4e,239));
		XTermColor256.add(new RGB(0x58,0x58,0x58,240));
		XTermColor256.add(new RGB(0x60,0x60,0x60,241));
		XTermColor256.add(new RGB(0x66,0x66,0x66,242));
		XTermColor256.add(new RGB(0x76,0x76,0x76,243));
		XTermColor256.add(new RGB(0x80,0x80,0x80,244));
		XTermColor256.add(new RGB(0x8a,0x8a,0x8a,245));
		XTermColor256.add(new RGB(0x94,0x94,0x94,246));
		XTermColor256.add(new RGB(0x9e,0x9e,0x9e,247));
		XTermColor256.add(new RGB(0xa8,0xa8,0xa8,248));
		XTermColor256.add(new RGB(0xb2,0xb2,0xb2,249));
		XTermColor256.add(new RGB(0xbc,0xbc,0xbc,250));
		XTermColor256.add(new RGB(0xc6,0xc6,0xc6,251));
		XTermColor256.add(new RGB(0xd0,0xd0,0xd0,252));
		XTermColor256.add(new RGB(0xda,0xda,0xda,253));
		XTermColor256.add(new RGB(0xe4,0xe4,0xe4,254));
		XTermColor256.add(new RGB(0xee,0xee,0xee,255));
		
		
		tables.put(ColorDepth.COL8, AnsiColor8);
		tables.put(ColorDepth.COL16, AnsiColor8);
		tables.put(ColorDepth.COL256, AnsiColor8);
		
	}

	
	public static Color convert(Color from,ColorDepth toDepth){
		
		if(from.depth() != toDepth){
			ColorTable<?> table = tables.get(toDepth);
			if(table.mustFind()){
				RGB toRGB = table.findNearestIndex(from.rgb());
				return table.finder().find(toRGB.index());
			}else{
				return new XTermColor256(from.rgb().x(), from.rgb().y(), from.rgb().z());
			}
		}
		return from;
	}
	
	private static final long serialVersionUID = -4863045937566709141L;

	private AnsiColorFinder<T> colorFinder = null;
	
	public ColorTable(){}
	public ColorTable(Class<? extends Enum<?>> eClass){
		colorFinder = new AnsiColorFinder<T>(eClass);
	}
	
	public AnsiColorFinder<?> finder(){
		return colorFinder;
	}
	
	public boolean mustFind(){
		return colorFinder!=null;
	}
	
	public RGB findNearestIndex(RGB col){
		double minDist=Double.MAX_VALUE; RGB min = get(0);
		
		Iterator<RGB> i=iterator();
		while(i.hasNext()){
			RGB color = i.next();
			
			double d = col.distance(color);
			
			if(d<minDist){
				minDist = d;
				min = color;
			}		
		}
		
		return min;
	}
}
