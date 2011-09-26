package fun.useless.xfer.util;

public class RawByteList{

	private static final int more = 10;
	private byte[] backArray;
	private int count = 0;
	
	
	public RawByteList(int initialCapacity){
		backArray = new byte[initialCapacity];
	}
	
	public RawByteList(){
		this(more);
	}
	
	public int size() {
		return count;
	}

	public boolean isEmpty() {
		return (count==0);
	}


	public byte[] toArray() {
		return Arrays.copyOf(backArray, count);
	}

	public boolean add(byte b) {
		ensureCapacity(count+1);
		backArray[count++] = b;
		return false;
	}

	private void ensureCapacity(int i) {
		while(i>backArray.length){
			backArray = Arrays.copyOf(backArray, backArray.length+more);
		}
	}

	public void clear() {
		backArray = new byte[more];
	}

	public byte get(int index) {
		return backArray[index];
	}

	public byte set(int index, byte b) {
		return (backArray[index] = b);
	}

}
