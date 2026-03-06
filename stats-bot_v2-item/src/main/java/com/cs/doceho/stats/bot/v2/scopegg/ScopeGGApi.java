package com.cs.doceho.stats.bot.v2.scopegg;

import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
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

    static String HISTORY_URL = "https://app.scope.gg/api/matches/getMyMatches";
    static String MATCH_URL = "https://app.scope.gg/api/matches/getMatchReviewDataBatch";

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

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
            resp = restTemplate.exchange(HISTORY_URL, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            throw new IOException("HTTP " + e.getRawStatusCode() + " " + e.getStatusText() + ": " + e.getResponseBodyAsString(), e);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return Collections.emptyList();
        }

        List<RawMatch> raw = mapper.readValue(resp.getBody(), new TypeReference<>() {});
        List<ScopeGGMatch> out = new ArrayList<>(raw.size());
        for (RawMatch r : raw) {
            String id = r.matchID != null ? r.matchID : "";
            String finishedAt = String.valueOf(r.matchTime);
            out.add(new ScopeGGMatch(id, finishedAt));
        }
        return out;
    }

    @SneakyThrows
    public GameInfo getGameInfo(String matchId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

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

        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(MATCH_URL, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            throw new IOException("HTTP " + e.getRawStatusCode() + " " + e.getStatusText() + ": " + e.getResponseBodyAsString(), e);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return null;
        }

        JsonNode root = mapper.readTree(resp.getBody());

        String csName = optText(root.path("map").path("csgoName"));
        MapType mapType = MapType.fromCSName(csName);

        long finishedAt = root.path("match_data").path("MatchTime").asLong(0L);
        String dataSource = optText(root.path("match_data").path("Gamemode"));

        MatchResult result = resolveMatchResult(root.path("base"));

        JsonNode scoreboardValues = root.path("scoreboard_values");
        JsonNode highlights = root.path("highlights");
        JsonNode clutches = root.path("clutches");

        List<PlayerStat> stats = new ArrayList<>();
        if (scoreboardValues.isObject()) {
            Iterator<String> it = scoreboardValues.fieldNames();
            while (it.hasNext()) {
                String playerId = it.next();
                JsonNode p = scoreboardValues.path(playerId);
                JsonNode basic = p.path("GeneralStats").path("ScoreboardPages").path("Basic");

                double rating2 = basic.path("Rating2").asDouble(0d);
                int openKills = basic.path("OpenKills").asInt(0);
                int tradeKills = basic.path("TradeKills").asInt(0);

                JsonNode h = highlights.path(playerId);
                int smoke = h.path("KillsThroughSmoke").path("Value").asInt(0);
                int wallbang = h.path("WallbangKills").path("Value").asInt(0);
                int flashA = h.path("FlashAssistsReal").path("Value").asInt(0);
                int triple = h.path("TripleKills").path("Value").asInt(0);
                int quad = h.path("QuadrupleKills").path("Value").asInt(0);
                int penta = h.path("PentaKills").path("Value").asInt(0);

                int[] clutch = new int[6];
                JsonNode playerClutches = clutches.path(playerId);
                if (playerClutches.isObject()) {
                    for (String side : new String[]{"CT", "T"}) {
                        JsonNode sideNode = playerClutches.path(side);
                        if (!sideNode.isObject()) {
                            continue;
                        }
                        JsonNode won = sideNode.path("Won");
                        if (won.isArray()) {
                            for (JsonNode w : won) {
                                int enemies = w.path("EnemiesCount").asInt(0);
                                if (enemies >= 1 && enemies <= 5) {
                                    clutch[enemies] += 1;
                                }
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

        return new GameInfo(mapType, result, finishedAt, dataSource, stats);
    }

    private MatchResult resolveMatchResult(JsonNode baseNode) {
        if (!baseNode.isArray() || baseNode.size() < 2) {
            throw new IllegalStateException("ScopeGG response does not contain valid 'base' teams block");
        }

        Set<String> desmondScopeggIds = new HashSet<>(PlayerName.DESMOND.getScopeggIds());

        int desmondTeamIndex = findTeamIndexByPlayerId(baseNode, desmondScopeggIds);
        if (desmondTeamIndex < 0) {
            throw new IllegalStateException("Player DESMOND was not found in ScopeGG match base block");
        }

        boolean firstWon = baseNode.path(0).path("Won").asBoolean(false);
        boolean secondWon = baseNode.path(1).path("Won").asBoolean(false);

        if (!firstWon && !secondWon) {
            return MatchResult.DRAW;
        }

        boolean desmondTeamWon = baseNode.path(desmondTeamIndex).path("Won").asBoolean(false);
        return desmondTeamWon ? MatchResult.WIN : MatchResult.LOSE;
    }

    private int findTeamIndexByPlayerId(JsonNode baseNode, Set<String> targetPlayerIds) {
        for (int teamIndex = 0; teamIndex < baseNode.size(); teamIndex++) {
            JsonNode players = baseNode.path(teamIndex).path("Players");
            if (!players.isArray()) {
                continue;
            }

            for (JsonNode player : players) {
                String playerId = optText(player.path("PlayerID"));
                if (targetPlayerIds.contains(playerId)) {
                    return teamIndex;
                }
            }
        }
        return -1;
    }

    private static String optText(JsonNode node) {
        return (node == null || node.isMissingNode() || node.isNull()) ? "" : node.asText("");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RawMatch {
        @JsonProperty("MatchID")
        String matchID;

        @JsonProperty("MatchTime")
        long matchTime;
    }
}