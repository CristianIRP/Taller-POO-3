package cl.icel.pasteleria;

public class Ingrediente {
    private String nombreIngrediente;
    private double cantidadIngrediente;
    private String unidadMedidaIngrediente;
    /* Constructor de los objetos
     * @param nombreIngrediente: nombre del ingrediente
     * @param cantidadIngrediente: cantidad del ingrediente
     * @param unidadMedidaIngrediente unidad de medida de los ingredientes
     */
    public Ingrediente(String nombreIngrediente, double cantidadIngrediente, String unidadMedidaIngrediente) {
        this.nombreIngrediente = nombreIngrediente;
        this.cantidadIngrediente = cantidadIngrediente;
        this.unidadMedidaIngrediente = unidadMedidaIngrediente;
    }

    public String getNombreIngrediente() {
        return nombreIngrediente;
    }

    public double getCantidadIngrediente() {
        return cantidadIngrediente;
    }

    public String getUnidadMedidaIngrediente() {
        return unidadMedidaIngrediente;
    }
}
