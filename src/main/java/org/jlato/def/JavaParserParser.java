package org.jlato.def;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

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
	public Object parseAll(File directory) throws Exception {
		List<File> files = collectAllJavaFiles(directory, new ArrayList<File>());

		String rootPath = directory.getAbsolutePath();
		if (!rootPath.endsWith("/")) rootPath = rootPath + "/";

		Map<String, Object> cus = new HashMap<String, Object>();
		for (File file : files) {
			final String path = file.getAbsolutePath().substring(rootPath.length());

			// There is no instance parser in JavaParser !
			cus.put(path, parseFile(file));
		}
		return cus;
	}

	@Override
	public Object parseFile(File file) throws ParseException {
		return JavaParser.parse(file, encoding, considerComments);
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
