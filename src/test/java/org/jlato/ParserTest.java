package org.jlato;

import org.jlato.def.BenchmarkedParser;
import org.jlato.def.JavacParser;
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
		unzipSources(new File("/home/didier/Downloads/Tech/Dev/openjdk-8-src-b132-03_mar_2014.zip"), "openjdk");
	}

	@After
	public void tearDown() throws IOException {
		rmTmpDir();
	}

	@Test
	@Ignore
	public void jdk_with_javac() throws Exception {
		parseJdkSources(new JavacParser(false, false, false));
	}

	protected Object parseJdkSources(BenchmarkedParser parser) throws Exception {
		return parser.parseAll(new File(makeTempDir("openjdk"), "openjdk/jdk/src/share/classes/"));
	}

	double[][][] a = new double[0][][];
}
