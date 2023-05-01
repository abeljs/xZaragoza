package abeljs.xzaragoza.apis;

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

import abeljs.xzaragoza.data.TiempoBus;

public class BusquedaPosteAPI {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void buscarPoste(BusquedaPosteCallback callback, String numPoste) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<TiempoBus> listaParadasLinea = buscarPosteSynchronous(numPoste);
                    callback.onBusquedaPosteComplete(listaParadasLinea);
                } catch (SAXException e) {
                    callback.onBusquedaPosteError("ERROR al obtener las lineas de buses.");
                } catch (IOException e) {
                    callback.onBusquedaPosteError("ERROR al obtener las lineas de buses.");
                } catch (ParserConfigurationException e) {
                    callback.onBusquedaPosteError("ERROR al obtener las lineas de buses.");
                } catch (RuntimeException e) {
                    callback.onBusquedaPosteError("ERROR número de poste incorrecto.");
                }
            }
        });
    }

    public ArrayList<TiempoBus> buscarPosteSynchronous(String numPoste)
            throws SAXException, IOException, ParserConfigurationException {
        ArrayList<TiempoBus> listaParadasLinea = new ArrayList<>();

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
//            String tituloParada = elementoResultado.getElementsByTagName("title").item(0).getTextContent()
//                    .substring(0, cadenaInformacion.indexOf("Línea"))
//                    .replace("(", "")
//                    .replaceFirst(" ", " - ").trim();

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
                listaParadasLinea.add(paradaLineaDeBus);

                int segundo;

                try {
                    segundo = Integer.valueOf(elementoResultado.getElementsByTagName("segundo").item(contador).getTextContent().split(" ")[0]);
                } catch (NumberFormatException e) {
                    segundo = 0;
                }

                paradaLineaDeBus = new TiempoBus(idParada, linea, segundo);
                listaParadasLinea.add(paradaLineaDeBus);
            }

        }

        Collections.sort(listaParadasLinea, new Comparator<TiempoBus>() {
            @Override
            public int compare(TiempoBus o1, TiempoBus o2) {
                int comp = o1.minutos - o2.minutos;
                return comp;
            }
        });
        return listaParadasLinea;
    }

}
