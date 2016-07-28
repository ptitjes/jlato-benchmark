package org.jlato;

import com.github.javaparser.JavaParser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class JavaParserParser implements BenchmarkedParser {

	private final String encoding;
	private final boolean considerComments;

	public JavaParserParser(String encoding, boolean considerComments) {
		this.encoding = encoding;
		this.considerComments = considerComments;
	}

	@Override
	public void parseAll(File directory) throws Exception {
		List<File> files = collectAllJavaFiles(directory, new ArrayList<File>());
		for (File file : files) {
			JavaParser.parse(file, encoding, considerComments);
		}
	}

	// TODO Use NIO filesystem walker
	private static List<File> collectAllJavaFiles(File rootDirectory, List<File> files) {
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
