package com.appexamen.tl01e1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appexamen.tl01e1.configuracion.SQLiteConexion;
import com.appexamen.tl01e1.configuracion.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Actividad principal de la aplicación.
 * Permite ingresar nuevos contactos o editar contactos existentes.
 * Maneja la captura de fotos (Cámara/Galería), validación de datos y almacenamiento en SQLite.
 */
public class MainActivity extends AppCompatActivity {

    // Constantes para identificar peticiones de cámara, galería y permisos
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 100;

    // Componentes de la interfaz de usuario
    ImageView imageViewFoto;
    Spinner spinnerPais;
    EditText editTextNombre, editTextTelefono, editTextNota;
    Button btnSalvarContacto, btnContactosSalvados;
    
    // Almacena la foto capturada o seleccionada en memoria
    Bitmap imageBitmap;
    
    // ID del contacto a actualizar. Si es -1, se considera un nuevo contacto.
    int contactIdToUpdate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de componentes vinculándolos con el archivo de diseño XML
        imageViewFoto = findViewById(R.id.imageViewFoto);
        
        // Al presionar el botón de la foto, mostramos el diálogo de opciones
        findViewById(R.id.fabAgregarFoto).setOnClickListener(v -> mostrarOpcionesImagen());

        spinnerPais = findViewById(R.id.spinnerPais);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextNota = findViewById(R.id.editTextNota);
        btnSalvarContacto = findViewById(R.id.btnSalvarContacto);
        btnContactosSalvados = findViewById(R.id.btnContactosSalvados);

        // Configuración del Spinner (lista desplegable) con los nombres de países
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.paises, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);

        // Lógica para detectar si la actividad se abrió para EDITAR un contacto existente
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            contactIdToUpdate = intent.getIntExtra("id", -1);
            String pais = intent.getStringExtra("pais");
            String nombre = intent.getStringExtra("nombre");
            String telefono = intent.getStringExtra("telefono");
            String nota = intent.getStringExtra("nota");
            byte[] imagenBytes = intent.getByteArrayExtra("imagen");

            editTextNombre.setText(nombre);
            editTextTelefono.setText(telefono);
            editTextNota.setText(nota);

            if (pais != null) {
                int spinnerPosition = adapter.getPosition(pais);
                spinnerPais.setSelection(spinnerPosition);
            }

            if (imagenBytes != null) {
                imageBitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                imageViewFoto.setImageBitmap(imageBitmap);
            }

            btnSalvarContacto.setText("Actualizar Contacto");
        }

        btnSalvarContacto.setOnClickListener(v -> salvarContacto());

        btnContactosSalvados.setOnClickListener(v -> {
            Intent intentList = new Intent(MainActivity.this, ActivityList.class);
            startActivity(intentList);
        });
    }

    /**
     * Muestra un diálogo para que el usuario elija entre Cámara o Galería.
     */
    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                comprobarPermisosCamara();
            } else if (which == 1) {
                abrirGaleria();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Verifica permisos y lanza la aplicación de cámara.
     */
    private void comprobarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Abre la galería para seleccionar una imagen.
     */
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Recibe el resultado de la cámara o la galería.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Resultado de la cámara
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageViewFoto.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                // Resultado de la galería
                try {
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    imageBitmap = BitmapFactory.decodeStream(imageStream);
                    imageViewFoto.setImageBitmap(imageBitmap);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Valida los datos e inserta o actualiza el contacto en la base de datos SQLite.
     */
    private void salvarContacto() {
        String pais = spinnerPais.getSelectedItem().toString();
        String nombre = editTextNombre.getText().toString();
        String telefono = editTextTelefono.getText().toString();
        String nota = editTextNota.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || nota.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios");
            return;
        }

        try {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DB_NAME, null, Transacciones.DB_VERSION);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Transacciones.COLUMNA_PAIS, pais);
            valores.put(Transacciones.COLUMNA_NOMBRE, nombre);
            valores.put(Transacciones.COLUMNA_TELEFONO, telefono);
            valores.put(Transacciones.COLUMNA_NOTA, nota);

            if (imageBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                valores.put(Transacciones.COLUMNA_IMAGEN, stream.toByteArray());
            }

            if (contactIdToUpdate != -1) {
                db.update(Transacciones.TABLA_CONTACTOS, valores, Transacciones.COLUMNA_ID + "=?", new String[]{String.valueOf(contactIdToUpdate)});
                Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                db.insert(Transacciones.TABLA_CONTACTOS, null, valores);
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
            db.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarAlerta(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    private void limpiarCampos() {
        spinnerPais.setSelection(0);
        editTextNombre.setText("");
        editTextTelefono.setText("");
        editTextNota.setText("");
        imageViewFoto.setImageResource(R.drawable.ic_launcher_foreground);
        imageBitmap = null;
    }
}
