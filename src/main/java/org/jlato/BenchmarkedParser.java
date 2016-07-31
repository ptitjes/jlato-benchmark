package org.jlato;

import org.jlato.parser.ParserConfiguration;

import java.io.File;

/**
 * @author Didier Villevalois
 */
public interface BenchmarkedParser {

	Object parseAll(File directory) throws Exception;

	BenchmarkedParser JavaParserParser = new JavaParserParser("UTF-8", false);
	BenchmarkedParser JavaParserParser_WithComments = new JavaParserParser("UTF-8", true);

	BenchmarkedParser JLaToParser = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(false));
	BenchmarkedParser JLaToParser_Preserving = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(true));
}
