package com.artrointel.everyonescomplication.crypto.model;

import androidx.annotation.NonNull;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CryptoAPI {
    private static String cryptoUri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
    private String mUserApiKey;
    CryptoAPI(String apiKey) {
        mUserApiKey = apiKey;
    }

    public String queryTopPrices(@NonNull Integer n)
            throws URISyntaxException, IOException {
        String response_content = "";

        URIBuilder query = new URIBuilder(cryptoUri);

        List<NameValuePair> queryParams = new ArrayList<>();

        queryParams.add(new BasicNameValuePair("start","1"));
        queryParams.add(new BasicNameValuePair("limit",n.toString()));
        queryParams.add(new BasicNameValuePair("convert","USD"));

        query.addParameters(queryParams);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", mUserApiKey);

        CloseableHttpResponse response = client.execute(request);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return response_content;
    }
}
