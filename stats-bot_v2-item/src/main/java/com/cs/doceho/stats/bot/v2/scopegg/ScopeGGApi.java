package com.cs.doceho.stats.bot.v2.scopegg;

// ScopeGGApi.java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScopeGGApi {

    private static final String URL = "https://app.scope.gg/api/matches/getMyMatches";

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

    /**
     * Выполняет POST-запрос на /getMyMatches с заголовком Cookie и возвращает историю матчей.
     * @param cookie значение заголовка Cookie (например, "sid=...; Path=/; ...")
     */
    @SneakyThrows
    public List<ScopeGGMatch> getHistory(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, cookie);

        // Тело запроса: пустой JSON, т.к. дополнительные параметры не предоставлены
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            // Прозрачная ошибка HTTP
            throw new IOException("HTTP " + e.getRawStatusCode() + " " + e.getStatusText() + ": " + e.getResponseBodyAsString(), e);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return Collections.emptyList();
        }

        // Топ-уровнево ответ — массив объектов с полями MatchID, MatchTime
        List<RawMatch> raw = mapper.readValue(resp.getBody(), new TypeReference<List<RawMatch>>() {});
        List<ScopeGGMatch> out = new ArrayList<>(raw.size());
        for (RawMatch r : raw) {
            String id = r.matchID != null ? r.matchID : "";
            String finishedAt = String.valueOf(r.matchTime); // приводится к строке по требованию
            out.add(new ScopeGGMatch(id, finishedAt));
        }
        return out;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RawMatch {
        @JsonProperty("MatchID")
        String matchID;

        @JsonProperty("MatchTime")
        long matchTime;
    }
}


