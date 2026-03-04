package com.appexamen.tl01e1.clases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appexamen.tl01e1.R;

import java.util.ArrayList;

/**
 * Clase ContactoAdapter: Adaptador personalizado para la lista de contactos.
 * Permite transformar una lista de objetos 'Contacto' en elementos visuales dentro de un ListView.
 */
public class ContactoAdapter extends ArrayAdapter<Contacto> {

    /**
     * Constructor del adaptador.
     * @param context Contexto de la aplicación.
     * @param contactos Lista de objetos Contacto a mostrar.
     */
    public ContactoAdapter(Context context, ArrayList<Contacto> contactos) {
        super(context, 0, contactos);
    }

    /**
     * Método getView: Se encarga de inflar el diseño de cada fila y asignar los datos del contacto.
     * @param position Posición del elemento en la lista.
     * @param convertView Vista reciclada si existe.
     * @param parent Contenedor padre.
     * @return La vista de la fila configurada.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        
        // Si la vista no ha sido creada, inflamos el diseño personalizado 'list_item_contacto'
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_contacto, parent, false);
        }

        // Obtener el contacto actual según su posición
        Contacto currentContacto = getItem(position);

        // Vincular componentes del diseño de la fila
        ImageView imageView = listItemView.findViewById(R.id.imageViewContacto);
        TextView textViewNombre = listItemView.findViewById(R.id.textViewNombreContacto);
        TextView textViewTelefono = listItemView.findViewById(R.id.textViewTelefonoContacto);
        TextView textViewNota = listItemView.findViewById(R.id.textViewNotaContacto);

        // Si el contacto existe, asignamos sus datos a los componentes visuales
        if (currentContacto != null) {
            // Conversión de bytes a Bitmap para mostrar la foto
            if (currentContacto.getImagen() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentContacto.getImagen(), 0, currentContacto.getImagen().length);
                imageView.setImageBitmap(bitmap);
            } else {
                // Imagen por defecto si no tiene foto
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
            textViewNombre.setText(currentContacto.getNombre());
            textViewTelefono.setText(currentContacto.getTelefono());
            textViewNota.setText(currentContacto.getNota());
        }

        return listItemView;
    }

    /**
     * Método filtrar: Actualiza la lista del adaptador tras realizar una búsqueda.
     * @param listaFiltrada Nueva lista con los contactos que coinciden con el filtro.
     */
    public void filtrar(ArrayList<Contacto> listaFiltrada) {
        clear(); // Limpia los datos actuales
        addAll(listaFiltrada); // Agrega los nuevos datos
        notifyDataSetChanged(); // Refresca el ListView
    }
}
