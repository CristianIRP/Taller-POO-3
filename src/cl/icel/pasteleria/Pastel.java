package cl.icel.pasteleria;
import java.time.LocalDate;
public class Pastel {
    private String idPastel;
    private String nombrePastel;
    private int porcionesPastel;
    private int precioPastel;
    private int stockPastel;
    private LocalDate fechaCaducidadPastel;
    /*
     * Constructor del objeto pastel
     * @param idPastel: Id correspondiente al pastel
     * @param nombrePastel: Nombre del pastel
     * @param porcionesPastel: Porciones del pastel
     * @param precioPastel: Precio del pastel
     * @param stockPastel: Stock actual del pastel
     * @param fechaCaducidadPastel: Fecha en la que el pastel vence y debe ser eliminado del stock
     */
    public Pastel(String idPastel, String nombrePastel, int porcionesPastel, int precioPastel, int stockPastel, LocalDate fechaCaducidadPastel) {
        this.idPastel = idPastel;
        this.nombrePastel = nombrePastel;
        this.porcionesPastel = porcionesPastel;
        this.precioPastel = precioPastel;
        this.stockPastel = stockPastel;
        this.fechaCaducidadPastel = fechaCaducidadPastel;
    }

    public String getIdPastel() {
        return idPastel;
    }

    public String getNombrePastel() {
        return nombrePastel;
    }

    public int getPorcionesPastel() {
        return porcionesPastel;
    }

    public int getPrecioPastel() {
        return precioPastel;
    }

    public int getStockPastel() {
        return stockPastel;
    }

    public LocalDate getFechaCaducidadPastel() {
        return fechaCaducidadPastel;
    }
}

