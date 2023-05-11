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
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import abeljs.xzaragoza.data.BusPostes;

public class BusquedaBusPostesAPI {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void getBusPostes(BusquedaBusPostesCallback callback, String numBus) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<BusPostes> listaBusPostes = getBusPostesSynchronous(numBus);
                    callback.onBusquedaBusPostesComplete(listaBusPostes);
                } catch (SAXException e) {
                    callback.onBusquedaBusPostesError("ERROR al obtener los postes.");
                } catch (IOException e) {
                    callback.onBusquedaBusPostesError("ERROR al obtener los postes.");
                } catch (ParserConfigurationException e) {
                    callback.onBusquedaBusPostesError("ERROR al obtener los postes.");
                }
            }
        });
    }

    public ArrayList<BusPostes> getBusPostesSynchronous(String numBus)
            throws SAXException, IOException, ParserConfigurationException, RuntimeException {
        ArrayList<BusPostes> listaBusPostes = new ArrayList<>();

        if (numBus.toUpperCase().equals("TUR")) {
            return null;
        }

        URL url = new URL("https://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/linea-autobus/" + numBus + ".xml");

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

            Log.e("prueba API", numBus);
            Element elementoResultado = (Element) respuesta.item(0);
            NodeList busPostesRespuesta = elementoResultado.getElementsByTagName("result").item(0).getChildNodes();
            String numPoste, destino, primerDestino = "";
            int contador;


            for (int i = 1; i < busPostesRespuesta.getLength(); i++) {
                contador = i;
                NodeList numPosteSize = elementoResultado.getElementsByTagName("description");
                if (numPosteSize.getLength() > 1 && numPosteSize.item(i) != null) {

                    numPoste = numPosteSize.item(i).getTextContent().replace("Poste ", "");

                    destino = getDestinoPoste(numPoste, numBus);
                    if (destino != null) {
//                        if (contador == 1) {
//                            primerDestino = destino;
//                        }
//                        if (!primerDestino.equals(destino)) {
//                            contador = 1;
//                        }
                        BusPostes busPostes = new BusPostes(numPoste, numBus, destino, contador);
                        listaBusPostes.add(busPostes);
                    }
                }
            }
        }
        return listaBusPostes;
    }


    public String getDestinoPoste(String numPoste, String numBus)
            throws SAXException, IOException, ParserConfigurationException {

        URL url = new URL("https://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/tuzsa-" + numPoste + ".xml");

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.connect();

        int codigoRespuesta = conn.getResponseCode();

        try {

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
                NodeList busPostesRespuesta = elementoResultado.getElementsByTagName("destino").item(0).getChildNodes();


                for (int i = 0, contador = 0; i < busPostesRespuesta.getLength(); i += 2, contador++) {
                    String numBusComprobacion = elementoResultado.getElementsByTagName("linea").item(contador).getTextContent();

                    if (numBusComprobacion.equals(numBus)) {
                        String destino = elementoResultado.getElementsByTagName("destino").item(i + 1).getTextContent();
                        Log.e("prueba Destinos", numBus + " " + numPoste + " " + destino);
                        return destino;
                    }
                }

                return null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e("prueba Exception", numPoste + " " + numBus);
        }
        return null;
    }
}
