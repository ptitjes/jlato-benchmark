package org.jlato.def;

import org.jlato.parser.ParserConfiguration;

import java.io.File;
import java.io.InputStream;

/**
 * @author Didier Villevalois
 */
public interface BenchmarkedParser {

	Object parse(InputStream inputStream, String encoding) throws Exception;
	Object parse(File file) throws Exception;
	Object parseAll(File directory) throws Exception;

	BenchmarkedParser JavaParserParser = new JavaParserParser("UTF-8", false);
	BenchmarkedParser JavaParserParser_WithComments = new JavaParserParser("UTF-8", true);

	BenchmarkedParser JLaToParser = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(false));
	BenchmarkedParser JLaToParser_Preserving = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(true));

	BenchmarkedParser Antlr4 = new AntlrParser(false, false, true, false, true);
}
