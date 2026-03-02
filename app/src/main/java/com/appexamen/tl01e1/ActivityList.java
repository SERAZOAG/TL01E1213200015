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

public class ActivityList extends AppCompatActivity {

    ListView listViewContactos;
    ArrayList<Contacto> listaContactos;
    ContactoAdapter adapter;
    EditText editTextBuscar;
    Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    Contacto contactoSeleccionado;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        init();

        obtenerContactos();

        adapter = new ContactoAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);

        btnAtras.setOnClickListener(v -> finish());

        editTextBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listViewContactos.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionado = (Contacto) parent.getItemAtPosition(position);
            Toast.makeText(this, "Contacto " + contactoSeleccionado.getNombre() + " seleccionado", Toast.LENGTH_SHORT).show();
        });

        listViewContactos.setOnItemLongClickListener((parent, view, position, id) -> {
            contactoSeleccionado = (Contacto) parent.getItemAtPosition(position);
            mostrarDialogoLlamada();
            return true;
        });

        btnCompartir.setOnClickListener(v -> compartirContacto());
        btnVerImagen.setOnClickListener(v -> verImagen());
        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnActualizar.setOnClickListener(v -> actualizarContacto());
    }

    private void init() {
        listViewContactos = findViewById(R.id.listViewContactos);
        editTextBuscar = findViewById(R.id.editTextBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnCompartir = findViewById(R.id.btnCompartirContacto);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnEliminar = findViewById(R.id.btnEliminarContacto);
        btnActualizar = findViewById(R.id.btnActualizarContacto);
    }

    private void obtenerContactos() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DB_NAME, null, Transacciones.DB_VERSION);
        SQLiteDatabase db = conexion.getReadableDatabase();
        listaContactos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.TABLA_CONTACTOS, null);

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

    private void filtrar(String texto) {
        ArrayList<Contacto> listaFiltrada = new ArrayList<>();
        for (Contacto contacto : listaContactos) {
            if (contacto.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(contacto);
            }
        }
        adapter.filtrar(listaFiltrada);
    }

    private void mostrarDialogoLlamada() {
        if (contactoSeleccionado == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea llamar a " + contactoSeleccionado.getNombre() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (ContextCompat.checkSelfPermission(ActivityList.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ActivityList.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                    } else {
                        llamarContacto();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void llamarContacto() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactoSeleccionado.getTelefono()));
        startActivity(intent);
    }

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
                    db.delete(Transacciones.TABLA_CONTACTOS, Transacciones.COLUMNA_ID + "=?", new String[]{String.valueOf(contactoSeleccionado.getId())});
                    db.close();
                    obtenerContactos();
                    adapter.notifyDataSetChanged();
                    contactoSeleccionado = null;
                })
                .setNegativeButton("No", null)
                .show();
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        obtenerContactos();
        adapter = new ContactoAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);
        contactoSeleccionado = null;
    }

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
