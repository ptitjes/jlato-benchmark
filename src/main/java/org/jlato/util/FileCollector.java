package org.jlato.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class FileCollector {

	// TODO Use NIO filesystem walker
	public static List<File> collectAllJavaFiles(File rootDirectory, List<File> files) {
		final File[] localFiles = rootDirectory.listFiles(JAVA_FILTER);
		assert localFiles != null;

		files.addAll(Arrays.asList(localFiles));

		for (File directory : rootDirectory.listFiles(DIRECTORY_FILTER)) {
			collectAllJavaFiles(directory, files);
		}

		return files;
	}

	private static final FileFilter JAVA_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".java");
		}
	};

	private static final FileFilter DIRECTORY_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
}
