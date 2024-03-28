package story.cheek.scrap.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Role;
import story.cheek.member.repository.MemberRepository;
import story.cheek.scrap.dto.response.ScrapResponse;
import story.cheek.scrap.service.ScrapService;
import story.cheek.security.TokenProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ScrapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ScrapService scrapService;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .role(Role.ROLE_USER)
                .email("qwer@chick.com")
                .build();
    }

    @Test
    void 스크랩을_생성한다() throws Exception {
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(scrapService.save(any(), any())).willReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/scraps/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void 모든_스크랩을_조회한다() throws Exception {
        List<ScrapResponse> response = List.of(
                new ScrapResponse(
                        1L,
                        "image.png",
                        LocalDateTime.now()
                ),
                new ScrapResponse(
                        2L,
                        "image2.png",
                        LocalDateTime.now()
                )
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(scrapService.findAllByMemberId(any())).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/scraps/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.[0].scrapId").value(1L))
                .andExpect(jsonPath("$.[1].scrapId").value(2L))
                .andDo(print());
    }

    @Test
    void 스크랩을_삭제한다() throws Exception {

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        doNothing().when(scrapService).delete(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/scraps/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }
}