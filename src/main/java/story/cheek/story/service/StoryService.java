package story.cheek.story.service;

import static story.cheek.common.exception.ErrorCode.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.constant.SortType;
import story.cheek.common.exception.ErrorCode;
import story.cheek.common.exception.NotFoundHighlightException;
import story.cheek.common.exception.NotFoundQuestionException;
import story.cheek.common.exception.NotFoundStoryException;
import story.cheek.common.exception.StoryForbiddenException;
import story.cheek.common.image.S3Service;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.repository.HighlightRepository;
import story.cheek.member.domain.Member;
import story.cheek.question.domain.Question;
import story.cheek.question.repository.QuestionRepository;
import story.cheek.story.domain.Story;
import story.cheek.story.dto.request.StoryCreateRequest;
import story.cheek.common.dto.SliceResponse;
import story.cheek.story.dto.response.StoryDetailResponse;
import story.cheek.story.dto.response.StoryResponse;
import story.cheek.story.repository.StoryRepository;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final QuestionRepository questionRepository;
    private final HighlightRepository highlightRepository;
    private final S3Service s3Service;

    @Transactional
    public Long save(Member member, StoryCreateRequest request) {
        validateStoryCreate(member);
        String imageUrl = s3Service.upload(request.multipartFile());

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new NotFoundQuestionException(QUESTION_NOT_FOUND));

        Story story = Story.createStory(
                request.occupation(),
                imageUrl,
                question,
                member);

        storyRepository.save(story);

        return story.getId();
    }

    public StoryDetailResponse findDetailById(Member member, Long storyId) {
        // TODO: 접근 권한 체크 NOT GUEST & BLIND (2차 스프린트)

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundStoryException(STORY_NOT_FOUND));

        return StoryDetailResponse.of(story);
    }

    public SliceResponse<StoryResponse> findAll(
            Member member,
            SortType sortType,
            String cursor) {
        // TODO: 접근 권한 체크 NOT GUEST & BLIND (2차 스프린트)

        if (sortType == SortType.LATEST) {
            return storyRepository.findAllByOrderByIdDesc(cursor, sortType);
        }
        return storyRepository.findAllByOrderByLikeCountDesc(cursor, sortType);
    }

    public SliceResponse<StoryResponse> findAllByHighlightId(
            Long highlightId,
            String cursor,
            SortType sortType
    ) {
        Highlight highlight = findHighlight(highlightId);
        return storyRepository.findAllByHighlightOrderByIdDesc(cursor, highlight, sortType);
    }

    private Highlight findHighlight(Long highlightId) {
        return highlightRepository.findById(highlightId)
                .orElseThrow(() -> new NotFoundHighlightException(HIGHLIGHT_NOT_FOUND));
    }

    private void validateStoryCreate(Member member) {
        if (!member.canMakeStory()) {
            throw new StoryForbiddenException(FORBIDDEN_STORY_CREATE);
        }
    }
}
