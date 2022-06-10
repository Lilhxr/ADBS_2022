package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.StringConstant;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;

import java.util.HashMap;

/***
 * a class that provide methods to check if tuple pass the condition from the Comparison Atom
 * @author JD
 *
 */
public class Compare {
	public Compare() {}
	
	/***
	 * To check if the tuple pass the condition from the comparison Atom
	 * @param tuple  the tuple to check if it pass the condition
	 * @param condition  the comparison condition
	 * @return true if the tuple pass the condition   false if the tuple does not pass
	 */
	public static boolean checkCondition(Tuple tuple, ComparisonAtom condition) {
		Term term1 = condition.getTerm1();
		Term term2 = condition.getTerm2();
		HashMap<String,Integer> refs = tuple.getRefs();
		//if the term of the condition not in this tuple, return true
		if(!refs.containsKey(term1.toString()) && !refs.containsKey(term2.toString())) {
			return true;
		}
		return check(tuple, term1, term2, refs, condition.getOp().toString());
	}

	public static boolean check(Tuple tuple, Term term1, Term term2, HashMap<String, Integer> refs, String operation)
	{
		if (term1 instanceof StringConstant) {
			switch(operation) {
				case "=":
					return term1.toString().equals(term2.toString());
				case "!=":
					return !term1.toString().equals(term2.toString());
				case ">":
					return term1.toString().compareTo(term2.toString()) > 0;
				case ">=":
					return term1.toString().compareTo(term2.toString()) >= 0;
				case "<":
					return term1.toString().compareTo(term2.toString()) < 0;
				case "<=":
					return term1.toString().compareTo(term2.toString()) <= 0;
			}
		}
		if (term1 instanceof Variable) {
			int pos1 = refs.get(term1.toString());
			String value1 = tuple.getValueAt(pos1);
			if (term2 instanceof Variable) {
				int pos2 = refs.get(term2.toString());
				String value2 = tuple.getValueAt(pos2);
				String valType = tuple.getSchemaAt(pos1);
				if (valType.equals("string")) {
					switch (operation) {
						case "=":
							return value1.equals(value2.toString());
						case "!=":
							return !value1.equals(value2.toString());
						case ">":
							return value1.compareTo(value2) > 0;
						case ">=":
							return value1.compareTo(value2) >= 0;
						case "<":
							return value1.compareTo(value2) < 0;
						case "<=":
							return value1.compareTo(value2) <= 0;
					}
				} else if (valType.equals("int")) {
					switch (operation) {
						case "=":
							return Integer.parseInt(value1) == Integer.parseInt(value2);
						case "!=":
							return Integer.parseInt(value1) != Integer.parseInt(value2);
						case ">":
							return Integer.parseInt(value1) > Integer.parseInt(value2);
						case ">=":
							return Integer.parseInt(value1) >= Integer.parseInt(value2);
						case "<":
							return Integer.parseInt(value1) < Integer.parseInt(value2);
						case "<=":
							return Integer.parseInt(value1) <= Integer.parseInt(value2);
					}
				}
			} else {
				String valType = tuple.getSchemaAt(pos1);
				if(valType.equals("string")) {
					switch (operation) {
						case "=":
							return value1.equals(term2.toString());
						case "!=":
							return !value1.equals(term2.toString());
						case ">":
							return value1.compareTo(term2.toString()) > 0;
						case ">=":
							return value1.compareTo(term2.toString()) >= 0;
						case "<":
							return value1.compareTo(term2.toString()) < 0;
						case "<=":
							return value1.compareTo(term2.toString()) <= 0;
					}
				}
				else if(valType.equals("int")){
					switch (operation) {
						case "=":
							return Integer.parseInt(value1) == Integer.parseInt(term2.toString());
						case "!=":
							return Integer.parseInt(value1) != Integer.parseInt(term2.toString());
						case ">":
							return Integer.parseInt(value1) > Integer.parseInt(term2.toString());
						case ">=":
							return Integer.parseInt(value1) >= Integer.parseInt(term2.toString());
						case "<":
							return Integer.parseInt(value1) < Integer.parseInt(term2.toString());
						case "<=":
							return Integer.parseInt(value1) <= Integer.parseInt(term2.toString());
					}
				}
			}
		}
		switch (operation) {
			case "=":
				return Integer.parseInt(term1.toString()) == Integer.parseInt(term2.toString());
			case "!=":
				return Integer.parseInt(term1.toString()) != Integer.parseInt(term2.toString());
			case ">":
				return Integer.parseInt(term1.toString()) > Integer.parseInt(term2.toString());
			case ">=":
				return Integer.parseInt(term1.toString()) >= Integer.parseInt(term2.toString());
			case "<":
				return Integer.parseInt(term1.toString()) < Integer.parseInt(term2.toString());
			case "<=":
				return Integer.parseInt(term1.toString()) <= Integer.parseInt(term2.toString());
		}
		return true;
	}
}
