package ru.yandex.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.hit.HitDto;

@Service
public class HitClient extends BaseClient {
    private static final String API_PREFIX = "/hit";

    @Autowired
    public HitClient(@Value("${http-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super( builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> hitEndpoint(HitDto requestDto) {
        return post("", requestDto);
    }
}