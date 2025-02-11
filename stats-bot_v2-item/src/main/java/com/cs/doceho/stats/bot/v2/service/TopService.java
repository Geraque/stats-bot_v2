package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.TopRepository;
import com.cs.doceho.stats.bot.v2.model.Top;
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
public class TopService {

  TopRepository topRepository;

  public List<TopItem> getAll() {
    return topRepository.findAll();
  }

  public TopItem get(UUID topId) {
    return topRepository.findById(topId)
        .orElseThrow(() -> new RuntimeException("Top not found for this id : " + topId));
  }

  public List<TopItem> getYearTop(int year) {
    return topRepository.findAll().stream()
        .filter(item -> item.getYear() == year)
        .collect(Collectors.toList());
  }

  public List<TopItem> getByName(String playerName) {
    return topRepository.findAll().stream()
        .filter(item -> item.getPlayerName().name().equals(playerName))
        .collect(Collectors.toList());
  }

  @Transactional
  public TopItem create(Top top) {
    TopItem build = TopItem.builder()
        .playerName(PlayerName.fromName(top.getPlayerName()))
        .place(top.getPlace())
        .rating(top.getRating())
        .year(top.getYear())
        .build();
    return topRepository.save(build);
  }

  @Transactional
  public TopItem update(UUID topId, Top topDetails) {
    TopItem top = get(topId);

    top.setPlayerName(PlayerName.fromName(topDetails.getPlayerName()));
    top.setRating(topDetails.getRating());
    top.setYear(topDetails.getYear());
    top.setPlace(topDetails.getPlace());

    return topRepository.save(top);
  }

  public void delete(UUID topId) {
    topRepository.deleteById(topId);
  }
}
