package story.cheek.question.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.BDDMockito.given;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import story.cheek.common.dto.SliceResponse;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Role;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.dto.request.QuestionCreateRequest;
import story.cheek.question.dto.request.QuestionUpdateRequest;
import story.cheek.question.dto.response.QuestionDetailResponse;
import story.cheek.question.dto.response.QuestionResponse;
import story.cheek.question.service.QuestionService;
import story.cheek.security.TokenProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    QuestionService questionService;

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
                .id(1L)
                .email("qwer@chick.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void 질문을_생성한다() throws Exception {
        QuestionCreateRequest request = new QuestionCreateRequest(
                "chick",
                "백엔드란",
                Occupation.DEVELOP
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/questions")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void 질문_상세_정보를_조회한다() throws Exception {
        QuestionDetailResponse response = new QuestionDetailResponse(
                1L,
                Occupation.DEVELOP,
                "chick",
                "백엔드란",
                LocalDateTime.now()
        );

        given(questionService.findDetailById(any())).willReturn(response);
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/questions/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                .param("questionId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }

    @Test
    void 질문을_수정한다() throws Exception {
        QuestionUpdateRequest request = new QuestionUpdateRequest(
                "updatedChick",
                "기획이란",
                Occupation.PLAN
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        doNothing().when(questionService).update(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/questions/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 모든_질문을_조회한다() throws Exception {
        SliceResponse<QuestionResponse> response = SliceResponse.of(
                List.of(
                        new QuestionResponse(
                                1L,
                                "chick",
                                "백엔드란",
                                "writer"
                        ),
                        new QuestionResponse(
                                2L,
                                "chick2",
                                "프런트란",
                                "writer2"
                        )
                ), null, null
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(questionService.findAll(5, null, null)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/questions")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.values[0].id").value(1L))
                .andExpect(jsonPath("$.values[1].id").value(2L))
                .andDo(print());
    }
}