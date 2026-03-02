package com.appexamen.tl01e1.configuracion;

public class Transacciones {

    // Nombre de la base de datos
    public static final String DB_NAME = "tl01e1";
    public static final int DB_VERSION = 1;

    // Tabla de contactos
    public static final String TABLA_CONTACTOS = "contactos";

    // Columnas de la tabla de contactos
    public static final String COLUMNA_ID = "id";
    public static final String COLUMNA_PAIS = "pais";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_TELEFONO = "telefono";
    public static final String COLUMNA_NOTA = "nota";
    public static final String COLUMNA_IMAGEN = "imagen";

    // Creación de la tabla de contactos
    public static final String CREATE_TABLE_CONTACTOS = "CREATE TABLE " + TABLA_CONTACTOS + " (" +
            COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMNA_PAIS + " TEXT, " +
            COLUMNA_NOMBRE + " TEXT, " +
            COLUMNA_TELEFONO + " TEXT, " +
            COLUMNA_NOTA + " TEXT, " +
            COLUMNA_IMAGEN + " BLOB)";

    // Eliminación de la tabla de contactos
    public static final String DROP_TABLE_CONTACTOS = "DROP TABLE IF EXISTS " + TABLA_CONTACTOS;
}
