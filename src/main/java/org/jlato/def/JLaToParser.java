package org.jlato.def;

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
	public Object parseAll(File directory) throws Exception {
		return parser.parseAll(directory, encoding);
	}

}
