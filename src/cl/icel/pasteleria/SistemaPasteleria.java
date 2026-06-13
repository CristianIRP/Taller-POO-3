package cl.icel.pasteleria;
import java.util.Collection;
import java.util.Map;
public interface SistemaPasteleria {

    //cargamos los archivos
    boolean cargarUsuarios(String rutaArchivo);
    boolean cargarPasteles(String rutaArchivo);

    // iniciar sesion y registro
    // Cuando agregamos a un usuario, este se guarda en csv cuando cerramos el programa
    Usuario iniciarSesion(String rut, String clave);
    boolean agregarUsuario(Usuario usuario);

    // gestion de pasteless
    // cuando registramos los pasteles, se guardan al cerrar el programa
    void registrarPastel(Pastel pastel);
    void verificarEliminacionAutomatica();

    //ventas y el historial
    Boleta generarBoleta(Usuario vendedor, Map<String, Integer> productosAVender);
    Collection<Boleta> obtenerHistorialVentas();
    Pastel obtenerPastelPorId(String idPastel);
    Boleta obtenerBoletaPorUuid(String uuid);
    
    // datos en CSV (usuarios y pasteles)
    boolean guardarDatos(String rutaUsuarios, String rutaPasteles);
}
