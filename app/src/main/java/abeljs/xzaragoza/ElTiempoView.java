package abeljs.xzaragoza;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import abeljs.xzaragoza.data.Temperatura;

public class ElTiempoView extends CardView {
    TextView txtTempActual;
    TextView txtTempMin;
    TextView txtTempMax;
    TextView txtEstadoCielo;
    ConstraintLayout clTiempo;

    public ElTiempoView(@NonNull Context context) {
        super(context);
    }

    public ElTiempoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElTiempoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Configura la apariencia y el comportamiento de tu vista personalizada aquí
        inflate(getContext(), R.layout.el_tiempo_view_layout, this);
        txtTempActual = findViewById(R.id.txtTemperaturaActual);
        txtTempMin = findViewById(R.id.txtTemperaturaMinima);
        txtTempMax = findViewById(R.id.txtTemperaturaMaxima);
        txtEstadoCielo = findViewById(R.id.txtEstadoCielo);
        clTiempo = findViewById(R.id.clTiempo);

    }

    public void setTemperatura(Temperatura temperatura) {
        Log.e("pruebaTemp", String.valueOf(temperatura.temperaturaActual));
        txtTempActual.setText(String.valueOf(temperatura.temperaturaActual));
        txtTempMin.setText(String.valueOf(temperatura.temperaturaMinima) + "º");
        txtTempMax.setText(String.valueOf(temperatura.temperaturaMaxima) + "º");
        String estadoTiempoCadena = temperatura.estadoDeCielo;

        clTiempo.setClipToOutline(true);

        if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*torment.*")) {
            clTiempo.setBackgroundResource(R.drawable.bg_tormenta);
        } else if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*lluvi.*")) {
            clTiempo.setBackgroundResource(R.drawable.bg_lluvia);
        } else if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*nub.*") ||
                comprobarExpresionRegular(estadoTiempoCadena, "(?i).*cubierto.*")) {
            if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*poc.*")) {
                clTiempo.setBackgroundResource(R.drawable.bg_pocas_nubes);
            } else {
                clTiempo.setBackgroundResource(R.drawable.bg_nublado);
            }
        } else if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*niebl.*")) {
            clTiempo.setBackgroundResource(R.drawable.bg_niebla);
        } else {
            clTiempo.setBackgroundResource(R.drawable.bg_sol);
        }
        txtEstadoCielo.setText(estadoTiempoCadena);
        for (int i = 0; i < clTiempo.getChildCount(); i++) {
            View view = clTiempo.getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }
        clTiempo.setVisibility(VISIBLE);
    }

    public void setDefault() {
        for (int i = 0; i < clTiempo.getChildCount(); i++) {
            View view = clTiempo.getChildAt(i);
            view.setVisibility(View.INVISIBLE);
        }
        clTiempo.setBackgroundResource(R.drawable.bg_default);
        clTiempo.setVisibility(VISIBLE);
    }

    private Boolean comprobarExpresionRegular(String cadenaAComparar, String expresionRegular) {
        Pattern pat = Pattern.compile(expresionRegular);
        Matcher mat = pat.matcher(cadenaAComparar);
        return mat.matches();
    }

}
