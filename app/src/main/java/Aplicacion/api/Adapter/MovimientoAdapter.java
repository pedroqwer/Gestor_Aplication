package Aplicacion.api.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import Aplicacion.api.Entity.Movimiento;
import Aplicacion.api.Entity.Tipo;
import com.example.gestor_aplication.R;

import java.util.List;

public class MovimientoAdapter extends ArrayAdapter<Movimiento> {

    private final LayoutInflater inflater;

    public MovimientoAdapter(Context context, List<Movimiento> movimientos) {
        super(context, 0, movimientos);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_movimiento, parent, false);
            holder = new ViewHolder();
            holder.cantidad = convertView.findViewById(R.id.text1);
            holder.descripcion = convertView.findViewById(R.id.text2);
            holder.detalle = convertView.findViewById(R.id.text3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Movimiento movimiento = getItem(position);
        if (movimiento != null) {
            holder.cantidad.setText(String.format("$ %.2f", movimiento.getCantidad()));
            holder.descripcion.setText(movimiento.getDescripcion());

            String tipoStr = movimiento.getTipo() == Tipo.INGRESO ? "Ingreso" : "Gasto";
            holder.detalle.setText(tipoStr);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView cantidad;
        TextView descripcion;
        TextView detalle;
    }
}
