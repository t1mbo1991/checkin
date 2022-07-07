package chicken.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExamWebCrawler {

    public static boolean isValidId(String id, String examName) throws Exception {
        URI uri = null;
        uri = new URI(
            "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
                + id + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().contains(examName);
    }
}
