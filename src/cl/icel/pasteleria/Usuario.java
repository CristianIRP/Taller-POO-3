package cl.icel.pasteleria;

public class Usuario {
    private String nombre;
    private String RUT;
    private String correo;
    private String semestre;
    private String rol;
    private String clave;

    public Usuario(String nombre, String RUT, String correo, String semestre, String rol, String clave) {
        this.nombre = nombre;
        this.RUT = RUT;
        this.correo = correo;
        this.semestre = semestre;
        this.rol = rol;
        this.clave = clave;
    }

    public String getNombre() {
        this.nombre = nombre;
        if (nombre != null){
        System.out.println("¡Usuario: " + nombre + "registrado con exito!");
    } else {
            System.out.println("Usuario invalido.");
            return null;
        }
        return nombre;
    }

    public String getRUT() {
        this.RUT = RUT;
        if (RUT == null){
            System.out.println("El rut ingresado no es valido.");
        }else {return RUT;}
        return RUT;
    }

    public String getCorreo() {
        this.correo = correo;
        if (correo != null){
            System.out.println("El correo ingresado es valido.");
        } else {
            System.out.println("El correo ingresado no es valido.");
            return null;}
        return correo;
    }

    public String getSemestre() {
        return semestre;
    }

    public String getRol() {
        return rol;
    }

    public String getClave() {
        this.clave = clave;
        if (clave != null){
            System.out.println("La clave ingresada es valida");
        } else {
            System.out.println("La clave ingresada no es valida");
            return null;

        }
        return clave;
    }
}
