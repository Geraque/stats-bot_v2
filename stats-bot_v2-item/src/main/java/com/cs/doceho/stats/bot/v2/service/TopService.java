package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.TopRepository;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import com.cs.doceho.stats.bot.v2.model.Top;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopService {

  TopRepository topRepository;

  public List<TopItem> getAll() {
    return topRepository.findAll();
  }

  public TopItem get(Long topId) {
    return topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));
  }

  public List<String> getYearTop(int year) {
    // Используем Stream API для фильтрации, преобразования и формирования списка.
    return topRepository.findAll().stream()
        .filter(item -> item.getYear() == year)
        .flatMap(item -> Stream.of(
            String.valueOf(year),
            item.getName().name(),
            String.valueOf(item.getPlace()),
            String.valueOf(item.getRating())))
        .collect(Collectors.toList());
  }

  public List<TopItem> getByName(String playerName) {
    return topRepository.findAll().stream()
        .filter(item -> item.getName().name().equals(playerName))
        .collect(Collectors.toList());
  }

  @Transactional
  public TopItem create(Top top) {
    TopItem build = TopItem.builder()
        .name(PlayerName.valueOf(top.getName().name()))
        .place(top.getPlace())
        .rating(top.getRating())
        .year(top.getYear())
        .build();
    return topRepository.save(build);
  }

  @Transactional
  public TopItem update(Long topId, Top topDetails) {
    TopItem top = get(topId);

    top.setName(PlayerName.valueOf(topDetails.getName().name()));
    top.setRating(topDetails.getRating());
    top.setYear(topDetails.getYear());
    top.setPlace(topDetails.getPlace());

    return topRepository.save(top);
  }

  public void delete(Long topId) {
    topRepository.deleteById(topId);
  }
}
