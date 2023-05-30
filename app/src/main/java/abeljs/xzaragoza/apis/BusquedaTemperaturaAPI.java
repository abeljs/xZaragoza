package abeljs.xzaragoza.apis;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

import abeljs.xzaragoza.data.Temperatura;

public class BusquedaTemperaturaAPI {
    private final Executor executor = Executors.newSingleThreadExecutor();

    public void getTemperatura(BusquedaTemperaturaCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Temperatura temperatura = getTemperaturaSynchronous();
                    callback.onBusquedaTemperaturaComplete(temperatura);
                }  catch (IOException e) {
                    e.printStackTrace();
                    callback.onBusquedaTemperaturaError("ERROR al obtener la temperatura.");
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    callback.onBusquedaTemperaturaError("ERROR al obtener la temperatura.");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    callback.onBusquedaTemperaturaError("ERROR al obtener la temperatura.");
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onBusquedaTemperaturaError("ERROR al obtener la temperatura.");
                }
            }
        });
    }

    private Temperatura getTemperaturaSynchronous()
            throws IOException, ParserConfigurationException, JSONException {

        URL url = new URL("https://www.el-tiempo.net/api/json/v2/provincias/50/municipios/50297");

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.connect();

        int codigoRespuesta = conn.getResponseCode();

        if (codigoRespuesta != 200) {
            throw new RuntimeException("HttpResponseCode: " + codigoRespuesta);
        } else {

            StringBuilder cadenaInformacion = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                cadenaInformacion.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject jObject = new JSONObject(cadenaInformacion.toString());
            int tempActual = Integer.valueOf(jObject.getString("temperatura_actual"));

            JSONObject jObjectTemps = jObject.getJSONObject("temperaturas");
            int tempMin = Integer.valueOf(jObjectTemps.getString("min"));
            int tempMax = Integer.valueOf(jObjectTemps.getString("max"));

            JSONObject jObjectCielo = jObject.getJSONObject("stateSky");
            String estadoCielo = jObjectCielo.getString("description");

            return new Temperatura(tempActual, tempMin, tempMax, estadoCielo);
        }
    }
}
