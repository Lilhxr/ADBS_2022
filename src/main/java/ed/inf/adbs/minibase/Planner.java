package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;

import java.util.ArrayList;
import java.util.List;

public class Planner {
	private final RelationalAtom head;
	private final String dbPath;
	private String sumVariable;
	private String avgVariable;
	
	private final ArrayList<ComparisonAtom> compAtoms = new ArrayList<>();
	private final ArrayList<RelationalAtom> relaAtoms = new ArrayList<>();
	
	private final ArrayList<ComparisonAtom> selectComparisonAtoms = new ArrayList<>();
	private final ArrayList<ComparisonAtom> joinComparisonAtoms = new ArrayList<>();
	
	public Planner(Query query, String dbPath) {
		this.head = query.getHead();
		List<Atom> body = query.getBody();
    	this.dbPath = dbPath;
    	
    	this.separateBodyAtoms(body);
    	this.separateComparisons(this.compAtoms);
	}

	/**
	 * Separate the body atom into relation and comparison.
	 * @param body
	 */
	private void separateBodyAtoms(List<Atom> body) {
		for(int i=0; i<body.size(); i++) {
			if(body.get(i) instanceof RelationalAtom) {
				this.relaAtoms.add((RelationalAtom)body.get(i));
			}
			else if(body.get(i) instanceof ComparisonAtom){
				this.compAtoms.add((ComparisonAtom)body.get(i));
			}
		}
	}

	/**
     * Separate the ComparisonAtom in the Query Body into two list selectComparisons and joinComparisons.
	 * @param compAtoms
     */
	private void separateComparisons(List<ComparisonAtom> compAtoms) {
		for(int i=0; i<compAtoms.size(); i++) {
			Term term1 = compAtoms.get(i).getTerm1();
			Term term2 = compAtoms.get(i).getTerm2();
			if(term1 instanceof Constant || term2 instanceof Constant) {
				this.selectComparisonAtoms.add(compAtoms.get(i));
			}
			else {
				this.joinComparisonAtoms.add(compAtoms.get(i));
			}
		}
	}

	/**
	 * Get the result of joining.
	 * @return the joined results.
	 */
	public ArrayList<Tuple> getResult(){
		ArrayList<Operator> selectOPs = new ArrayList<Operator>();
		for(int i=0; i<this.relaAtoms.size(); i++) {
			Operator scOp = new ScanOperator(this.relaAtoms.get(i), this.dbPath);
			selectOPs.add(new SelectOperator(scOp, this.selectComparisonAtoms));
		}
		ArrayList<Operator> joinOPs = new ArrayList<Operator>();
		if(selectOPs.size()>1) {
			Operator preJoinOP = new JoinOperator(selectOPs.get(0), selectOPs.get(1), this.joinComparisonAtoms);
			joinOPs.add(preJoinOP);
			for(int i=1; i<selectOPs.size()-1; i++) {
				Operator joinOP = new JoinOperator(joinOPs.get(i-1), selectOPs.get(i+1), this.joinComparisonAtoms);
				joinOPs.add(joinOP);
			}
		}
		ProjectOperator projectOP;
		if(selectOPs.size()==1) {
			projectOP = new ProjectOperator(selectOPs.get(0), this.head);
		}
		else {
			projectOP = new ProjectOperator(joinOPs.get(joinOPs.size()-1), this.head);
		}
		return projectOP.dump();
	}

	
}
