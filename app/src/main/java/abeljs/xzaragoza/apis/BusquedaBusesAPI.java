package abeljs.xzaragoza.apis;

import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import abeljs.xzaragoza.data.Buses;

import java.util.concurrent.Executors;

public class BusquedaBusesAPI {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void getBuses(BusquedaBusesCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Buses> listaBuses = getBusesSynchronous();
                    callback.onBusquedaLineasDeBusesComplete(listaBuses);
                } catch (SAXException e) {
                    callback.onBusquedaLineasDeBusesError("ERROR al obtener las lineas de buses.");
                } catch (IOException e) {
                    callback.onBusquedaLineasDeBusesError("ERROR al obtener las lineas de buses.");
                } catch (ParserConfigurationException e) {
                    callback.onBusquedaLineasDeBusesError("ERROR al obtener las lineas de buses.");
                } catch (RuntimeException e) {
                    callback.onBusquedaLineasDeBusesError("ERROR al obtener las lineas de buses.");
                }
            }
        });
    }

    public List<Buses> getBusesSynchronous()
            throws SAXException, IOException, ParserConfigurationException {
        List<Buses> listaBuses = new ArrayList<>();

        URL url = new URL("https://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/linea-autobus.xml");

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
            NodeList busesRespuesta = elementoResultado.getElementsByTagName("result").item(0).getChildNodes();
            String numBus, numBusModificado, urlBus, direccion1, direccion2;

            for (int i = 0; i < busesRespuesta.getLength(); i++) {
                urlBus = busesRespuesta.item(i).getTextContent();
                numBus = urlBus.substring(urlBus.lastIndexOf('/') + 1);

                if ((!numBus.contains("_") && !numBus.contains("R") && !numBus.contains("T") && !numBus.equals("1"))) {
                    if (numBus.startsWith("N")) {
                        numBusModificado = numBus.substring(0, 1) + "0" + numBus.substring(1);
                        direccion1 = buscarDireccionesSynchronous(numBusModificado);
                    } else if (numBus.startsWith("CI")) {
                        direccion1 = buscarDireccionesSynchronous(numBus);
                    } else if (numBus.equals("C1")) {
                        numBusModificado = "0" + numBus.substring(0);
                        direccion1 = buscarDireccionesSynchronous(numBusModificado);
                    } else {
                        direccion1 = buscarDireccionesSynchronous(numBus);
                    }

                    direccion2 = "";
                    if (direccion1.contains("-")) {
                        String[] destinos = direccion1.split("-");
                        for (int pos = destinos.length - 1; pos >= 0; pos--) {
                            if (pos == 0) {
                                direccion2 = direccion2 + destinos[pos].trim();
                            } else {
                                direccion2 = direccion2 + destinos[pos].trim() + " - ";
                            }
                        }
                    } else {
                        direccion2 = direccion1 + " -";
                    }

                    Buses lineaBus = new Buses(numBus, direccion1, direccion2);
//                    Log.d("Prueba", numLineaBus);
                    listaBuses.add(lineaBus);
                }
            }
        }
        return listaBuses;
    }

    public static String buscarDireccionesSynchronous(String numLinea)
            throws SAXException, IOException, ParserConfigurationException {

        URL url = new URL("https://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/equipamiento/linea-transporte/" + numLinea + ".xml");

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

            NodeList respuesta = doc.getElementsByTagName("linea-transporte");

            if (respuesta.getLength() == 0) {
                return null;
            }

            Element elementoResultado = (Element) respuesta.item(0);
            NodeList lineasBusRespuesta = elementoResultado.getElementsByTagName("name");
            String direcciones = lineasBusRespuesta.item(0).getTextContent();

            return direcciones.toUpperCase();
        }

    }
}
