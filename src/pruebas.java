import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class pruebas {

	public static void main(String[] args) {
		int[] array = new int[] {1,2,5,8,7,4,5,6,3,2,5,2,5,4,8,5,2,3,6,9,8,4,7,4,5,2,3,6,5,2,3,0,3,2,5,8,7,5,0,2,5,0,0,0,0,1,4,7,8,8,7};
		System.out.println(Arrays.toString(Arrays.stream(array).distinct().sorted().toArray()));
		HashSet<Integer> set = new HashSet<>();
		for (int i : array) {
			set.add(i);
		}
		System.out.println(set);
	}
}