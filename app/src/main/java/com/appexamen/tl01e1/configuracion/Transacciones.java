package com.appexamen.tl01e1.configuracion;

/**
 * Clase Transacciones: Contiene las constantes que definen la estructura de la base de datos SQLite.
 * Se utiliza para centralizar los nombres de tablas, columnas y sentencias SQL, facilitando el mantenimiento.
 */
public class Transacciones {

    // Nombre de la base de datos física que se creará en el dispositivo
    public static final String DB_NAME = "tl01e1";
    // Versión de la base de datos. Se debe incrementar si se cambia la estructura de las tablas.
    public static final int DB_VERSION = 1;

    // Nombre de la tabla de contactos
    public static final String TABLA_CONTACTOS = "contactos";

    /* --- Definición de los nombres de las columnas para la tabla 'contactos' --- */
    public static final String COLUMNA_ID = "id";
    public static final String COLUMNA_PAIS = "pais";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_TELEFONO = "telefono";
    public static final String COLUMNA_NOTA = "nota";
    public static final String COLUMNA_IMAGEN = "imagen";

    /**
     * Sentencia SQL para la creación de la tabla de contactos.
     * Define tipos de datos: INTEGER para el ID (autoincrementable), TEXT para strings y BLOB para imágenes.
     */
    public static final String CREATE_TABLE_CONTACTOS = "CREATE TABLE " + TABLA_CONTACTOS + " (" +
            COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMNA_PAIS + " TEXT, " +
            COLUMNA_NOMBRE + " TEXT, " +
            COLUMNA_TELEFONO + " TEXT, " +
            COLUMNA_NOTA + " TEXT, " +
            COLUMNA_IMAGEN + " BLOB)";

    /**
     * Sentencia SQL para eliminar la tabla si ya existe, utilizada durante actualizaciones de versión.
     */
    public static final String DROP_TABLE_CONTACTOS = "DROP TABLE IF EXISTS " + TABLA_CONTACTOS;
}
