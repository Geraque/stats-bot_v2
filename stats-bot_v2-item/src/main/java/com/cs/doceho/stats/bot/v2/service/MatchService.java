package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Player;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchService {

  MatchRepository matchRepository;

  public List<MatchItem> getAll() {
    return matchRepository.findAll();
  }

  public MatchItem get(UUID matchId) {
    return matchRepository.findById(matchId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Match not found for this id : " + matchId));
  }

  public List<MatchItem> getByName(String playerName) {
    return getAll()
        .stream()
        .filter(m -> m.getPlayerName().getName().equals(playerName))
        .sorted(Comparator.comparing(MatchItem::getDate).reversed())
        .collect(Collectors.toList());
  }

  public Player getPlayerStats(String playerName) {
    Player player = Player.builder()
        .name(playerName)
        .matches(0)
        .rating((double) 0)
        .smokeKill(0)
        .openKill(0)
        .threeKill(0)
        .fourKill(0)
        .ace(0)
        .flash(0)
        .trade(0)
        .wallBang(0)
        .clutchFive(0)
        .clutchFour(0)
        .clutchOne(0)
        .clutchThree(0)
        .clutchTwo(0)
        .build();

    getAll().stream()
        .filter(match -> match.getPlayerName().getName().equals(playerName))
        .forEach(match -> {
          player.setMatches(player.getMatches() + 1);
          player.setRating(player.getRating() + match.getRating());
          player.setSmokeKill(player.getSmokeKill() + match.getSmokeKill());
          player.setOpenKill(player.getOpenKill() + match.getOpenKill());
          player.setThreeKill(player.getThreeKill() + match.getThreeKill());
          player.setFourKill(player.getFourKill() + match.getFourKill());
          player.setAce(player.getAce() + match.getAce());
          player.setFlash(player.getFlash() + match.getFlash());
          player.setTrade(player.getTrade() + match.getTrade());
          player.setWallBang(player.getWallBang() + match.getWallBang());
          player.setClutchOne(player.getClutchOne() + match.getClutchOne());
          player.setClutchTwo(player.getClutchTwo() + match.getClutchTwo());
          player.setClutchThree(player.getClutchThree() + match.getClutchThree());
          player.setClutchFour(player.getClutchFour() + match.getClutchFour());
          player.setClutchFive(player.getClutchFive() + match.getClutchFive());
        });

    if (player.getMatches() > 0) {
      player.setRating(player.getRating() / player.getMatches());
      player.setSmokeKill(player.getSmokeKill() / player.getMatches());
      player.setOpenKill(player.getOpenKill() / player.getMatches());
      player.setThreeKill(player.getThreeKill() / player.getMatches());
      player.setFourKill(player.getFourKill() / player.getMatches());
      player.setAce(player.getAce() / player.getMatches());
      player.setFlash(player.getFlash() / player.getMatches());
      player.setTrade(player.getTrade() / player.getMatches());
      player.setWallBang(player.getWallBang() / player.getMatches());
      player.setClutchOne(player.getClutchOne() / player.getMatches());
      player.setClutchTwo(player.getClutchTwo() / player.getMatches());
      player.setClutchThree(player.getClutchThree() / player.getMatches());
      player.setClutchFour(player.getClutchFour() / player.getMatches());
      player.setClutchFive(player.getClutchFive() / player.getMatches());
    }

    return player;
  }

  public List<Player> getAllStats() {
    // Группируем статистику по имени игрока
    return getAll().stream()
        .collect(Collectors.groupingBy(
            matchItem -> matchItem.getPlayerName().getName(),
            Collectors.toList()))
        .entrySet()
        .stream()
        .map(entry -> {
          String playerName = entry.getKey();
          List<MatchItem> matches = entry.getValue();

          Player player = new Player();
          player.setName(playerName);
          player.setMatches(matches.size());

          player.setRating(matches.stream().mapToDouble(MatchItem::getRating).sum() / matches.size());
          player.setSmokeKill(matches.stream().mapToInt(MatchItem::getSmokeKill).sum() / matches.size());
          player.setOpenKill(matches.stream().mapToInt(MatchItem::getOpenKill).sum() / matches.size());
          player.setThreeKill(matches.stream().mapToInt(MatchItem::getThreeKill).sum() / matches.size());
          player.setFourKill(matches.stream().mapToInt(MatchItem::getFourKill).sum() / matches.size());
          player.setAce(matches.stream().mapToInt(MatchItem::getAce).sum() / matches.size());
          player.setFlash(matches.stream().mapToInt(MatchItem::getFlash).sum() / matches.size());
          player.setTrade(matches.stream().mapToInt(MatchItem::getTrade).sum() / matches.size());
          player.setWallBang(matches.stream().mapToInt(MatchItem::getWallBang).sum() / matches.size());
          player.setClutchOne(matches.stream().mapToInt(MatchItem::getClutchOne).sum() / matches.size());
          player.setClutchTwo(matches.stream().mapToInt(MatchItem::getClutchTwo).sum() / matches.size());
          player.setClutchThree(matches.stream().mapToInt(MatchItem::getClutchThree).sum() / matches.size());
          player.setClutchFour(matches.stream().mapToInt(MatchItem::getClutchFour).sum() / matches.size());
          player.setClutchFive(matches.stream().mapToInt(MatchItem::getClutchFive).sum() / matches.size());

          return player;
        })
        .collect(Collectors.toList());
  }

  public String[] getRating() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getRating();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getRating();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getRating();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getRating();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getRating();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    //Выяснение топ 1
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getOpenKill() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getOpenKill();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getOpenKill();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getOpenKill();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getOpenKill();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getOpenKill();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getFlash() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getFlash();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getFlash();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getFlash();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getFlash();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getFlash();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getTrade() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getTrade();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getTrade();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getTrade();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getTrade();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getTrade();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getWallBang() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getWallBang();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getWallBang();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getWallBang();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getWallBang();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getWallBang();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getThreeKill() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getThreeKill();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getThreeKill();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getThreeKill();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getThreeKill();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getThreeKill();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getFourKill() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getFourKill();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getFourKill();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getFourKill();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getFourKill();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getFourKill();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getAce() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getAce();
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getAce();
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getAce();
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getAce();
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] += 1;
        nekit[1] += des.getAce();
      }
    }
    int num = 0;
    double max = desmond[1] / desmond[0];
    double[] arr = {desmond[1] / desmond[0], blackVision[1] / blackVision[0],
        tilt[1] / tilt[0], gloxinia[1] / gloxinia[0], nekit[1] / nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]),
          String.valueOf(desmond[1] / desmond[0])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1] / blackVision[0])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1] / tilt[0])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]),
          String.valueOf(gloxinia[1] / gloxinia[0])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1] / nekit[0])};
    }
    return res;
  }

  public String[] getClutches() {
    List<MatchItem> doceho = getAll();

    int[] desmond = new int[7];
    int[] blackVision = new int[7];
    int[] gloxinia = new int[7];
    int[] tilt = new int[7];
    int[] nekit = new int[7];

    for (MatchItem des : doceho) {
      if (des.getPlayerName().equals(PlayerName.DESMOND)) {
        desmond[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        desmond[1] += des.getClutchOne();
        desmond[2] += des.getClutchTwo();
        desmond[3] += des.getClutchThree();
        desmond[4] += des.getClutchFour();
        desmond[5] += des.getClutchFive();
        desmond[6] += 1;
      } else if (des.getPlayerName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        blackVision[1] += des.getClutchOne();
        blackVision[2] += des.getClutchTwo();
        blackVision[3] += des.getClutchThree();
        blackVision[4] += des.getClutchFour();
        blackVision[5] += des.getClutchFive();
        blackVision[6] += 1;
      } else if (des.getPlayerName().equals(PlayerName.B4ONE)) {
        tilt[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        tilt[1] += des.getClutchOne();
        tilt[2] += des.getClutchTwo();
        tilt[3] += des.getClutchThree();
        tilt[4] += des.getClutchFour();
        tilt[5] += des.getClutchFive();
        tilt[6] += 1;
      } else if (des.getPlayerName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        gloxinia[1] += des.getClutchOne();
        gloxinia[2] += des.getClutchTwo();
        gloxinia[3] += des.getClutchThree();
        gloxinia[4] += des.getClutchFour();
        gloxinia[5] += des.getClutchFive();
        gloxinia[6] += 1;
      } else if (des.getPlayerName().equals(PlayerName.NEKIT)) {
        nekit[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        nekit[1] += des.getClutchOne();
        nekit[2] += des.getClutchTwo();
        nekit[3] += des.getClutchThree();
        nekit[4] += des.getClutchFour();
        nekit[5] += des.getClutchFive();
        nekit[6] += 1;
      }
    }
    int num = 0;
    int max = desmond[0];
    int[] arr = {desmond[0], blackVision[0], tilt[0], gloxinia[0], nekit[0]};
    for (int i = 1; i < 5; i++) {
      if (arr[i] > max) {
        num = i;
        max = arr[i];
      }
    }
    String[] res;
    if (num == 0) {
      res = new String[]{"Desmond", String.valueOf(desmond[0]), String.valueOf(desmond[1]),
          String.valueOf(desmond[2]), String.valueOf(desmond[3]),
          String.valueOf(desmond[4]), String.valueOf(desmond[5]), String.valueOf(desmond[6])};
    } else if (num == 1) {
      res = new String[]{"BlackVision", String.valueOf(blackVision[0]),
          String.valueOf(blackVision[1]),
          String.valueOf(blackVision[2]), String.valueOf(blackVision[3]),
          String.valueOf(blackVision[4]), String.valueOf(blackVision[5]),
          String.valueOf(blackVision[6])};
    } else if (num == 2) {
      res = new String[]{"B4one", String.valueOf(tilt[0]), String.valueOf(tilt[1]),
          String.valueOf(tilt[2]), String.valueOf(tilt[3]),
          String.valueOf(tilt[4]), String.valueOf(tilt[5]), String.valueOf(tilt[6])};
    } else if (num == 3) {
      res = new String[]{"Gloxinia", String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]),
          String.valueOf(gloxinia[2]), String.valueOf(gloxinia[3]),
          String.valueOf(gloxinia[4]), String.valueOf(gloxinia[5]), String.valueOf(gloxinia[6])};
    } else {
      res = new String[]{"221w33", String.valueOf(nekit[0]), String.valueOf(nekit[1]),
          String.valueOf(nekit[2]), String.valueOf(nekit[3]),
          String.valueOf(nekit[4]), String.valueOf(nekit[5]), String.valueOf(nekit[6])};
    }
    return res;
  }

  @Transactional
  public MatchItem create(Match match) {
    MatchItem matchItem = MatchItem.builder()
        .playerName(PlayerName.fromName(match.getPlayerName()))
        .date(match.getDate())
        .rating(match.getRating())
        .smokeKill(match.getSmokeKill())
        .openKill(match.getOpenKill())
        .threeKill(match.getThreeKill())
        .fourKill(match.getFourKill())
        .ace(match.getAce())
        .flash(match.getFlash())
        .trade(match.getTrade())
        .wallBang(match.getWallBang())
        .clutchOne(match.getClutchOne())
        .clutchTwo(match.getClutchTwo())
        .clutchThree(match.getClutchThree())
        .clutchFour(match.getClutchFour())
        .clutchFive(match.getClutchFive())
        .type(MatchType.fromName(match.getType()))
        .build();

    return matchRepository.save(matchItem);
  }

  @Transactional
  public MatchItem update(UUID matchId,
      Match matchDetails) {
    MatchItem matchItem = get(matchId);

    matchItem.setPlayerName(PlayerName.fromName(matchDetails.getPlayerName()));
    matchItem.setDate(matchDetails.getDate());
    matchItem.setRating(matchDetails.getRating());
    matchItem.setSmokeKill(matchDetails.getSmokeKill());
    matchItem.setOpenKill(matchDetails.getOpenKill());
    matchItem.setThreeKill(matchDetails.getThreeKill());
    matchItem.setFourKill(matchDetails.getFourKill());
    matchItem.setAce(matchDetails.getAce());
    matchItem.setFlash(matchDetails.getFlash());
    matchItem.setTrade(matchDetails.getTrade());
    matchItem.setWallBang(matchDetails.getWallBang());
    matchItem.setClutchOne(matchDetails.getClutchOne());
    matchItem.setClutchTwo(matchDetails.getClutchTwo());
    matchItem.setClutchThree(matchDetails.getClutchThree());
    matchItem.setClutchFour(matchDetails.getClutchFour());
    matchItem.setClutchFive(matchDetails.getClutchFive());
    matchItem.setType(MatchType.fromName(matchDetails.getType()));

    return matchRepository.save(matchItem);
  }

  public void delete(UUID id) {
    matchRepository.deleteById(id);
  }

}
