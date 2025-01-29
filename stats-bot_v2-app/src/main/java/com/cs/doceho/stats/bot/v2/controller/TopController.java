package com.cs.doceho.stats.bot.v2.controller;


import com.cs.doceho.stats.bot.v2.api.TopApi;
import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.TopRepository;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import com.cs.doceho.stats.bot.v2.model.Top;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//Запросы к таблице с итогами года
@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopController implements TopApi {

  @Autowired
  TopRepository topRepository;
  MapperFacade mapper;

  //Получение всей информации
  @GetMapping("/all")
  public List<Top> getAllTop() {
    return topRepository.findAll().stream()
        .map(it -> mapper.map(it, Top.class))
        .collect(Collectors.toList());
  }

  //Получение информации по году
  @GetMapping("/getbyyear/{year}")
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

  //Получение информации по id
  @GetMapping("/{id}")
  public ResponseEntity<Top> getTopById(Long topId)
      throws ResourceNotFoundException {
    TopItem top = topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));
    return ResponseEntity.ok().body(mapper.map(top, Top.class));
  }

  //Получение информации по имени
  @GetMapping("/getbyname/{name}")
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

  //Создание итогов
  @PostMapping("/create")
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

  //Изменение итогов по id
  @PutMapping("/{id}")
  public ResponseEntity<Top> updateTop(@PathVariable(value = "id") Long topId,
      @Valid @RequestBody Top topDetails) throws ResourceNotFoundException {
    TopItem top = topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));

    top.setName(PlayerName.valueOf(topDetails.getName().name()));
    top.setRating(topDetails.getRating());
    top.setYear(topDetails.getYear());
    top.setPlace(topDetails.getPlace());

    final TopItem updatedTop = topRepository.save(top);
    return ResponseEntity.ok(mapper.map(updatedTop, Top.class));
  }

  //Удаление информации по id
  @DeleteMapping("/{id}")
  public Map<String, Boolean> deleteTop(@PathVariable(value = "id") Long topId)
      throws ResourceNotFoundException {
    TopItem top = topRepository.findById(topId)
        .orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));

    topRepository.delete(top);
    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return response;
  }
}
