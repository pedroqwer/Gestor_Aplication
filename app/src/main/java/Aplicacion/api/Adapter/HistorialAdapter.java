package Aplicacion.api.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.gestor_aplication.R;

import java.util.List;

import Aplicacion.api.Entity.Auditoria;
import Aplicacion.api.gestor_aplication.Historial.Historial;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
public class HistorialAdapter extends ArrayAdapter<Auditoria> {

    private final LayoutInflater inflater;

    public HistorialAdapter(Context context, List<Auditoria> historials){
        super(context,0,historials);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_historial, parent, false);
            holder = new ViewHolder();

            // Initialize both accion and fecha correctly
            holder.accion = convertView.findViewById(R.id.text_accion);
            holder.fecha = convertView.findViewById(R.id.text_fecha);  // Correct this line

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Auditoria historial = getItem(position);
        if(historial != null){
            // Set the text to the correct views
            holder.accion.setText(historial.getAccion());
            holder.fecha.setText(historial.getDetalles());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView accion;
        TextView fecha;
    }
}
