package cl.icel.pasteleria;

import java.time.LocalDate;
import java.util.List;

public class Boleta {
    private String UUID;
    private String nombreVendedor;
    private LocalDate fechaDeVenta;
    private List<DetalleBoleta> detalles;
    private int totalVenta;

    /* Constructor del objeto Boleta
     * @param UUID: uuid de la transaccion, es unico
     * @param nombreVendedor: nombre del vendedor que realizo la venta
     * @param fechaDeVenta: fecha en la que se realizo la venta
     * @param detalles: lista de productos y cantidades asociados a la venta
     * @param totalVenta: total de la venta realizada
     */
    public Boleta(String UUID, String nombreVendedor, LocalDate fechaDeVenta, List<DetalleBoleta> detalles, int totalVenta) {
        this.UUID = UUID;
        this.nombreVendedor = nombreVendedor;
        this.fechaDeVenta = fechaDeVenta;
        this.detalles = detalles;
        this.totalVenta = totalVenta;
    }

    public String getUUID() {
        return UUID;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public LocalDate getFechaDeVenta() {
        return fechaDeVenta;
    }

    public List<DetalleBoleta> getDetalles() {
        return detalles;
    }

    public int getTotalVenta() {
        return totalVenta;
    }
}