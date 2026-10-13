package pe.edu.cibertec.sigip.inventoryservice.model;

public enum TipoMovimiento {
    /** Compra o devolución: suma stock. */
    ENTRADA,
    /** Consumo en producción o merma: resta stock. */
    SALIDA
}
