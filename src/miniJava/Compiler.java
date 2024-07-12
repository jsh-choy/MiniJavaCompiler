package miniJava;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Compiler {

	// Main function, the file to compile will be an argument.
	public static void main(String[] args) {
		// Check if directory path is given in args
		if (args.length == 0) {
			System.out.println("Usage: java Compiler <file path>");
		}

		// TODO: Create the inputStream using new FileInputStream
		FileInputStream inputStream = null;

		// TODO: Check to make sure a file path is given in args
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.err.println("File " + args[0] + " not found");
			System.exit(-1);
		}

		// TODO: Instantiate the ErrorReporter object
		ErrorReporter report = new ErrorReporter();
		// TODO: Instantiate the scanner with the input stream and error object
		Scanner scanner = new Scanner(inputStream, report);
		// TODO: Instantiate the parser with the scanner and error object
		Parser parser = new Parser(scanner, report);
		// TODO: Call the parser's parse function
		parser.parse();

		// TODO: Check if any errors exist, if so, println("Error")
		//  then output the errors
		if (report.hasErrors()) {
			System.out.println("Error");
			report.outputErrors();
			System.exit(-1);
		} else {
			System.out.println("Success");
			System.exit(0);
		}
	}
}