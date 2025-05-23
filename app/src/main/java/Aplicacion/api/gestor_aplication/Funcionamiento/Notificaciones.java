package Aplicacion.api.gestor_aplication.Funcionamiento;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notificaciones {

    private static final String CANAL_ID = "1";
    private static final String CANAL_NOMBRE = "CanalNotificaciones";

    public static void mostrarNotificacionSimple(Context context, String titulo, String mensaje) {
        // Crear el NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Obtener el servicio de notificaciones
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear el canal si estamos en Android 8.0 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(CANAL_ID, CANAL_NOMBRE, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(canal);
        }

        // Enviar la notificación
        notificationManager.notify(1, builder.build());
    }
}
