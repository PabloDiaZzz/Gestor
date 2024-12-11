import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Utilidades {
	static int[] diasMes = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	public static void menu(boolean input,String[] a) {
		int maxValue = 0;
		for (int i = 0; i < a.length; i++) {
			maxValue = Math.max(maxValue, String.valueOf(a.length).length() - String.valueOf(i).length() + ((input ? i + ". " : "") + a[i]).length());

		}
		System.out.print('╔');
		for (int x = 0; x < maxValue + 2; x++) {
			System.out.print('═');
		}
		System.out.println('╗');
		int contador = 1;
		for (String s : a) {
			System.out.print("║ ");
			int espacios = String.valueOf(a.length).length() - String.valueOf(contador).length();
			for (int x = 0; x < espacios; x++) {
				System.out.print(" ");
			}
			System.out.print(input ? contador + ". " : "");
			System.out.print(s);
			for (int y = 0; y < 1 + maxValue - ((input ? espacios : 0) + (input ? contador + ". " + s : s).length()); y++) {
				System.out.print(' ');
			}
			System.out.println("║");
			contador++;
		}
		System.out.print('╚');
		for (int x = 0; x < maxValue + 2; x++) {
			System.out.print('═');
		}
		System.out.println('╝');
		System.out.print(input ? ">> " : "");
	}

	public static boolean isNumber(String... s) {
		int contador = 0;
		for (String string : s) {
			try {
				Integer.parseInt(string);
			} catch (NumberFormatException e) {
				contador++;
			}
		}
		return contador != s.length;
	}

	public static String mayus(String s, int n) {
		if (s.isBlank()) {
			return s;
		}
		if (n == 1) {
			return s.toUpperCase();
		} else if (n == 2) {
			return s.toLowerCase();
		} else if (n == 3) {
			String[] a = s.split(" ");
			for (int x = 0; x < a.length; x++) {
				a[x] = a[x].substring(0, 1).toUpperCase() + a[x].substring(1).toLowerCase();
			}
			return String.join(" ", a);
		}
		return s;
	}

	public static ArrayList<ArrayList<String>> aaListCopy(ArrayList<ArrayList<String>> l) {
		ArrayList<ArrayList<String>> lCopy = new ArrayList<>();
		for (int x = 0; x < l.size(); x++) {
			lCopy.add(new ArrayList<>());
			for (int y = 0; y < l.get(x).size(); y++) {
				lCopy.get(x).add(l.get(x).get(y));
			}
		}
		return lCopy;
	}

	public static ArrayList<ArrayList<Dato>> aaListCopy2(ArrayList<ArrayList<Dato>> l) {
		ArrayList<ArrayList<Dato>> lCopy = new ArrayList<>();
		for (int x = 0; x < l.size(); x++) {
			lCopy.add(new ArrayList<>());
			for (int y = 0; y < l.get(x).size(); y++) {
				lCopy.get(x).add(new Dato(l.get(x).get(y).getValor(), l.get(x).get(y).getId(), Arrays.asList(Dato.tipos).indexOf(l.get(x).get(y).getTipo())));
			}
		}
		return lCopy;
	}

	public static int tipo() {
		Scanner sc = new Scanner(System.in);
		System.out.println("\nSeleccione el tipo de dato:");
		for (int i = 1; i < Dato.tipos.length; i++) {
			System.out.println(i + ". " + Dato.tipos[i]);
		}
		System.out.print(">> ");
		int tipo = sc.nextInt();
		sc.nextLine();
		if (tipo < 1 || tipo > Dato.tipos.length) {
			System.out.println("Tipo de dato no válido");
			return -1;
		}
		return tipo;
	}

	public static int campo(ArrayList<ArrayList<Dato>> l,boolean callCampos) {
		return campo(l, callCampos, 0, l.getFirst().size()-1);
	}

	public static int campo(ArrayList<ArrayList<Dato>> l,boolean callCampos , int extremo1) {
		return campo(l, callCampos, extremo1, l.getFirst().size()-1);
	}

	public static int campo(ArrayList<ArrayList<Dato>> l,boolean callCampos , int extremo1, int extremo2) {
		Scanner sc = new Scanner(System.in);
		int campo;
		int contador = 1;
		System.out.println("\nSeleccione el campo:");
		if (extremo1 >= l.getFirst().size() || extremo1 < 0) {
			extremo1 = 0;
		}
		if (extremo2 >= l.getFirst().size() || extremo1 < extremo2) {
			extremo2 = l.getFirst().size() - 1;
		}
		for (int x = extremo1; x <= extremo2; x++) {
			System.out.print(callCampos ? (contador) + ". " + l.getFirst().get(x).getValor() + "\n" : "");
			contador++;
		}
		System.out.print(">> ");
		campo = sc.nextInt();
		sc.nextLine();
		if (campo < 1 || campo >= contador) {
			System.out.println("Campo no válido");
			return -1;
		}
		return campo + extremo1 - 1;
	}
}