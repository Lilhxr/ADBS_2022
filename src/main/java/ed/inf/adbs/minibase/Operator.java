package ed.inf.adbs.minibase;

import java.util.ArrayList;

public abstract class Operator {
	abstract Tuple getNextTuple();
	abstract void reset();
	public ArrayList<Tuple> dump() {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        while (true){
            Tuple tuple = getNextTuple();
            
            if (tuple == null) {
                break;
            }
            tuples.add(tuple);
//            System.out.println(tuple.getValues());
//            System.out.println(tuple.getTerms());
//            System.out.println(tuple.getSchema());
//            System.out.println(tuple.getVariableRefernce());
//            System.out.println("===========================");
        }
        return tuples;
	}
}
