package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class ScanOperator extends Operator {
	private final Catalog catalog;
	private final String relation;
	private Scanner scanner;
	private final ArrayList<String> schemas;
	private final ArrayList<Term> terms;
	private final HashMap<String, Integer> refs;

	public ScanOperator(RelationalAtom atom, String dbDir) {
		this.catalog = Catalog.getInstance(dbDir);
		this.relation = atom.getName();
		this.reset();
		this.schemas = catalog.getSchemaMap().get(relation);
		this.refs = new HashMap<>();
		this.terms = new ArrayList<>();
		List<Term> t = atom.getTerms();
		for (int i = 0; i < t.size(); i++) {
			Term term = t.get(i);
			this.terms.add(term);
			refs.put(term.toString(), i);
		}
	}

	@Override
	Tuple getNextTuple() {
		if (scanner.hasNextLine()) {
			return new Tuple(new ArrayList<>(Arrays.asList(scanner.nextLine().split(", "))), this.schemas, this.terms, this.refs);
		}
		return null;
	}
	@Override
	void reset() {
		try {
			String fDir = this.catalog.getNameMap().get(this.relation);
			scanner = new Scanner(new File(fDir));
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}