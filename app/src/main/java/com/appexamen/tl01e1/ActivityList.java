package com.appexamen.tl01e1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appexamen.tl01e1.clases.Contacto;
import com.appexamen.tl01e1.clases.ContactoAdapter;
import com.appexamen.tl01e1.configuracion.SQLiteConexion;
import com.appexamen.tl01e1.configuracion.Transacciones;

import java.util.ArrayList;

/**
 * Clase ActivityList: Se encarga de mostrar la lista de todos los contactos guardados en la BD.
 * Proporciona funcionalidades de búsqueda, llamada directa, eliminación y actualización.
 */
public class ActivityList extends AppCompatActivity {

    // Componentes de la interfaz
    ListView listViewContactos;
    ArrayList<Contacto> listaContactos;
    ContactoAdapter adapter;
    EditText editTextBuscar;
    Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    
    // Almacena el contacto que el usuario ha seleccionado en la lista
    Contacto contactoSeleccionado;
    
    // Código de petición para el permiso de realizar llamadas
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Inicializar componentes UI
        init();

        // Cargar datos desde SQLite
        obtenerContactos();

        // Configurar el adaptador para llenar el ListView
        adapter = new ContactoAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);

        btnAtras.setOnClickListener(v -> finish());

        // Implementación de filtro de búsqueda en tiempo real
        editTextBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Llama al método filtrar cada vez que el texto cambia
                filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Evento al hacer un clic corto en un elemento de la lista (SELECCIONAR)
        listViewContactos.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionado = (Contacto) parent.getItemAtPosition(position);
            Toast.makeText(this, "Contacto " + contactoSeleccionado.getNombre() + " seleccionado", Toast.LENGTH_SHORT).show();
        });

        // Evento al mantener presionado un elemento (LLAMAR)
        listViewContactos.setOnItemLongClickListener((parent, view, position, id) -> {
            contactoSeleccionado = (Contacto) parent.getItemAtPosition(position);
            mostrarDialogoLlamada();
            return true; // Indica que el evento fue manejado
        });

        // Configuración de botones de acción
        btnCompartir.setOnClickListener(v -> compartirContacto());
        btnVerImagen.setOnClickListener(v -> verImagen());
        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnActualizar.setOnClickListener(v -> actualizarContacto());
    }

    /**
     * Vincula las variables con los objetos definidos en el XML.
     */
    private void init() {
        listViewContactos = findViewById(R.id.listViewContactos);
        editTextBuscar = findViewById(R.id.editTextBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnCompartir = findViewById(R.id.btnCompartirContacto);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnEliminar = findViewById(R.id.btnEliminarContacto);
        btnActualizar = findViewById(R.id.btnActualizarContacto);
    }

    /**
     * Consulta la base de datos para obtener todos los registros de contactos.
     */
    private void obtenerContactos() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DB_NAME, null, Transacciones.DB_VERSION);
        SQLiteDatabase db = conexion.getReadableDatabase();
        listaContactos = new ArrayList<>();
        
        // Ejecutar consulta SELECT *
        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.TABLA_CONTACTOS, null);

        // Recorrer el cursor para crear objetos Contacto y añadirlos a la lista
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.COLUMNA_ID));
            String pais = cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.COLUMNA_PAIS));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.COLUMNA_NOMBRE));
            String telefono = cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.COLUMNA_TELEFONO));
            String nota = cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.COLUMNA_NOTA));
            byte[] imagen = cursor.getBlob(cursor.getColumnIndexOrThrow(Transacciones.COLUMNA_IMAGEN));
            
            listaContactos.add(new Contacto(id, pais, nombre, telefono, nota, imagen));
        }

        cursor.close();
        db.close();
    }

    /**
     * Filtra la lista de contactos basándose en el nombre ingresado.
     */
    private void filtrar(String texto) {
        ArrayList<Contacto> listaFiltrada = new ArrayList<>();
        for (Contacto contacto : listaContactos) {
            if (contacto.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(contacto);
            }
        }
        adapter.filtrar(listaFiltrada); // Actualizar el adaptador con los resultados filtrados
    }

    /**
     * Muestra un diálogo de confirmación antes de iniciar una llamada.
     */
    private void mostrarDialogoLlamada() {
        if (contactoSeleccionado == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea llamar a " + contactoSeleccionado.getNombre() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Verificar si se tiene permiso para realizar llamadas
                    if (ContextCompat.checkSelfPermission(ActivityList.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ActivityList.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                    } else {
                        llamarContacto();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Inicia una llamada telefónica utilizando un Intent implícito con ACTION_CALL.
     */
    private void llamarContacto() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactoSeleccionado.getTelefono()));
        startActivity(intent);
    }

    /**
     * Permite compartir la información del contacto seleccionado a través de otras apps.
     */
    private void compartirContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Contacto: " + contactoSeleccionado.getNombre() + " - " + contactoSeleccionado.getTelefono());
        startActivity(Intent.createChooser(intent, "Compartir Contacto"));
    }

    /**
     * Navega a la actividad para visualizar la imagen del contacto en pantalla completa.
     */
    private void verImagen() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contactoSeleccionado.getImagen() == null) {
            Toast.makeText(this, "El contacto no tiene imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ActivityVerImagen.class);
        intent.putExtra("imagen", contactoSeleccionado.getImagen());
        startActivity(intent);
    }

    /**
     * Elimina el contacto seleccionado de la base de datos tras confirmar.
     */
    private void eliminarContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Contacto")
                .setMessage("¿Está seguro que desea eliminar este contacto?")
                .setPositiveButton("Si", (dialog, which) -> {
                    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DB_NAME, null, Transacciones.DB_VERSION);
                    SQLiteDatabase db = conexion.getWritableDatabase();
                    // Eliminar registro por ID
                    db.delete(Transacciones.TABLA_CONTACTOS, Transacciones.COLUMNA_ID + "=?", new String[]{String.valueOf(contactoSeleccionado.getId())});
                    db.close();
                    
                    // Refrescar la lista de datos desde la BD
                    obtenerContactos();
                    // Actualizar el adaptador con la nueva lista para que el ListView se refresque visualmente
                    adapter.filtrar(listaContactos);
                    
                    contactoSeleccionado = null;
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Envía los datos del contacto seleccionado de vuelta a MainActivity para su edición.
     */
    private void actualizarContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", contactoSeleccionado.getId());
        intent.putExtra("pais", contactoSeleccionado.getPais());
        intent.putExtra("nombre", contactoSeleccionado.getNombre());
        intent.putExtra("telefono", contactoSeleccionado.getTelefono());
        intent.putExtra("nota", contactoSeleccionado.getNota());
        intent.putExtra("imagen", contactoSeleccionado.getImagen());
        startActivity(intent);
    }

    /**
     * Se ejecuta cuando regresamos a esta actividad. Refresca la lista de contactos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        obtenerContactos();
        adapter = new ContactoAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);
        contactoSeleccionado = null;
    }

    /**
     * Gestiona la respuesta del permiso de llamada.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamarContacto();
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
