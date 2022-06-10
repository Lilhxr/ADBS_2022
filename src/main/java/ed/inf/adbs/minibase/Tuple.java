package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Term;

import java.util.ArrayList;
import java.util.HashMap;

public class Tuple {
	private ArrayList<String> values;
	private ArrayList<String> schemas;
	private ArrayList<Term> terms;
	private HashMap<String, Integer> refs;

	/**
	 *
	 * @param values
	 * @param schemas
	 * @param terms
	 * @param refs
	 */
	public Tuple(ArrayList<String> values, ArrayList<String> schemas, ArrayList<Term> terms, HashMap<String,Integer> refs) {
		this.values = values;
		this.schemas = schemas;
		this.refs = refs;
		this.terms = terms;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<String> getValues(){
		return this.values;
	}
	
	public String getValueAt(int i) {
		return this.values.get(i);
	}
	
	public ArrayList<String> getSchemas() {
		return this.schemas;
	}
	
	public String getSchemaAt(int i) {
		return this.schemas.get(i);
	}
	
	public HashMap<String,Integer> getRefs(){
		return this.refs;
	}
	
	public ArrayList<Term> getTerms(){
		return this.terms;
	}
	
	public void setValAt(int i, String val) {
		this.values.set(i, val);
	}
}
