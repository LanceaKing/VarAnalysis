package edu.iastate.symex.run;

import java.io.File;

import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.WriteDataModelToIfDefs;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunSymexForFile {
	
	/**
	 * PHP file to test
	 */	
	public static String PHP_FILE = "/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/test.php";

	/**
	 * PHP file to be executed
	 */
	private File file;

	/**
	 * The entry point of the program.
	 */
	public static void main(String[] args) {
		DataModel dataModel = new RunSymexForFile(new File(PHP_FILE)).execute();
		MyLogger.log(MyLevel.INFO, WriteDataModelToIfDefs.convert(dataModel));
	}
	
	/**
	 * Constructor.
	 * @param file The PHP file to be executed
	 */
	public RunSymexForFile(File file) {
		this.file = file;
	}
	
	/**
	 * Executes the file.
	 */
	public DataModel execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[RunSymexForFile:" + file + "] Started.");
		
		DataModel dataModel = new PhpExecuter().execute(file);
		
		MyLogger.log(MyLevel.PROGRESS, "[RunSymexForFile:" + file + "] Done in " + timer.getElapsedSecondsInText() + ".");
		return dataModel;
	}
		
}