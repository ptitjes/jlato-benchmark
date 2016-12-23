package org.jlato.def;

import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;

import java.io.File;

/**
 * @author Didier Villevalois
 */
public class JLaToParser implements BenchmarkedParser {

	public static class JLaToFactory implements Factory {

		private final String encoding;
		private final ParserConfiguration configuration;

		public JLaToFactory(String encoding, ParserConfiguration configuration) {
			this.encoding = encoding;
			this.configuration = configuration;
		}

		@Override
		public BenchmarkedParser instantiate() {
			return new JLaToParser(encoding, configuration);
		}
	}

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

	@Override
	public Object parseFile(File file) throws Exception {
		return parser.parse(file, encoding);
	}
}
