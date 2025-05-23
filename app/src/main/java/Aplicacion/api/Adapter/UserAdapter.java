package Aplicacion.api.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import Aplicacion.api.Entity.User;
import com.example.gestor_aplication.R;

public class UserAdapter extends BaseAdapter {
    private final Context context;
    private final List<User> usuarios;

    public UserAdapter(Context context, List<User> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public Object getItem(int position) {
        return usuarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return usuarios.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();
            holder.username = convertView.findViewById(R.id.username);
            holder.nombre = convertView.findViewById(R.id.nombre);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = usuarios.get(position);
        holder.username.setText("Usuario: " + cortar(user.getUsername(), 30));
        holder.nombre.setText("Nombre: " + cortar(user.getNombre(), 30));

        return convertView;
    }

    static class ViewHolder {
        TextView username;
        TextView nombre;
    }

    private String cortar(String texto, int max) {
        return texto.length() > max ? texto.substring(0, max) + "..." : texto;
    }
}
