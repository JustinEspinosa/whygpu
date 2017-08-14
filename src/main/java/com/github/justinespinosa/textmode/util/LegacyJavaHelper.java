package com.github.justinespinosa.textmode.util;

/**
 * To help achieve 1.5 compatibility
 * @author justin
 * @param <T>
 *
 */
public final class LegacyJavaHelper {
	public static <T extends Throwable> T throwWithCause(T t,Throwable cause){
		t.initCause(cause);
		return t;
	}

}
