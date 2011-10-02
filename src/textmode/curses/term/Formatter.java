package textmode.curses.term;

public class Formatter {

	private StringBuilder builder;
	private String        formatStr;
	
	private class ParamPointer{
		private int      current   = 0;
		private int[]    params;
		public ParamPointer(int[] p){
			params = p;
		}
		public int next(){
			int val = params[current];

			current++;

			return val;
		}
		public void swap(){
			int temp = params[current];
			params[current]=params[current+1];
			params[current+1]=temp;
		}
		public void increment(){
			params[current]++;
			params[current+1]++;
		}
		public void backup(){
			current--;
		}
		public void skip(){
			current++;
		}
		public void spen(){
			params[current]   = params[current] ^ 0x60;
			params[current+1] = params[current+1] ^ 0x60;
		}
		public void spem(){
			params[current] = ~params[current];
			params[current+1] = ~params[current+1];			
		}
		public void speB(){
			params[current] = (params[current] / 10) * 16 + params[current] % 10;
		}
		public void speD(){
			params[current] -= 2 * (params[current] % 16);
		}
		public void condinc(char c,char incval){
			if(params[current] > c)
				params[current]+=incval;
		}
		public void position(char pos){
			current = pos - 0x31;
		}
		public void aop(char op, char type, char pos){
			int operand = 0;
			if(type=='p')
				operand = params[current+pos-64];
			if(type=='c')
				operand = pos&(~0x80);
			
			switch(op){
			case '=':
				params[current] = operand;
				break;
			case '+':
				params[current] += operand;
				break;
			case '-':
				params[current] -= operand;
				break;
			case '*':
				params[current] *= operand;
				break;
			case '/':
				params[current] /= operand;
				break;
			}
		}
	}
	
	public Formatter(String str){
		formatStr = str;
	}
	
	private String prependZero(int i,int cnt)
	{
		String s = String.valueOf(i);
		while(s.length()<cnt)
			s = "0".concat(s);
		return s;
	}

	
	public String format(int ... params)
	{
		builder   = new StringBuilder();
		
		int fOffset   = 0;
		ParamPointer pp = new ParamPointer(params);
		
		while(fOffset<formatStr.length()){
		
			char nc = formatStr.charAt(fOffset);
			fOffset++;
			
			if(nc=='%'){
				nc = formatStr.charAt(fOffset);
				fOffset++;
				
				switch(nc){
				case 'p':
					char p = formatStr.charAt(fOffset);
					fOffset++;
					pp.position(p);
					break;
				case '%':
					builder.append(nc);
					break;
				case 'd':
					builder.append(pp.next());
					break;
				case '2':
					builder.append(prependZero(pp.next(),2));
					break;
				case '3':
					builder.append(prependZero(pp.next(),3));
					break;
				case '.':
					builder.append((char)pp.next());
					break;
				case '+':
					nc = formatStr.charAt(fOffset);
					fOffset++;
					builder.append((char)(nc+(char)pp.next()));
					break;
				case 'i':
					pp.increment();
					break;
				case 'r':
					pp.swap();
					break;
				case 's':
					pp.skip();
					break;
				case 'b':
					pp.backup();
					break;
				case '>':
					char a = formatStr.charAt(fOffset);
					fOffset++;
					char b = formatStr.charAt(fOffset);
					fOffset++;
					pp.condinc(a, b);
					break;
				case 'a':
					char d = formatStr.charAt(fOffset);
					fOffset++;
					char e = formatStr.charAt(fOffset);
					fOffset++;
					char f = formatStr.charAt(fOffset);
					fOffset++;
					pp.aop(d,e,f);
					break;
				case 'n':
					pp.spen();
					break;
				case 'm':
					pp.spem();
					break;
				case 'B':
					pp.speB();
					break;				
				case 'D':
					pp.speD();
					break;
				}
				
			}else{
				builder.append(nc);
			}
			
		}
		
		return builder.toString();
	}
}
