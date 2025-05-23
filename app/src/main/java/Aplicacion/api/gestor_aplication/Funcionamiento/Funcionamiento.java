package Aplicacion.api.gestor_aplication.Funcionamiento;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.gestor_aplication.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Funcionamiento extends AppCompatActivity {
    ScrollView scrollView;
    TextView textView, textViewScroll;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcionamiento);

        Inicializar();

        mediaPlayer = MediaPlayer.create(this, R.raw.musica);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Botón para volver
        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();  // Detener música
                finish();            // Cerrar actividad
            }
        });
    }

    private void Inicializar() {
        scrollView = findViewById(R.id.scrollView); // Asegúrate de que exista en tu XML
        textView = findViewById(R.id.textoSuperior);
        textViewScroll = findViewById(R.id.textoInferior);

        // Cargar contenido desde el archivo informacion.txt
        String textoInformacion = leerArchivoRaw(R.raw.informacion);
        textViewScroll.setText(textoInformacion);
    }

    private String leerArchivoRaw(int resourceId) {
        StringBuilder builder = new StringBuilder();
        try (InputStream inputStream = getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                builder.append(linea).append("\n");
            }
        } catch (Exception e) {
            builder.append("Error al cargar el contenido.");
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
