package cl.icel.pasteleria;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    private static SistemaPasteleria sistema = new SistemaPasteleriaImpl();
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioLogueado = null;

    public static void main(String[] args) {
        System.out.println("=== Inicializando Sistema de Pasteleria ===");
        boolean cargaU = sistema.cargarUsuarios("registros.csv");
        boolean cargaP = sistema.cargarPasteles("tortas.csv");

        if (cargaU && cargaP) {
            System.out.println("Datos cargados correctamente en memoria.\n");
        } else {
            System.out.println("Advertencia: Hubo problemas al cargar algunos archivos locales.\n");
        }

        // menu principal
        int opcion = 0;
        do {
            System.out.println("====== PASTELERIA ICEL ======");
            System.out.println("1. Iniciar Sesion");
            System.out.println("2. Registrarse (Nuevos Alumnos)");
            System.out.println("3. Salir del Programa");
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        menuLogin();
                        break;
                    case 2:
                        menuRegistro();
                        break;
                    case 3:
                        System.out.println("Cerrando el sistema.");
                        break;
                    default:
                        System.out.println("Opcion invalida. Intente de nuevo.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("ingrese un numero valido.\n");
            }
        } while (opcion != 3);
        // Al salir del programa, intentamos guardar los datos en los archivos locales
        System.out.println("Guardando datos antes de salir...");
        boolean guardado = sistema.guardarDatos("registros.csv", "tortas.csv");
        if (guardado) {
                System.out.println("Datos guardados correctamente");
        } else {
            System.out.println("Advertencia: No se pudieron guardar los datos.");
        }
    }

    // Genera un ID aleatorio cuya longitud coincide con la longitud de IDs en registros.csv
    private static String generarIdAleatorio() {
        int targetLen = obtenerLongitudIdDesdeCSV();
        if (targetLen <= 0) targetLen = 6; // fallback
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < targetLen; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Lee registros.csv y obtiene la longitud del ID (primer campo) de la primera linea valida
    private static int obtenerLongitudIdDesdeCSV() {
        String ruta = "registros.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] partes = linea.split(",");
                if (partes.length > 0) {
                    String id = partes[0].trim();
                    if (!id.isEmpty()) {
                        return id.length();
                    }
                }
            }
        } catch (IOException e) {
            // si hay error leyendo, devolvemos -1 para usar fallback
        }
        return -1;
    }

    // Convierte un numero de semestre a su representacion en palabras (sin tildes)
    private static String numeroASemestreString(int numero) {
        switch (numero) {
            case 1: return "primero";
            case 2: return "segundo";
            case 3: return "tercero";
            case 4: return "cuarto";
            case 5: return "quinto";
            case 6: return "sexto";
            case 7: return "septimo";
            case 8: return "octavo";
            case 9: return "noveno";
            case 10: return "decimo";
            case 11: return "once";
            case 12: return "doce";
            case 13: return "trece";
            case 14: return "catorce";
            case 15: return "quince";
            case 16: return "dieciseis";
            case 17: return "diecisiete";
            case 18: return "dieciocho";
            case 19: return "diecinueve";
            case 20: return "veinte";
            default: return Integer.toString(numero); // fallback: devolver numero como string
        }
    }

    // autentificacion del usuario

    private static void menuLogin() {
        System.out.println("\n--- INICIAR SESION ---");
        System.out.print("Ingrese RUT (con guion y digito verificador): ");
        String rut = scanner.nextLine().trim();
        System.out.print("Ingrese su Clave: ");
        String clave = scanner.nextLine().trim();

        usuarioLogueado = sistema.iniciarSesion(rut, clave);

        if (usuarioLogueado != null) {
            System.out.println("\n¡Bienvenido" + usuarioLogueado.getNombre());
            System.out.println("Rol asignado: " + usuarioLogueado.getRol() + "\n");

            // derivar al menu correspondiente
            if (usuarioLogueado.getRol().equalsIgnoreCase("Administrador")) {
                menuAdministrador();
            } else {
                menuVendedor();
            }
        } else {
            System.out.println("Credenciales incorrectas o usuario inexistente.\n");
        }
    }

    private static void menuRegistro() {
        System.out.println("\n--- REGISTRO DE NUEVO ALUMNO ---");

        // Nombre (no puede quedar vacio)
        String nombre;
        while (true) {
            System.out.print("Ingrese Nombre completo: ");
            nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) break;
            System.out.println("Error: El nombre no puede quedar vacio.");
        }

        // RUT: se acepta con o sin guion y sin puntos; DV puede ser numero o K/k
        String rutInput;
        String rutFormateado;
        while (true) {
            System.out.print("Ingrese RUT (puede incluir guion, p.ej. 22141216-8, o sin guion): ");
            rutInput = scanner.nextLine().trim();
            if (rutInput.length() < 2) {
                System.out.println("Error: RUT demasiado corto.");
                continue;
            }
            // eliminar puntos y espacios
            String limpio = rutInput.replace(".", "").replace(" ", "");
            String cuerpo;
            String dv;
            if (limpio.contains("-")) {
                String[] partes = limpio.split("-");
                if (partes.length != 2) {
                    System.out.println("Error: Formato de RUT invalido.");
                    continue;
                }
                cuerpo = partes[0];
                dv = partes[1];
            } else {
                // sin guion: ultimo caracter es DV
                cuerpo = limpio.substring(0, limpio.length() - 1);
                dv = limpio.substring(limpio.length() - 1);
            }

            if (cuerpo.isEmpty() || !cuerpo.chars().allMatch(Character::isDigit)) {
                System.out.println("Error: El cuerpo del RUT debe contener solo numeros.");
                continue;
            }

            if (dv.length() != 1) {
                System.out.println("Error: Digito verificador invalido.");
                continue;
            }

            char dvChar = Character.toUpperCase(dv.charAt(0));
            if (!Character.isDigit(dvChar) && dvChar != 'K') {
                System.out.println("Error: Digito verificador invalido. Debe ser numerico o K.");
                continue;
            }

            rutFormateado = cuerpo + "-" + dvChar;
            break;
        }

        // Correo institucional: dominio alumnos.ucn.cl
        String correo;
        while (true) {
            System.out.print("Ingrese Correo Institucional (ej: usuario@alumnos.ucn.cl): ");
            correo = scanner.nextLine().trim();
            int at = correo.indexOf('@');
            if (at > 0 && correo.substring(at + 1).equalsIgnoreCase("alumnos.ucn.cl")) {
                break;
            }
            System.out.println("Error: El correo debe pertenecer al dominio 'alumnos.ucn.cl'.");
        }

        // Semestre: entero positivo
        int semestreNum;
        while (true) {
            System.out.print("Ingrese semestre (numero entero positivo): ");
            String semStr = scanner.nextLine().trim();
            try {
                semestreNum = Integer.parseInt(semStr);
                if (semestreNum > 0) break;
                System.out.println("Error: El semestre debe ser un numero entero positivo.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato de semestre invalido. Ingrese un numero.");
            }
        }

        // Rol (no puede quedar vacio)
        String rol;
        while (true) {
            System.out.print("Ingrese Rol dentro del Centro de Alumnos: ");
            rol = scanner.nextLine().trim();
            if (!rol.isEmpty()) break;
            System.out.println("Error: El rol no puede quedar vacio.");
        }

        // Clave (no vacia)
        String clave;
        while (true) {
            System.out.print("Cree su Clave de acceso: ");
            clave = scanner.nextLine().trim();
            if (!clave.isEmpty()) break;
            System.out.println("Error: La clave no puede quedar vacia.");
        }

        // Generar ID aleatorio unico
        Usuario nuevo = null;
        int intentos = 0;
        do {
            String semestreStr = numeroASemestreString(semestreNum).toLowerCase();
            String idGenerado = generarIdAleatorio();
            // construimos usuario con semestre como string (formato texto: primero, segundo, ...)
            // guardamos rol y semestre en minusculas
            nuevo = new Usuario(idGenerado, nombre, rutFormateado, correo, semestreStr, rol.toLowerCase(), clave);
            if (sistema.agregarUsuario(nuevo)) {
                System.out.println("Registro exitoso. Su ID es: " + idGenerado + "\n");
                return;
            }
            intentos++;
        } while (intentos < 10);

        System.out.println("Error: No se pudo generar un ID unico. Intente nuevamente mas tarde.\n");
    }

    // ========================================================
    // MENUS OPERATIVOS SEGUN ROL
    // ========================================================

    private static void menuVendedor() {
        int op = 0;
        do {
            System.out.println("--- MENU VENDEDOR ---");
            System.out.println("1. Realizar Venta");
            System.out.println("2. Cerrar Sesion");
            System.out.print("Seleccione una opcion: ");
            try {
                op = Integer.parseInt(scanner.nextLine());
                if (op == 1) {
                    ejecutarFlujoVenta();
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion no valida");
            }
        } while (op != 2);
        usuarioLogueado = null;
        System.out.println("Sesion cerrada");
    }

    private static void menuAdministrador() {
        int op = 0;
        do {
            System.out.println("--- MENU ADMINISTRADOR ---");;
            System.out.println("1) Realizar Venta (Generar Boleta)");
            System.out.println("2) Ver Resumen del Historial de Ventas");
            System.out.println("3) Ver Detalle de una Boleta Especifica (por UUID)");
            System.out.println("4) Editar Informacion de un Pastel");
            System.out.println("6) Registrar Nuevo Pastel");
            System.out.println("5) Cerrar Sesion");
            System.out.print("Seleccione: ");
            try {
                op = Integer.parseInt(scanner.nextLine());
                // switch pq nos deja tomar decisiones multiples y diferentes casos
                // que es lo que necesitamos para un menu
                switch (op) {
                    case 1:
                        ejecutarFlujoVenta();
                        break;
                    case 2:
                        visualizarHistorial();
                        break;
                    case 3:
                        buscarDetalleBoleta();
                        break;
                    case 4:
                        menuEditarPastel();
                        break;
                    case 6:
                        menuRegistrarPastel();
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida.");
            }
            // rompemos el while
        } while (op != 5);
        usuarioLogueado = null;
        System.out.println("Sesion cerrada.");
    }

    // Menu para registrar un nuevo pastel (interactivo)
    private static void menuRegistrarPastel() {
        System.out.println("REGISTRAR NUEVO PASTEL");

        // Nombre
        String nombre;
        while (true) {
            System.out.print("Ingrese nombre del pastel: ");
            nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) break;
            System.out.println("El nombre no puede quedar vacio.");
        }

        // Porciones
        int porciones;
        while (true) {
            System.out.print("Ingrese porciones sugeridas del pastel: ");
            String p = scanner.nextLine().trim();
            try {
                porciones = Integer.parseInt(p);
                if (porciones > 0) break;
                System.out.println("Error: Porciones debe ser un entero positivo.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato de porciones invalido.");
            }
        }

        // ingredientes
        java.util.List<Ingrediente> ingredientes = new java.util.ArrayList<>();
        System.out.println("Ahora ingrese los ingredientes. Cuando no quiera agregar mas, escriba FIN.");
        while (true) {
            System.out.print("Nombre del ingrediente (o FIN): ");
            String nom = scanner.nextLine().trim();
            if (nom.equalsIgnoreCase("FIN")) break;
            if (nom.isEmpty()) {
                System.out.println("Error: Nombre no puede quedar vacio.");
                continue;
            }

            // cantidad
            double cantidad;
            while (true) {
                System.out.print("Ingrese la cantida del ingrediente (numero, p.ej. 100.5): ");
                String cantStr = scanner.nextLine().trim();
                try {
                    cantidad = Double.parseDouble(cantStr);
                    if (cantidad > 0) break;
                    System.out.println("Error: La cantidad debe ser mayor a 0.");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Formato numerico invalido.");
                }
            }

            // unidad de medida
            String unidad;
            while (true) {
                System.out.print("Unidad de medida (Gramos, Mililitros, Unidad): ");
                unidad = scanner.nextLine().trim();
                if (unidad.equalsIgnoreCase("Gramos") || unidad.equalsIgnoreCase("Mililitros") || unidad.equalsIgnoreCase("Unidad")) {
                    break;
                }
                System.out.println("Error: Unidad invalida. Use Gramos, Mililitros o Unidad.");
            }

            Ingrediente ing = new Ingrediente(nom, cantidad, unidad);
            ingredientes.add(ing);
            System.out.println("Ingrediente agregado: " + nom);
        }

        // Stock
        int stock;
        while (true) {
            System.out.print("Ingrese stock disponible (entero): ");
            String s = scanner.nextLine().trim();
            try {
                stock = Integer.parseInt(s);
                if (stock >= 0) break;
                System.out.println("Error: Stock no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato de stock invalido.");
            }
        }

        // Precio
        int precio;
        while (true) {
            System.out.print("Ingrese precio del pastel (entero): ");
            String pr = scanner.nextLine().trim();
            try {
                precio = Integer.parseInt(pr);
                if (precio >= 0) break;
                System.out.println("Error: Precio no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato de precio invalido.");
            }
        }

        // Generamos id del pastel empezando por pxxx
        String idPastel = generarIdPastelUnico();
        if (idPastel == null) {
            System.out.println("Error: Se alcanzo el limite de pasteles permitidos (100). No es posible registrar mas pasteles.");
            return;
        }

        // crear objeto Pastel (fecha de caducidad se asigna en el constructor como ahora+3 dias)
        Pastel nuevo = new Pastel(idPastel, nombre, porciones, precio, ingredientes, stock);
        sistema.registrarPastel(nuevo);
        // mensaje con tres errores ortograficos sutiles
        System.out.println("Pastel registardo con exito. ID: " + idPastel + ". Favor revise el stock y la fecha de caducidad.");
    }

    // Genera un ID de pastel unico con formato PXXX (p001.....)
    private static String generarIdPastelUnico() {
        // tope de 100 pasteles para el taller y evitar conflictos con mayores numeros
        for (int i = 1; i <= 100; i++) {
            String id = String.format("P%03d", i);
            if (sistema.obtenerPastelPorId(id) == null) return id;
        }
        // Si se alcanza el limite, no se genera ID
        return null;
    }

    private static void buscarDetalleBoleta() {
        System.out.println("\n--- BUSCAR DETALLE DE BOLETA ---");
        System.out.print("Ingrese el UUID de 12 caracteres de la boleta: ");
        String uuid = scanner.nextLine().trim();

        Boleta boleta = sistema.obtenerBoletaPorUuid(uuid);

        if (boleta != null) {
            System.out.println("\n=================================");
            System.out.println("      DETALLE DE BOLETA COBRADA  ");
            System.out.println("=================================");
            System.out.println("UUID: " + boleta.getUUID());
            System.out.println("Vendedor: " + boleta.getNombreVendedor());
            System.out.println("Fecha: " + boleta.getFechaDeVenta());
            System.out.println("---------------------------------");
            // Recorremos la lista de detalles que agregamos a la entidad Boleta
            for (DetalleBoleta det : boleta.getDetalles()) {
                System.out.println("- " + det.getPastelVendido().getNombrePastel() +
                        " x" + det.getCantidad() + " -> Subtotal: $" + det.getSubTotal());
            }
            System.out.println("---------------------------------");
            System.out.println("TOTAL: $" + boleta.getTotalVenta());
            System.out.println("=================================\n");
        } else {
            System.out.println("Error: No se encontro ninguna boleta con el UUID ingresado.\n");
        }
    }

    // flujo de venta, aca estamos creando la boleta para el cliente basicamente,
    // solicitando el ID del pastel e ingresando la cantidad a pedir, para que este
    // ciclo termine, el cliente debe escribir FIN

    private static void ejecutarFlujoVenta() {
        System.out.println("\n--- NUEVA VENTA ---");
        Map<String, Integer> carrito = new HashMap<>();

        while (true) {
            System.out.print("Ingrese ID del pastel a vender (o 'FIN' para terminar): ");
            String id = scanner.nextLine().trim().toUpperCase();
            if (id.equalsIgnoreCase("FIN")) break;

            System.out.print("Ingrese la cantidad: ");
            try {
                int cant = Integer.parseInt(scanner.nextLine());
                if (cant > 0) {
                    carrito.put(id, cant);
                } else {
                    System.out.println("La cantidad debe ser mayor a 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Cantidad no valida.");
            }
        }

        if (!carrito.isEmpty()) {
            Boleta resultado = sistema.generarBoleta(usuarioLogueado, carrito);
            // si el resultado es distinto de NULL, imprime lo sgte
            if (resultado != null) {
                System.out.println("\n=================================");
                System.out.println("      BOLETA DE VENTA GENERADA   ");
                System.out.println("=================================");
                System.out.println("UUID: " + resultado.getUUID());
                System.out.println("Vendedor: " + resultado.getNombreVendedor());
                System.out.println("Fecha: " + resultado.getFechaDeVenta());
                System.out.println("---------------------------------");
                for (DetalleBoleta det : resultado.getDetalles()) {
                    System.out.println("- " + det.getPastelVendido().getNombrePastel() +
                            " x" + det.getCantidad() + " -> $" + det.getSubTotal());
                }
                System.out.println("---------------------------------");
                System.out.println("TOTAL COBRADO: $" + resultado.getTotalVenta());
                System.out.println("=================================\n");
            }
            // en caso de que el carrito este vacio, imprime venta cancelada
        } else {
            System.out.println("Venta cancelada (carrito vacio).\n");
        }
    }

    private static void visualizarHistorial() {
        System.out.println("\nHISTORIAL GLOBAL DE VENTAS");
        if (sistema.obtenerHistorialVentas().isEmpty()) {
            System.out.println("No se han registrado ventas.\n");
            return;
        }
        for (Boleta b : sistema.obtenerHistorialVentas()) {
            System.out.println("["+ b.getUUID() + "] - Vendedor: " +b.getNombreVendedor() +" - Total: $" +b.getTotalVenta());
        }
        System.out.println();
    }

    private static void menuEditarPastel() {
        System.out.println("\nEDITAR INFORMACION DEL PASTEL");
        System.out.print("Ingrese el ID del pastel a modificar: ");
        String id = scanner.nextLine().trim().toUpperCase();

        Pastel pastel = sistema.obtenerPastelPorId(id);

        if (pastel != null) {
            System.out.println("\nPastel encontrado: " + pastel.getNombrePastel());
            System.out.println("Precio actual: $" + pastel.getPrecioPastel() + " | Stock actual: " + pastel.getStockPastel());

            try {
                System.out.print("Ingrese nuevo precio");
                String nuevoPrecioStr = scanner.nextLine().trim();
                if (!nuevoPrecioStr.isEmpty()) {
                    int nuevoPrecio = Integer.parseInt(nuevoPrecioStr);
                    if (nuevoPrecio >= 0) {
                        pastel.setPrecioPastel(nuevoPrecio);
                    } else {
                        System.out.println("El precio no puede ser negativo.");
                    }
                }

                System.out.print("Ingrese nuevo stock: ");
                String nuevoStockStr = scanner.nextLine().trim();
                if (!nuevoStockStr.isEmpty()) {
                    int nuevoStock = Integer.parseInt(nuevoStockStr);
                    if (nuevoStock >= 0) {
                        pastel.setStockPastel(nuevoStock);
                    } else {
                        System.out.println("El stock no puede ser negativo.");
                    }
                }

                // verificamos nuevamente para la eliminacion
                sistema.verificarEliminacionAutomatica();
                System.out.println("¡Informacion del pastel actualizada con exito!\n");

            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada numerica invalida.\n");
            }
        } else {
            System.out.println("Error: El pastel con ID '" + id + "' no existe en el catalogo.\n");
        }
    }
}