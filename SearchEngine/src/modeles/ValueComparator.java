package modeles;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ValueComparator implements Comparator<Integer> {

	HashMap<Integer, Double> base;
	//Constructeur
	public ValueComparator(HashMap<Integer, Double> base) {
		this.base = base;
	}

	/*public int compare(Integer a, Integer b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}*/
	
	public int compare(Integer a, Integer b) {
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

		public Comparator<Integer> reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator<Integer> thenComparing(Comparator<? super Integer> arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public <U extends Comparable<? super U>> Comparator<Integer> thenComparing(
				Function<? super Integer, ? extends U> arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public <U> Comparator<Integer> thenComparing(
				Function<? super Integer, ? extends U> arg0,
				Comparator<? super U> arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator<Integer> thenComparingDouble(
				ToDoubleFunction<? super Integer> arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator<Integer> thenComparingInt(
				ToIntFunction<? super Integer> arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator<Integer> thenComparingLong(
				ToLongFunction<? super Integer> arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public static void main(String[] args) {
			HashMap<Integer,Double> map = new HashMap<Integer,Double>();
			ValueComparator comparateur = new ValueComparator(map);
			TreeMap<Integer,Double> mapTriee = new TreeMap<Integer,Double>(comparateur);
			map.put(1,12.5);
			map.put(2,67.4);
			map.put(3,65.2);
			System.out.println("map non-triée: "+map); //La commande suivante affichera map non-triée
			mapTriee.putAll(map);
			System.out.println("resultat du tri: "+ mapTriee); //La commande suivante affichera résultat
		}

	}