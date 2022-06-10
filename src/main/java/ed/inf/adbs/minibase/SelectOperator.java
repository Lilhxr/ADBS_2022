package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.Term;

import java.util.ArrayList;
import java.util.List;


public class SelectOperator extends Operator {
	private Operator childOperator;
	private ArrayList<ComparisonAtom> conditions;
	
	public SelectOperator(Operator childOperator, ArrayList<ComparisonAtom> conditions) {
		this.childOperator = childOperator;
		this.conditions = conditions;
	}
	@Override
	Tuple getNextTuple() {
		Tuple tuple = childOperator.getNextTuple();
		while(tuple != null) {
			boolean pass = true;
			if(!this.checkConstantTerms(tuple)) {
				pass = false;
			}
			for(int i=0; i<this.conditions.size(); i++) {
				if(!Compare.checkCondition(tuple, this.conditions.get(i))) {
					pass = false;
				}
			}
			if(pass) {
				return tuple;
			}
			tuple = childOperator.getNextTuple();
		}
		return null;
	}

	@Override
	void reset() {
		childOperator.reset();
	}
	/***
	 * if the atom has constant term, check if the tuple match
	 * @param tuple  the tuple to check
	 * @return true if the tuple match that constant,  false if not
	 */
	private boolean checkConstantTerms(Tuple tuple) {
		List<String> values = tuple.getValues();
		List<Term> terms = tuple.getTerms();
		for (int i = 0; i < terms.size(); i++) {
			if (terms.get(i) instanceof Constant && !terms.get(i).toString().equals(values.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	
	
	
}
