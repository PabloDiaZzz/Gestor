import javax.swing.table.TableColumn;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;

public class Dato extends TableColumn implements Serializable {
	private String valor;
	private int id;
	private String tipo;
	private int ordenCambio;
	static final String[] tipos = {"null", "Texto", "Entero", "Flotante", "Caracter", "Fecha", "Hora", "Bool", "Clave"};

	public Dato(String valor, int id, int tipo) {
		this.valor = valor;
		this.id = id;
		this.tipo = (tipo < tipos.length && tipo >= 0) ? tipos[tipo] : "null";
		this.ordenCambio = 0;
	}
	public Dato(String valor, int id, String tipoS) {
		this.valor = valor;
		this.id = id;
		tipo = Arrays.stream(tipos).filter(t -> t.equals(tipoS)).map(t -> t == "" ? "null" : t).collect(Collectors.joining());
		this.tipo = tipo.isEmpty() ? "null" : tipo;
		this.ordenCambio = 0;
	}

	public Dato(String valor, String tipoS) {
		this.valor = valor;
		this.id = 0;
		tipo = Arrays.stream(tipos).filter(t -> t.equals(tipoS)).map(t -> t == "" ? "null" : t).collect(Collectors.joining());
		this.tipo = tipo.isEmpty() ? "null" : tipo;
		this.ordenCambio = 0;
	}

	public Dato(String valor, int id) {
		this.valor = valor;
		this.id = id;
		this.setTipo(0);
		this.ordenCambio = 0;
	}

	public Dato(String valor) {
		this.valor = valor;
		this.setId(0);
		this.setTipo(0);
		this.ordenCambio = 0;
	}

	public Dato() {
		this.setValor(" ");
		this.setId(0);
		this.setTipo(0);
		this.ordenCambio = 0;
	}

	public Dato(String valor, int id, String tipoS, int ordenCambio) {
		this.valor = valor;
		this.id = id;
		tipo = Arrays.stream(tipos).filter(t -> t.equals(tipoS)).map(t -> t == "" ? "null" : t).collect(Collectors.joining());
		this.tipo = tipo.isEmpty() ? "null" : tipo;
		this.ordenCambio = ordenCambio;
	}

	public Dato(String valor, int id, int tipo, int ordenCambio) {
		this.valor = valor;
		this.id = id;
		this.tipo = (tipo < tipos.length && tipo >= 0) ? tipos[tipo] : "null";
		this.ordenCambio = ordenCambio;
	}

	public int getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public String getValor() {
		return valor;
	}

	public int getOrdenCambio() {
		return ordenCambio;
	}

	public void setTipo(int tipo) {
		this.tipo = tipos[tipo];
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public void setOrdenCambio(int increment) {
		this.ordenCambio = increment + this.ordenCambio;
	}

	public void setOrdenCambio() {
		this.ordenCambio = 1;
	}

	public boolean checkTipo(int callFeedback, boolean changeTipo) {
		this.formatTipo();
		boolean pass = false;
		if (this.getValor().isBlank()) {
			pass = true;
		} else {
			switch (this.getTipo()) {
				case "Texto":
					pass = ! this.getValor().isEmpty();
					break;
				case "Entero":
					try {
						Integer.parseInt(this.getValor());
						pass = true;
					} catch (NumberFormatException ignored) {
					}
					break;
				case "Flotante":
					try {
						Float.parseFloat(this.getValor());
						pass = true;
					} catch (NumberFormatException ignored) {
					}
					break;
				case "Caracter":
					pass = this.getValor().length() == 1;
					break;
				case "Fecha":
					try {
						pass = this.getValor().matches("\\d{2}/\\d{2}/\\d\\d+") && Integer.parseInt(this.getValor().substring(0, 2)) <= 31 && Integer.parseInt(this.getValor().substring(3, 5)) <= 12 && Integer.parseInt(this.getValor().substring(0, 2)) >= 1 && Integer.parseInt(this.getValor().substring(3, 5)) >= 1;
					} catch (NumberFormatException ignored) {
					}
					break;
				case "Hora":
					try {
						pass = this.getValor().matches("\\d{2}:\\d{2}") && Integer.parseInt(this.getValor().substring(0, 2)) <= 23 && Integer.parseInt(this.getValor().substring(3, 5)) <= 59 && Integer.parseInt(this.getValor().substring(0, 2)) >= 0 && Integer.parseInt(this.getValor().substring(3, 5)) >= 0;
					} catch (NumberFormatException ignored) {
					}
					break;
				case "Bool":
					pass = this.getValor().equalsIgnoreCase("true") || this.getValor().equalsIgnoreCase("false") || this.getValor().equalsIgnoreCase("verdadero") || this.getValor().equalsIgnoreCase("falso");
					break;
				case "Clave":
					pass = this.getValor().matches("[a-zA-Z]*\\d+") || this.getValor().matches("\\d+[a-zA-Z]*");
				default:
					break;
			}
		}
		if (!pass) {
			if (callFeedback == 1 || callFeedback == 2) {
				System.out.println("El dato " + this.getValor() + " no es valido [" + this.getTipo() + "]" + (changeTipo ? "- El dato se ha restablecido a vacío" : ""));
			}
			if (changeTipo) {
				this.setValor(" ");
			}
		} else if (callFeedback == 2) {
			System.out.println("El dato es válido");
		}
		return pass;
	}

	public Dato assignTipo(int callFeedback, boolean changeTipo) {
		int tipo;
		if (this.getValor().isBlank()) {
			tipo = 1;
		} else if (this.getValor().matches("[a-zA-Z]*\\d+") || this.getValor().matches("\\d+[a-zA-Z]*")) {
			tipo = 8;
		}else if (this.getValor().equalsIgnoreCase("true") || this.getValor().equalsIgnoreCase("false") || this.getValor().equalsIgnoreCase("verdadero") || this.getValor().equalsIgnoreCase("falso")) {
			tipo = 7;
		} else if (this.getValor().matches("\\d{2}:\\d{2}") && Integer.parseInt(this.getValor().substring(0, 2)) <= 23 && Integer.parseInt(this.getValor().substring(3, 5)) <= 59 && Integer.parseInt(this.getValor().substring(0, 2)) >= 0 && Integer.parseInt(this.getValor().substring(3, 5)) >= 0) {
			tipo = 6;
		} else if (this.getValor().matches("\\d{2}/\\d{2}/\\d{4}") && Integer.parseInt(this.getValor().substring(0, 2)) <= 31 && Integer.parseInt(this.getValor().substring(3, 5)) <= 12 && Integer.parseInt(this.getValor().substring(0, 2)) >= 1 && Integer.parseInt(this.getValor().substring(3, 5)) >= 1) {
			tipo = 5;
		} else if (Utilidades.isNumber(this.getValor())) {
			tipo = 2;
		} else if (this.getValor().matches("\\d*\\.?\\d+") && Float.parseFloat(this.getValor()) != Float.POSITIVE_INFINITY && Float.parseFloat(this.getValor()) != Float.NEGATIVE_INFINITY) {
			tipo = 3;
		} else if (this.getValor().length() == 1) {
			tipo = 4;
		} else {
			tipo = 1;
		}
		if (changeTipo) {
			this.setTipo(tipo);
		}
		if (callFeedback == 1) {
			System.out.println("El dato de tipo [" + this.getTipo() + "]");
		}
		return this;
	}

	public Dato formatTipo() {
		this.setValor(this.getValor().trim());
		if (this.getValor().isBlank()) {
			return this;
		}
		switch (this.getTipo()) {
			case "Clave":
				if (this.getValor().matches("[a-zA-Z]\\d+[a-zA-Z]+")) {
					this.setValor(this.getValor().replaceAll("([a-zA-Z])(\\d+)([a-zA-Z]+)", "$1$2"));
				}
				break;
			case "Flotante":
				if (Utilidades.isNumber(this.getValor())) {
					this.setValor(String.valueOf(Float.parseFloat(this.getValor())));
				}
				break;
			case "Entero":
				this.setValor(String.valueOf((int) Float.parseFloat(this.getValor())));
				break;
			case "Bool":
				if (this.getValor().equals("1")) {
					this.setValor("true");
				} else if (this.getValor().equals("0")) {
					this.setValor("false");
				}
				break;
			case "Caracter":
				this.setValor(this.getValor().replaceAll(" ", ""));
				break;
			case "Hora":
				if (this.getValor().matches("\\d+:\\d+")) {
					String[] parts = this.getValor().split(":");
					parts = Arrays.stream(parts).map(v -> v.length() == 1 ? "0" + v : v).toArray(String[]::new);
					if (Integer.parseInt(parts[1]) > 60) {
						parts[1] = "00";
					}
					this.setValor(String.join(":", parts));
				}
				break;
			case "Fecha":
				if (this.getValor().matches("\\d+/\\d+/\\d+")) {
					try {
						String[] parts = this.getValor().split("/");
						parts = Arrays.stream(parts).map(v -> v.length() == 1 ? "0" + v : v).toArray(String[]::new);
						if (parts[2].length() == 2) {
							if (Integer.parseInt(parts[2]) > Year.now().getValue() - 2000) {
								parts[2] = "19" + parts[2];
							} else {
								parts[2] = "20" + parts[2];
							}
						} else if (parts[2].length() > 4) {
							parts[2] = Arrays.stream(parts[2].split("")).limit(4).collect(Collectors.joining());
						}
						if (Integer.parseInt(parts[2]) % 4 == 0 ? Integer.parseInt(parts[0]) > Utilidades.diasMes[Integer.parseInt(parts[1]) - 1] +1 : Integer.parseInt(parts[0]) > Utilidades.diasMes[Integer.parseInt(parts[1]) - 1]) {
							parts[0] = "01";
						}
						this.setValor(String.join("/", parts));
					} catch (ArrayIndexOutOfBoundsException ignored) {
						break;
					}
				}
				break;
		}
		return this;
	}

	@Override
	public String toString() {
		String s = "";
		Field[] fields = Arrays.stream(this.getClass().getDeclaredFields()).limit(this.getClass().getDeclaredFields().length - 3).toArray(Field[]::new);
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				s = (s.isEmpty()) ? "[" + field.getName() + ": " + field.get(this) : s + ", " + field.getName() + ": " + field.get(this);
			} catch (IllegalAccessException ignored) {
			}
		}
		s = s + "]";
		return s;
	}

	 static final Comparator<Dato> datoCompare = (o1, o2) -> {
		try {
			if (! o1.getTipo().equals(o2.getTipo())) {
				return 1;
			}
			String tipo = o1.getTipo();
			switch (tipo) {
				case "Texto", "Bool", "Caracter", "null" -> {
					String[] valores = new String[]{o1.getValor(), o2.getValor()};
					Arrays.sort(valores);
					if (! valores[0].equals(valores[1]) && valores[1].equals(o2.getValor())) {
						return - 1;
					}
				}
				case "Entero", "Flotante" -> {
					String[] valores = new String[]{o1.getValor(), o2.getValor()};
					return (int) (Float.parseFloat(valores[0]) - Float.parseFloat(valores[1]));
				}
				case "Hora" -> {
					int[] hora1 = Arrays.stream(o1.getValor().split(":")).mapToInt(Integer::parseInt).toArray();
					int[] hora2 = Arrays.stream(o2.getValor().split(":")).mapToInt(Integer::parseInt).toArray();
					if (hora1[0] < hora2[0]) {
						return - 1;
					} else if (hora1[0] == hora2[0]) {
						if (hora1[1] < hora2[1]) {
							return - 1;
						}
					}
				}
				case "Fecha" -> {
					int[] fecha1 = Arrays.stream(o1.getValor().split("/")).mapToInt(Integer::parseInt).toArray();
					int[] fecha2 = Arrays.stream(o2.getValor().split("/")).mapToInt(Integer::parseInt).toArray();
					if (fecha1[2] < fecha2[2]) {
						return - 1;
					} else if (fecha1[2] == fecha2[2]) {
						if (fecha1[1] < fecha2[1]) {
							return - 1;
						} else if (fecha1[1] == fecha2[1]) {
							if (fecha1[0] < fecha2[0]) {
								return - 1;
							}
						}
					}
				}
				case "Clave" -> {
					if (o1.getValor().equals(o1.getValor().replaceAll("([A-Z_a-z]*)(\\d+)([A-Z_a-z]*)", "$2")) && o2.getValor().equals(o2.getValor().replaceAll("([A-Z_a-z]*)(\\d+)([A-Z_a-z]*)", "$2"))) {
						return compare(Integer.parseInt(o1.getValor()), Integer.parseInt(o2.getValor()));
					}
					String[] o1Array = o1.getValor().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
					String[] o2Array = o2.getValor().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
					ArrayList<String> o1List = new ArrayList<>(Arrays.asList(o1Array));
					ArrayList<String> o2List = new ArrayList<>(Arrays.asList(o2Array));
					for (int i = 0; i < 3; i++) {
						if (o1List.size() < 3) {
							o1List.add("");
						}
						if (o2List.size() < 3) {
							o2List.add("");
						}
					}
					if ((!o1List.get(0).isEmpty() || !o2List.get(0).isEmpty()) && !o1List.get(0).equals(o2List.get(0))) {
						try {
							int num1 = Integer.parseInt(o1List.get(0));
							int num2 = Integer.parseInt(o2List.get(0));
							return compare(num1, num2);
						} catch (NumberFormatException e) {
							return o1List.get(0).compareTo(o2List.get(0));
						}
					} else if (((!o1List.get(1).isEmpty() || !o2List.get(1).isEmpty()) && !o1List.get(1).equals(o2List.get(1)))) {
						try {
							int num1 = Integer.parseInt(o1List.get(1));
							int num2 = Integer.parseInt(o2List.get(1));
							return compare(num1, num2);
						} catch (NumberFormatException e) {
							return o1List.get(1).compareTo(o2List.get(1));
						}
					} else {
						try {
							int num1 = Integer.parseInt(o1List.get(2));
							int num2 = Integer.parseInt(o2List.get(2));
							return compare(num1, num2);
						} catch (NumberFormatException e) {
							return o1List.get(2).compareTo(o2List.get(2));
						}
					}
				}
			}

		} catch (NumberFormatException ignored) {
		}
		return 1;
	};

	static final Comparator<Dato> datoCompareInv  = (o1, o2) -> datoCompare.compare(o2, o1);

}