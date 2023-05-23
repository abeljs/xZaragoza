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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import abeljs.xzaragoza.data.Noticias;

public class BusquedaNoticiasAPI {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void getNoticias(BusquedaNoticiasCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Noticias> listaNoticias = getNoticiasSynchronous();
                    callback.onBusquedaNoticiasComplete(listaNoticias);
                } catch (SAXException e) {
                    e.printStackTrace();
                    callback.onBusquedaNoticiasError("ERROR al obtener las noticias.");
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onBusquedaNoticiasError("ERROR al obtener las noticias.");
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    callback.onBusquedaNoticiasError("ERROR al obtener las noticias.");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    callback.onBusquedaNoticiasError("ERROR al obtener las noticias.");
                } catch (ParseException e) {
                    e.printStackTrace();
                    callback.onBusquedaNoticiasError("ERROR al obtener las noticias.");
                }
            }
        });
    }

    public List<Noticias> getNoticiasSynchronous()
            throws SAXException, IOException, ParserConfigurationException, ParseException {

        List<Noticias> listaNoticias = new ArrayList<>();

        URL url = new URL("https://www.zaragoza.es/sede/servicio/noticia.xml");

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
            NodeList noticiasRespuesta = elementoResultado.getElementsByTagName("noticia");
            String titulo, urlNoticia;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date fecha = new Date();

            for (int i = 0; i < noticiasRespuesta.getLength(); i++) {
                urlNoticia = "https://www.zaragoza.es/sede/servicio/noticia/" +
                        ((Element) noticiasRespuesta.item(i)).getElementsByTagName("id").item(0).getTextContent().trim();
                titulo = ((Element) noticiasRespuesta.item(i)).getElementsByTagName("title").item(0).getTextContent().trim();
                fecha = dateFormat.parse(((Element) noticiasRespuesta.item(i)).getElementsByTagName("dateCreated").item(0).getTextContent().trim());

                Noticias noticia = new Noticias(urlNoticia, titulo, fecha);
                Log.e("pruebaNoticias", noticia.toString());
                listaNoticias.add(noticia);
            }
        }
        return listaNoticias;
    }
}
