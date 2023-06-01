package abeljs.xzaragoza.apis;

import android.content.Context;

import androidx.room.Room;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.PostesDao;
import abeljs.xzaragoza.data.TiempoBus;

public class BusquedaTiemposPosteAPI {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void getTiemposPoste(Context context, BusquedaTiemposPosteCallback callback, String numPoste) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BaseDeDatos db = Room.databaseBuilder(context,
                        BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
                PostesDao postesDao = db.daoPoste();
                try {
                    ArrayList<TiempoBus> listaTiemposPoste = getTiemposPosteSynchronous(numPoste);
                    callback.onBusquedaTiemposPosteComplete(listaTiemposPoste);
                } catch (SAXException e) {
                    callback.onBusquedaTiemposPosteError("ERROR al obtener los tiempos de los buses.");
                } catch (IOException e) {
                    callback.onBusquedaTiemposPosteError("ERROR al obtener los tiempos de los buses.");
                } catch (ParserConfigurationException e) {
                    callback.onBusquedaTiemposPosteError("ERROR al obtener los tiempos de los buses.");
                } catch (RuntimeException e) {
                    if (postesDao.getPoste(numPoste).isEmpty()) {
                        callback.onBusquedaTiemposPosteError("Número de poste incorrecto.");
                    } else {
                        callback.onBusquedaTiemposPosteError("ERROR al obtener los tiempos del poste.");
                    }
                }
            }
        });
    }

    public ArrayList<TiempoBus> getTiemposPosteSynchronous(String numPoste)
            throws SAXException, IOException, ParserConfigurationException {
        ArrayList<TiempoBus> listaTiemposPoste = new ArrayList<>();

        URL url = new URL("https://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/tuzsa-" + numPoste + ".xml");

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

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(cadenaInformacion.toString())));

            NodeList respuesta = doc.getElementsByTagName("poste");

            if (respuesta.getLength() == 0) {
                return null;
            }

            Element elementoResultado = (Element) respuesta.item(0);
            String idParada = elementoResultado.getElementsByTagName("id").item(0).getTextContent();

            NodeList lineas = elementoResultado.getElementsByTagName("linea");

            int numLineas = lineas.getLength();

            for (int contador = 0; contador < numLineas; contador++) {
                String linea = lineas.item(contador).getTextContent();
                int primero;

                try {
                    primero = Integer.valueOf(elementoResultado.getElementsByTagName("primero").item(contador).getTextContent().split(" ")[0]);
                } catch (NumberFormatException e) {
                    primero = 0;
                }
                TiempoBus paradaLineaDeBus = new TiempoBus(idParada, linea, primero);
                listaTiemposPoste.add(paradaLineaDeBus);

                int segundo;

                try {
                    segundo = Integer.valueOf(elementoResultado.getElementsByTagName("segundo").item(contador).getTextContent().split(" ")[0]);
                } catch (NumberFormatException e) {
                    segundo = 0;
                }

                paradaLineaDeBus = new TiempoBus(idParada, linea, segundo);
                listaTiemposPoste.add(paradaLineaDeBus);
            }

        }

        Collections.sort(listaTiemposPoste, new Comparator<TiempoBus>() {
            @Override
            public int compare(TiempoBus o1, TiempoBus o2) {
                int comp = o1.minutos - o2.minutos;
                return comp;
            }
        });
        return listaTiemposPoste;
    }

}
