package modeles;

/******************************************************************************
 *  Compilation:  javac SparseVector.java
 *  Execution:    java SparseVector
 *  
 *  A sparse vector, implementing using a symbol table.
 *
 *  [Not clear we need the instance variable N except for error checking.]
 *
 ******************************************************************************/

public class SparseVector {
    private final int N;             // length
    private ST<String, Double> st;  // the vector, represented by index-value pairs
    private double norme;
    // initialize the all 0s vector of length N
    public SparseVector(int N) {
        this.N  = N;
        this.st = new ST<String, Double>();
        norme = 0;
    }

    // put st[i] = value
    public void put(String i, double value) {
        //if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (value == 0.0) st.delete(i);
        else              st.put(i, value);        
    }

    // return st[i]
    public double get(String i) {
        //if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (st.contains(i)) return st.get(i);
        else                return 0.0;
    }

    // return the number of nonzero entries
    public int nnz() {
        return st.size();
    }

    // return the size of the vector
    public int size() {
        return N;
    }

    // return the dot product of this vector a with b
    public double dot(SparseVector b) {
        SparseVector a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        double sum = 0.0;

        // iterate over the vector with the fewest nonzeros
        if (a.st.size() <= b.st.size()) {
            for (String i : a.st)
                if (b.st.contains(i)) sum += a.get(i) * b.get(i);
        }
        else  {
            for (String i : b.st)
                if (a.st.contains(i)) sum += a.get(i) * b.get(i);
        }
        return sum;
    }

    // return cosin similarity
    public double cosinSim(SparseVector b) {    	
    	//return this.dot(b)/(getNorme()*b.norm());
    	return this.dot(b)/(norm()*b.norm());
    }
    
    // return the 2-norm
    public double norm() {
    	if(norme==0){
		    SparseVector a = this;
		    norme = Math.sqrt(a.dot(a));
		}		    
    	return norme;
    }

    // return alpha * a
    public SparseVector scale(double alpha) {
        SparseVector a = this;
        SparseVector c = new SparseVector(N);
        for (String i : a.st) c.put(i, alpha * a.get(i));
        return c;
    }

    // return a + b
    public SparseVector plus(SparseVector b) {
        SparseVector a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        SparseVector c = new SparseVector(N);
        for (String i : a.st) c.put(i, a.get(i));                // c = a
        for (String i : b.st) c.put(i, b.get(i) + c.get(i));     // c = c + b
        return c;
    }

    // return a string representation
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String i : st) {
            s.append("(" + i + ", " + st.get(i) + ") ");
        }
        return s.toString();
    }

    public double getNorme(){ return norme;}
    // test client
    public static void main(String[] args) {
        SparseVector a = new SparseVector(10);
        SparseVector b = new SparseVector(10);
        a.put("bonjour", 0.50);
        a.put("monsieur", 0.75);
        a.put("Hollande", 0.11);
        a.put("Hollande", 0.00);
        b.put("monsieur", 0.60);
        b.put("madame", 0.90);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("a dot b = " + a.dot(b));
        System.out.println("a + b   = " + a.plus(b));
    }

}