package org.jlato;

import org.jlato.util.Report;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;

/**
 * @author Didier Villevalois
 */
public class Runner {

	public static void main(String[] args) throws Exception {
		CommandLineOptions commandLineOptions = new CommandLineOptions(args);
		Options options = new OptionsBuilder().parent(commandLineOptions)
				.build();

		Collection<RunResult> runResults = new org.openjdk.jmh.runner.Runner(options).run();

		new Report().makeReport(runResults);
	}
}
