package com.appexamen.tl01e1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 100;

    ImageView imageViewFoto;
    Spinner spinnerPais;
    EditText editTextNombre, editTextTelefono, editTextNota;
    Button btnSalvarContacto, btnContactosSalvados;
    Bitmap imageBitmap;
    int contactIdToUpdate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewFoto = findViewById(R.id.imageViewFoto);
        findViewById(R.id.fabAgregarFoto).setOnClickListener(v -> tomarFoto());

        spinnerPais = findViewById(R.id.spinnerPais);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextNota = findViewById(R.id.editTextNota);
        btnSalvarContacto = findViewById(R.id.btnSalvarContacto);
        btnContactosSalvados = findViewById(R.id.btnContactosSalvados);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.paises, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);

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

    private void tomarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageViewFoto.setImageBitmap(imageBitmap);
        }
    }

    private void salvarContacto() {
        String pais = spinnerPais.getSelectedItem().toString();
        String nombre = editTextNombre.getText().toString();
        String telefono = editTextTelefono.getText().toString();
        String nota = editTextNota.getText().toString();

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe escribir un nombre");
            return;
        }

        if (telefono.isEmpty()) {
            mostrarAlerta("Debe escribir un telefono");
            return;
        }

        if (nota.isEmpty()) {
            mostrarAlerta("Debe escribir una nota");
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
                int rowsAffected = db.update(Transacciones.TABLA_CONTACTOS, valores, Transacciones.COLUMNA_ID + "=?", new String[]{String.valueOf(contactIdToUpdate)});
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Contacto actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa a la lista de contactos
                } else {
                    Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                }
            } else {
                long resultado = db.insert(Transacciones.TABLA_CONTACTOS, null, valores);
                if (resultado != -1) {
                    Toast.makeText(this, "Contacto salvado exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                } else {
                    Toast.makeText(this, "Error al salvar el contacto", Toast.LENGTH_SHORT).show();
                }
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
