package com.davidvicario.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView[][] matriz;

    private TextView txtVictoria;

    private TextView txtCronometro;
    private CountDownTimer countDownTimer;
    private int segundosTranscurridos = 0;

    private int contadorMovimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ocultar la barra de la aplicación
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        matriz = new ImageView[][] {
                {findViewById(R.id.imageView), findViewById(R.id.imageView2), findViewById(R.id.imageView3), findViewById(R.id.imageBlanco1)},
                {findViewById(R.id.imageView6), findViewById(R.id.imageView7), findViewById(R.id.imageView8), findViewById(R.id.imageBlanco2)},
                {findViewById(R.id.imageView10), findViewById(R.id.imageView11), findViewById(R.id.imageView12), findViewById(R.id.imageView13)}
        };

        txtVictoria = findViewById(R.id.txtVictoria);

        txtCronometro = findViewById(R.id.crono);

        iniciarCronometro();

        contadorMovimientos = 0;

        Button colocar = findViewById(R.id.btnColocar);

        colocar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colocarImagenes();
            }
        });

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                int idImagen = matriz[i][j].getDrawable().getConstantState().hashCode();
                matriz[i][j].setTag(idImagen);
                matriz[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView ficha = (ImageView) v;
                        int[] hueco = encontrarHueco();

                        for (int fila = -1; fila <= 1; fila++) {
                            for (int columna = -1; columna <= 1; columna++) {
                                if (fila == 0 && columna == 0) continue;

                                int nuevoI = hueco[0] + fila;
                                int nuevoJ = hueco[1] + columna;

                                if (nuevoI >= 0 && nuevoI < matriz.length && nuevoJ >= 0 && nuevoJ < matriz[0].length) {
                                    if (matriz[nuevoI][nuevoJ] == ficha) {
                                        intercambiarFicha(ficha, matriz[hueco[0]][hueco[1]]);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        inicioRandom();
    }

    private void inicioRandom() {
        int[] idImagen = {
                R.drawable.i001, R.drawable.i002, R.drawable.i003,
                R.drawable.i004, R.drawable.i005, R.drawable.i006,
                R.drawable.i007, R.drawable.i008, R.drawable.i009,
                R.drawable.i000
        };

        List<Integer> imagenes = new ArrayList<>();
        for (int id : idImagen) {
            imagenes.add(id);
        }
        Collections.shuffle(imagenes, new Random());

        int aux = 0;
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (i == 0 && j == 3 || i == 1 && j == 3) continue;

                if (matriz[i][j].getDrawable().getConstantState() != getResources().getDrawable(R.drawable.blanco).getConstantState()) {
                    matriz[i][j].setImageResource(imagenes.get(aux));
                    matriz[i][j].setTag(imagenes.get(aux));
                    aux++;
                }
            }
        }
    }

    private void colocarImagenes() {
        int[][] imageIds = {
                {R.drawable.i001, R.drawable.i002, R.drawable.i003},
                {R.drawable.i004, R.drawable.i006, R.drawable.i008},
                {R.drawable.i007, R.drawable.i005, R.drawable.i009, R.drawable.i000}
        };

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j].getDrawable().getConstantState() != getResources().getDrawable(R.drawable.blanco).getConstantState()) {
                    matriz[i][j].setImageResource(imageIds[i][j]);
                    matriz[i][j].setTag(imageIds[i][j]);
                }
            }
        }
    }

    private int[] encontrarHueco() {
        int[] posicionHueco = new int[2];
        int huecoId = R.drawable.i000;

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if ((int) matriz[i][j].getTag() == huecoId) {
                    posicionHueco[0] = i;
                    posicionHueco[1] = j;
                    break;
                }
            }
        }
        return posicionHueco;
    }

    private void intercambiarFicha(ImageView img1, ImageView img2) {
        int img1Id = (Integer) img1.getTag();
        int img2Id = (Integer) img2.getTag();

        img1.setImageResource(img2Id);
        img1.setTag(img2Id);

        img2.setImageResource(img1Id);
        img2.setTag(img1Id);

        incrementarContadorMovimientos();
        verificarVictoria();
    }
    private void verificarVictoria() {
        int[][] posicionesCorrectas = {
                {R.drawable.i001, R.drawable.i002, R.drawable.i003},
                {R.drawable.i004, R.drawable.i005, R.drawable.i006},
                {R.drawable.i007, R.drawable.i008, R.drawable.i009, R.drawable.i000}
        };

        boolean victoria = true;

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j].getDrawable().getConstantState() == getResources().getDrawable(R.drawable.blanco).getConstantState()) {
                    continue;
                }
                if ((int) matriz[i][j].getTag() != posicionesCorrectas[i][j]) {
                    victoria = false;
                    break;
                }
            }
            if (!victoria) {
                break;
            }
        }

        if (victoria) {
            String mensaje = "Lo has conseguido!!!\n";
            mensaje += "Tiempo empleado: " + segundosTranscurridos + " s\n";
            mensaje += "Movimientos realizados: " + contadorMovimientos;
            txtVictoria.setText(mensaje);
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        } else {
            txtVictoria.setText("");
        }
    }

    private void iniciarCronometro() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                segundosTranscurridos++;
                txtCronometro.setText(segundosTranscurridos + " s");
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    private void incrementarContadorMovimientos() {
        contadorMovimientos++;
        TextView txtContador = findViewById(R.id.contador);
        txtContador.setText(String.valueOf(contadorMovimientos));
    }
}