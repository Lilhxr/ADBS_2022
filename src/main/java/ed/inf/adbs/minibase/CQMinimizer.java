package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;

import ed.inf.adbs.minibase.parser.QueryParser;

////////////Modified/////////////
import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.IntegerConstant;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;
import ed.inf.adbs.minibase.base.StringConstant;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
////////////////////////////////
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;


/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeCQ(inputFile, outputFile);

//        parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */
    public static void minimizeCQ(String inputFile, String outputFile) {
        // TODO: add your implementation
    	//get and parse the query
    	Query query = null;
    	try {
    		query = QueryParser.parse(Paths.get(inputFile));
    	}
    	catch (Exception e){
    		System.err.println("Exception occurred during parsing");
            e.printStackTrace();
    	}
    	
    	//get the head and body;
    	RelationalAtom head = query.getHead();
    	List<Atom> body = query.getBody();
    	
    	//store all terms of head into a set
    	Set<String> headTerms = new HashSet<String>();
    	for(Term t : head.getTerms()) {
    		headTerms.add(t.toString());
    	}
    	
    	//1. Remove the duplicate Atom in the body
    	removeDuplicate(body);
    	for(int i=0; i<body.size();i++) {
    		//2. Try to remove the Atom in position i
    		List<Atom> newBody = new ArrayList<Atom>(body);
    		newBody.remove(i);
    		
    		//3. Try to find the homo from body to new body
			Boolean foundHomo = true;
			HashMap<String,String> map = findMapping(body,newBody,headTerms);
			for(int k=0; k<body.size(); k++) {
				//3.1 If the atom from body that after mapping does not belong to the new body, then it means there is no homo
				if(!atomInBody(mapping(body.get(k),map), newBody)) {
					foundHomo = false;
				}
			}
			
			//4. If we found the homo, remove the Atom in position i
			if(foundHomo) {
				body = newBody;
				i--;
			}
    	}
    	
    	Query minimizedQuery = new Query(head, body);
    	System.out.println(minimizedQuery.toString());
    	saveCQtxt(minimizedQuery, outputFile);
    	
    }
    /***
     * Save the query as a txt file
     * @param minimizedQuery  The minimised query that needed to be save
     * @param outputFile  the file path
     */
    private static void saveCQtxt(Query minimizedQuery, String outputFile) {
    	String str = minimizedQuery.toString();
        BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(str);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}     
    }
    /**
     * To check if a specific atom is in certain query body
     * @param atom  the Atom to check
     * @param body  the query body
     * @return true if this atom is in the body    false if this atom is not in the body
     */
    private static boolean atomInBody(Atom atom, List<Atom> body) {
    	for(int i=0; i<body.size(); i++) {
    		if(isEqualAtom(atom, body.get(i))) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Map the terms of an atom to new terms
     * @param atom  the atom to map
     * @param m     the map 
     * @return a new Atom after mapping
     */
    private static Atom mapping(Atom atom, HashMap<String, String> m) {
    	List<Term> terms = atom.getTerms();
    	List<Term> newTerms = new ArrayList<Term>();
    	Atom newAtom = new RelationalAtom(atom.getName(), newTerms);
    	for(int i=0; i<terms.size(); i++) {
    		Term term = terms.get(i);
			if(m.containsKey(term.toString())) {
				if(m.get(term.toString()).charAt(0)=='\'') {
					newTerms.add(new StringConstant(m.get(term.toString()).replace("\'", "")));
				}
				else if(!Character.isDigit(m.get(term.toString()).charAt(0))) {
					newTerms.add(new Variable(m.get(term.toString())));
				}
				else {
					newTerms.add(new IntegerConstant(Integer.parseInt(m.get(term.toString()))));
				}
			}
			else {
				String s = term.toString();
				if(term instanceof Variable) {
					newTerms.add(new Variable(s));
				}
				else if(term instanceof StringConstant) {
					newTerms.add(new StringConstant(s.substring(1,s.length()-1)));
				}
				else {
					newTerms.add(new IntegerConstant(Integer.parseInt(s)));
				}
			}
    	}
    	return newAtom;
    }
    
    /**
     * To check if two Atoms are equal
     * @param a1  the atom to check
     * @param a2  the second atom to check
     * @return true if these atoms are equal    false if these atoms are not equal
     */
    private static boolean isEqualAtom(Atom a1, Atom a2) {
    	if(!a1.getName().equals(a2.getName())) {
    		return false;
    	}
    	List<Term> ts1 = a1.getTerms();
    	List<Term> ts2 = a2.getTerms();
    	for(int i=0; i<ts1.size(); i++) {
    		Term t1 = ts1.get(i);
    		Term t2 = ts2.get(i);
    		if(!t1.toString().equals(t2.toString())) {
    			return false;
    			
    		}
    	}
    	return true;
    }
    
    /**
     * To find a possible map from a query to a new query
     * @param body  the body of a query
     * @param newBody  the body of another query
     * @param headTerms  the undistinguished terms that in the query head
     * @return a HashMap from string of a term to a new string of a term
     */
    private static HashMap<String, String> findMapping(List<Atom> body, List<Atom> newBody, Set<String> headTerms){
    	HashMap<String,String> map = new HashMap<String,String>();
    	for(int bIdx = 0; bIdx<body.size(); bIdx++) {
    		for(int nbIdx = 0; nbIdx<newBody.size(); nbIdx++) {
    			Atom bAtom = body.get(bIdx);
    			Atom nbAtom = newBody.get(nbIdx);
    			HashMap<String, String> possMap = new HashMap<String, String>();
    			if(bAtom.getName().equals(nbAtom.getName())) {
    				List<Term> bTerms = bAtom.getTerms();
    				List<Term> nbTerms = nbAtom.getTerms();
    				for(int k=0; k<bTerms.size(); k++) {
    					Term bTerm = bTerms.get(k);
    					Term nbTerm = nbTerms.get(k);
    					if(headTerms.contains(bTerm.toString())) {
    						if(headTerms.contains(nbTerm.toString())) {
    							possMap.put(bTerm.toString(), nbTerm.toString());
    						}
    						else {
    							break;
    						}
    					}
    					if(bTerm instanceof Constant) {
    						if(nbTerm instanceof Constant) {
    							if(bTerm.toString().equals(nbTerm.toString())) {
    								possMap.put(bTerm.toString(), nbTerm.toString());
    							}
    							else {
    								break;
    							}
    						}
    						else {
    							break;
    						}
    					}
    					if(bTerm instanceof Variable) {
    						possMap.put(bTerm.toString(), nbTerm.toString());
    					}
    				}
    				for(String key:possMap.keySet()) {
    					map.put(key, possMap.get(key));
    				}
    			}
    		}
    	}
    	return map;
    }
    
    /***
     * to remove the duplicated atom the the query body
     * @param body  the query body
     */
    private static void removeDuplicate(List<Atom> body) {
    	for(int i=0; i<body.size(); i++) {
    		Atom a1 = body.get(i);
    		for(int j=0; j<body.size(); j++) {
    			if(j==i) {
    				continue;
    			}
    			Atom a2 = body.get(j);
    			if(isEqualAtom(a1,a2)) {
    				body.remove(i);
    				i--;
    				break;
    			}
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
//            Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
//            Query query = QueryParser.parse("Q() :- R(x, 'z'), S(4, z, w)");

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
