package site.selector.trade.arbitrage.livecoin.service;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.function.BiConsumer;

import static site.selector.trade.arbitrage.livecoin.utils.LiveCoinSignUtils.createSignature;

/**
 * Created by Stepan Litvinov on 08.11.17.
 */
@Service
public class LiveCoinRequestService {


    @Value("${arbitrage.livecoin.baseUrl}")
    public String baseUrl;
    @Value("${arbitrage.livecoin.apiKey}")
    private String apiKey;
    @Value("${arbitrage.livecoin.secKey}")
    public String secKey;
    @Value("${arbitrage.livecoin.permitsPerSecond}")
    private double permitsPerSecond;

    private RateLimiter rateLimiter;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        rateLimiter = RateLimiter.create(permitsPerSecond);

    }

    public <T> T post(String url, Map<String, String> postData, ParameterizedTypeReference<T> responseType) {
        String queryString = buildQueryString(postData);
        String signature = createSignature(queryString, secKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", apiKey);
        headers.set("Sign", signature);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        postData.forEach(map::add);
        HttpEntity<?> entity = new HttpEntity<>(map, headers);
//        URI uri = createUri(url, queryString);
        try {
            return exchange(new URI(baseUrl + url), responseType, entity, HttpMethod.POST).getBody();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

//        String queryString = buildQueryString(postData);
//        String signature = createSignature(queryString, secKey);
//        URI uri = createUri(url, queryString);
//        URL queryUrl = null;
//        try {
//            queryUrl = new URL(uri.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection connection = null;
//        StringBuilder sb = null;
//        try {
//            connection = (HttpURLConnection)queryUrl.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Api-Key", apiKey);
//            connection.setRequestProperty("Sign", signature);
//            connection.getOutputStream().write(queryString.getBytes());
//
//            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            sb = new StringBuilder();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line + '\n');
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public <T> T get(String url, Map<String, String> uriVariables, ParameterizedTypeReference<T> responseType) {
        String queryString = buildQueryString(uriVariables);
        String signature = createSignature(queryString, secKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", apiKey);
        headers.set("Sign", signature);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        URI uri = createUri(url, queryString);
        return exchange(uri, responseType, entity, HttpMethod.GET).getBody();
    }

    private URI createUri(String url, String queryString) {
        URI uri;
        try {
            String result = Joiner.on("?").join(ImmutableList.of(baseUrl + url, queryString));
            uri = new URI(result);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    private <T> ResponseEntity<T> exchange(URI url, ParameterizedTypeReference<T> responseType,
                                           HttpEntity<?> entity, HttpMethod httpMethod) {
        rateLimiter.acquire();
        return restTemplate.exchange(url, httpMethod, entity, responseType);
    }


    private static String buildQueryString(Map<String, String> args) {
        StringBuilder result = new StringBuilder();
        args.keySet().stream().sorted().forEach(hashKey -> {
            if (result.length() > 0) {
                result.append('&');
            }
            try {
                result.append(URLEncoder.encode(hashKey, "UTF-8"))
                      .append('=').append(URLEncoder.encode(args.get(hashKey), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return result.toString();
    }
}
