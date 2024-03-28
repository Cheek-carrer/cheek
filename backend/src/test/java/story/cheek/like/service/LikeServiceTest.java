package story.cheek.like.service;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import story.cheek.member.domain.Member;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.repository.QuestionRepository;
import story.cheek.story.domain.Story;
import story.cheek.story.repository.StoryRepository;

@SpringBootTest
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LikeServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LikeService likeService;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    QuestionRepository questionRepository;

    List<Member> members = new ArrayList<>();

    Story story;

    Question question;

    @BeforeAll
    void setUp() {
        for (int i = 0; i < 30; i++) {
            Member member = Member.builder()
                    .email("email" + i)
                    .build();

            members.add(memberRepository.save(member));
        }

        question = questionRepository.save(Question.createQuestion(
                        Occupation.DEVELOP,
                        "question1",
                        "content",
                        members.get(0)));

        story = storyRepository.save(Story.createStory(
                Occupation.DEVELOP,
                "qwe.png",
                question,
                members.get(0)));
    }

    @Test
    @Order(1)
    void 동시에_좋아요_요청_테스트() throws InterruptedException {
        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int memberIdx = i;
            executorService.submit(() -> {
                try {
                    likeService.likeStory(story.getId(), members.get(memberIdx));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Story findStory = storyRepository.findById(story.getId()).orElseThrow();

        Assertions.assertThat(findStory.getLikeCount()).isEqualTo(30);
    }

    @Test
    @Order(2)
    void 동시에_좋아요_취소_요청_테스트() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int memberIdx = i;
            executorService.submit(() -> {
                try {
                    likeService.cancelStoryLike(story.getId(), members.get(memberIdx));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Story savedStory = storyRepository.findById(story.getId()).orElseThrow();

        Assertions.assertThat(savedStory.getLikeCount()).isEqualTo(20);
    }
}