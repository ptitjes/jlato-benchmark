package org.jlato;

import org.jlato.parser.ParserConfiguration;

import java.io.File;

/**
 * @author Didier Villevalois
 */
public interface BenchmarkedParser {

	Object parseAll(File directory) throws Exception;

	BenchmarkedParser JavaParserParser_Standard = new JavaParserParser("UTF-8", false);
	BenchmarkedParser JavaParserParser_WithComments = new JavaParserParser("UTF-8", true);

	BenchmarkedParser JLaToParser_New = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(false).setParser("new"));
	BenchmarkedParser JLaToParser_Old = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(false).setParser("old"));
	BenchmarkedParser JLaToParser_Preserving_New = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(true).setParser("new"));
	BenchmarkedParser JLaToParser_Preserving_Old = new JLaToParser("UTF-8",
			ParserConfiguration.Default.preserveWhitespaces(true).setParser("old"));
}
