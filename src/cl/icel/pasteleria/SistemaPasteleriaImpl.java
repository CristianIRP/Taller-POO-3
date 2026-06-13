package cl.icel.pasteleria;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.io.IOException;

public class SistemaPasteleriaImpl implements SistemaPasteleria {
    private Map<String, Usuario> usuarios;
    private Map<String, Boleta> historialBoletas;
    private Map<String, Pastel> pasteles;
    private Map<String, Integer> porcionesDisponibles = new HashMap<>();

    public SistemaPasteleriaImpl() {
        this.usuarios = new HashMap<>();
        this.historialBoletas = new HashMap<>();
        this.pasteles = new HashMap<>();

    }

    @Override
    public boolean cargarUsuarios(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 7) {
                    String idUsuario = datos[0].trim();
                    String nombreUsuario = datos[1].trim();
                    String rut = datos[2].trim();
                    String correo = datos[3].trim();
                    String semestre = datos[4].trim(); // Cargado como String para aceptar palabras
                    String rol = datos[5].trim();
                    String clave = datos[6].trim();

                    if (!this.usuarios.containsKey(idUsuario)) {
                        Usuario nuevoUsuario = new Usuario(idUsuario, nombreUsuario, rut, correo, semestre, rol, clave);
                        this.usuarios.put(idUsuario, nuevoUsuario);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al cargar usuarios");
            return false;
        }
    }
    // cargamos los pasteles desde tortas.csv
    @Override
    public boolean cargarPasteles(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                // ya que solicitamos 6 datos para los pasteles (creacion), colocamos un ciclo if para cerciorarnos
                // de que tienen 6 datos separados por comas.
                // Aceptamos lineas con 6 campos (incluye ingredientes) o 5 campos (sin ingredientes)
                if (datos.length == 6 || datos.length == 5) {
                    String id = datos[0].trim();
                    String nombre = datos[1].trim();
                    int porciones = Integer.parseInt(datos[2].trim());
                    int precio = Integer.parseInt(datos[3].trim());
                    String stringIngredientes = "";
                    int stock;
                    if (datos.length == 6) {
                        stringIngredientes = datos[4].trim();
                        stock = Integer.parseInt(datos[5].trim());
                    } else {
                        // formato sin campo de ingredientes: id,nombre,porciones,precio,stock
                        stock = Integer.parseInt(datos[4].trim());
                    }

                    List<Ingrediente> listaIngredientes = new ArrayList<>();
                    String[] ingredientesIndividuales = stringIngredientes.isEmpty() ? new String[0] : stringIngredientes.split(" ");

                    for (String ingStr : ingredientesIndividuales) {
                        String[] detalleIng = ingStr.split(":");
                        if (detalleIng.length == 3) {
                            String nomIng = detalleIng[0].trim();
                            double cantIng = Double.parseDouble(detalleIng[1].trim());
                            String uniIng = detalleIng[2].trim();

                            Ingrediente ingrediente = new Ingrediente(nomIng, cantIng, uniIng);
                            listaIngredientes.add(ingrediente);
                        }
                    }

                    if (!this.pasteles.containsKey(id)) {
                        Pastel nuevoPastel = new Pastel(id, nombre, porciones, precio, listaIngredientes, stock);
                        this.pasteles.put(nuevoPastel.getIdPastel(), nuevoPastel);
                    }
                }
            }
            verificarEliminacionAutomatica();
            return true;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar el archivo de pasteles");
            return false;
        }
    }

    @Override
    public Usuario iniciarSesion(String rut, String clave) {
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getRUT().equals(rut) && usuario.getClave().equals(clave)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getIdUsuario())) {
            return false;
        }
        usuarios.put(usuario.getIdUsuario(), usuario);
        return true;
    }

    @Override
    public void registrarPastel(Pastel pastel) {
        pasteles.put(pastel.getIdPastel(), pastel);
        verificarEliminacionAutomatica();
    }

    @Override
    public void verificarEliminacionAutomatica() {
        this.pasteles.values().removeIf(pastel ->
                pastel.getStockPastel() == 0 || pastel.getFechaCaducidadPastel().isBefore(java.time.LocalDate.now())
        );
    }

    @Override
    public Boleta generarBoleta(Usuario vendedor, Map<String, Integer> productosAVender) {
        if (productosAVender == null || productosAVender.isEmpty()) {
            System.out.println("No hay productos seleccionados para la venta");
            return null;
        }

        List<DetalleBoleta> listaDetalles = new ArrayList<>();
        int totalBoleta = 0;

        // primero validamos stock y vigencia de todo
        for (Map.Entry<String, Integer> entrada : productosAVender.entrySet()) {
            String idPastel = entrada.getKey().toUpperCase().trim();
            int cantidadSolicitada = entrada.getValue();

            boolean esPorcion = idPastel.endsWith("-P");
            String idPastelRealizado = esPorcion ? idPastel.replace("-P", "") : idPastel;

            if (!this.pasteles.containsKey(idPastelRealizado)) {
                System.out.println("El pastel con ID " + idPastelRealizado + " no existe en el catalogo");
                return null;
            }

            Pastel pastel = this.pasteles.get(idPastelRealizado);

            if (pastel.getFechaCaducidadPastel().isBefore(LocalDate.now())) {
                System.out.println("El pastel '" + pastel.getNombrePastel() + "' esta vencido. No se puede vender.");
                verificarEliminacionAutomatica();
                return null;
            }

            if (esPorcion) {
                // calculo de porciones totales combinando vitrina y tortas enteras
                int porcionesActuales = porcionesDisponibles.getOrDefault(idPastelRealizado, 0);
                int porcionesMaximasPosibles = porcionesActuales + (pastel.getStockPastel() * pastel.getPorcionesPastel());
                if (porcionesMaximasPosibles < cantidadSolicitada) {
                    System.out.println("No hay suficientes porciones disponibles para '" + pastel.getNombrePastel() + "'");
                    return null;
                }
            } else {
                // validacion normal para pasteles completos
                if (pastel.getStockPastel() < cantidadSolicitada) {
                    System.out.println("Stock insuficiente para '" + pastel.getNombrePastel() + "'. Disponible: " + pastel.getStockPastel() + ", Solicitado: " + cantidadSolicitada);
                    return null;
                }
            }
        }

        // segundo procesamos la venta aplicando los descuentos reales
        for (Map.Entry<String, Integer> entrada : productosAVender.entrySet()) {
            String idPastel = entrada.getKey().toUpperCase().trim();
            int cantidad = entrada.getValue();

            boolean esPorcion = idPastel.endsWith("-P");
            String idPastelRealizado = esPorcion ? idPastel.replace("-P", "") : idPastel;
            Pastel pastel = this.pasteles.get(idPastelRealizado);

            int precioUnitario = 0;
            String nombreItem = "";

            if (esPorcion) {
                nombreItem = "Porcion de " + pastel.getNombrePastel();
                precioUnitario = pastel.getPrecioPastel() / pastel.getPorcionesPastel();

                int porcionesEnVitrina = porcionesDisponibles.getOrDefault(idPastelRealizado, 0);

                // si faltan porciones cortadas, abrimos pasteles completos del stock
                while (porcionesEnVitrina < cantidad) {
                    pastel.setStockPastel(pastel.getStockPastel() - 1);
                    porcionesEnVitrina += pastel.getPorcionesPastel();
                }

                // guardamos el remanente de porciones en la vitrina
                porcionesDisponibles.put(idPastelRealizado, porcionesEnVitrina - cantidad);
            } else {
                nombreItem = pastel.getNombrePastel() + " (Completo)";
                precioUnitario = pastel.getPrecioPastel();
                pastel.setStockPastel(pastel.getStockPastel() - cantidad);
            }

            int subtotal = precioUnitario * cantidad;
            totalBoleta += subtotal;

            // clonamos temporalmente el objeto para el desglose de la boleta
            Pastel itemDetalle = new Pastel(idPastel, nombreItem, pastel.getPorcionesPastel(), precioUnitario, pastel.getListaIngredientes(), cantidad);
            DetalleBoleta detalle = new DetalleBoleta(itemDetalle, cantidad, subtotal);
            listaDetalles.add(detalle);
        }

        String uuidCorto = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        Boleta nuevaBoleta = new Boleta(uuidCorto, vendedor.getNombre(), LocalDate.now(), listaDetalles, totalBoleta);

        this.historialBoletas.put(uuidCorto, nuevaBoleta);
        verificarEliminacionAutomatica();

        return nuevaBoleta;
    }

    @Override
    public Collection<Boleta> obtenerHistorialVentas() {
        return this.historialBoletas.values();
    }

    @Override
    public Pastel obtenerPastelPorId(String idPastel) {
        return this.pasteles.get(idPastel);
    }

    @Override
    public Boleta obtenerBoletaPorUuid(String uuid) {
        return this.historialBoletas.get(uuid.toUpperCase().trim());
    }
    @Override
    public boolean guardarDatos(String rutaUsuarios, String rutaPasteles) {
        // 1. Guardar usuarios actualizados
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(rutaUsuarios))) {
            for (Usuario u : usuarios.values()) {
                String semestre = u.getSemestre() != null ? u.getSemestre().toLowerCase() : "";
                String rol = u.getRol() != null ? u.getRol().toLowerCase() : "";
                pw.println(u.getIdUsuario()+","+ u.getNombre() + ","+u.getRUT() +"," +
                        u.getCorreo()+","+ semestre + "," + rol + ","+u.getClave());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios");
            return false;
        }

        // Guardar pasteles actualizados
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(rutaPasteles))) {
            for (Pastel p : pasteles.values()) {
                StringBuilder ingStr = new StringBuilder();
                for (Ingrediente ing : p.getListaIngredientes()) {
                    ingStr.append(ing.getNombreIngrediente()).append(":")
                            .append(ing.getCantidadIngrediente()).append(":")
                            .append(ing.getUnidadMedidaIngrediente()).append(" ");
                }
                // Si no hay ingredientes, escribimos una linea con 5 campos (sin campo de ingredientes)
                if (ingStr.toString().trim().isEmpty()) {
                    pw.println(p.getIdPastel() + "," + p.getNombrePastel() + "," + p.getPorcionesPastel() + "," +
                            p.getPrecioPastel() + "," + p.getStockPastel());
                } else {
                    pw.println(p.getIdPastel() + "," + p.getNombrePastel() + "," + p.getPorcionesPastel() + "," +
                            p.getPrecioPastel() + "," + ingStr.toString().trim() + "," + p.getStockPastel());
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar pasteles");
            return false;
        }
    }
}