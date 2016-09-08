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

package org.jlato;

import org.jlato.def.BenchmarkedParser;
import org.jlato.util.ParseBenchmarkBase;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PartiallyParsingJDK extends ParseBenchmarkBase {

	@Setup(Level.Trial)
	public void unzipJDK() throws IOException {
		mkTmpDir();
		unzipSources(new File("/home/didier/Downloads/Tech/Dev/openjdk-8-src-b132-03_mar_2014.zip"), "openjdk");
	}

	@TearDown(Level.Trial)
	public void cleanTempDirectory() throws IOException {
		rmTmpDir();
	}

	@Benchmark
	public Object jdk_with_jlato1() throws Exception {
		return parseJdkSources(BenchmarkedParser.JLaToParser);
	}

	@Benchmark
	public Object jdk_with_jlato2() throws Exception {
		return parseJdkSources(BenchmarkedParser.JLaToParser2);
	}

	@Benchmark
	public Object jdk_with_jlato3() throws Exception {
		return parseJdkSources(BenchmarkedParser.JLaToParser3);
	}

	@Benchmark
	public Object jdk_with_javaparser() throws Exception {
		return parseJdkSources(BenchmarkedParser.JavaParserParser);
	}

	@Benchmark
	public Object jdk_with_antlr() throws Exception {
		return parseJdkSources(BenchmarkedParser.Antlr4);
	}

	protected Object parseJdkSources(BenchmarkedParser parser) throws Exception {
		return parser.parseAll(new File(makeTempDir("openjdk"), "openjdk/jdk/src/share/classes/"));
	}
}
