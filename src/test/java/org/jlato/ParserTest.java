package org.jlato;

import org.jlato.def.BenchmarkedParser;
import org.jlato.def.JLaToParser;
import org.jlato.def.JavacParser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.util.ParseBenchmarkBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

/**
 * @author Didier Villevalois
 */
@RunWith(JUnit4.class)
public class ParserTest extends ParseBenchmarkBase {

	@Before
	public void tearUp() throws IOException {
		mkTmpDir();
		unzipSources("openjdk-8-src-b132-03_mar_2014.zip", "openjdk");
		unjarSources("javaparser-core", "2.5.1");
		unjarSources("javaslang", "1.2.2");
		unjarSources("jlato", "0.0.6");
	}

	@After
	public void tearDown() throws IOException {
		rmTmpDir();
	}

	@Test
	public void parseLibs() throws Exception {
		JLaToParser.JLaToFactory factory = new JLaToParser.JLaToFactory("UTF-8",
				ParserConfiguration.Default.preserveWhitespaces(false));

		BenchmarkedParser parser = factory.instantiate();

		parseSources("javaparser-core", "2.5.1", parser);
		parseSources("javaslang", "1.2.2", parser);
		parseSources("jlato", "0.0.6", parser);
	}

	@Test
	public void parseJDK() throws Exception {
		JLaToParser.JLaToFactory factory = new JLaToParser.JLaToFactory("UTF-8",
				ParserConfiguration.Default.preserveWhitespaces(false));

		BenchmarkedParser parser = factory.instantiate();

		parseJdkSources(parser);
	}

	protected Object parseJdkSources(BenchmarkedParser parser) throws Exception {
		return parser.parseAll(new File(makeTempDirFile("openjdk"), "openjdk/jdk/src/share/classes/"));
	}
}
