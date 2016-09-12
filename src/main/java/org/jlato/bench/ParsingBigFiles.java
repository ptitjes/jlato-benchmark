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
			"JavaParser",
			"Javac",
			"Antlr4-Java7",
	})
	private String parser;

	@Setup(Level.Trial)
	public void copyBigFiles() throws IOException {
		mkTmpDir();
		copyResource(source, source);
	}

	@TearDown(Level.Trial)
	public void cleanTempDirectory() throws IOException {
		rmTmpDir();
	}

	@Benchmark
	public Object time() throws Exception {
		BenchmarkedParser benchmarkedParser = BenchmarkedParser.All.get(this.parser);
		return benchmarkedParser.parseFile(makeTempDirFile(source));
	}
}
