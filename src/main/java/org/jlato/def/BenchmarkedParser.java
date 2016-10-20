package org.jlato.def;

import org.jlato.parser.ParserConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public interface BenchmarkedParser {

	public interface Factory {
		BenchmarkedParser instantiate();
	}

	Object parseAll(File directory) throws Exception;

	Object parseFile(File file) throws Exception;

	Map<String, Factory> All = AllInit.get();

	class AllInit {
		static Map<String, Factory> get() {
			Map<String, Factory> all = new HashMap();

			all.put("JavaParser", new JavaParserParser.JavaParserFactory("UTF-8", false));
			all.put("JavaParser-cm", new JavaParserParser.JavaParserFactory("UTF-8", true));

			all.put("JLaTo", new JLaToParser.JLaToFactory("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(false)));
			all.put("JLaTo2", new JLaToParser.JLaToFactory("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(false).setParser("2")));
			all.put("JLaTo2 x2", new JLaToParser.JLaToFactory("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(false).setParser("2")));
			all.put("JLaTo-lex", new JLaToParser.JLaToFactory("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(true)));

			all.put("Javac", new JavacParser.JavacFactory(true, true, true));

			all.put("Antlr4-Java7", new AntlrJavaParser.AntlrJavaFactory(false, true, false, true, true));
			all.put("Antlr4-Java7 x2", new AntlrJavaParser.AntlrJavaFactory(false, true, false, true, true));
			all.put("Antlr4-Java8", new AntlrJava8Parser.AntlrJava8Factory(false, true, false, true, true));

			return all;
		}
	}
}
