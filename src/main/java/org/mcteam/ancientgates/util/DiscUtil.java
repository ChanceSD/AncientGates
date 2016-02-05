package org.mcteam.ancientgates.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// Hard disc related methods such as read and write.
public class DiscUtil {

	// Convenience function for writing a string to a file.
	public static void write(final File file, final String content) throws IOException {
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF8"));
		out.write(content);
		out.close();
	}

	// Convenience function for reading a file as a string.
	public static String read(final File file) throws IOException {
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String ret = new String(new byte[0], "UTF-8");

		String line;
		while ((line = in.readLine()) != null) {
			ret += line;
		}
		in.close();

		return ret;
	}

}
