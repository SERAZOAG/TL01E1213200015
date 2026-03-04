package com.appexamen.tl01e1.clases;

/**
 * Clase Contacto: Representa el modelo de datos de un contacto en la aplicación.
 * Esta clase se utiliza para transportar la información de un contacto entre las distintas capas
 * de la aplicación (base de datos, adaptadores y actividades).
 */
public class Contacto {
    private int id;             // Identificador único del contacto en la base de datos
    private String pais;        // País de origen seleccionado para el contacto
    private String nombre;      // Nombre completo del contacto
    private String telefono;    // Número de teléfono del contacto
    private String nota;        // Nota o descripción adicional
    private byte[] imagen;      // Imagen del contacto almacenada como arreglo de bytes (BLOB en SQLite)

    /**
     * Constructor de la clase Contacto para inicializar todos sus atributos.
     * @param id Identificador único.
     * @param pais País del contacto.
     * @param nombre Nombre del contacto.
     * @param telefono Teléfono del contacto.
     * @param nota Nota o comentario.
     * @param imagen Arreglo de bytes que representa la imagen.
     */
    public Contacto(int id, String pais, String nombre, String telefono, String nota, byte[] imagen) {
        this.id = id;
        this.pais = pais;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.imagen = imagen;
    }

    /* --- Métodos Getter para acceder a la información privada del objeto --- */

    public int getId() {
        return id;
    }

    public String getPais() {
        return pais;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNota() {
        return nota;
    }

    public byte[] getImagen() {
        return imagen;
    }
}
