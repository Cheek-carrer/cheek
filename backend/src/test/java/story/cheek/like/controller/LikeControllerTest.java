package story.cheek.like.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import story.cheek.like.service.LikeService;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Role;
import story.cheek.member.repository.MemberRepository;
import story.cheek.security.TokenProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    LikeService likeService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("qwer@chick.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void 좋아요를_한다() throws Exception {
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(likeService.likeStory(any(), any())).willReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/likes/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void 좋아요를_취소한다() throws Exception {
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        doNothing().when(likeService).cancelStoryLike(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/likes/cancellations/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }
}