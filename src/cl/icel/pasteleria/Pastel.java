package cl.icel.pasteleria;

import java.time.LocalDate;
import java.util.List;

public class Pastel {
    private String idPastel;
    private String nombrePastel;
    private int porcionesPastel;
    private int precioPastel;
    private int stockPastel;
    // Cambiado de String a List<Ingrediente> para cumplir con la composicion orientada a objetos
    private List<Ingrediente> listaIngredientes;
    private LocalDate fechaCaducidadPastel;

    /**
     * Constructor del objeto pastel
     * @param idPastel Id correspondiente al pastel
     * @param nombrePastel Nombre del pastel
     * @param porcionesPastel Porciones del pastel
     * @param precioPastel Precio del pastel
     * @param listaIngredientes Lista con los objetos Ingrediente asociados
     * @param stockPastel Stock inicial del pastel
     */
    public Pastel(String idPastel, String nombrePastel, int porcionesPastel, int precioPastel, List<Ingrediente> listaIngredientes, int stockPastel) {
        this.idPastel = idPastel;
        this.nombrePastel = nombrePastel;
        this.porcionesPastel = porcionesPastel;
        this.precioPastel = precioPastel;
        this.listaIngredientes = listaIngredientes;
        this.stockPastel = stockPastel;
        this.fechaCaducidadPastel = LocalDate.now().plusDays(3);
    }


    // metodos getters necesarios


    public List<Ingrediente> getListaIngredientes() {
        return listaIngredientes;
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

    // setter para actualizar stock

    public void setStockPastel(int stockPastel) {
        this.stockPastel = stockPastel;
    }

    public void setPrecioPastel(int nuevoPrecio) {
        this.precioPastel = nuevoPrecio;
    }
}
