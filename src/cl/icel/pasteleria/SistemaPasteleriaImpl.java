package cl.icel.pasteleria;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class SistemaPasteleriaImpl implements SistemaPasteleria {
    private Map<String, Usuario> usuarios;
    private Map<String, Boleta> historialBoletas;
    private Map<String, Pastel> pasteles;

    @Override
    public boolean cargarUsuarios(String rutaArchivo) {
        // TODO: Leer usuarios
        return false;
    }

    @Override
    public boolean cargarPasteles(String rutaArchivo) {
        //TODO: Leer pasteles
        return false;
    }

    @Override
    public Usuario iniciarSesion(String rut, String clave) {
        // recorremos los usuarios y verificamos sus credenciales
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getRUT().equals(rut) && usuario.getClave().equals(clave)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public boolean agregarUsuario(Usuario usuario) {
        // validamos que no este repetido por id
        if (usuarios.containsKey(usuario.getIdUsuario())) {
            return false;
        }
        usuarios.put(usuario.getIdUsuario(), usuario);
        return true;
    }

    @Override
    public void registrarPastel(Pastel pastel) {
        //agregamos pastel al catalogo
        pasteles.put(pastel.getIdPastel(), pastel);
        // cada vez que actualizamos la info, se activa eliminacionAutomatica
        // para la cuenta regresiva de 3 dias.
        verificarEliminacionAutomatica();
    }

    @Override
    public void verificarEliminacionAutomatica() {
        // elimina pasteles con stock 0 o que pasaron su fecha de caducidad
        pasteles.values().removeIf(pastel->
                pastel.getStockPastel() == 0 || pastel.getFechaCaducidadPastel().isBefore(LocalDate.now())
                );

    }

    @Override
    public Boleta generarBoleta(Usuario vendedor, Map<String, Integer> productosAVender) {
        // TODO: Implementar la logica de bajar stock y calculo de los totales
        return null;
    }

    @Override
    public Collection<Boleta> obtenerHistorialVentas() {
        return historialBoletas.values();
    }
}
