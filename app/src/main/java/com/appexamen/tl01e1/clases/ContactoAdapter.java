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

public class ContactoAdapter extends ArrayAdapter<Contacto> {

    public ContactoAdapter(Context context, ArrayList<Contacto> contactos) {
        super(context, 0, contactos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_contacto, parent, false);
        }

        Contacto currentContacto = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.imageViewContacto);
        TextView textViewNombre = listItemView.findViewById(R.id.textViewNombreContacto);
        TextView textViewTelefono = listItemView.findViewById(R.id.textViewTelefonoContacto);
        TextView textViewNota = listItemView.findViewById(R.id.textViewNotaContacto);

        if (currentContacto != null) {
            if (currentContacto.getImagen() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentContacto.getImagen(), 0, currentContacto.getImagen().length);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
            textViewNombre.setText(currentContacto.getNombre());
            textViewTelefono.setText(currentContacto.getTelefono());
            textViewNota.setText(currentContacto.getNota());
        }

        return listItemView;
    }

    public void filtrar(ArrayList<Contacto> listaFiltrada) {
        clear();
        addAll(listaFiltrada);
        notifyDataSetChanged();
    }
}
