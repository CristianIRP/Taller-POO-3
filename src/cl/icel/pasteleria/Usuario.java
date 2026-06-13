package cl.icel.pasteleria;

public class Usuario {
    private String idUsuario;
    private String nombre;
    private String RUT;
    private String correo;
    private String semestre;
    private String rol;
    private String clave;

    public Usuario(String idUsuario, String nombre, String RUT, String correo, String semestre, String rol, String clave) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.RUT = RUT;
        this.correo = correo;
        this.semestre = semestre;
        this.rol = rol;
        this.clave = clave;
    }



    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRUT() {
        return RUT;
    }

    public String getCorreo() {
        return correo;
    }

    public String getSemestre() {
        return semestre;
    }

    public String getRol() {
        return rol;
    }

    public String getClave() {
        return clave;
    }
}
