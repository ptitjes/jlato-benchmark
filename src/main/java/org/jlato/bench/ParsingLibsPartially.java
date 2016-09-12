/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jlato.bench;

import org.jlato.def.BenchmarkedParser;
import org.jlato.util.ParseBenchmarkBase;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ParsingLibsPartially extends ParseBenchmarkBase {

	@Param({
			"javaparser-core:2.5.1",
			"javaslang:1.2.2",
			"jlato:0.0.6",
	})
	private String source;

	@Param({
			"JLaTo",
			"JavaParser",
			"Javac",
			"Antlr4-Java7",
//			"Antlr4-Java8",
	})
	private String parser;

	@Setup(Level.Trial)
	public void unjarSources() throws IOException {
		tmpDir.mkdirs();
		String[] artifactVersion = source.split(":");
		unjarSources(artifactVersion[0], artifactVersion[1]);
	}

	@TearDown(Level.Trial)
	public void cleanTempDirectory() throws IOException {
		rmdir(tmpDir);
	}

	@Benchmark
	public Object time() throws Exception {
		String[] artifactVersion = source.split(":");
		BenchmarkedParser benchmarkedParser = BenchmarkedParser.All.get(this.parser);
		return parseSources(artifactVersion[0], artifactVersion[1], benchmarkedParser);
	}
}
