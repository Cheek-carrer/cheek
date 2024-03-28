package story.cheek.story.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import story.cheek.common.constant.SortType;
import story.cheek.common.dto.SliceResponse;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Role;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.security.TokenProvider;
import story.cheek.story.dto.request.StoryCreateRequestWithoutImage;
import story.cheek.story.dto.response.StoryDetailResponse;
import story.cheek.story.dto.response.StoryResponse;
import story.cheek.story.service.StoryService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StoryService storyService;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    ObjectMapper objectMapper;

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
    void 스토리를_생성한다() throws Exception {
        StoryCreateRequestWithoutImage request = new StoryCreateRequestWithoutImage(
                1L,
                Occupation.DEVELOP
        );

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                IMAGE_PNG_VALUE,
                "image".getBytes());

        MockMultipartFile requestWithoutImage = new MockMultipartFile(
                "request",
                "request",
                APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        given(storyService.save(any(), any())).willReturn(1L);
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/v1/stories")
                        .file(image)
                        .file(requestWithoutImage)
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void 스토리_상세_정보를_조회한다() throws Exception {
        StoryDetailResponse response = new StoryDetailResponse(
                1L,
                1L,
                1L,
                Occupation.DEVELOP,
                "image.png",
                LocalDateTime.now()
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(storyService.findDetailById(any())).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stories/1")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.storyId").value(1L))
                .andDo(print());
    }

    @Test
    void 모든_스토리를_조회한다() throws Exception {

        SliceResponse<StoryResponse> response = SliceResponse.of(
                List.of(
                        new StoryResponse(
                                1L,
                                "image.png",
                                LocalDateTime.now(),
                                0
                        ),
                        new StoryResponse(
                                2L,
                                "image2.png",
                                LocalDateTime.now(),
                                0
                        )), null, null
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(storyService.findAll(5, SortType.LATEST, null)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stories")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.values[0].storyId").value(1L))
                .andExpect(jsonPath("$.values[1].storyId").value(2L))
                .andDo(print());
    }

    @Test
    void 하이라이트에_속한_스토리를_모두_조회한다() throws Exception {

        SliceResponse<StoryResponse> response = SliceResponse.of(
                List.of(
                        new StoryResponse(
                                1L,
                                "image.png",
                                LocalDateTime.now(),
                                0
                        ),
                        new StoryResponse(
                                2L,
                                "image2.png",
                                LocalDateTime.now(),
                                0
                        )), null, null
        );


        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        given(storyService.findAllByHighlightId(5, 1L, null, SortType.LATEST)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stories/highlights")
                .header(AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken("qwer@chick.com"))
                        .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.values[0].storyId").value(1L))
                .andExpect(jsonPath("$.values[1].storyId").value(2L))
                .andDo(print());
    }
}