package com.appexamen.tl01e1.configuracion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase SQLiteConexion: Gestiona la creación y actualización de la base de datos local de la aplicación.
 * Hereda de SQLiteOpenHelper para proporcionar los métodos necesarios para manejar la base de datos SQLite.
 */
public class SQLiteConexion extends SQLiteOpenHelper {

    /**
     * Constructor de la clase para inicializar la conexión con la base de datos.
     * @param context Contexto de la aplicación.
     * @param dbName Nombre de la base de datos.
     * @param factory Fábrica opcional para la creación de cursores.
     * @param dbVersion Versión de la base de datos.
     */
    public SQLiteConexion(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, dbName, factory, dbVersion);
    }

    /**
     * Se llama cuando la base de datos se crea por primera vez.
     * @param db La instancia de la base de datos.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecuta la sentencia SQL para crear la tabla de contactos definida en la clase Transacciones.
        db.execSQL(Transacciones.CREATE_TABLE_CONTACTOS);
    }

    /**
     * Se llama cuando la base de datos necesita ser actualizada (por ejemplo, cambio de versión).
     * @param db La instancia de la base de datos.
     * @param oldVersion Versión antigua de la base de datos.
     * @param newVersion Nueva versión de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina la tabla existente para evitar duplicados o conflictos.
        db.execSQL(Transacciones.DROP_TABLE_CONTACTOS);
        // Vuelve a crear la tabla.
        onCreate(db);
    }
}
