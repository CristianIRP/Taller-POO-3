package cl.icel.pasteleria;
import java.time.LocalDate;

public
class Boleta {
    private String UUID;
    private String nombreVendedor;
    private LocalDate fechaDeVenta;
    private int totalVenta;

    /* Constructor del objeto Boleta
     * @param UUID: uuid de la transaccion, es unico
     * @param nombreVendedor: nombre del vendedor que realizo la venta
     * @param fechaDeVenta: fecha en la que se realizo la venta
     * @param totalVenta: total de la venta realizada
     */
    public Boleta(String UUID, String nombreVendedor, LocalDate fechaDeVenta, int totalVenta) {
        this.UUID = UUID;
        this.nombreVendedor = nombreVendedor;
        this.fechaDeVenta = fechaDeVenta;
        this.totalVenta = totalVenta;
    }
}