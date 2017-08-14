
package com.github.justinespinosa.textmode.util;


public class Base64 {

    private static final char digit[] = 
    {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T',
     'U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n',
     'o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7',
     '8','9', '+','/'};
    
    private static final int rdigit[] =
    {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,62,0,0,0,63,52,53,54,55,56,57,58,59,60,61,0,0,
     0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,
     15,16,17,18,19,20,21,22,23,24,25,0,0,0,0,0,0,26,27,28,
     29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,
     49,50,51,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    
    private static final char pad='=';
    
    private static int min(int a,int b){
    	return (a<b)?a:b;
    }
    
    private static int len(String str){
    	int l = str.length(), lpad=0;
    	while(str.charAt(l-(lpad+1))==pad) ++lpad;
    	return l-(l>>2)-lpad;
    }
    
    public static void quartetToTrio(char[] in,int inoffset,byte[] out,int outoffset){
    	int n=0,i,l=-1;
    	for(i=3;i>-1;--i){
    		if(in[inoffset]==pad) ++l;
    		n+=(0x3f&(int)rdigit[in[inoffset++]])*(1<<i*6);
    	}
    	for(i=2;i>l;--i) out[outoffset++]=(byte)(0xff&(n>>i*8));
    }
    
    public static void trioToQuartet(byte[] in,int inoffset,char[] out,int outoffset){
    	int n=0,l=min(in.length-inoffset,3),l4=l+1,i;
		for(i=2;i>2-l;--i)  n+=(0xff&(int)in[inoffset++])*(1<<i*8);
		for(i=3;i>3-l4;--i) out[outoffset++]=digit[0x3f&(n>>i*6)];
		for(i=l4;i<4;++i)   out[outoffset++]=pad;
	}
    
    public static byte[] decode(String str){
    	int outoffset=0, inoffset=0; char[] chrdata = str.toCharArray();
    	byte[] result = new byte[len(str)];
    	for(inoffset=0;inoffset<chrdata.length;inoffset+=4){
    		quartetToTrio(chrdata,inoffset,result,outoffset);
    		outoffset+=3;
    	}
    	return result;
    }
    
    public static String encode(byte[] in){
    	int inoffset; char[] out=new char[4];
    	StringBuilder builder = new StringBuilder(in.length+(in.length>>2));
    	for(inoffset=0;inoffset<in.length;inoffset+=3){
    		trioToQuartet(in, inoffset, out, 0);
    		builder.append(out);
    	}
    	return builder.toString();
    }
    
}
