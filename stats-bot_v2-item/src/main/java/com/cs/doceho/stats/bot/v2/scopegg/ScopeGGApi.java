package com.cs.doceho.stats.bot.v2.scopegg;

// ScopeGGApi.java

import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.scopegg.dto.GameInfo;
import com.cs.doceho.stats.bot.v2.scopegg.dto.PlayerStat;
import com.cs.doceho.stats.bot.v2.scopegg.dto.ScopeGGMatch;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScopeGGApi {

    static String URL = "https://app.scope.gg/api/matches/getMyMatches";
    static String URL_REVIEW = "https://app.scope.gg/api/matches/getMatchReviewDataBatch";

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

    /**
     * Выполняет POST-запрос на /getMyMatches с заголовком Cookie и возвращает историю матчей.
     *
     * @param cookie значение заголовка Cookie (например, "sid=...; Path=/; ...")
     */
    @SneakyThrows
    public List<ScopeGGMatch> getHistory(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<String> entity = new HttpEntity<>("{\n" +
            "  \"filter\": {\n" +
            "    \"sources\": [],\n" +
            "    \"maps\": [],\n" +
            "    \"tags\": [],\n" +
            "    \"favouriteOnly\": false,\n" +
            "    \"bannedOnly\": false\n" +
            "  },\n" +
            "  \"sort\": {\n" +
            "    \"by\": \"date\",\n" +
            "    \"direction\": -1\n" +
            "  },\n" +
            "  \"offset\": 0,\n" +
            "  \"limit\": 15\n" +
            "}", headers);

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
        List<RawMatch> raw = mapper.readValue(resp.getBody(), new TypeReference<>() {
        });
        List<ScopeGGMatch> out = new ArrayList<>(raw.size());
        for (RawMatch r : raw) {
            String id = r.matchID != null ? r.matchID : "";
            String finishedAt = String.valueOf(r.matchTime); // приводится к строке по требованию
            boolean hasWon = false;
            boolean myResult = false;
            for (RawMatch.TeamInfos team : r.teams) {
                if (team.isUserTeam) {
                    myResult = team.won;
                }
                if (team.won) {
                    hasWon = true;
                }
            }
            MatchResult result;
            if (!hasWon) result = MatchResult.DRAW;
            else if (myResult) result = MatchResult.WIN;
            else result = MatchResult.LOSE;

            out.add(new ScopeGGMatch(id, finishedAt, result));
        }
        return out;
    }

    /**
     * Получает агрегированную информацию по матчу и маппит её в GameInfo.
     * В payload постоянны blocks и steamAccountId, варьируется только matchId.
     * Для авторизации повторно передаётся cookie (как и в getHistory()).
     */
    @SneakyThrows
    public GameInfo getGameInfo(String matchId) {
        // 1) Заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2) Тело запроса (всё постоянно, кроме matchId)
        ObjectNode body = mapper.createObjectNode();
        body.put("matchId", matchId);
        ArrayNode blocks = body.putArray("blocks");
        List<String> fixedBlocks = Arrays.asList(
            "base",
            "round_infos",
            "scoreboard_teams_data",
            "scoreboard_values",
            "map",
            "weapons",
            "match_data",
            "records",
            "highlights",
            "mistakes",
            "fun_labels",
            "clutches"
        );
        fixedBlocks.forEach(blocks::add);
        body.put("steamAccountId", 152932932);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(body), headers);

        // 3) Вызов
        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(URL_REVIEW, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            throw new IOException("HTTP " + e.getRawStatusCode() + " " + e.getStatusText() + ": " + e.getResponseBodyAsString(), e);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return null;
        }

        // 4) Парсинг JSON
        JsonNode root = mapper.readTree(resp.getBody());

        // map.csgoName -> MapType
        String csName = optText(root.path("map").path("csgoName"));
        MapType mapType = MapType.fromCSName(csName); // см. пример csgoName="de_dust2" :contentReference[oaicite:12]{index=12}

        // match_data.MatchTime (в примере — миллисекунды; приводится к секундам для int)
        long finishedAt = root.path("match_data").path("MatchTime").asLong(0L); // мс

        // match_data.Gamemode
        String dataSource = optText(root.path("match_data").path("Gamemode")); // "premier" :contentReference[oaicite:14]{index=14}

        // playerStats из scoreboard_values
        JsonNode scoreboardValues = root.path("scoreboard_values"); // :contentReference[oaicite:15]{index=15}
        JsonNode highlights = root.path("highlights");              // набор счётчиков по игрокам :contentReference[oaicite:16]{index=16}
        JsonNode clutches = root.path("clutches");                  // структура клатчей (CT/T → Won/Lost) :contentReference[oaicite:17]{index=17}

        List<PlayerStat> stats = new ArrayList<>();
        if (scoreboardValues.isObject()) {
            Iterator<String> it = scoreboardValues.fieldNames();
            while (it.hasNext()) {
                String playerId = it.next();
                JsonNode p = scoreboardValues.path(playerId);
                JsonNode basic = p.path("GeneralStats").path("ScoreboardPages").path("Basic"); // :contentReference[oaicite:18]{index=18}

                double rating2 = basic.path("Rating2").asDouble(0d);
                int openKills = basic.path("OpenKills").asInt(0);
                int tradeKills = basic.path("TradeKills").asInt(0);

                JsonNode h = highlights.path(playerId); // может отсутствовать
                int smoke = h.path("KillsThroughSmoke").path("Value").asInt(0);   // :contentReference[oaicite:19]{index=19}
                int wallbang = h.path("WallbangKills").path("Value").asInt(0);    // :contentReference[oaicite:20]{index=20}
                int flashA = h.path("FlashAssistsReal").path("Value").asInt(0);   // :contentReference[oaicite:21]{index=21}
                int triple = h.path("TripleKills").path("Value").asInt(0);        // см. highlights-структуру :contentReference[oaicite:22]{index=22}
                int quad = h.path("QuadrupleKills").path("Value").asInt(0);
                int penta = h.path("PentaKills").path("Value").asInt(0);

                // Клатчи из clutches
                int[] clutch = new int[6]; // 1..5
                JsonNode playerClutches = clutches.path(playerId);
                if (playerClutches.isObject()) {
                    // обе стороны: CT, T
                    for (String side : new String[]{"CT", "T"}) {
                        JsonNode sideNode = playerClutches.path(side);
                        if (!sideNode.isObject()) continue;
                        JsonNode won = sideNode.path("Won");
                        if (won.isArray()) {
                            for (JsonNode w : won) {
                                int enemies = w.path("EnemiesCount").asInt(0);
                                if (enemies >= 1 && enemies <= 5) clutch[enemies] += 1;
                            }
                        }
                    }
                }

                PlayerStat ps = new PlayerStat();
                ps.setId(playerId);
                ps.setHltvRating(rating2);
                ps.setOpenKill(openKills);
                ps.setTradeKillsSucceeded(tradeKills);
                ps.setSmokeKill(smoke);
                ps.setWallBang(wallbang);
                ps.setFlashAssist(flashA);
                ps.setMulti3k(triple);
                ps.setMulti4k(quad);
                ps.setMulti5k(penta);
                ps.setClutchOne(clutch[1]);
                ps.setClutchTwo(clutch[2]);
                ps.setClutchThree(clutch[3]);
                ps.setClutchFour(clutch[4]);
                ps.setClutchFive(clutch[5]);

                stats.add(ps);
            }
        }

        return new GameInfo(mapType, finishedAt, dataSource, stats);
    }

    // безопасное чтение текстовых полей
    private static String optText(JsonNode node) {
        return (node == null || node.isMissingNode() || node.isNull()) ? "" : node.asText("");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RawMatch {
        @JsonProperty("MatchID")
        String matchID;
        @JsonProperty("MatchTime")
        long matchTime;
        @JsonProperty("TeamInfos")
        List<TeamInfos> teams;

        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class TeamInfos {
            @JsonProperty("Won")
            Boolean won;
            @JsonProperty("IsUserTeam")
            Boolean isUserTeam;
        }
    }
}