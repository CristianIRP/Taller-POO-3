package cl.icel.pasteleria;
import java.util.Collection;
import java.util.Map;
public interface SistemaPasteleria {

    //cargamos los archivos
    boolean cargarUsuarios(String rutaArchivo);
    boolean cargarPasteles(String rutaArchivo);

    // iniciar sesion y registro
    Usuario iniciarSesion(String rut, String clave);
    boolean agregarUsuario(Usuario usuario);

    // gestion de pasteles
    void registrarPastel(Pastel pastel);
    void verificarEliminacionAutomatica();

    //ventas y el historial
    Boleta generarBoleta(Usuario vendedor, Map<String, Integer> productosAVender);
    Collection<Boleta> obtenerHistorialVentas();
}
