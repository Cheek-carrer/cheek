package story.cheek.highlight.service;

import static story.cheek.common.exception.ErrorCode.*;
import static story.cheek.common.exception.ErrorCode.FORBIDDEN_HIGHLIGHT_CREATE;
import static story.cheek.common.exception.ErrorCode.FORBIDDEN_HIGHLIGHT_DELETE;
import static story.cheek.common.exception.ErrorCode.HIGHLIGHT_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.ForbiddenHighlightException;
import story.cheek.common.exception.NotFoundHighlightException;
import story.cheek.common.exception.NotFoundMemberException;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.dto.request.HighlightCreateRequest;
import story.cheek.highlight.dto.response.HighlightResponse;
import story.cheek.highlight.repository.HighlightRepository;
import story.cheek.highlight.repository.StoryHighlightRepository;
import story.cheek.member.domain.Member;
import story.cheek.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final StoryHighlightRepository storyHighlightRepository;
    private final MemberRepository memberRepository;

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

    private Member findMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(MEMBER_NOT_FOUND));
        return member;
    }

    private Highlight findHighlight(Long highlightId) {
        Highlight highlight = highlightRepository.findById(highlightId)
                .orElseThrow(() -> new NotFoundHighlightException(HIGHLIGHT_NOT_FOUND));
        return highlight;
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
