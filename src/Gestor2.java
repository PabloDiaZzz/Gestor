import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Programa de gestor de información en tablas
 *
 * @author Pablo Diaz
 * @version 21.11.24
 */

public class Gestor2 {
	static int limit = 15;
	static ArrayList<Integer> camposDupe = new ArrayList<>(List.of(0));
	static int sortBy = 0;
	static boolean autoSave = false;
	public static void main(String[] args) throws IOException {
		String defaultDir = defDir(0);
		ArrayList<ArrayList<Dato>> list;
		Scanner sc = new Scanner(System.in);
		list = cargarLista(defaultDir);
		if (list == null) {
			list = new ArrayList<>();
			System.out.print("Introduce un primer campo identificador (sus valores se asignarán automáticamente): ");
			list.getFirst().add(new Dato(sc.nextLine(), "Clave"));
		}
		while (true) {
			if (autoSave) {
				guardarLista(list, defDir(0), 0);
			}
			try {
				list.getFirst()
						.forEach(a -> a.setValor(a.getValor().toUpperCase()));
				ArrayList<ArrayList<Dato>> finalList = list;
				IntStream.range(0, list.size())
						.forEach(i -> finalList.get(i).getFirst().setId(i));
				for (int campo : camposDupe) {
					dupe(list, campo);
				}
				boolean changed = false;
				for (int x = 0; x < list.getFirst().size(); x++) {
					if (list.getFirst().get(x).getValor().matches(".*[△▽]")) {
						list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("△", ""));

						if (list.getFirst().get(x).getValor().matches(".*▽")) {
							list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("▽", ""));
							list.getFirst().get(sortBy).setValor(list.getFirst().get(x).getValor() + "▽");
							ordenar(list, sortBy,1);
						} else {
							list.getFirst().get(sortBy).setValor(list.getFirst().get(sortBy).getValor() + "△");
							ordenar(list, sortBy);
						}
						changed = true;
						break;
					}
				}
				if (!changed) {
					list.getFirst().get(sortBy).setValor(list.getFirst().get(sortBy).getValor() + "△");
					ordenar(list, sortBy);
				}
				for (int x = 1; x < list.size(); x++) {
					for (int y = 1; y < list.get(x).size(); y++) {
						if (!list.get(x).get(y).getTipo().equals("null")) {
							list.get(x).get(y).formatTipo().checkTipo(1, true);
						} else {
							list.get(x).get(y).assignTipo(1, true);
						}
					}
				}
				if (autoSave) {
					guardarLista(list, defDir(0), 0);
				}
				System.out.println();
				Utilidades.menu(true, new String[]{"Imprimir datos", "Modificar datos", "Añadir campo", "Cambiar campo", "Añadir entidad", "Eliminar entidad", "Exportar lista", "Importar lista", "Limpiar lista", "Limpiar atributos", "Funciones extra", "Salir"});
				int opt = sc.nextInt();
				sc.nextLine();
				System.out.println();
				switch (opt) {
					case 1:
						showInfo(list);
						System.out.println();
						break;
					case 2:
						System.out.println("¿Qué entidad deseas modificar?: ");
						showInfo(list);
						System.out.print("id >> ");
						int indexEntidad = buscarId(sc.nextLine(), list);

						if (indexEntidad >= 1 && indexEntidad < list.size()) {
							System.out.println("Campos de la entidad seleccionada:");
							for (int j = 1; j <= list.getFirst().size(); j++) {
								System.out.println((j) + ". " + list.getFirst().get(j - 1).getValor() + ": " + list.get(indexEntidad).get(j - 1).getValor());
							}
							int campo = Utilidades.campo(list, false);
							if (campo != -1) {
								System.out.println("Valor actual: " + list.get(indexEntidad).get(campo).getValor());
								System.out.print("Introduce el nuevo valor: ");
								String nuevoValor = sc.nextLine();
							if (nuevoValor.isEmpty()) {
								nuevoValor = " ";
							}
								Dato antiguoDato = new Dato(list.get(indexEntidad).get(campo).getValor(), indexEntidad, list.getFirst().get(campo).getTipo());
								if (campo == 0) {
									try {
										Integer.parseInt(nuevoValor);
										list.get(indexEntidad).get(campo).setValor(nuevoValor);
										for (ArrayList<Dato> d : list) {
											d.get(campo).setOrdenCambio(1);
										}
										list.get(indexEntidad).get(campo).setOrdenCambio();
									} catch (NumberFormatException e) {
										System.out.println("El valor introducido no es numerico.");
									}
								} else {
									for (ArrayList<Dato> d : list) {
										d.get(campo).setOrdenCambio(1);
									}
									list.get(indexEntidad).get(campo).setOrdenCambio();
									list.get(indexEntidad).get(campo).setValor(nuevoValor);
									System.out.println();
									if (!list.get(indexEntidad).get(campo).checkTipo(0, false)) {
										System.out.println("El dato no es valido, se intentara revertir el cambio");
										list.get(indexEntidad).set(campo, antiguoDato);
										list.get(indexEntidad).get(campo).checkTipo(1, true);
									} else {
										list.get(indexEntidad).get(campo).checkTipo(2, true);
									}

								}
							}
						} else {
							System.out.println("Índice de entidad no válido.");
						}
						System.out.println();
						break;
					case 3:
						System.out.print("Introduce el campo: ");
						String text = sc.nextLine();

						if (list.getFirst().stream().anyMatch(dato -> dato.getValor().equalsIgnoreCase(text))) {
							System.out.println("El campo ya existe.");
							break;
						} else if (text.isBlank()) {
							System.out.println("El campo no puede estar vacío.");
							break;
						}

						int tipo = Utilidades.tipo();
						if (tipo == -1) {
							break;
						}
						list.getFirst().add(new Dato(text, 0, tipo)); // Añade el nuevo campo a la primera fila
						for (int i = 1; i < list.size(); i++) { // Añade un valor vacío para ese campo en cada entidad ya existente
							list.get(i).add(new Dato(" ", i, tipo));
						}
						System.out.println("Campo añadido.");
						System.out.println();
						break;
					case 4:
						int campo = Utilidades.campo(list, true);
						if (campo != -1) {
							System.out.println("1. Cambiar nombre");
							if (campo > 0) {
								System.out.println("2. Cambiar tipo");
								System.out.println("3. Eliminar campo");
							}
							System.out.print(">> ");
							int accion = sc.nextInt();
							sc.nextLine();
							switch (accion) {
								case 1:
									System.out.print("Introduce el nuevo nombre: ");
									String newName = sc.nextLine();
									list.getFirst().get(campo).setValor(newName);
									System.out.println("Campo cambiado.");
									break;
								case 2:
									Utilidades.menu(false, new String[] {"Se borraran los datos que no concuerden con el nuevo tipo"});
									System.out.println("(-1 para cancelar)");
									if (campo != 0) {
										int newType = Utilidades.tipo();
										if (newType != -1) {
											for (int x = 1; x < list.size(); x++) {
												list.get(x).get(campo).setTipo(newType);
												list.get(x).get(campo).formatTipo();
												if (list.get(x).get(campo).getId() != 0) {
													list.get(x).get(campo).checkTipo(1, true);
												}
											}
										} else {
											System.out.println();
											break;
										}
									} else{
										System.out.println("Seleccione un campo válido.");
									}
									break;
								case 3:
									if (campo != 0) {
										for (int x = 0; x < list.size(); x++) {
											list.get(x).remove(campo);
										}
										System.out.println("Campo eliminado.");
										if (camposDupe.contains(campo)) {
											camposDupe.remove((Integer) campo);
										}
									} else {
										System.out.println("Seleccione un campo válido.");
									}
									break;
							}
						}
						System.out.println();
						break;
					case 5:
						ArrayList<Dato> newEntity = new ArrayList<>(); // Creas una lista (fila)
						newEntity.add(new Dato(String.valueOf(list.size()), -1, "Clave"));  // Asigna el ID automáticamente

						for (int i = 1; i < list.getFirst().size(); i++) {  // Añade un espacio para cada campo existente en la primera fila
							newEntity.add(new Dato(" ", -1, list.getFirst().get(i).getTipo()));  // Añade un valor vacío para cada campo (espacio y no valor vacio para que se guarde al exportar)
						}
						list.add(newEntity);  // Añade la nueva fila a la lista
						System.out.println("Nueva entidad añadida.");
						System.out.println();
						break;
					case 6:
						System.out.println("¿Qué entidad deseas eliminar?");
						showInfo(list);
						System.out.print("ID >> ");
						int entidad = buscarId(sc.next(), list);
						if (entidad >= 0 && entidad < list.size()) {
							list.remove(entidad);  // Elimina fila completa seleccionada
							System.out.println("Entidad eliminada.");
						} else {
							System.out.println("Índice no válido.");
						}
						System.out.println();
						break;
					case 7:
						guardarLista(list, chooseDir(), 1);
						System.out.println();
						break;
					case 8:
						list = cargarLista(chooseDir());
						int output = list != null ? 1 : -1;
						if (output == 1) {
							System.out.println("\nDatos cargados correctamente.");
							System.out.println("Comprobando tipos de datos...\n");
							for (int x = 1; x < list.size(); x++) {
								for (int y = 1; y < list.get(x).size(); y++) {
									if (!list.get(x).get(y).getTipo().equals("null")) {
										list.get(x).get(y).checkTipo(1, true);
									} else {
										list.get(x).get(y).assignTipo(1, true);
									}
								}
							}
							System.out.println("\nComprobado\n");
						} else {
							System.out.println("\nError al cargar los datos.");
						}
						System.out.println();
						break;
					case 9:
						list.clear();
						System.out.println("Lista limpiada.");
						System.out.println();
						main(args);
						return;
					case 10:
						showInfo(list);
						System.out.print("Introduzca el id de la entidad (* para seleccionar todas) >> ");
						try {
							String input = sc.nextLine();
							if (Utilidades.isNumber(input)) {
								try {
									int id = buscarId(input, list);
									for (int y = 1; y < list.get(id).size(); y++) {
										list.get(id).get(y).setValor("");
									}
								} catch (IndexOutOfBoundsException ex) {
									System.out.println();
									System.out.println("Indice no valido");
								}
							} else {
								if (input.equals("*")) {
									for (int x = 1; x < list.size(); x++) {
										for (int y = 1; y < list.get(x).size(); y++) {
											list.get(x).get(y).setValor(" ");
										}
									}
								} else {
									System.out.println();
									System.out.println("Debe introducir un número o '*'");
								}
							}
						} catch (InputMismatchException ex) {
							System.out.println();
							System.out.println("Error: " + ex + "\nDebe introducir un número o '*'");
						}
						System.out.println();
						break;
					case 11:
						extra(list);
						break;
					case 12:
						System.out.println("Saliendo...");
						return;
					default:
						System.out.println("Opción no válida.");
						System.out.println();
						break;
				}
			} catch (InputMismatchException e) {
				if (sc.next().equals("autosave"))
					if (!autoSave) {
						autoSave = true;
						System.out.println("AutoSave activado.");
					} else {
						autoSave = false;
						System.out.println("AutoSave desactivado.");
					}
				else {
					System.out.println("Introduzca una opcion válida.");
				}
			}
		}
	}

	public static void showInfo(ArrayList<ArrayList<Dato>> infoList) {
		ArrayList<ArrayList<Integer>> maxValue = new ArrayList<>();
		maxValue.add(new ArrayList<>());
		maxValue.add(new ArrayList<>());
		char esquinaSI = '╔';
		char esquinaSD = '╗';
		char esquinaII = '╚';
		char esquinaID = '╝';
		char fila = '—';
		char cruz = '╫';
		char bordeVert = '║';
		char bordeHor = '═';
		char interI = '╠';
		char interD = '╣';
		char interAb = '╩';
		char interAr = '╦';
		char interC = '╬';
		try {
			for (int y = 0; y < infoList.getFirst().size(); y++) { // Bucle para recorrer las columnas
				maxValue.getFirst().add(0); // Iniciar lista
				for (int x = 0; x < infoList.size(); x++) { // Bucle para guardar en la lista la longitud la palabra/dato más grande de cada columna
					int[] max = getLineaLong(infoList.get(x).get(y).getValor(), limit);
					maxValue.getFirst().set(y, Math.max(max[0], maxValue.getFirst().get(y)));

				}
			}
			for (int x = 0; x < infoList.size(); x++) {
				maxValue.getLast().add(0);
				for (int y = 0; y < infoList.get(x).size(); y++) {
					int[] max = getLineaLong(infoList.get(x).get(y).getValor(), limit);
					maxValue.getLast().set(x, Math.max(max[1], maxValue.getLast().get(x)));
				}
			}
		} catch (IndexOutOfBoundsException ignored) {
			System.out.println("\n\nLista no válida");
		}
		System.out.println();
		System.out.print(esquinaSI);
		for (int h1 = 0; h1 < maxValue.getFirst().size(); h1++) {
			for (int h2 = 0; h2 < (maxValue.getFirst().get(h1) + 2); h2++) {
				System.out.print(bordeHor);
			}
			if (h1 != maxValue.getFirst().size() - 1) {
				System.out.print(interAr);
			}
		}
		System.out.print(esquinaSD);
		try {
			for (int x = 0; x < infoList.size(); x++) { // Print de la lista (valor original int x = 1)
				String[] linea = new String[infoList.getFirst().size()];
				for (int j = 0; j < linea.length; j++) {
					linea[j] = infoList.get(x).get(j).getValor();
				}
				String[] lS;
				for (int y = 0; y < linea.length; y++) {
					int gRange = ((maxValue.getLast().get(x) % 2 == 0) ? (maxValue.getLast().get(x)/2) - getLineaLong(linea[y], limit)[1]: ((maxValue.getLast().get(x)+1)/2) - getLineaLong(linea[y], limit)[1]);
					if (maxValue.getLast().get(x) != 1) {
						for (int g = 0; g < gRange; g++) {
							while ( gRange > Collections.frequency(List.of(linea[y].split(" ")),"#*salto#*")) {
								linea[y] = "#*salto#*" + " " + linea[y];
							}
						}
					}
				}
				lS = printLinea(linea, maxValue);
				for (int h = 0; h < lS.length; h++) {
					if (lS[h] != null) {
						lS = printLinea(lS, maxValue);
						h = 0;
					}
				}

				boolean isText = false;
				try {
					Integer.parseInt(infoList.get(x + 1).getFirst().getValor());
				} catch (NumberFormatException e) {
					isText = true;
				} catch (IndexOutOfBoundsException ignored) {}
				if (x == 0 || x == infoList.size() - 1 || isText) { // If para añadir barra separadora después de la fila de campos y al final
					System.out.println();
					if (x == 0 || isText) {
						System.out.print(interI);
						for (int h1 = 0; h1 < maxValue.getFirst().size(); h1++) {
							for (int h2 = 0; h2 < (maxValue.getFirst().get(h1) + 2); h2++) {
								System.out.print(bordeHor);
							}
							if (h1 != maxValue.getFirst().size() - 1) {
								System.out.print(interC);
							}
						}
						System.out.print(interD);
					} else {
						System.out.print(esquinaII);
						for (int h1 = 0; h1 < maxValue.getFirst().size(); h1++) {
							for (int h2 = 0; h2 < (maxValue.getFirst().get(h1) + 2); h2++) {
								System.out.print(bordeHor);
							}
							if (h1 != maxValue.getFirst().size() - 1) {
								System.out.print(interAb);
							}
						}
						System.out.print(esquinaID);
					}
				} else {
					System.out.println();
					System.out.print(bordeVert);
					for (int h1 = 0; h1 < maxValue.getFirst().size(); h1++) {
						for (int h2 = 0; h2 < (maxValue.getFirst().get(h1) + 2); h2++) {
							System.out.print(fila);
						}
						if (h1 != maxValue.getFirst().size() - 1) {
							System.out.print(cruz);
						}
					}
					System.out.print(bordeVert);
				}
			}
		} catch (IndexOutOfBoundsException ignored) {
			System.out.println("\n\nLista no válida");
		}
		System.out.println();
		System.out.println();
	}

	private static String[] printLinea(String[] linea, ArrayList<ArrayList<Integer>> maxValue) {
		char bordeVert = '║';
		char columnaInt = '║';
		String[] lS = new String[linea.length];
		System.out.print("\n" + bordeVert);
		linea = Arrays.stream(linea).map(v -> v == null ? "" : v).toArray(String[]::new);
		for (int y = 0; y < linea.length; y++) {
			String a = null;

			String[] palabra = Arrays.stream(linea[y].split(" ")).map(v -> v.equals("#*salto#*") ? " ".repeat(limit + 1) : v).toArray(String[]::new);
			for (String s : palabra) {
				if (a == null || a.length() <= limit) {
					a = (a == null) ? s : a + " " + s;
				}
			}

			int lineaIndividual = (a != null && a.isBlank()) ? 0 : (a != null ? a.length() : 0);
			for (int z = 0; z < ((maxValue.getFirst().get(y) - lineaIndividual) / 2 == 0 ? 1 : ((maxValue.getFirst().get(y) - lineaIndividual) / 2 ) + 1); z++) {
				System.out.print(" ");
			}

			linea[y] = null;
			for (String s : palabra) {
				if (linea[y] == null || linea[y].length() <= limit) {
					linea[y] = linea[y] == null ? s : linea[y] + " " + s;
				} else {
					lS[y] = lS[y] == null ? s : lS[y] + " " + s;
				}
			}
			System.out.print(linea[y] == null || linea[y].isBlank() ? "" : linea[y]);

			if ((maxValue.getFirst().get(y) - lineaIndividual) % 2 == 0) { // If para el caso de que el número de espacios sea impar
				for (int z = 0; z < ((maxValue.getFirst().get(y) - lineaIndividual) / 2 == 0 ? 1 : ((maxValue.getFirst().get(y) - lineaIndividual) / 2 ) + 1); z++) {
					System.out.print(" ");
				}
			} else {
				for (int z = 0; z < ((maxValue.getFirst().get(y) - lineaIndividual) / 2 == 0 ? 1 : ((maxValue.getFirst().get(y) - lineaIndividual) / 2 ) + 1) + 1; z++) {
					System.out.print(" ");
				}
			}
			if (y != linea.length - 1) {
				System.out.print(columnaInt);
			}
		}
		System.out.print(bordeVert); // Caracter del borde exterior derecho

		return lS;
	}

	private static int[] getLineaLong(String linea, int limit) {
		int lineaLong;
		String a = null;
		int b = 0;
		int contador = 1;
		String[] valor = linea.split(" ");
		for (int l = 0; l < valor.length; l++) {
			if (a == null || a.length() <= limit) {
				a = (a == null) ? valor[l] : a + " " + valor[l];
			} else {
				a = null;
				l -= 1;
				contador++;
			}
			b = a != null ? Math.max(b, a.length()) : b;
		}
		lineaLong = b;
		return new int[]{lineaLong, contador};
	}

	public static void guardarLista(ArrayList<ArrayList<Dato>> list, String fileDir, int call) { // Funcion para exportar una lista
		try { // Seleccionar el archivo
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileDir));
				oos.writeObject(list);
				oos.close();
				System.out.println("Lista serializada");
			} catch (IOException e) {
				System.out.println("Error al serializar la lista: " + e);
			}
			if (call == 1) {
				System.out.println("\nDatos guardados");
			}
		} catch (NullPointerException ignored) {
		}
	}

	public static ArrayList<ArrayList<Dato>> cargarLista(String fileDir) {
		if (fileDir == null) {
			return null;
		}
		try {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileDir));
				ArrayList<ArrayList<Dato>> l = (ArrayList<ArrayList<Dato>>) ois.readObject();
				ois.close();
				System.out.println("Lista cargada");
				return l;
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Error al cargar la lista serializada: " + e);
			}
		} catch (NullPointerException ignored) {
		}
		return null;
	}

	private static String chooseDir() { // Funcion para seleccionar un archivo
		JFileChooser fC = new JFileChooser();

		// Establecer el directorio predeterminado
		fC.setCurrentDirectory(new File(System.getProperty("user.dir")));

		// Abrir diálogo de guardar archivo
		int result = fC.showSaveDialog(null);

		// Comprobar si el usuario seleccionó un archivo
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fC.getSelectedFile();
			System.out.println("Archivo en: " + f.getAbsolutePath());
			return f.getAbsolutePath();
		}
		if (result == JFileChooser.CANCEL_OPTION) {
			System.out.println("Operacion cancelada");
		}
		if (result == JFileChooser.ERROR_OPTION) {
			System.out.println("Error");
		}
		return null;
	}

	private static void extra(ArrayList<ArrayList<Dato>> l) throws FileNotFoundException {
		ArrayList<ArrayList<Dato>> staticL = Utilidades.aaListCopy2(l);
		Scanner sc = new Scanner(System.in);
		ArrayList<Dato> newEntity;
		int campo;
		int temp;
		while (true) {
			if (autoSave) {
				guardarLista(l, defDir(0), 0);
			}
			ArrayList<ArrayList<Dato>> tempL = Utilidades.aaListCopy2(l);
			System.out.println("Selecione una funcion:\n");
			Utilidades.menu(true, new String[] {"Imprimir lista", "Calcular valor", "Establecer lista predeterminada", "Buscar entidad", "Ordenar lista","Orden de campos", "Mayusculas/Minusculas", "Tipo de datos de cada campo", "Salto de linea","Duplicados" , "Salir"});
			int opt = sc.nextInt();
			sc.nextLine();
			System.out.println();
			switch (opt) {
				case 1:
					showInfo(staticL);
					break;
				case 2:
					System.out.println();
					Utilidades.menu(true, new String[] {"Media", "Maximo", "Minimo"});
					opt = sc.nextInt();
					sc.nextLine();
					switch (opt) {
						case 1:
							newEntity = new ArrayList<>();
							int contador = 0;
							campo = Utilidades.campo(staticL, true, 1);
							if (campo != -1) {
								float sumaTotal = 0f;
								for (int y = 1; y < staticL.size (); y++) {
									try {
										Integer.parseInt (staticL.getFirst().get(campo).getValor());
										sumaTotal += Float.parseFloat (staticL.get(y).get(campo).getValor());
										contador++;
									} catch (Exception ignored) {
										if (staticL.getFirst().get(campo).getTipo().equals("Hora")) {
											int[] hora = Arrays.stream(staticL.get(y).get(campo).getValor().split(":")).mapToInt(Integer::parseInt).toArray();
											sumaTotal += hora[0] * 60 + hora[1];
											contador++;
										}
									}
								}
								if (contador > 0) {
									String valor;
									if (staticL.getFirst().get(campo).getTipo().equals("Hora")) {
										valor = String.format("%02d:%02d", (int)(sumaTotal/(contador*60)), (int)(sumaTotal/contador)%60);
									} else {
										valor = String.valueOf (sumaTotal / contador);
									}
									if (buscarId ("Media", staticL) == - 1) {
										newEntity.add(new Dato("Media", - 1, 0));
										for (int x = 1; x < staticL.getFirst().size(); x++) {
											newEntity.add(new Dato("", - 1, 0));
										}
										newEntity.get(campo).setValor(valor);
										staticL.add(newEntity);
									} else {
										staticL.get (buscarId ("Media", staticL)).get(campo).setValor(valor);
									}
									showInfo (staticL);
								} else {
									System.out.println ("\nNo se puede calcular la media");
								}
							}
							break;
						case 2:
							newEntity = new ArrayList<>();
							campo = Utilidades.campo(staticL, true, 1);
							if (campo != -1) {
								temp = - 1;
								for (int x = 1; x < staticL.getFirst ().size (); x++) {
									try {
										Float.parseFloat (staticL.get (x).get(campo).getValor());
										temp = x;
									} catch (NumberFormatException ignored) {
									}
								}
								if (temp == - 1) {
									System.out.println ("No se puede calcular el maximo");
									break;
								}
								float maxValue = 0f;
								if (buscarId ("Maximo", staticL) == - 1) {
									newEntity.add(new Dato("Maximo", -1, 0));
									for (int x = 1; x < staticL.getFirst().size (); x++) {
										newEntity.add(new Dato("", -1, 0));
									}

									for (int x = 0; x < staticL.size (); x++) {
										try {
											maxValue = Math.max (Float.parseFloat (staticL.get (x).get (campo).getValor()), maxValue);
										} catch (Exception ignored) {
										}
									}
									newEntity.get(campo).setValor(String.valueOf(maxValue));
									staticL.add(newEntity);
								} else {
									for (int x = 0; x < staticL.size (); x++) {
										try {
											maxValue = Math.max (Float.parseFloat (staticL.get (x).get (campo).getValor()), maxValue);
										} catch (Exception ignored) {
										}
									}
									staticL.get (buscarId ("Maximo", staticL)).get(campo).setValor(String.valueOf(maxValue));
								}
								showInfo(staticL);
							} else {
								System.out.println("Seleccione un campo existente.");
							}
							break;
						case 3:
							newEntity = new ArrayList<>();
							campo = Utilidades.campo(staticL, true, 1);
							if (campo != -1) {
								temp = - 1;
								for (int x = 1; x < staticL.getFirst ().size (); x++) {
									try {
										Float.parseFloat (staticL.get (x).get(campo).getValor());
										temp = x;
									} catch (NumberFormatException ignored) {
									}
								}
								if (temp == - 1) {
									System.out.println ("No se puede calcular el minimo");
									break;
								}
								float minValue = Float.parseFloat (staticL.get (temp).get (campo).getValor());
								if (buscarId ("Minimo", staticL) == - 1) {
									newEntity.add (new Dato("Minimo", -1, 0));
									for (int x = 1; x < staticL.getFirst ().size (); x++) {
										newEntity.add (new Dato("", -1, 0));
									}
									for (int x = 0; x < staticL.size (); x++) {
										try {
											minValue = Math.min (Float.parseFloat (staticL.get (x).get (campo).getValor()), minValue);
										} catch (Exception ignored) {
										}
									}
									newEntity.get(campo).setValor(String.valueOf(minValue));
									staticL.add (newEntity);
								} else {
									for (int x = 0; x < staticL.size (); x++) {
										try {
											minValue = Math.min (Float.parseFloat (staticL.get (x).get (campo).getValor()), minValue);
										} catch (Exception ignored) {
										}
									}
									staticL.get (buscarId ("Minimo", staticL)).get(campo).setValor(String.valueOf(minValue));
								}
								showInfo (staticL);
							} else {
								System.out.println("Seleccione un campo existente.");
							}
							break;
						default:
							System.out.println("Opción no válida.");
							break;
					}
					System.out.println();
					break;
				case 3:
					Utilidades.menu(true, new String[] {"Establecer lista predeterminada", "Borrar lista predeterminada"});
					opt = sc.nextInt();
					sc.nextLine();
					switch (opt) {
						case 1:
							defDir(1);
							break;
						case 2:
							defDir(-1);
							break;
						default:
							System.out.println("Opción no válida.");
							break;
					}
					System.out.println();
					break;
				case 4:
					System.out.println();
					Utilidades.menu(true, new String[] {"Buscar por atributo", "Buscar por campo"});
					opt = sc.nextInt();
					sc.nextLine();
					switch (opt) {
						case 1:
							System.out.println("Selecione el campo: ");
							for (int x = 1; x < tempL.getFirst().size(); x++) {
								System.out.println((x) + ". " + tempL.getFirst().get(x).getValor());
							}
							System.out.print(">> ");
							campo = sc.nextInt();
							sc.nextLine();
							if (campo >= 0 && campo < tempL.getFirst().size()) {  // Verifica que el campo esté en el rango
								System.out.print("Termino a buscar: ");
								String buscar = sc.nextLine();
								ArrayList<Integer> id = buscarEntVert(campo, buscar, tempL);
								if (id.isEmpty()) {
									System.out.println("No se ha encontrado la entidad.");
								} else {
									for (int x = tempL.size() - 1; x > 0; x--) {
										if (!id.contains(x)) {
											tempL.remove(x);
										}
									}
									showInfo(tempL);
								}
							} else {
								System.out.println("Seleccione un campo existente.");
							}
							break;
						case 2:
							campo = Utilidades.campo(tempL, true, 1);
							if (campo != -1) {
								String memoria = tempL.getFirst ().get (campo).getValor();
								for (int y = tempL.getFirst ().size () - 1; y > 0; y--) {
									if (! tempL.getFirst ().get (y).equals (tempL.getFirst ().get (buscarEntHor (0, memoria, tempL).getFirst ()))) {
										for (int z = 0; z < tempL.size (); z++) { // Elimina toda la columna seleccionada
											tempL.get (z).remove (y);
										}
									}
								}
								showInfo (tempL);
							} else {
								System.out.println("Seleccione un campo existente.");
							}
						default:
							System.out.println("Opción no válida.");
					}
					break;
				case 5:
					boolean changed = false;
					System.out.println();
					campo = Utilidades.campo(l, true);
					sortBy = campo;
					if (campo != -1) {
						for (int x = 0; x < l.getFirst().size(); x++) {
							if (x == campo && l.getFirst().get(x).getValor().matches(".*△")) {
								l.getFirst().get(x).setValor(l.getFirst().get(x).getValor().replace("△", ""));
								l.getFirst().get(campo).setValor(l.getFirst().get(campo).getValor() + "▽");
								ordenar(l, campo,1);
								changed = true;
							} else if (x == campo) {
								l.getFirst().get(x).setValor(l.getFirst().get(x).getValor().replace("▽", ""));
								l.getFirst().get(campo).setValor(l.getFirst().get(campo).getValor() + "△");
								ordenar(l, campo,0);
								changed = true;
							} else {
								l.getFirst().get(x).setValor(l.getFirst().get(x).getValor().replace("△", ""));
								l.getFirst().get(x).setValor(l.getFirst().get(x).getValor().replace("▽", ""));
							}


						}
						if (!changed) {
							l.getFirst().get(campo).setValor(l.getFirst().get(campo).getValor() + "△");
							ordenar(l, campo,0);
						}

						showInfo(l);

					}
					break;
				case 6:
					ArrayList<Dato> tempArray = new ArrayList<>();
					int campo2;
					System.out.println();
					campo = Utilidades.campo(l, true, 1);
					if (campo != -1) {
						System.out.println("Seleccione el campo por el que intercambiar");
						System.out.print(">> ");
						campo2 = sc.nextInt();
						sc.nextLine();
						if (campo2 < 1 && campo2 >= l.getFirst().size()) {
							System.out.println("Seleccione un campo existente.");
							break;
						}
						if (campo == campo2) {
							System.out.println("Seleccione dos campos diferentes.");
							break;
						}
						for (int x = 0; x < l.size(); x++) {
							tempArray.add(l.get(x).get(campo));
							l.get(x).set(campo, l.get(x).get(campo2));
							l.get(x).set(campo2, tempArray.get(x));
						}
						showInfo(l);
						staticL = Utilidades.aaListCopy2(l);
					}
					break;
				case 7:
					System.out.println();
					Utilidades.menu(true, new String[]{"Seleccionar por entidad", "Seleccionar por campo", "Seleccionar toda la tabla"});
					int mode = sc.nextInt();
					campo = -1;
					int entity = -1;
					sc.nextLine();
					if (mode == 1) {
						System.out.println();
						showInfo(l);
						System.out.println();
						System.out.print("id >> ");
						entity = buscarId(String.valueOf(sc.nextInt()), l);
						if (entity == - 1) {
							System.out.println("No se ha encontrado la entidad.");
							break;
						}
					} else if (mode == 2) {
						System.out.println();
						campo = Utilidades.campo(l, true, 1);
						if (campo == -1) {
							break;
						}
					} else if (mode != 3) {
						System.out.println("Opción no válida.");
						break;
					}
					Utilidades.menu(true, new String[]{"Todo a mayusculas", "Todo a minusculas", "Primera letra de cada palabra a mayuscula"});
					int mode2 = sc.nextInt();
					sc.nextLine();
					int a;
					if (mode == 1) {
						a = l.get(entity).size();
					} else {
						a = l.size();
					}
					for (int x = 1; x < a; x++) {
						if (mode == 1) {
							l.get(entity).get(x).setValor(Utilidades.mayus(l.get(entity).get(x).getValor(), mode2));
						} else if (mode == 2) {
							l.get(x).get(campo).setValor(Utilidades.mayus(l.get(x).get(campo).getValor(), mode2));
						} else {
							for (int y = 1; y < l.get(x).size(); y++) {
								l.get(x).get(y).setValor(Utilidades.mayus(l.get(x).get(y).getValor(), mode2));
							}
						}
					}
					showInfo(l);
					staticL = Utilidades.aaListCopy2(l);
					break;
				case 8:
					System.out.println();
					String[] listaTipos = new String[l.getFirst().size()-1];
					for (int x = 1; x < l.getFirst().size(); x++) {
						listaTipos[x-1] = l.getFirst().get(x).getValor() + " - [" + l.getFirst().get(x).getTipo() + "]";
					}
					Utilidades.menu(false, listaTipos);
					break;
				case 9:
					System.out.println();
					System.out.print("Numero de caracteres límite (extensible para no cortar palabras): ");
					limit = sc.nextInt();
					sc.nextLine();
					break;
				case 10:
					System.out.println("Seleccione los campos quedeberan tener valores unicos: ");
					for (int x = 0; x < l.getFirst().size(); x++) {
						if (camposDupe.contains(x)) {
							l.getFirst().get(x).setValor(l.getFirst().get(x).getValor() + " - ON");
						} else {
							l.getFirst().get(x).setValor(l.getFirst().get(x).getValor().replace(" - ON", ""));
						}
					}
					campo = Utilidades.campo(l, true, 1);
					for (Dato campoValor : l.getFirst()) {
						campoValor.setValor(campoValor.getValor().replace(" - ON", ""));
					}
					if (campo == -1) {
						System.out.println("Seleccione un campo válido.");
						break;
					}
					if (camposDupe.contains(campo)) {
						camposDupe.remove((Integer) campo);
					} else {
						camposDupe.add(campo);
					}
					break;
				case 11:
					System.out.println();
					return;
				default:
					System.out.println("Opción no válida.");
					System.out.println();
					break;
			}
			System.out.println();
		}
	}

	public static void ordenar(ArrayList<ArrayList<Dato>> l, int campo, int sentido) {
		Dato[] a = l.stream().skip(1).flatMap(row -> row.stream().filter(element -> row.indexOf(element) == campo)).toArray(Dato[]::new);
		Dato[] aSort = Arrays.stream(Arrays.copyOf(a, a.length)).sorted(Dato.datoCompare).toArray(Dato[]::new);

		if (Arrays.equals(a, aSort) && l.getFirst().get(campo).getValor().matches(".*△▽")) {
			sentido = 1;
		}
		if (sentido == 1) {
			aSort = Arrays.stream(aSort).sorted(Dato.datoCompareInv).toArray(Dato[]::new);
		}
		for (int y = 0; y < a.length; y++) {
			if (!aSort[y].getValor().equals(l.get(y + 1).get(campo).getValor())) {
				ArrayList<Dato> temp = l.remove(y + 1);
				l.add(temp);
				y = - 1;
			}
		}
	}

	public static void ordenar(ArrayList<ArrayList<Dato>> l, int campo) {
		ordenar(l, campo, 0);
	}

	private static int buscarId(String n, ArrayList<ArrayList<Dato>> l) {
		int id = -1;
		for (int x = 0; x < l.size(); x++) {
			try {
				if (Integer.parseInt(l.get(x).getFirst().getValor()) == Integer.parseInt(n)) {
					id = x;
					break;
				}
			} catch (NumberFormatException e) {
				if (l.get(x).getFirst().getValor().equals(n)) {
					id = x;
					break;
				}
			}
		}
		return id;
	}

	private static ArrayList<Integer> buscarEntVert(int campo, String n, ArrayList<ArrayList<Dato>> l) {
		n = n.replaceAll("([A-Z_a-z]+)(△*)(▽*)", "$1");
		ArrayList<Integer> id = new ArrayList<>(0);
		for (int x = 0; x < l.size(); x++) {
			try {
				if (Integer.parseInt(l.get(x).get(campo).getValor().replaceAll("([A-Z_a-z]+)(△*)(▽*)", "$1")) == Integer.parseInt(n)) {
					id.add(x);
					break;
				}
			} catch (NumberFormatException e) {
				if (l.get(x).get(campo).getValor().toUpperCase().replaceAll("([A-Z_a-z]+)(△*)(▽*)", "$1").equals(n)) {
					id.add(x);
				}
			}
		}
		return id;
	}

	private static ArrayList<Integer> buscarEntHor(int ent, String n, ArrayList<ArrayList<Dato>> l) {
		ArrayList<Integer> id = new ArrayList<>(0);
		n = n.replaceAll("([A-Z_a-z]+)(△*)(▽*)", "$1");
		for (int x = 0; x < l.get(ent).size(); x++) {
			try {
				if (Integer.parseInt(l.get(ent).get(x).getValor().replaceAll("([A-Z_a-z]+)(△*)(▽*)", "$1")) == Integer.parseInt(n)) {
					id.add(x);
					break;
				}
			} catch (NumberFormatException e) {
				if (l.get(ent).get(x).getValor().toUpperCase().replaceAll("([A-Z_a-z]+)(△*)(▽*)", "$1").equals(n)) {
					id.add(x);
				}
			}
		}
		return id;
	}

	private static void dupe(ArrayList<ArrayList<Dato>> l, int campo) {
		int id;
		boolean repeat = true;
		Dato[] lista = l.stream().skip(1).flatMap(row -> row.stream().filter(element -> row.indexOf(element) == campo)).sorted(Comparator.comparingInt(Dato::getOrdenCambio)).toArray(Dato[]::new);
		do {
			id = -1;
			if (repeat) {
				repeat = false;

				for (int x = 0; x < lista.length; x++) {
					for (int y = 0; y < lista.length; y++) {
						try {
							if (x != y && lista[x].getValor().equals(lista[y].getValor())) {
								id = lista[x].getOrdenCambio() < lista[y].getOrdenCambio() ? x : y;
							} else {
								continue;
							}
							if (id != y) {
								continue;
							}
							int finalId = id;
							switch (lista[x].getTipo()) {
								case "Clave" -> {
									int[] valores = l.stream().skip(1).flatMap(row -> row.stream().filter(element -> row.indexOf(element) == campo && ! element.getValor().isBlank())).map(Dato::getValor).map(s -> s.replaceAll("([A-Z_a-z]*)(\\d*)([A-Z_a-z]*)", "$2")).mapToInt(Integer::parseInt).distinct().sorted().toArray();
									int nuevoId = valores[0];
									for (int n : valores) {
										if (nuevoId == n) {
											nuevoId++;
										}
									}

									lista[x].setValor(lista[x].getValor().replaceAll("([A-Z_a-z]*)(\\d+)([A-Z_a-z]*)", "$1" + nuevoId + "$3"));
								}
								case "Fecha" -> {
									ArrayList<int[]> fechas = new ArrayList<>();
									for (int z = 1; z < lista.length; z++) {
										if (! lista[z].getValor().isBlank()) {
											fechas.add(Arrays.stream(lista[z].getValor().split("/")).mapToInt(Integer::parseInt).toArray());
										}
									}
									int contador = 0;
									boolean salir;
									do {
										salir = true;
										int[] nuevoId = fechas.get(x - 1);
										for (int[] n : fechas) {
											if (nuevoId == n) {
												nuevoId[contador]++;
											}
											if (contador == 1 ? nuevoId[1] > 12 : contador == 0 && (n[contador] % 4 == 0 ? nuevoId[0] > Utilidades.diasMes[n[1] - 1] + 1 : nuevoId[0] > Utilidades.diasMes[n[1] - 1])) {
												nuevoId[contador] = 1;
												contador++;
												salir = false;
											}
										}
										lista[x].setValor(Arrays.stream(nuevoId).mapToObj(String::valueOf).collect(Collectors.joining("/")));
									} while (! salir);
								}
								case "Hora" -> {
									ArrayList<int[]> horas = new ArrayList<>();
									for (int z = 1; z < lista.length; z++) {
										if (! lista[z].getValor().isBlank()) {
											horas.add(Arrays.stream(lista[z].getValor().split(":")).mapToInt(Integer::parseInt).toArray());
										}
									}
									int contador = 1;
									boolean salir;
									do {
										salir = true;
										int[] nuevoId = horas.get(x - 1);
										for (int[] n : horas) {
											if (nuevoId == n) {
												nuevoId[contador]++;
											}
											if (contador == 0 ? nuevoId[1] > 59 : nuevoId[1] > 23) {
												nuevoId[contador] = 0;
												contador--;
												salir = false;
											}
										}
										lista[x].setValor(Arrays.stream(nuevoId).mapToObj(String::valueOf).collect(Collectors.joining(":")));
									} while (! salir);
								}
								case "Null", "Texto" -> {

									String[] valores = Arrays.stream(lista)
											.map(Dato::getValor)
											.filter(s -> Arrays.equals(
													new StringBuilder(s)
															.reverse()
															.toString()
															.chars()
															.dropWhile(Character::isDigit)
															.toArray(),
													new StringBuilder(l.get(finalId).get(campo).getValor())
															.reverse()
															.toString()
															.chars()
															.dropWhile(Character::isDigit)
															.toArray()))
											.sorted()
											.distinct()
											.toArray(String[]::new);
									int[] ids = Arrays.stream(valores)
											.map(s -> Arrays.stream(
													new StringBuilder(s)
															.reverse()
															.chars()
															.takeWhile(Character::isDigit)
															.toArray())
													.mapToObj(Character::toString)
													.collect(Collectors.joining()))
											.map(h -> Arrays.stream(
													new StringBuilder(h)
															.reverse()
															.chars()
															.toArray())
													.mapToObj(Character::toString)
													.collect(Collectors.joining()))
											.mapToInt(Integer::parseInt)
											.distinct()
											.sorted()
											.toArray();
									int nuevoId = 1;
									for (int n : ids) {
										if (nuevoId == n) {
											nuevoId++;
										}

									}
									String nuevoValor = Arrays.stream(
											new StringBuilder(lista[x].getValor())
													.reverse()
													.chars()
													.dropWhile(Character::isDigit)
													.toArray())
											.mapToObj(Character::toString)
											.collect(Collectors.joining());
									nuevoValor = new StringBuilder(nuevoValor)
											.reverse()
											.toString();
									lista[x].setValor(nuevoValor + nuevoId);
								}
								case "Entero", "Flotante" -> {
									double[] valores = Arrays.stream(lista)
											.map(Dato::getValor)
											.mapToDouble(Float::parseFloat)
											.distinct()
											.sorted()
											.toArray();
									double nuevoId = valores[0];
									for (double n : valores) {
										if (nuevoId == n) {
											nuevoId++;
										}
									}
									lista[x].setValor(String.valueOf((float) nuevoId));
								}
							}
							repeat = true;
							lista[x].formatTipo();
							for (Dato dato : lista) {
								for (ArrayList<Dato> datoes : l) {
									if (dato.getId() == datoes.get(campo).getId()) {
										datoes.get(campo).setValor(dato.getValor());
									}
								}
							}
							System.out.println("Valor duplicado [ID: " + lista[x].getId() + " CAMPO: " + l.getFirst().get(campo).getValor() + "] se ha cambiado [" + lista[y].getValor() + "] por [" + lista[x].getValor() + "]");
							x = y = lista.length - 1;
						} catch (NumberFormatException | IndexOutOfBoundsException e) {
							if (e instanceof IndexOutOfBoundsException) {
								x = y = lista.length - 1;
							}
						}
					}
				}
			}
		} while (id != -1);
	}

	private static String defDir(int n) throws FileNotFoundException {
		String d = null;
		if (n == 1) {
			try {
				d = chooseDir();
				PrintStream ps = new PrintStream(new FileOutputStream("default2.dat"));
				ps.println(d);
				System.out.println("\nDatos guardados");
			} catch (FileNotFoundException | NullPointerException e) {
				System.out.println("Error: " + e);
				return null;
			}
		} else if (n == 0) {

			try {
				BufferedReader br = new BufferedReader(new FileReader("default2.dat"));
				d = br.readLine();
			} catch (NullPointerException | IOException ignored) {
			}
		} else if (n == -1) {
			PrintStream ps = new PrintStream(new FileOutputStream("default2.dat"));
			ps.println((String) null);
		}
		return d;
	}
}