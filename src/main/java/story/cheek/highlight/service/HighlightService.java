package story.cheek.highlight.service;

import static story.cheek.common.exception.ErrorCode.*;
import static story.cheek.common.exception.ErrorCode.FORBIDDEN_HIGHLIGHT_CREATE;
import static story.cheek.common.exception.ErrorCode.FORBIDDEN_HIGHLIGHT_DELETE;
import static story.cheek.common.exception.ErrorCode.HIGHLIGHT_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.ErrorCode;
import story.cheek.common.exception.ForbiddenHighlightException;
import story.cheek.common.exception.NotFoundHighlightException;
import story.cheek.common.exception.NotFoundMemberException;
import story.cheek.common.exception.NotFoundStoryException;
import story.cheek.common.exception.StoryForbiddenException;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.domain.StoryHighlight;
import story.cheek.highlight.dto.request.HighlightCreateRequest;
import story.cheek.highlight.dto.request.HighlightStoryCreateRequest;
import story.cheek.highlight.dto.response.HighlightResponse;
import story.cheek.highlight.repository.HighlightRepository;
import story.cheek.highlight.repository.StoryHighlightRepository;
import story.cheek.member.domain.Member;
import story.cheek.member.repository.MemberRepository;
import story.cheek.story.domain.Story;
import story.cheek.story.repository.StoryRepository;

@Service
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final StoryHighlightRepository storyHighlightRepository;
    private final MemberRepository memberRepository;
    private final StoryRepository storyRepository;

    public Long save(Member member, HighlightCreateRequest request) {
        validateHighlightCreate(member);
        Highlight highlight = Highlight.createHighlight(member, request.title());

        return highlightRepository.save(highlight).getId();
    }

    @Transactional
    public void delete(Member member, Long highlightId) {
        Highlight highlight = findHighlight(highlightId);
        validateHighlightDelete(member, highlight);

        highlight.getStoryHighlights().stream()
                .forEach(storyHighlightRepository::delete);

        highlightRepository.delete(highlight);
    }

    public SliceResponse<HighlightResponse> findAll(Long memberId, String cursor) {
        Member member = findMember(memberId);
        return highlightRepository.findAllByMemberOrderByIdDesc(member, cursor);
    }

    @Transactional
    public Long saveHighlightStory(Member member, HighlightStoryCreateRequest request) {
        Story story = findStory(request.storyId());
        Highlight highlight = findHighlight(request.highlightId());
        validateStoryAdd(member, story);
        validateHighlightUpdate(member, highlight);
        StoryHighlight storyHighlight = StoryHighlight.of(highlight, story);
        storyHighlightRepository.save(storyHighlight);

        return story.getId();
    }

    private void validateHighlightUpdate(Member member, Highlight highlight) {
        if (!member.hasAuthority(highlight.getMember().getId())) {
            throw new ForbiddenHighlightException(FORBIDDEN_HIGHLIGHT_FULL_ACCESS);
        }
    }

    private void validateStoryAdd(Member member, Story story) {
        if (!member.hasAuthority(story.getMember().getId())) {
            throw new StoryForbiddenException(FORBIDDEN_STORY_ADD);
        }
    }

    private Story findStory(Long storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundStoryException(STORY_NOT_FOUND));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(MEMBER_NOT_FOUND));
    }

    private Highlight findHighlight(Long highlightId) {
        return  highlightRepository.findById(highlightId)
                .orElseThrow(() -> new NotFoundHighlightException(HIGHLIGHT_NOT_FOUND));

    }

    private void validateHighlightDelete(Member member, Highlight highlight) {
        if (!member.hasAuthority(highlight.getMember().getId())) {
            throw new ForbiddenHighlightException(FORBIDDEN_HIGHLIGHT_DELETE);
        }
    }

    private void validateHighlightCreate(Member member) {
        if (!member.isMentor()) {
            throw new ForbiddenHighlightException(FORBIDDEN_HIGHLIGHT_CREATE);
        }
    }
}
