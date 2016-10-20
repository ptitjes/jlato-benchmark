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
			"JLaTo2",
			"JLaTo2 x2",
			"JavaParser",
			"Javac",
			"Antlr4-Java7",
			"Antlr4-Java7 x2",
	})
	private String parser;

	private BenchmarkedParser implementation;

	@Setup(Level.Trial)
	public void copyBigFiles() throws IOException {
		mkTmpDir();
		copyResource(source, source);
	}

	@Setup(Level.Iteration)
	public void setupParser() throws Exception {
		implementation = BenchmarkedParser.All.get(this.parser).instantiate();

		if (this.parser.endsWith("x2")) doParse();
	}

	@TearDown(Level.Trial)
	public void cleanTempDirectory() throws IOException {
		rmTmpDir();
	}

	@Benchmark
	public Object time() throws Exception {
		return doParse();
	}

	public Object doParse() throws Exception {
		return implementation.parseFile(makeTempDirFile(source));
	}
}
