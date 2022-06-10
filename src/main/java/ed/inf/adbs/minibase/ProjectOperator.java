package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectOperator extends Operator {
	private Operator childOperator;
	private ArrayList<String> projectVar;
	
	public ProjectOperator(Operator childOperator, RelationalAtom headAtom) {
		this.childOperator = childOperator;
		this.projectVar = this.getProjectionVar(headAtom);
	}
	@Override
	Tuple getNextTuple() {
		Tuple tuple = childOperator.getNextTuple();
		while(tuple != null) {
			return this.projection(tuple);
		}
		return null;
	}
	
	/***
	 * project the tuple to a new tuple base on the variables on the query head
	 * @param tuple  the given tuple that needed to be projected
	 * @return a new tuple after projection
	 */
	private Tuple projection(Tuple tuple) {
		ArrayList<String> newValues = new ArrayList<String>();
		ArrayList<String> newSchema = new ArrayList<String>();
		ArrayList<Term> newTerms = new ArrayList<Term>();
		HashMap<String, Integer> newVarRef = new HashMap<String, Integer>();
		
		ArrayList<String> values = tuple.getValues();
		ArrayList<String> schema = tuple.getSchemas();
		ArrayList<Term> terms = tuple.getTerms();
		HashMap<String, Integer> varRef = tuple.getRefs();
		for(int i=0; i<this.projectVar.size(); i++) {
			int pos = varRef.get(projectVar.get(i));
			newValues.add(values.get(pos));
			newSchema.add(schema.get(pos));
			newTerms.add(terms.get(pos));
			newVarRef.put(terms.get(pos).toString(), i);
		}
		
		return new Tuple(newValues, newSchema, newTerms, newVarRef);
	}
	
	/***
	 * get the projection variable from the query head
	 * @param atom The query head
	 * @return an ArrayList<String> that contains the variable name of the head
	 */
	private ArrayList<String> getProjectionVar(RelationalAtom atom) {
		ArrayList<String> l = new ArrayList<String>();
		List<Term> terms = atom.getTerms();
		for(int i=0; i<terms.size(); i++) {
			l.add(terms.get(i).toString());
		}
		return l;
	}

	@Override
	void reset() {
		this.childOperator.reset();
	}

}
