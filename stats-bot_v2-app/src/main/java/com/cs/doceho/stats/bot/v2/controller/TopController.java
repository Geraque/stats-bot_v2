package com.cs.doceho.stats.bot.v2.controller;


import com.cs.doceho.stats.bot.v2.api.TopApi;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import com.cs.doceho.stats.bot.v2.model.Top;
import com.cs.doceho.stats.bot.v2.service.TopService;
import io.vavr.control.Try;
import java.util.List;
import java.util.NoSuchElementException;
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

  TopService topService;
  MapperFacade mapper;

  @Override
  public List<Top> getAllTop() {
    return topService.getAll().stream()
        .map(it -> mapper.map(it, Top.class))
        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<List<String>> getYearTop(int year) {
    return Try.of(() -> topService.getYearTop(year))
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Top> getTopById(Long topId) {
    return Try.of(() -> topService.get(topId))
        .map(reference -> mapper.map(reference, Top.class))
        .map(ResponseEntity::ok)
        .recover(NoSuchElementException.class, ResponseEntity.notFound().build())
        .get();
  }

  @Override
  public ResponseEntity<List<Top>> getTopByName(String playerName) {
    return Try.of(() -> topService.getByName(playerName))
        .map(reference -> reference.stream()
            .map(it -> mapper.map(it, Top.class))
            .collect(Collectors.toList()))
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Top> createTop(Top top) {
    return Try.of(() -> topService.create(top))
        .map(reference -> mapper.map(reference, Top.class))
        .map(ResponseEntity::ok)
        .recover(NoSuchElementException.class, ResponseEntity.notFound().build())
        .get();
  }

  @Override
  public ResponseEntity<Top> updateTop(Long topId, Top topDetails)
      throws ResourceNotFoundException {
    return Try.of(() -> topService.update(topId, topDetails))
        .map(reference -> mapper.map(reference, Top.class))
        .map(ResponseEntity::ok)
        .recover(NoSuchElementException.class, ResponseEntity.notFound().build())
        .get();
  }

  @Override
  public ResponseEntity<?> deleteTop(Long topId)
      throws ResourceNotFoundException {
    topService.delete(topId);
    return ResponseEntity.ok().build();
  }
}
