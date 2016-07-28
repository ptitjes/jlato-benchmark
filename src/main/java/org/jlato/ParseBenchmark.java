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

import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ParseBenchmark {

	@Setup(Level.Trial)
	public void unjarSources() throws IOException {
		tmpDir.mkdirs();
		unjarSources("javaparser-core", "2.5.1");
		unjarSources("javaslang", "1.2.2");
	}

	@TearDown(Level.Trial)
	public void cleanTempDirectory() throws IOException {
		rmdir(tmpDir);
	}

	@Benchmark
	public void parseJavaParserCore_JLaToParser_New() throws Exception {
		parseSources("javaparser-core", "2.5.1", BenchmarkedParser.JLaToParser_New);
	}

	@Benchmark
	public void parseJavaParserCore_JLaToParser_Old() throws Exception {
		parseSources("javaparser-core", "2.5.1", BenchmarkedParser.JLaToParser_Old);
	}

	@Benchmark
	public void parseJavaParserCore_JLaToParser_Preserving() throws Exception {
		parseSources("javaparser-core", "2.5.1", BenchmarkedParser.JLaToParser_Preserving);
	}

	@Benchmark
	public void parseJavaParserCore_JavaParserParser_Standard() throws Exception {
		parseSources("javaparser-core", "2.5.1", BenchmarkedParser.JavaParserParser_Standard);
	}

	@Benchmark
	public void parseJavaParserCore_JavaParserParser_WithComments() throws Exception {
		parseSources("javaparser-core", "2.5.1", BenchmarkedParser.JavaParserParser_WithComments);
	}

	@Benchmark
	public void parseJavaSlang_JLaToParser_New() throws Exception {
		parseSources("javaslang", "1.2.2", BenchmarkedParser.JLaToParser_New);
	}

	@Benchmark
	public void parseJavaSlang_JLaToParser_Old() throws Exception {
		parseSources("javaslang", "1.2.2", BenchmarkedParser.JLaToParser_Old);
	}

	@Benchmark
	public void parseJavaSlang_JLaToParser_Preserving() throws Exception {
		parseSources("javaslang", "1.2.2", BenchmarkedParser.JLaToParser_Preserving);
	}

	@Benchmark
	public void parseJavaSlang_JavaParserParser_Standard() throws Exception {
		parseSources("javaslang", "1.2.2", BenchmarkedParser.JavaParserParser_Standard);
	}

	@Benchmark
	public void parseJavaSlang_JavaParserParser_WithComments() throws Exception {
		parseSources("javaslang", "1.2.2", BenchmarkedParser.JavaParserParser_WithComments);
	}

	private final File tmpDir = new File("tmp/");

	private void parseSources(String artifactId, String version, BenchmarkedParser parser) throws Exception {
		parser.parseAll(makeTempDir(artifactId, version, "sources"));
	}

	private void unjarSources(String artifactId, String version) throws IOException {
		final InputStream resourceStream =
				ClassLoader.getSystemResourceAsStream(makeResourceName(artifactId, version));
		File localJarFile = makeLocalJarFile(artifactId, version);

		copyStreams(resourceStream, new FileOutputStream(localJarFile));
		unJar(localJarFile, makeTempDir(artifactId, version, "sources"));
	}

	private static void unJar(File file, File workDirectory) throws IOException {
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String name = jarEntry.getName();
			if (name.endsWith(".java")) {
				InputStream inputStream = jarFile.getInputStream(jarEntry);

				File out = new File(workDirectory, "/" + name);
				out.getParentFile().mkdirs();
				copyStreams(inputStream, new FileOutputStream(out));
			}
		}
	}

	private static void copyStreams(InputStream is, OutputStream os) throws IOException {
		try {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	private static void rmdir(File file)
			throws IOException {
		if (file.isDirectory()) {
			for (String name : file.list()) {
				File child = new File(file, name);
				rmdir(child);
			}
		}
		file.delete();
	}

	private String makeResourceName(String artifactId, String version) {
		return "sources/" + artifactId + "-" + version + "-sources.jar";
	}

	private File makeLocalJarFile(String artifactId, String version) {
		return new File(tmpDir, artifactId + "-" + version + "-sources.jar");
	}

	private File makeTempDir(String artifactId, String version, String variant) {
		return new File(tmpDir, artifactId + "/" + version + "/" + variant + "/");
	}
}
