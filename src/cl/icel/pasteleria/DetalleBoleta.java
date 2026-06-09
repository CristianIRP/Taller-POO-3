package cl.icel.pasteleria;

public class DetalleBoleta {
    private Pastel pastelVendido;
    private int cantidad;
    private int subTotal;
    /* Constructor del objeto DetalleBoleta
     * @param pastelVendido: Pastel que se vendio
     * @param cantidad: Cantidad de pasteles vendidos
     * @param subTotal: Subtotal de la venta del pastel
     */
    public DetalleBoleta(Pastel pastelVendido, int cantidad, int subTotal) {
        this.pastelVendido = pastelVendido;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
    }
}
