package org.jlato.bench;

import org.jlato.def.BenchmarkedParser;
import org.jlato.util.ParseBenchmarkBase;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Didier Villevalois
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ParsingBigFiles extends ParseBenchmarkBase {

	@Param({
			"RandoopTest0.java",
			"ContactsProvider2.java",
			"R.java",
	})
	private String source;

	@Param({
			"JLaTo",
			"JLaTo-x2",
			"JavaParser",
			"Javac",
			"Antlr4-Java7",
			"Antlr4-Java7-x2",
	})
	private String parser;

	@Setup(Level.Trial)
	public void prepareSources() throws IOException {
		mkTmpDir();
		copyResource(source, source);
	}

	@TearDown(Level.Trial)
	public void cleanupSources() throws IOException {
		rmTmpDir();
	}

	private BenchmarkedParser.Factory factory;
	private BenchmarkedParser warmParser;

	@Setup(Level.Trial)
	public void setupParser() throws Exception {
		boolean warmUp = parser.endsWith("-x2");
		String parserName = !warmUp ? parser : parser.substring(0, parser.length() - 3);

		factory = BenchmarkedParser.All.get(parserName);
		if (warmUp) {
			warmParser = factory.instantiate();
			doParse();
		}
	}

	@Benchmark
	public Object time() throws Exception {
		return doParse();
	}

	public Object doParse() throws Exception {
		BenchmarkedParser parser = warmParser != null ? warmParser : factory.instantiate();
		return parser.parseFile(makeTempDirFile(source));
	}
}
