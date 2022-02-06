
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=iSKkTlPbVt8yguFGWaSpDo52ESdVueRZuk5z3g4x";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);

        // сохраняем в объект класса ResponceNASA
        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(body);
        ResponceNASA responceNASA = mapper.readValue(body, ResponceNASA.class);
        System.out.println(responceNASA);

        String targetFile = responceNASA.getUrl();
        int position = targetFile.lastIndexOf('/');
        String targetFileName = targetFile.substring(position + 1);

        // получаем изображение и сохраняем в файл
        HttpGet request1 = new HttpGet(targetFile);
        request1.setHeader(HttpHeaders.ACCEPT, ContentType.IMAGE_JPEG.getMimeType());
        CloseableHttpResponse response1 = httpClient.execute(request1);

        try (FileOutputStream fos = new FileOutputStream(targetFileName)) {
            byte[] inStream = response1.getEntity().getContent().readAllBytes();
            fos.write(inStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
