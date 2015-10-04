package de.smv_am_mpg.smvapp;

import java.io.InputStream;
import java.util.Scanner;

public class StreamUtils {
	public static String streamToString(InputStream is) {
		Scanner scanner = null;
		StringBuilder sb = null;
		try {
			scanner = new Scanner(is);
			sb = new StringBuilder(500);
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();

		}
		return sb.toString();
	}
}
