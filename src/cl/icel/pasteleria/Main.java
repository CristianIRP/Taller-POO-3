package cl.icel.pasteleria;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static SistemaPasteleria sistema = new SistemaPasteleriaImpl();
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioLogueado = null;

    public static void main(String[] args) {
        // 1. CARGA AUTOMÁTICA DE ARCHIVOS AL INICIAR
        System.out.println("=== Inicializando Sistema de Pastelería ===");
        boolean cargaU = sistema.cargarUsuarios("registros.csv");
        boolean cargaP = sistema.cargarPasteles("tortas.csv");

        if (cargaU && cargaP) {
            System.out.println("Datos cargados correctamente en memoria.\n");
        } else {
            System.out.println("Advertencia: Hubo problemas al cargar algunos archivos locales.\n");
        }

        // 2. MENÚ PRINCIPAL (AUTENTICACIÓN)
        int opcion = 0;
        do {
            System.out.println("====== PASTELERÍA ICEL ======");
            System.out.println("1. Iniciar Sesión");
            System.out.println("2. Registrarse (Nuevos Alumnos)");
            System.out.println("3. Salir del Programa");
            System.out.print("Seleccione una opción: ");

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
                        System.out.println("Cerrando el sistema. ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente nuevamente.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.\n");
            }
        } while (opcion != 3);
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
            System.out.println("\n¡Bienvenido/a " + usuarioLogueado.getNombre() + "!");
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
        System.out.print("Ingrese ID único (ej: U020): ");
        String id = scanner.nextLine().trim();
        System.out.print("Ingrese Nombre Completo: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Ingrese RUT: ");
        String rut = scanner.nextLine().trim();

        // validacion de Correo Institucional
        String correo = "";
        while (true) {
            System.out.print("Ingrese Correo Institucional (@alumnos.ucn.cl): ");
            correo = scanner.nextLine().trim();
            if (correo.endsWith("@alumnos.ucn.cl")) {
                break;
            }
            System.out.println("Error: El correo debe pertenecer de forma obligatoria al dominio '@alumnos.ucn.cl'.");
        }

        // leemos los semestres y se leen en string
        System.out.print("Ingrese Semestre actual (ej: noveno, sexto, primero): ");
        String semestre = scanner.nextLine().trim();

        System.out.print("Cree su Clave de acceso: ");
        String clave = scanner.nextLine().trim();

        // el constructor ahora recibe el semestre como String de forma correcta
        Usuario nuevo = new Usuario(id, nombre, rut, correo, semestre, "Vendedor", clave);

        if (sistema.agregarUsuario(nuevo)) {
            System.out.println("¡Registro exitoso!\n");
        } else {
            System.out.println("Error: El ID de usuario ya se encuentra registrado en el sistema.\n");
        }
    }

    // ========================================================
    // MENÚS OPERATIVOS SEGÚN ROL
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
            System.out.println("1. Realizar Venta (Generar Boleta)");
            System.out.println("2. Ver Resumen del Historial de Ventas");
            System.out.println("3. Ver Detalle de una Boleta Específica (por UUID)");
            System.out.println("4. Editar Informacion de un Pastel");
            System.out.println("5. Cerrar Sesion");
            System.out.print("Seleccione: ");
            try {
                op = Integer.parseInt(scanner.nextLine());
                switch (op) {
                    case 1:
                        ejecutarFlujoVenta();
                        break;
                    case 2:
                        visualizarHistorial();
                        break;
                    case 3:
                        buscarDetalleBoleta(); // <-- NUEVO
                        break;
                    case 4:
                        menuEditarPastel();    // <-- NUEVO
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
            }
        } while (op != 5); // Cambiado a 5 para salir
        usuarioLogueado = null;
        System.out.println("Sesión cerrada.\n");
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
            System.out.println("Error: No se encontró ninguna boleta con el UUID ingresado.\n");
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
                System.out.println("Cantidad no válida.");
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
            System.out.println("Venta cancelada (carrito vacío).\n");
        }
    }

    private static void visualizarHistorial() {
        System.out.println("\n--- HISTORIAL GLOBAL DE VENTAS ---");
        if (sistema.obtenerHistorialVentas().isEmpty()) {
            System.out.println("No se han registrado ventas.\n");
            return;
        }
        for (Boleta b : sistema.obtenerHistorialVentas()) {
            System.out.println("[" + b.getUUID() + "] - Vendedor: " + b.getNombreVendedor() + " - Total: $" + b.getTotalVenta());
        }
        System.out.println();
    }

    private static void menuEditarPastel() {
        System.out.println("\n--- EDITAR INFORMACION DEL PASTEL ---");
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