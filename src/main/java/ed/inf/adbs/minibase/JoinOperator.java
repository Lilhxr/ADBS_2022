package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinOperator extends Operator {
	private final Operator left;
	private final Operator right;
	private final ArrayList<ComparisonAtom> conditions;
	private Tuple leftTuple;
	
	public JoinOperator(Operator leftChildOP, Operator rightChildOP, ArrayList<ComparisonAtom> conditions) {
		this.left = leftChildOP;
		this.right = rightChildOP;
		this.conditions = conditions;
		this.leftTuple = this.left.getNextTuple();
	}
	
	@Override
	Tuple getNextTuple() {
		if(leftTuple == null) {
			return null;
		}
		Tuple rightTuple = right.getNextTuple();
		while(rightTuple != null) {
			Tuple mergedTuple = this.tryMergeTuple(leftTuple, rightTuple);
			if(mergedTuple != null) {
				boolean pass = true;
				for (ComparisonAtom condition : conditions) {
					if (!Compare.checkCondition(mergedTuple, condition)) {
						pass = false;
					}
				}
				if(pass) {
					return mergedTuple;
				}
			}
			rightTuple = right.getNextTuple();
		}
		leftTuple = left.getNextTuple();
		if(leftTuple!=null) {
			right.reset();
			return this.getNextTuple();
		}
		return null;
	}
	
	/***
	 * Try to merge two tuples.
	 * @param tuple1 tuple to be merged.
	 * @param tuple2 another tuple to be merged.
	 * @return null if two tuples are not allowed to be merged, or a new tuple merged two tuples.
	 */
	private Tuple tryMergeTuple(Tuple tuple1, Tuple tuple2) {
		int size = 0;
		HashMap<String, Integer> refs = new HashMap<>();
		
		ArrayList<String> values1 = tuple1.getValues();
		ArrayList<String> schema1 = tuple1.getSchemas();
		ArrayList<Term> terms1 = tuple1.getTerms();
		
		ArrayList<String> values2 = tuple2.getValues();
		ArrayList<String> schema2 = tuple2.getSchemas();
		ArrayList<Term> terms2 = tuple2.getTerms();

		ArrayList<String> values = new ArrayList<>(values1);
		ArrayList<String> schemas = new ArrayList<>(schema1);
		ArrayList<Term> terms = new ArrayList<>(terms1);

		for(int i = 0; i < values1.size(); i++) {
			refs.put(terms1.get(i).toString(), i);
			size++;
		}
		for(int i = 0; i < values2.size(); i++) {
			String val2 = values2.get(i);
			Term term2 = terms2.get(i);
			
			if(term2 instanceof Variable && refs.containsKey(term2.toString())) {
					String newVal = values.get(refs.get(term2.toString()));
					size--;
					if(!newVal.equals(val2)) {
						return null;
					}
			}
			else {
				values.add(values2.get(i));
				schemas.add(schema2.get(i));
				terms.add(terms2.get(i));
				refs.put(terms2.get(i).toString(), size + i);
			}
		}
		return new Tuple(values, schemas, terms, refs);
	}

	@Override
	void reset() {
		this.left.reset();
		this.right.reset();
	}

}
