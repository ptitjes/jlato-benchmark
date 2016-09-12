package org.jlato;

import org.jlato.util.Report;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Defaults;
import org.openjdk.jmh.runner.NoBenchmarksException;
import org.openjdk.jmh.runner.ProfilersFailedException;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.*;

import java.util.Collection;

/**
 * @author Didier Villevalois
 */
public class Runner {

	public static void main(String[] args) throws Exception {
		try {
			CommandLineOptions e = new CommandLineOptions(args);
			org.openjdk.jmh.runner.Runner runner = new org.openjdk.jmh.runner.Runner(e);

			if(e.shouldHelp()) {
				e.showHelp();
				return;
			}

			if(e.shouldList()) {
				runner.list();
				return;
			}

			if(e.shouldListWithParams()) {
				runner.listWithParams(e);
				return;
			}

			if(e.shouldListProfilers()) {
				e.listProfilers();
				return;
			}

			if(e.shouldListResultFormats()) {
				e.listResultFormats();
				return;
			}

			try {
				Collection<RunResult> runResults = runner.run();

				new Report().makeReport(runResults);
			} catch (NoBenchmarksException var4) {
				System.err.println("No matching benchmarks. Miss-spelled regexp?");
				if(e.verbosity().orElse(Defaults.VERBOSITY) != VerboseMode.EXTRA) {
					System.err.println("Use " + VerboseMode.EXTRA + " verbose mode to debug the pattern matching.");
				} else {
					runner.list();
				}

				System.exit(1);
			} catch (ProfilersFailedException var5) {
				System.err.println(var5.getMessage());
				System.exit(1);
			} catch (RunnerException var6) {
				System.err.print("ERROR: ");
				var6.printStackTrace(System.err);
				System.exit(1);
			}
		} catch (CommandLineOptionException var7) {
			System.err.println("Error parsing command line:");
			System.err.println(" " + var7.getMessage());
			System.exit(1);
		}
	}
}
