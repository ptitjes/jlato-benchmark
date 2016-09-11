package org.jlato.def;

import com.antlr.grammarsv4.java.JavaLexer;
import com.antlr.grammarsv4.java.JavaParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jlato.util.FileCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public class AntlrJavaParser implements BenchmarkedParser {

	public final boolean printTree;
	public final boolean SLL;
	public final boolean diag;
	public final boolean bail;
	public final boolean quiet;

	public AntlrJavaParser(boolean printTree, boolean SLL, boolean diag, boolean bail, boolean quiet) {
		this.printTree = printTree;
		this.SLL = SLL;
		this.diag = diag;
		this.bail = bail;
		this.quiet = quiet;
	}

	@Override
	public Object parseAll(File directory) throws Exception {
		List<File> files = FileCollector.collectAllJavaFiles(directory, new ArrayList<File>());

		String rootPath = directory.getAbsolutePath();
		if (!rootPath.endsWith("/")) rootPath = rootPath + "/";

		Map<String, ParseTree> cus = new HashMap<String, ParseTree>();
		for (File file : files) {
			final String path = file.getAbsolutePath().substring(rootPath.length());

			// There is no instance parser in JavaParser !
			cus.put(path, parseFile(file));
		}
		return cus;
	}

	private ParseTree parseFile(File f) throws Exception {
		try {
			if (!quiet) System.err.println(f);
			// Create a scanner that reads from the input stream passed to us
			Lexer lexer = new JavaLexer(new ANTLRFileStream(f.getAbsolutePath()));

			CommonTokenStream tokens = new CommonTokenStream(lexer);
//			long start = System.currentTimeMillis();
//			tokens.fill(); // load all and check time
//			long stop = System.currentTimeMillis();
//			lexerTime += stop-start;

			// Create a parser that reads from the scanner
			JavaParser parser = new JavaParser(tokens);
			if (diag) parser.addErrorListener(new DiagnosticErrorListener());
			if (bail) parser.setErrorHandler(new BailErrorStrategy());
			if (SLL) parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

			// start parsing at the compilationUnit rule
			ParserRuleContext t = parser.compilationUnit();
			if (printTree) System.out.println(t.toStringTree(parser));
			return t.getChild(0);
		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			e.printStackTrace();   // so we can get stack trace
			throw e;
		}
	}
}
