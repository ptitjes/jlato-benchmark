package org.jlato.def;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.util.Context;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class JavacParser implements BenchmarkedParser {

	public static class JavacFactory implements Factory {

		private final boolean keepDocComments;
		private final boolean keepEndPos;
		private final boolean keepLineMap;

		public JavacFactory(boolean keepDocComments, boolean keepEndPos, boolean keepLineMap) {
			this.keepDocComments = keepDocComments;
			this.keepEndPos = keepEndPos;
			this.keepLineMap = keepLineMap;
		}

		@Override
		public BenchmarkedParser instantiate() {
			return new JavacParser(keepDocComments, keepEndPos, keepLineMap);
		}
	}

	private final boolean keepDocComments;
	private final boolean keepEndPos;
	private final boolean keepLineMap;
	private final Context context = new Context();

	{
		JavacFileManager.preRegister(context);
	}

	private final ParserFactory factory = ParserFactory.instance(context);

	public JavacParser(boolean keepDocComments, boolean keepEndPos, boolean keepLineMap) {
		this.keepDocComments = keepDocComments;
		this.keepEndPos = keepEndPos;
		this.keepLineMap = keepLineMap;
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
	public Object parseFile(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileChannel fc = fis.getChannel();

		// Create a read-only CharBuffer on the file
		ByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
		CharBuffer charBuffer = Charset.forName("UTF-8").newDecoder().decode(byteBuffer);

		try {
			return parse(charBuffer);
		} finally {
			fc.close();
		}
	}

	private Object parse(CharSequence input) {
		com.sun.tools.javac.parser.JavacParser parser = factory.newParser(input, keepDocComments, keepEndPos, keepLineMap);
		return parser.parseCompilationUnit();
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
