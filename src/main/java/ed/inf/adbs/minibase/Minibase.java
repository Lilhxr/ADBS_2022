package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }
        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];
        evaluateCQ(databaseDir, inputFile, outputFile);
    }

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
    	
    	Query query = null;
    	try {
    		query = QueryParser.parse(Paths.get(inputFile));
    	}
    	catch (Exception e){
    		System.err.println("Exception occurred during parsing");
            e.printStackTrace();
    	}
    	Planner planner = new Planner(query, databaseDir);
    	ArrayList<Tuple> result = planner.getResult();
    	saveCSV(result, outputFile);
    	
    }
    /***
     * save a list of tuples to a csv file
     * @param tuples
     * @param outputPath
     */
    private static void saveCSV(ArrayList<Tuple> tuples, String outputPath) {
    	FileWriter writer = null;

    	 try {
    	     writer = new FileWriter(outputPath);
    	     for(int i=0; i<tuples.size(); i++) {
    	    	 ArrayList<String> vals = tuples.get(i).getValues();
    	    	 for(int k=0; k<vals.size(); k++) {
    	    		 writer.append(vals.get(k));
    	    		 if(k!=vals.size()-1) {
    	    			 writer.append(',');
    	    		 }
    	    	 }
    	    	 if(i!=tuples.size()-1) {
    	    		 writer.append('\n');
    	    	 }
    	     }

    	     System.out.println("CSV file is created...");

    	  } catch (IOException e) {
    	     e.printStackTrace();
    	  } finally {
    	        try {
    	      writer.flush();
    	      writer.close();
    	        } catch (IOException e) {
    	      e.printStackTrace();
    	}
    	}
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));
//            Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
//            Query query = QueryParser.parse("Q(x, w) :- R(x, 'z'), S(4, z, w), 4 < 'test string' ");

            System.out.println("Entire query: " + query);
            RelationalAtom head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
