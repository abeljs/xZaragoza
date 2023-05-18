package abeljs.xzaragoza.apis;


import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import abeljs.xzaragoza.data.Postes;

public class BusquedaPostesAPI {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void getPostes(BusquedaPostesCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Postes> listaParadasLinea = getPostesSynchronous();
                    callback.onBusquedaPostesComplete(listaParadasLinea);
                } catch (SAXException e) {
                    callback.onBusquedaPostesError("ERROR al obtener los postes.");
                } catch (IOException e) {
                    callback.onBusquedaPostesError("ERROR al obtener los postes.");
                } catch (ParserConfigurationException e) {
                    callback.onBusquedaPostesError("ERROR al obtener los postes.");
                }
                catch (RuntimeException e) {
                    callback.onBusquedaPostesError("ERROR al obtener los postes.");
                }
            }
        });
    }


    public ArrayList<Postes> getPostesSynchronous()
            throws SAXException, IOException, ParserConfigurationException {
        ArrayList<Postes> listaPostes = new ArrayList<>();

        URL url = new URL("https://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus.xml");

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

            NodeList respuesta = doc.getElementsByTagName("resultado");

            if (respuesta.getLength() == 0) {
                return null;
            }

            Element elementoResultado = (Element) respuesta.item(0);
            NodeList postesRespuesta = elementoResultado.getElementsByTagName("result").item(0).getChildNodes();
            String numPoste, nombrePoste;

            for (int i = 0; i < postesRespuesta.getLength(); i++) {
                nombrePoste = elementoResultado.getElementsByTagName("title").item(i).getTextContent();
                numPoste = nombrePoste.substring(nombrePoste.indexOf("(") + 1, nombrePoste.indexOf(")"));

                String[] temporal = nombrePoste.replaceFirst("\\)", "\\$").replace("Línea", "$").split("\\$");


                String nombrePosteLimpio = temporal.length > 0 ? temporal[1] : nombrePoste;


                Postes poste = new Postes(numPoste, nombrePosteLimpio);
                listaPostes.add(poste);
            }

        }
        return listaPostes;
    }

}
