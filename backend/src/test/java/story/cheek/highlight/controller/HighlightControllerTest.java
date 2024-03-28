package story.cheek.highlight.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import story.cheek.common.dto.SliceResponse;
import story.cheek.highlight.dto.request.HighlightCreateRequest;
import story.cheek.highlight.dto.request.HighlightStoryCreateRequest;
import story.cheek.highlight.dto.response.HighlightResponse;
import story.cheek.highlight.service.HighlightService;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Role;
import story.cheek.member.repository.MemberRepository;
import story.cheek.security.TokenProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HighlightControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    HighlightService highlightService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .email("qwer@chick.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void 하이라이트를_생성한다() throws Exception {
        HighlightCreateRequest request = new HighlightCreateRequest("chick");

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(highlightService.save(any(), any())).willReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/highlights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void 하이라이트를_삭제한다() throws Exception {
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        doNothing().when(highlightService).delete(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/highlights/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }

    @Test
    void 본인의_하이라이트를_조회한다() throws Exception {
        SliceResponse<HighlightResponse> response = SliceResponse.of(
                List.of(
                        new HighlightResponse(
                                1L,
                                "chick"
                        ),
                        new HighlightResponse(
                                2L,
                                "chick2"
                        )
                ), null, null
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(highlightService.findAll(5, 1L, null)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/highlights")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.values[0].id").value(1L))
                .andExpect(jsonPath("$.values[1].id").value(2L))
                .andDo(print());
    }

    @Test
    void 다른_유저의_하이라이트를_조회한다() throws Exception {
        SliceResponse<HighlightResponse> response = SliceResponse.of(
                List.of(
                        new HighlightResponse(
                                1L,
                                "chick"
                        ),
                        new HighlightResponse(
                                2L,
                                "chick2"
                        )
                ), null, null
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(highlightService.findAll(5, 1L, null)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/highlights/members")
                        .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                        .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.values[0].id").value(1L))
                .andExpect(jsonPath("$.values[1].id").value(2L))
                .andDo(print());
    }

    @Test
    void 하이라이트에_스토리를_추가한다() throws Exception {

        HighlightStoryCreateRequest request = new HighlightStoryCreateRequest(
                1L,
                1L
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(highlightService.saveStoryHighlight(any(), any())).willReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/highlights/stories")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void 하이라이트에서_스토리를_삭제한다() throws Exception {
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        doNothing().when(highlightService).deleteStoryHighlight(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/highlights/stories/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                .param("highlightId", "1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }
}