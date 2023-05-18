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
                    List<Buses> listaLineasDeBus = getBusesSynchronous();
                    callback.onBusquedaLineasDeBusesComplete(listaLineasDeBus);
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
        List<Buses> listaLineasBus = new ArrayList<>();

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
            NodeList lineasBusRespuesta = elementoResultado.getElementsByTagName("result").item(0).getChildNodes();
            String numLineaBus, numLineaBusModificado, urlLineaBus, direccion1, direccion2;

            for (int i = 0; i < lineasBusRespuesta.getLength(); i++) {
                urlLineaBus = lineasBusRespuesta.item(i).getTextContent();
                numLineaBus = urlLineaBus.substring(urlLineaBus.lastIndexOf('/') + 1);

                if ((!numLineaBus.contains("_") && !numLineaBus.contains("R") && !numLineaBus.contains("T") && !numLineaBus.equals("1"))) {
                    if (numLineaBus.startsWith("N")) {
                        numLineaBusModificado = numLineaBus.substring(0, 1) + "0" + numLineaBus.substring(1);
                        direccion1 = buscarDireccionesSynchronous(numLineaBusModificado);
                    } else if (numLineaBus.startsWith("CI")) {
                        direccion1 = buscarDireccionesSynchronous(numLineaBus);
                    } else if (numLineaBus.equals("C1")) {
                        numLineaBusModificado = "0" + numLineaBus.substring(0);
                        direccion1 = buscarDireccionesSynchronous(numLineaBusModificado);
                    } else {
                        direccion1 = buscarDireccionesSynchronous(numLineaBus);
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

                    Buses lineaBus = new Buses(numLineaBus, direccion1, direccion2);
//                    Log.d("Prueba", numLineaBus);
                    listaLineasBus.add(lineaBus);
                }
            }
        }
        return listaLineasBus;
    }

    public static String buscarDireccionesSynchronous(String numLinea)
            throws SAXException, IOException, ParserConfigurationException {

        Log.e("prueba hola", numLinea);
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
