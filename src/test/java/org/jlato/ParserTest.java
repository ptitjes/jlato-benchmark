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
//		unzipSources("openjdk-8-src-b132-03_mar_2014.zip", "openjdk");
		unjarSources("javaparser-core", "2.5.1");
	}

	@After
	public void tearDown() throws IOException {
		rmTmpDir();
	}

	@Test
	@Ignore
	public void jdk() throws Exception {
		JLaToParser.JLaToFactory factory = new JLaToParser.JLaToFactory("UTF-8",
				ParserConfiguration.Default.preserveWhitespaces(false).setParser("2"));
		for (int i = 0; i < 1000; i++) {
			BenchmarkedParser parser = factory.instantiate();

			if (i % 20 == 0) System.out.println(i);
//			parseJdkSources(parser);
			parseSources("javaparser-core", "2.5.1", parser);
			parseSources("javaparser-core", "2.5.1", parser);
		}
	}

	protected Object parseJdkSources(BenchmarkedParser parser) throws Exception {
		return parser.parseAll(new File(makeTempDirFile("openjdk"), "openjdk/jdk/src/share/classes/"));
	}

	double[][][] a = new double[0][][];
}
