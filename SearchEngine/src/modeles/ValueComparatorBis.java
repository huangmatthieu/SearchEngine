package modeles;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ValueComparatorBis implements Comparator<String> {

	HashMap<String, Double> base;
	//Constructeur
	public ValueComparatorBis(HashMap<String, Double> base) {
		this.base = base;
	}

	/*public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}*/
	
	public int compare(String a, String b) {
	     /*String a = (String) a1;
	     String b = (String) b1;*/
		 Double x = base.get(a);
	        Double y = base.get(b);
	        if (x.equals(y)) {
	            return a.compareTo(b);
	        }
	        return y.compareTo(x);
	    }

	public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
			Function<? super T, ? extends U> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T, U> Comparator<T> comparing(
			Function<? super T, ? extends U> arg0, Comparator<? super U> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingDouble(
			ToDoubleFunction<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingInt(ToIntFunction<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingLong(ToLongFunction<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> nullsFirst(Comparator<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> nullsLast(Comparator<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator<String> reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator<String> thenComparing(Comparator<? super String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public <U extends Comparable<? super U>> Comparator<String> thenComparing(
			Function<? super String, ? extends U> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public <U> Comparator<String> thenComparing(
			Function<? super String, ? extends U> arg0,
			Comparator<? super U> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator<String> thenComparingDouble(
			ToDoubleFunction<? super String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator<String> thenComparingInt(
			ToIntFunction<? super String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator<String> thenComparingLong(
			ToLongFunction<? super String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}

	