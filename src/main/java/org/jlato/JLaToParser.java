package org.jlato;

import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;

import java.io.File;

/**
 * @author Didier Villevalois
 */
public class JLaToParser implements BenchmarkedParser {

	private final String encoding;
	private final Parser parser;

	public JLaToParser(String encoding, ParserConfiguration configuration) {
		this.encoding = encoding;
		this.parser = new Parser(configuration);
	}

	@Override
	public void parseAll(File directory) throws Exception {
		parser.parseAll(directory, encoding);
	}
}
