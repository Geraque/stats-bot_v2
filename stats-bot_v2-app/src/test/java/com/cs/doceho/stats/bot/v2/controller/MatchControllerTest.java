package com.cs.doceho.stats.bot.v2.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cs.doceho.stats.bot.v2.service.MatchService;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MatchControllerTest {

  @Mock
  private MatchService matchService;

  @Mock
  private MapperFacade mapperFacade;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(new MatchController(matchService, mapperFacade)).build();
  }

  @Test
  void shouldReturnSqlExportForExplicitLimit() throws Exception {
    when(matchService.exportLatestAsSql(5, false)).thenReturn("INSERT INTO public.matches ...;");

    mockMvc.perform(get("/match/export/sql").param("limit", "5"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("INSERT INTO public.matches ...;"));

    verify(matchService).exportLatestAsSql(5, false);
  }

  @Test
  void shouldUseDefaultLimitWhenParameterIsMissing() throws Exception {
    when(matchService.exportLatestAsSql(10, false)).thenReturn("-- no matches found");

    mockMvc.perform(get("/match/export/sql"))
        .andExpect(status().isOk())
        .andExpect(content().string("-- no matches found"));

    verify(matchService).exportLatestAsSql(10, false);
  }

  @Test
  void shouldReturnSqlExportWithOnConflictDoNothing() throws Exception {
    when(matchService.exportLatestAsSql(5, true))
        .thenReturn("INSERT INTO public.matches ...\nON CONFLICT (id) DO NOTHING;");

    mockMvc.perform(get("/match/export/sql")
            .param("limit", "5")
            .param("onConflictDoNothing", "true"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("INSERT INTO public.matches ...\nON CONFLICT (id) DO NOTHING;"));

    verify(matchService).exportLatestAsSql(5, true);
  }

  @Test
  void shouldReturnBadRequestWhenLimitIsTooSmall() throws Exception {
    when(matchService.exportLatestAsSql(0, false))
        .thenThrow(new IllegalArgumentException("Query parameter 'limit' must be between 1 and 1000."));

    mockMvc.perform(get("/match/export/sql").param("limit", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Query parameter 'limit' must be between 1 and 1000."));
  }

  @Test
  void shouldReturnBadRequestWhenLimitIsTooLarge() throws Exception {
    when(matchService.exportLatestAsSql(1001, false))
        .thenThrow(new IllegalArgumentException("Query parameter 'limit' must be between 1 and 1000."));

    mockMvc.perform(get("/match/export/sql").param("limit", "1001"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Query parameter 'limit' must be between 1 and 1000."));
  }
}
