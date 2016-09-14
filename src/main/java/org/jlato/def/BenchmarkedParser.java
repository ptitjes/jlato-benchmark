package org.jlato.def;

import org.jlato.parser.ParserConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public interface BenchmarkedParser {

	Object parseAll(File directory) throws Exception;

	Object parseFile(File file) throws Exception;

	Map<String, BenchmarkedParser> All = AllInit.get();

	class AllInit {
		static Map<String, BenchmarkedParser> get() {
			Map<String, BenchmarkedParser> all = new HashMap<String, BenchmarkedParser>();

			all.put("JavaParser", new JavaParserParser("UTF-8", false));
			all.put("JavaParser-cm", new JavaParserParser("UTF-8", true));

			all.put("JLaTo", new JLaToParser("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(false)));
			all.put("JLaTo2", new JLaToParser("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(false).setParser("2")));
			all.put("JLaTo-lex", new JLaToParser("UTF-8",
					ParserConfiguration.Default.preserveWhitespaces(true)));

			all.put("Javac", new JavacParser(true, true, true));

			all.put("Antlr4-Java7", new AntlrJavaParser(false, true, false, true, true));
			all.put("Antlr4-Java8", new AntlrJava8Parser(false, true, false, true, true));

			return all;
		}
	}
}
