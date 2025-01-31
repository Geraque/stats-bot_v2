package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import com.cs.doceho.stats.bot.v2.model.Match;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

  MatchRepository matchRepository;

  public List<MatchItem> getAll() {
    return matchRepository.findAll();
  }

  public MatchItem get(Long matchId) {
    return matchRepository.findById(matchId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Match not found for this id : " + matchId));
  }

  public List<MatchItem> getByName(String playerName) {
    //Получаем все матчи и ревёрсим, чтобы первыми шли последние матчи
    List<MatchItem> matchItems = getAll();
    Collections.reverse(matchItems);
    List<MatchItem> result = new ArrayList<>();
    int count = 0;
    //Выдаём только последние 7 матчей
    for (MatchItem des : matchItems) {
      if (des.getName().name().equals(playerName) && count < 7) {
        result.add(des);
        count++;
      }
    }
    return result;
  }

  public double[] getPlayerStats(String playerName) {
    List<MatchItem> matchItems = getAll();

    double[] arr = new double[15];
    //Распределение информации об игроке по различным категориям
    for (MatchItem des : matchItems) {
      if (des.getName().name().equals(playerName)) {
        arr[0]++;
        arr[1] += des.getRating();
        arr[2] += des.getSmokeKill();
        arr[3] += des.getOpenKill();
        arr[4] += des.getThreeKill();
        arr[5] += des.getFourKill();
        arr[6] += des.getAce();
        arr[7] += des.getFlash();
        arr[8] += des.getTrade();
        arr[9] += des.getWallBang();
        arr[10] += des.getClutchOne();
        arr[11] += des.getClutchTwo();
        arr[12] += des.getClutchThree();
        arr[13] += des.getClutchFour();
        arr[14] += des.getClutchFive();
      }
    }
    //Вычисление средней статистики за матч, а не общей
    for (int i = 1; i < 15; i++) {
      arr[i] /= arr[0];
    }
    return arr;
  }

  public double[] getAllStats() {
    List<MatchItem> matchItems = getAll();
    double[] arr = new double[75];
    for (MatchItem des : matchItems) {
      switch (des.getName()) {
        case DESMOND:
          arr[0]++;
          arr[1] += des.getRating();
          arr[2] += des.getSmokeKill();
          arr[3] += des.getOpenKill();
          arr[4] += des.getThreeKill();
          arr[5] += des.getFourKill();
          arr[6] += des.getAce();
          arr[7] += des.getFlash();
          arr[8] += des.getTrade();
          arr[9] += des.getWallBang();
          arr[10] += des.getClutchOne();
          arr[11] += des.getClutchTwo();
          arr[12] += des.getClutchThree();
          arr[13] += des.getClutchFour();
          arr[14] += des.getClutchFive();
          break;
        case BLACK_VISION:
          arr[15]++;
          arr[16] += des.getRating();
          arr[17] += des.getSmokeKill();
          arr[18] += des.getOpenKill();
          arr[19] += des.getThreeKill();
          arr[20] += des.getFourKill();
          arr[21] += des.getAce();
          arr[22] += des.getFlash();
          arr[23] += des.getTrade();
          arr[24] += des.getWallBang();
          arr[25] += des.getClutchOne();
          arr[26] += des.getClutchTwo();
          arr[27] += des.getClutchThree();
          arr[28] += des.getClutchFour();
          arr[29] += des.getClutchFive();
          break;
        case B4ONE:
          arr[30]++;
          arr[31] += des.getRating();
          arr[32] += des.getSmokeKill();
          arr[33] += des.getOpenKill();
          arr[34] += des.getThreeKill();
          arr[35] += des.getFourKill();
          arr[36] += des.getAce();
          arr[37] += des.getFlash();
          arr[38] += des.getTrade();
          arr[39] += des.getWallBang();
          arr[40] += des.getClutchOne();
          arr[41] += des.getClutchTwo();
          arr[42] += des.getClutchThree();
          arr[43] += des.getClutchFour();
          arr[44] += des.getClutchFive();
          break;
        case GLOXINIA:
          arr[45]++;
          arr[46] += des.getRating();
          arr[47] += des.getSmokeKill();
          arr[48] += des.getOpenKill();
          arr[49] += des.getThreeKill();
          arr[50] += des.getFourKill();
          arr[51] += des.getAce();
          arr[52] += des.getFlash();
          arr[53] += des.getTrade();
          arr[54] += des.getWallBang();
          arr[55] += des.getClutchOne();
          arr[56] += des.getClutchTwo();
          arr[57] += des.getClutchThree();
          arr[58] += des.getClutchFour();
          arr[59] += des.getClutchFive();
          break;
        case NEKIT:
          arr[60]++;
          arr[61] += des.getRating();
          arr[62] += des.getSmokeKill();
          arr[63] += des.getOpenKill();
          arr[64] += des.getThreeKill();
          arr[65] += des.getFourKill();
          arr[66] += des.getAce();
          arr[67] += des.getFlash();
          arr[68] += des.getTrade();
          arr[69] += des.getWallBang();
          arr[70] += des.getClutchOne();
          arr[71] += des.getClutchTwo();
          arr[72] += des.getClutchThree();
          arr[73] += des.getClutchFour();
          arr[74] += des.getClutchFive();
          break;
      }
    }
    for (int i = 1; i < 15; i++) {
      arr[i] /= arr[0];
    }
    for (int i = 16; i < 30; i++) {
      arr[i] /= arr[15];
    }
    for (int i = 31; i < 45; i++) {
      arr[i] /= arr[30];
    }
    for (int i = 46; i < 60; i++) {
      arr[i] /= arr[45];
    }
    for (int i = 61; i < 75; i++) {
      arr[i] /= arr[60];
    }
    return arr;
  }

  public String[] getRating() {
    List<MatchItem> doceho = getAll();

    double[] desmond = new double[2];
    double[] blackVision = new double[2];
    double[] gloxinia = new double[2];
    double[] tilt = new double[2];
    double[] nekit = new double[2];

    for (MatchItem des : doceho) {
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getRating();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getRating();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getRating();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getRating();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getOpenKill();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getOpenKill();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getOpenKill();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getOpenKill();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getFlash();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getFlash();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getFlash();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getFlash();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getTrade();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getTrade();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getTrade();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getTrade();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getWallBang();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getWallBang();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getWallBang();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getWallBang();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getThreeKill();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getThreeKill();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getThreeKill();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getThreeKill();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getFourKill();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getFourKill();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getFourKill();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getFourKill();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] += 1;
        desmond[1] += des.getAce();
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] += 1;
        blackVision[1] += des.getAce();
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] += 1;
        tilt[1] += des.getAce();
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] += 1;
        gloxinia[1] += des.getAce();
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
      if (des.getName().equals(PlayerName.DESMOND)) {
        desmond[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        desmond[1] += des.getClutchOne();
        desmond[2] += des.getClutchTwo();
        desmond[3] += des.getClutchThree();
        desmond[4] += des.getClutchFour();
        desmond[5] += des.getClutchFive();
        desmond[6] += 1;
      } else if (des.getName().equals(PlayerName.BLACK_VISION)) {
        blackVision[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        blackVision[1] += des.getClutchOne();
        blackVision[2] += des.getClutchTwo();
        blackVision[3] += des.getClutchThree();
        blackVision[4] += des.getClutchFour();
        blackVision[5] += des.getClutchFive();
        blackVision[6] += 1;
      } else if (des.getName().equals(PlayerName.B4ONE)) {
        tilt[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        tilt[1] += des.getClutchOne();
        tilt[2] += des.getClutchTwo();
        tilt[3] += des.getClutchThree();
        tilt[4] += des.getClutchFour();
        tilt[5] += des.getClutchFive();
        tilt[6] += 1;
      } else if (des.getName().equals(PlayerName.GLOXINIA)) {
        gloxinia[0] +=
            des.getClutchOne() + des.getClutchTwo() + des.getClutchThree() + des.getClutchFour()
                + des.getClutchFive();
        gloxinia[1] += des.getClutchOne();
        gloxinia[2] += des.getClutchTwo();
        gloxinia[3] += des.getClutchThree();
        gloxinia[4] += des.getClutchFour();
        gloxinia[5] += des.getClutchFive();
        gloxinia[6] += 1;
      } else if (des.getName().equals(PlayerName.NEKIT)) {
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
        .name(PlayerName.valueOf(match.getName().name()))
        .data(match.getData())
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
        .type(MatchType.valueOf(match.getType().name()))
        .build();

    return matchRepository.save(matchItem);
  }

  @Transactional
  public MatchItem update(Long matchId,
      Match matchDetails) {
    MatchItem matchItem = get(matchId);

    matchItem.setName(PlayerName.valueOf(matchDetails.getName().name()));
    matchItem.setData(matchDetails.getData());
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
    matchItem.setType(MatchType.valueOf(matchDetails.getType().name()));

    return matchRepository.save(matchItem);
  }

  public void delete(Long id) {
    matchRepository.deleteById(id);
  }

}
