package com.cs.doceho.stats.bot.v2.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.service.utils.CalculationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class MatchServiceExportTest {

  @Test
  void shouldRequestLatestMatchesUsingStableSort() {
    MatchRepository repository = mock(MatchRepository.class);
    MatchSqlFormatter formatter = mock(MatchSqlFormatter.class);
    MatchService service = new MatchService(repository, mock(CalculationService.class), formatter);
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
    when(formatter.format(List.of(), false)).thenReturn("-- no matches found");

    service.exportLatestAsSql(5, false);

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(captor.capture());
    Pageable pageable = captor.getValue();
    assertThat(pageable.getPageSize()).isEqualTo(5);
    assertThat(pageable.getPageNumber()).isEqualTo(0);
    assertThat(pageable.getSort()).isEqualTo(
        Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id")));
  }

  @Test
  void shouldValidateExportLimitRange() {
    MatchService service = new MatchService(
        mock(MatchRepository.class),
        mock(CalculationService.class),
        mock(MatchSqlFormatter.class)
    );

    assertThatIllegalArgumentException()
        .isThrownBy(() -> service.exportLatestAsSql(0, false))
        .withMessage("Query parameter 'limit' must be between 1 and 1000.");

    assertThatIllegalArgumentException()
        .isThrownBy(() -> service.exportLatestAsSql(1001, false))
        .withMessage("Query parameter 'limit' must be between 1 and 1000.");
  }

  @Test
  void shouldPassRepositoryResultToFormatter() {
    MatchRepository repository = mock(MatchRepository.class);
    MatchSqlFormatter formatter = mock(MatchSqlFormatter.class);
    MatchService service = new MatchService(repository, mock(CalculationService.class), formatter);
    List<MatchItem> matches = List.of(mock(MatchItem.class));
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(matches));
    when(formatter.format(matches, true)).thenReturn("sql");

    String result = service.exportLatestAsSql(3, true);

    assertThat(result).isEqualTo("sql");
    verify(formatter).format(matches, true);
  }
}
