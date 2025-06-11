package Aplicacion.api.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import Aplicacion.api.Entity.PresupuestoEntity;
import com.example.gestor_aplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PresupuestoAdapter extends ArrayAdapter<PresupuestoEntity> {

    private final LayoutInflater inflater;
    private final int maxTexto = 30;

    public PresupuestoAdapter(Context context, List<PresupuestoEntity> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        PresupuestoEntity item = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            holder = new ViewHolder();
            holder.text1 = convertView.findViewById(android.R.id.text1);
            holder.text2 = convertView.findViewById(android.R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String mes = obtenerMesDesdeFecha(item.getFecha()); // Obtener mes desde fecha

        holder.text1.setText(cortarCadenaMaximo("Presupuesto de: " + mes, maxTexto));
        holder.text2.setText(cortarCadenaMaximo(item.getCantidadLimite().toString(), maxTexto));

        return convertView;
    }

    private static class ViewHolder {
        TextView text1;
        TextView text2;
    }

    private String cortarCadenaMaximo(String texto, int max) {
        if (texto.length() > max) {
            return texto.substring(0, max) + "...";
        }
        return texto;
    }

    private String obtenerMesDesdeFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) return "Fecha no disponible";
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = formatoEntrada.parse(fecha);

            SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES")); // Ej: junio 2025
            return formatoMes.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Fecha inválida";
        }
    }
}
