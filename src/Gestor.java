import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Programa de gestor de información en tablas
 *
 * @author Pablo Diaz
 * @version 04.11.24
 */

public class Gestor {
    public static void main(String[] args) throws IOException {
        String defaultDir = defDir(0);
        ArrayList<ArrayList<String>> list = new ArrayList<>(); // Inicializa una lista de listas (ArrayList bidimensional)
        Scanner sc = new Scanner(System.in);
        if (cargarLista(list, defaultDir) == -1) {
            list.add(new ArrayList<>());
            System.out.print("Introduce un primer campo identificador (sus valores se asignarán automáticamente): ");
            list.getFirst().add(sc.nextLine());  // Añade el primer campo a la primera fila
        }
        while (true) {
            try {
                list.getFirst().replaceAll(String::toUpperCase);
                ordenar(list, "id");
                dupe(list);
                ordenar(list, "id");
                Utilidades.menu(true, new String[]{"Imprimir datos", "Modificar datos", "Añadir campo", "Eliminar campo", "Añadir entidad", "Eliminar entidad", "Exportar lista", "Importar lista", "Limpiar lista", "Limpiar atributos", "Funciones extra", "Salir"});
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
                        int indexEntidad = buscarId(sc.next(), list);

                        if (indexEntidad >= 1 && indexEntidad < list.size()) {
                            System.out.println("Campos de la entidad seleccionada:");
                            //                            System.out.println(list.getFirst().getFirst() + ": " + list.get(indexEntidad).getFirst()); // Print del identificador
                            for (int j = 1; j <= list.getFirst().size(); j++) { // Print de cada campo y su valor numerados
                                System.out.println((j) + ". " + list.getFirst().get(j - 1) + ": " + list.get(indexEntidad).get(j - 1));
                            }

                            System.out.print("¿Qué campo deseas modificar? (1 a " + (list.getFirst().size()) + "): ");
                            int campo = sc.nextInt() - 1;
                            if (campo >= 0 && campo < list.getFirst().size()) {  // Verifica que el campo esté en el rango
                                System.out.println("Valor actual: " + list.get(indexEntidad).get(campo));
                                sc.nextLine();
                                System.out.print("Introduce el nuevo valor: ");
                                String nuevoValor = sc.nextLine();
                                if (campo == 0) {
                                    try {
                                        Integer.parseInt(nuevoValor);
                                        list.get(indexEntidad).set(campo, nuevoValor + "c");
                                    } catch (NumberFormatException e) {
                                        System.out.println("El valor introducido no es numerico.");
                                    }
                                } else {
                                    list.get(indexEntidad).set(campo, nuevoValor);
                                    System.out.println("Campo modificado correctamente.");
                                }
                            } else {
                                System.out.println("Índice de campo no válido para esta entidad.");
                            }
                        } else {
                            System.out.println("Índice de entidad no válido.");
                        }
                        System.out.println();
                        break;
                    case 3:
                        System.out.print("Introduce el campo: ");
                        String text = sc.nextLine();
                        if (list.getFirst().contains(text.toUpperCase())) {
                            System.out.println("El campo ya existe.");
                            break;
                        } else if (text.isBlank()) {
                            System.out.println("El campo no puede estar vacío.");
                            break;
                        }
                        list.getFirst().add(text); // Añade el nuevo campo a la primera fila
                        for (int i = 1; i < list.size(); i++) { // Añade un valor vacío para ese campo en cada entidad ya existente
                            list.get(i).add("");
                        }
                        System.out.println("Campo añadido.");
                        System.out.println();
                        break;
                    case 4:
                        System.out.println("Seleccione el campo para eliminar:");
                        for (int j = 1; j < list.getFirst().size(); j++) { // Print de todos los campos excepto el identificador
                            System.out.println((j) + ". " + list.getFirst().get(j));
                        }
                        System.out.print(">> ");
                        int campo = sc.nextInt();
                        if (campo >= 0 && campo < list.getFirst().size()) {
                            for (int x = 0; x < list.size (); x++) { // Elimina toda la columna seleccionada
                                list.get (x).remove (campo);
                            }
                            System.out.println ("Campo eliminado.");
                        } else {
                            System.out.println("Campo no válido");
                            System.out.println("Accion cancelada");
                        }
                        System.out.println();
                        break;
                    case 5:
                        ArrayList<String> newEntity = new ArrayList<>(); // Creas una lista (fila)
                        newEntity.add(String.valueOf(list.size()));  // Asigna el ID automáticamente

                        for (int i = 1; i < list.getFirst().size(); i++) {  // Añade un espacio para cada campo existente en la primera fila
                            newEntity.add(" ");  // Añade un valor vacío para cada campo (espacio y no valor vacio para que se guarde al exportar)
                        }
                        list.add(newEntity);  // Añade la nueva fila a la lista
                        System.out.println("Nueva entidad añadida.");
                        System.out.println();
                        break;
                    case 6:
                        System.out.println("¿Qué entidad deseas eliminar? (1 a " + (list.size() - 1) + ")");
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
                        guardarLista(list, chooseDir());
                        System.out.println();
                        break;
                    case 8:
                        int output = cargarLista(list, chooseDir());
                        if (output == 1) {
                            System.out.println("\nDatos cargados correctamente.");
                        } else if (output == -1) {
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
                            try {
                                int id = Integer.parseInt(input);
                                for (int y = 1; y < list.get(id).size(); y++) {
                                    list.get(id).set(y, "");
                                }
                            } catch (IndexOutOfBoundsException ex) {
                                System.out.println();
                                System.out.println("Indice no valido");
                            } catch (Exception e) {
                                if (Objects.equals(input, "*")) {
                                    for (int x = 1; x < list.size(); x++) {
                                        for (int y = 1; y < list.get(x).size(); y++) {
                                            list.get(x).set(y, "");
                                        }
                                    }
                                } else {
                                    System.out.println();
                                    System.out.println("Error: " + e + "\nDebe introducir un número o '*'");
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
                System.out.println("Introduzca una opcion válida.");
                sc.nextLine();
            }
        }
    }

    private static void showInfo(ArrayList<ArrayList<String>> infoList) {
        ArrayList<Integer> maxValue = new ArrayList<>(); // Crear lista de tamaño variable
        char esquinaSI = '╔';
        char esquinaSD = '╗';
        char esquinaII = '╚';
        char esquinaID = '╝';
        char fila = '—';
        char columnaInt = '║';
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
               maxValue.add(0); // Iniciar lista
               for (int x = 0; x < infoList.size(); x++) { // Bucle para guardar en la lista la longitud la palabra/dato más grande de cada columna
                   maxValue.set(y, Math.max(infoList.get(x).get(y).length(), maxValue.get(y)));
               }
           }
       } catch (IndexOutOfBoundsException ignored) {
           System.out.println("\n\nLista no válida");
       }
        System.out.println();
        System.out.print(esquinaSI);
        for (int h1 = 0; h1 < maxValue.size(); h1++) {
            for (int h2 = 0; h2 < (maxValue.get(h1) + 2); h2++) {
                System.out.print(bordeHor);
            }
            if (h1 != maxValue.size() - 1) {
                System.out.print(interAr);
            }
        }
        System.out.print(esquinaSD);
        try {
            for (int x = 0; x < infoList.size(); x++) { // Print de la lista (valor original int x = 1)
                System.out.print("\n" + bordeVert); // Salto de línea y caracter del borde exterior izquierdo
                for (int y = 0; y < infoList.get(x).size(); y++) {
                    if ((maxValue.get(y) - infoList.get(x).get(y).length()) % 2 == 0) { // If para el caso de que el número de espacios sea impar
                        for (int z = 0; z < (maxValue.get(y) - infoList.get(x).get(y).length()) / 2 + 1; z++) {
                            System.out.print(" ");
                        }
                    } else {
                        for (int z = 0; z < (maxValue.get(y) - infoList.get(x).get(y).length()) / 2 + 2; z++) { // Se pondra el espacio sobrante a la
                            System.out.print(" ");                                                         // para alinear el texto a la izquierda
                        }
                    }

                    System.out.print(infoList.get(x).get(y)); // Print del elemento
                    for (int z = 0; z < (maxValue.get(y) - infoList.get(x).get(y).length()) / 2 + 1; z++) { // Bucle para escribir los espacios adicionales
                        System.out.print(" ");                                                         // si la palabra/dato mide menos del maximo
                    }
                    if (y != infoList.get(x).size() - 1) {
                        System.out.print(columnaInt);
                    }
                }
                System.out.print(bordeVert); // Caracter del borde exterior derecho
                boolean isText = false;
                try {
                    Integer.parseInt(infoList.get(x + 1).getFirst());
                } catch (NumberFormatException e) {
                    isText = true;
                } catch (IndexOutOfBoundsException ignored) {
                }
                if (x == 0 || x == infoList.size() - 1 || isText) { // If para añadir barra separadora después de la fila de campos y al final
                    System.out.println();
                    if (x == 0 || isText) {
                        System.out.print(interI);
                        for (int h1 = 0; h1 < maxValue.size(); h1++) {
                            for (int h2 = 0; h2 < (maxValue.get(h1) + 2); h2++) {
                                System.out.print(bordeHor);
                            }
                            if (h1 != maxValue.size() - 1) {
                                System.out.print(interC);
                            }
                        }
                        System.out.print(interD);
                    } else {
                        System.out.print(esquinaII);
                        for (int h1 = 0; h1 < maxValue.size(); h1++) {
                            for (int h2 = 0; h2 < (maxValue.get(h1) + 2); h2++) {
                                System.out.print(bordeHor);
                            }
                            if (h1 != maxValue.size() - 1) {
                                System.out.print(interAb);
                            }
                        }
                        System.out.print(esquinaID);
                    }
                } else {
                    System.out.println();
                    System.out.print(bordeVert);
                    for (int h1 = 0; h1 < maxValue.size(); h1++) {
                        for (int h2 = 0; h2 < (maxValue.get(h1) + 2); h2++) {
                            System.out.print(fila);
                        }
                        if (h1 != maxValue.size() - 1) {
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

    private static void guardarLista(ArrayList<ArrayList<String>> list, String fileDir) { // Funcion para exportar una lista
        try { // Seleccionar el archivo
            try {
                PrintStream out = new PrintStream(new FileOutputStream(fileDir)); // Inicializamos la variable out que servira
                // para editar el archivo correspondiente
                for (ArrayList<String> strings : list) { // Añadir como nueva línea al archivo cada fila de la tabla
                    out.println(strings);

                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: " + e);
            }
            System.out.println("\nDatos guardados");
        } catch (NullPointerException ignored) {
        }
    }

    private static int cargarLista(ArrayList<ArrayList<String>> l, String fileDir) throws IOException { // Funcion para importar una lista
        if (fileDir == null) {
            return -1;
        }
        int lines = 0;
        try { // Seleccionar el archivo
            BufferedReader reader = new BufferedReader(new FileReader(fileDir));
            try {
                while (reader.readLine() != null) lines++;
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
            reader.close();
            BufferedReader br = new BufferedReader(new FileReader(fileDir));
            if (lines != 0) {
                l.clear();
            }
            for (int x = 0; x < lines; x++) {
                String linea = br.readLine();
                String[] newEntity = linea.substring(1, linea.length() - 1).split(", ");
                ArrayList<String> datosImport = new ArrayList<>(Arrays.asList(newEntity));
                l.add(datosImport);
            }
            br.close();
        } catch (NullPointerException | FileNotFoundException e) {
            return -1;
        }
        return 1;
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

    private static void extra(ArrayList<ArrayList<String>> l) throws FileNotFoundException {
        ArrayList<ArrayList<String>> staticL = Utilidades.aaListCopy(l);
        Scanner sc = new Scanner(System.in);
        ArrayList<String> newEntity;
        int campo;
        int temp;
        while (true) {
            ArrayList<ArrayList<String>> tempL = Utilidades.aaListCopy(l);
            System.out.println("Selecione una funcion:\n");
            Utilidades.menu(true, new String[] {"Imprimir lista", "Media de un campo", "Valor máximo", "Valor mínimo", "Establecer lista predeterminada", "Buscar entidad", "Ordenar lista","Orden de campos", "Mayusculas/Minusculas" , "Salir"});
            int opt = sc.nextInt();
            sc.nextLine();
            System.out.println();
            switch (opt) {
                case 1:
                    showInfo(staticL);
                    break;
                case 2:
                    newEntity = new ArrayList<>();
                    int contador = 0;
                    System.out.println("Selecione el campo: ");
                    for (int x = 1; x < staticL.getFirst().size(); x++) {
                        System.out.println((x) + ". " + staticL.getFirst().get(x));
                    }
                    System.out.print(">> ");
                    campo = sc.nextInt();
                    sc.nextLine();
                    if (campo >= 0 && campo < tempL.getFirst().size()) {
                        float sumaTotal = 0f;
                        for (int y = 1; y < staticL.size (); y++) {
                            try {
                                Integer.parseInt (staticL.get (y).getFirst ());
                                sumaTotal += Float.parseFloat (staticL.get (y).get (campo));
                                contador++;
                            } catch (Exception ignored) {
                            }
                        }
                        if (contador > 0) {
                            if (buscarId ("Media", staticL) == - 1) {
                                newEntity.add ("Media");
                                for (int x = 1; x < staticL.getFirst ().size (); x++) {
                                    newEntity.add ("");
                                }
                                newEntity.set (campo, String.valueOf (sumaTotal / contador));
                                staticL.add (newEntity);
                            } else {
                                staticL.get (buscarId ("Media", staticL)).set (campo, String.valueOf (sumaTotal / contador));
                            }
                            showInfo (staticL);
                        } else {
                            System.out.println ("\nNo se puede calcular la media");
                        }
                    } else {
                        System.out.println("Seleccione un campo existente.");
                    }
                    break;
                case 3:
                    newEntity = new ArrayList<>();
                    System.out.println("Selecione el campo: ");
                    for (int x = 1; x < staticL.getFirst().size(); x++) {
                        System.out.println((x) + ". " + staticL.getFirst().get(x));
                    }
                    System.out.print(">> ");
                    campo = sc.nextInt();
                    sc.nextLine();
                    if (campo >= 0 && campo < tempL.getFirst().size()) {
                        temp = - 1;
                        for (int x = 1; x < staticL.getFirst ().size (); x++) {
                            try {
                                Float.parseFloat (staticL.get (x).get (campo));
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
                            newEntity.add ("Maximo");
                            for (int x = 1; x < staticL.getFirst ().size (); x++) {
                                newEntity.add ("");
                            }

                            for (int x = 0; x < staticL.size (); x++) {
                                try {
                                    maxValue = Math.max (Float.parseFloat (staticL.get (x).get (campo)), maxValue);
                                } catch (Exception ignored) {
                                }
                            }
                            newEntity.set (campo, String.valueOf (maxValue));
                            staticL.add (newEntity);
                        } else {
                            for (int x = 0; x < staticL.size (); x++) {
                                try {
                                    maxValue = Math.max (Float.parseFloat (staticL.get (x).get (campo)), maxValue);
                                } catch (Exception ignored) {
                                }
                            }
                            staticL.get (buscarId ("Maximo", staticL)).set (campo, String.valueOf (maxValue));
                        }
                        showInfo (staticL);
                    } else {
                        System.out.println("Seleccione un campo existente.");
                    }
                    break;
                case 4:
                    newEntity = new ArrayList<>();
                    System.out.println("Selecione el campo: ");
                    for (int x = 1; x < staticL.getFirst().size(); x++) {
                        System.out.println((x) + ". " + staticL.getFirst().get(x));
                    }
                    System.out.print(">> ");
                    campo = sc.nextInt();
                    sc.nextLine();
                    if (campo >= 0 && campo < tempL.getFirst().size()) {
                        temp = - 1;
                        for (int x = 1; x < staticL.getFirst ().size (); x++) {
                            try {
                                Float.parseFloat (staticL.get (x).get (campo));
                                temp = x;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        if (temp == - 1) {
                            System.out.println ("No se puede calcular el minimo");
                            break;
                        }
                        float minValue = Float.parseFloat (staticL.get (temp).get (campo));
                        if (buscarId ("Minimo", staticL) == - 1) {
                            newEntity.add ("Minimo");
                            for (int x = 1; x < staticL.getFirst ().size (); x++) {
                                newEntity.add ("");
                            }
                            for (int x = 0; x < staticL.size (); x++) {
                                try {
                                    minValue = Math.min (Float.parseFloat (staticL.get (x).get (campo)), minValue);
                                } catch (Exception ignored) {
                                }
                            }
                            newEntity.set (campo, String.valueOf (minValue));
                            staticL.add (newEntity);
                        } else {
                            for (int x = 0; x < staticL.size (); x++) {
                                try {
                                    minValue = Math.min (Float.parseFloat (staticL.get (x).get (campo)), minValue);
                                } catch (Exception ignored) {
                                }
                            }
                            staticL.get (buscarId ("Minimo", staticL)).set (campo, String.valueOf (minValue));
                        }
                        showInfo (staticL);
                    } else {
                        System.out.println("Seleccione un campo existente.");
                    }
                    break;
                case 5:
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
                case 6:
                    System.out.println();
                    Utilidades.menu(true, new String[] {"Buscar por atributo", "Buscar por campo"});
                    opt = sc.nextInt();
                    sc.nextLine();
                    switch (opt) {
                        case 1:
                            System.out.println("Selecione el campo: ");
                            for (int x = 1; x < tempL.getFirst().size(); x++) {
                                System.out.println((x) + ". " + tempL.getFirst().get(x));
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
                            System.out.println("Selecione el campo: ");
                            for (int x = 1; x < tempL.getFirst().size(); x++) {
                                System.out.println((x) + ". " + tempL.getFirst().get(x));
                            }
                            System.out.print(">> ");
                            campo = sc.nextInt();
                            if (campo >= 0 && campo < tempL.getFirst().size()) {
                                String memoria = tempL.getFirst ().get (campo);
                                sc.nextLine ();
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
                case 7:
                    System.out.println();
                    System.out.println("Seleccione el campo de referencia:");
                    for (int x = 1; x < tempL.getFirst().size(); x++) {
                        System.out.println((x) + ". " + tempL.getFirst().get(x));
                    }
                    System.out.print(">> ");
                    campo = sc.nextInt();
                    sc.nextLine();
                    if (campo >= 0 && campo < tempL.getFirst().size()) {  // Verifica que el campo esté en el rango
                        ordenar(tempL, tempL.getFirst().get(campo));
                        tempL.getFirst().set(campo, tempL.getFirst().get(campo) + "△▽");
                        showInfo(tempL);
                    } else {
                        System.out.println("Seleccione un campo existente.");
                    }
                    break;
                case 8:
                    ArrayList<String> tempArray = new ArrayList<>();
                    int campo2;
                    System.out.println();
                    System.out.println("Seleccione el campo:");
                    for (int x = 1; x < l.getFirst().size(); x++) {
                        System.out.println((x) + ". " + l.getFirst().get(x));
                    }
                    System.out.print(">> ");
                    campo = sc.nextInt();
                    sc.nextLine();
                    if (campo >= 1 && campo < l.getFirst().size()) {  // Verifica que el campo esté en el rango
                        System.out.println("Seleccione el campo por el que intercambiar");
                        System.out.print(">> ");
                        campo2 = sc.nextInt();
                        sc.nextLine();
                        if (campo2 >= 1 && campo2 < l.getFirst().size()) {
                            if (campo != campo2) {
                                for (int x = 0; x < l.size(); x++) {
                                    tempArray.add(l.get(x).get(campo));
                                    l.get(x).set(campo, l.get(x).get(campo2));
                                    l.get(x).set(campo2, tempArray.get(x));
                                }
                                showInfo(l);
                                staticL = Utilidades.aaListCopy(l);
                            } else {
                                System.out.println("Seleccione dos campos diferentes.");
                            }
                        } else {
                            System.out.println("Seleccione un campo existente.");
                        }
                    } else {
                        System.out.println("Seleccione un campo existente.");
                    }
                    break;
                case 9:
                    System.out.println();
                    Utilidades.menu(true, new String[]{"Seleccionar por entidad", "Seleccionar por campo", "Seleccionar toda la lista"});
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
                        System.out.println ();
                        System.out.println ("Seleccione el campo:");
                        for (int x = 1; x < l.getFirst ().size (); x++) {
                            System.out.println ((x) + ". " + l.getFirst ().get (x));
                        }
                        System.out.print (">> ");
                        campo = sc.nextInt ();
                        sc.nextLine ();
                        if (campo < 1 || campo >= l.getFirst ().size ()) {
                            System.out.println ("Seleccione un campo existente.");
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
                            l.get(entity).set(x, Utilidades.mayus(l.get(entity).get(x), mode2));
                        } else if (mode == 2) {
                            l.get(x).set(campo, Utilidades.mayus(l.get(x).get(campo), mode2));
                        } else {
                            for (int y = 1; y < l.get(x).size(); y++) {
                                l.get(x).set(y, Utilidades.mayus(l.get(x).get(y), mode2));
                            }
                        }
                    }
                    showInfo(l);
                    staticL = Utilidades.aaListCopy(l);
                    break;
                case 10:
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

    private static void ordenar(ArrayList<ArrayList<String>> l,String sCampo) {
        int campo = buscarEntHor(0, sCampo.toUpperCase(), l).getFirst ();
        for (int x = 1; x < l.size(); x++) {
            String [] a = new String[]{l.get(x-1).get(campo),l.get(x).get(campo)};
            String [] b = a.clone();
            Arrays.sort(b);
            try {
                if (Integer.parseInt(l.get(x).get(campo)) < Integer.parseInt(l.get(x - 1).get(campo))) {
                    ArrayList<String> temp = l.get(x);
                    l.set(x, l.get(x - 1));
                    l.set(x - 1, temp);
                    x = 0;
                }
            } catch (NumberFormatException e) {
                if (campo == 0 && l.get(x).get(campo).charAt(l.get(x).get(campo).length() - 1) == 'c' && !l.get(x - 1).get(campo).equals(l.getFirst().get(campo))) {
                    ArrayList<String> temp = l.get(x);
                    l.set(x, l.get(x - 1));
                    l.set(x - 1, temp);
                    x = 0;
                } else if (!Arrays.equals(a,b) && x >= 2 && !Utilidades.isNumber (l.get (x).get (campo), l.get (x - 1).get (campo))) {
                    ArrayList<String> temp = l.get(x);
                    l.set(x, l.get(x - 1));
                    l.set(x - 1, temp);
                    x = 1;
                }
            }
        }
    }

    private static int buscarId(String n, ArrayList<ArrayList<String>> l) {
        int id = -1;
        for (int x = 0; x < l.size(); x++) {
            try {
                if (Integer.parseInt(l.get(x).getFirst()) == Integer.parseInt(n)) {
                    id = x;
                    break;
                }
            } catch (NumberFormatException e) {
                if (l.get(x).getFirst().equals(n)) {
                    id = x;
                    break;
                }
            }
        }
        return id;
    }

    private static ArrayList<Integer> buscarEntVert(int campo, String n, ArrayList<ArrayList<String>> l) {
        ArrayList<Integer> id = new ArrayList<>(0);
        for (int x = 0; x < l.size(); x++) {
            try {
                if (Integer.parseInt(l.get(x).get(campo)) == Integer.parseInt(n)) {
                    id.add(x);
                    break;
                }
            } catch (NumberFormatException e) {
                if (l.get(x).get(campo).toUpperCase().equals(n)) {
                    id.add(x);
                }
            }
        }
        return id;
    }

    private static ArrayList<Integer> buscarEntHor(int ent, String n, ArrayList<ArrayList<String>> l) {
        ArrayList<Integer> id = new ArrayList<>(0);
        for (int x = 0; x < l.get(ent).size(); x++) {
            try {
                if (Integer.parseInt(l.get(ent).get(x)) == Integer.parseInt(n)) {
                    id.add(x);
                    break;
                }
            } catch (NumberFormatException e) {
                if (l.get(ent).get(x).toUpperCase().equals(n)) {
                    id.add(x);
                }
            }
        }
        return id;
    }

    private static void dupe(ArrayList<ArrayList<String>> l) {
        int id = -1;
        do {
            for (int x = 1; x < l.size(); x++) {
                try {
                    Integer.parseInt(l.get(x).getFirst());
                    id = -1;
                } catch (NumberFormatException e) {
                    id = x;
                    break;
                }
            }
            if (id == -1) {
                break;
            }
            l.get(id).set(0, l.get(id).getFirst().substring(0, l.get(id).getFirst().length() - 1));
            for (int x = 1; x < l.size(); x++) {
                for (int y = 1; y < l.size(); y++) {
                    try {
                        if (Integer.parseInt(l.get(x).getFirst()) == Integer.parseInt(l.get(y).getFirst())) {
                            if (x == y) {
                                break;
                            }
                            if (x == id) {
                                l.get(y).set(0, String.valueOf(Integer.parseInt(l.getLast().getFirst()) + 1));
                            } else if (y == id) {
                                if (l.getLast().getFirst().equals(l.get(id).getFirst())) {
                                    l.get(x).set(0, String.valueOf(Integer.parseInt(l.get(l.size()-2).getFirst()) + 1));
                                } else {
                                    l.get(x).set(0, String.valueOf(Integer.parseInt(l.getLast().getFirst()) + 1));
                                }
                            }
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        if (e instanceof IndexOutOfBoundsException) {
                            x = y = l.size() - 1;
                        }
                    }
                }
            }
        } while (true);
    }

    private static String defDir(int n) throws FileNotFoundException {
        String d = null;
        if (n == 1) {
            try {
                d = chooseDir();
                PrintStream ps = new PrintStream(new FileOutputStream("default.dat"));
                ps.println(d);
                System.out.println("\nDatos guardados");
            } catch (FileNotFoundException | NullPointerException e) {
                System.out.println("Error: " + e);
                return null;
            }
        } else if (n == 0) {

            try {
                BufferedReader br = new BufferedReader(new FileReader("default.dat"));
                d = br.readLine();
            } catch (NullPointerException | IOException ignored) {
            }
        } else if (n == -1) {
            PrintStream ps = new PrintStream(new FileOutputStream("default.dat"));
            ps.println((String) null);
        }
        return d;
    }
}