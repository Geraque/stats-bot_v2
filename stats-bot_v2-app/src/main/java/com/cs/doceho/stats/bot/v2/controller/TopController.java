package com.cs.doceho.stats.bot.v2.controller;


import com.cs.doceho.stats.bot.v2.api.TopApi;
import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.TopRepository;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import com.cs.doceho.stats.bot.v2.model.Top;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopController implements TopApi {

  TopRepository topRepository;
  MapperFacade mapper;

  public List<Top> getAllTop() {
    return topRepository.findAll().stream()
        .map(it -> mapper.map(it, Top.class))
        .collect(Collectors.toList());
  }

  public ResponseEntity<List<String>> getYearTop(int year) {
    List<TopItem> doceho = topRepository.findAll();
    List<String> list = new ArrayList<>();
    list.add(String.valueOf(year));
    for (TopItem des : doceho) {
      if (des.getYear() == year) {
        list.add(des.getName().name());
        list.add(String.valueOf(des.getPlace()));
        list.add(String.valueOf(des.getRating()));
      }
    }
    return ResponseEntity.ok().body(list);
  }

  public ResponseEntity<Top> getTopById(Long topId)
      throws ResourceNotFoundException {
    TopItem top = topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));
    return ResponseEntity.ok().body(mapper.map(top, Top.class));
  }

  public ResponseEntity<List<Top>> getTopByName(String playerName) {
    List<TopItem> top = topRepository.findAll();
    List<Top> result = new ArrayList<>();
    for (TopItem des : top) {
      if (des.getName().name().equals(playerName)) {
        result.add(mapper.map(des, Top.class));
      }
    }
    return ResponseEntity.ok().body(result);
  }

  public Top createTop(Top top) {
    TopItem build = TopItem.builder()
        .name(PlayerName.valueOf(top.getName().name()))
        .place(top.getPlace())
        .rating(top.getRating())
        .year(top.getYear())
        .build();
    TopItem save = topRepository.save(build);
    return mapper.map(save, Top.class);
  }

  public ResponseEntity<Top> updateTop(Long topId, Top topDetails)
      throws ResourceNotFoundException {
    TopItem top = topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));

    top.setName(PlayerName.valueOf(topDetails.getName().name()));
    top.setRating(topDetails.getRating());
    top.setYear(topDetails.getYear());
    top.setPlace(topDetails.getPlace());

    final TopItem updatedTop = topRepository.save(top);
    return ResponseEntity.ok(mapper.map(updatedTop, Top.class));
  }

  public Map<String, Boolean> deleteTop(Long topId)
      throws ResourceNotFoundException {
    TopItem top = topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));

    topRepository.delete(top);
    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return response;
  }
}
