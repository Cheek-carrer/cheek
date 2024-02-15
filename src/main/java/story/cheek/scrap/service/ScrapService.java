package story.cheek.scrap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.exception.*;
import story.cheek.member.domain.Member;
import story.cheek.member.repository.MemberRepository;
import story.cheek.scrap.dto.response.ScrapResponse;
import story.cheek.scrap.repository.ScrapRepository;
import story.cheek.story.domain.Scrap;
import story.cheek.story.domain.Story;
import story.cheek.story.repository.StoryRepository;

import java.util.List;

import static story.cheek.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long save(Member member, Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundStoryException(STORY_NOT_FOUND));

        validateDuplicateScrap(member);
        Scrap scrap = Scrap.of(member, story);
        scrapRepository.save(scrap);

        return scrap.getId();
    }

    public List<ScrapResponse> findAllByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(MEMBER_NOT_FOUND));

        return scrapRepository.findAllByMember(member)
                .stream()
                .map(ScrapResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Member member, Long scrapId) {
        Scrap scrap = scrapRepository.findById(scrapId)
                .orElseThrow(() -> new NotFoundScrapException(SCRAP_NOT_FOUND));

        validateScrapDelete(member, scrap);

        scrapRepository.deleteById(scrapId);
    }

    private void validateScrapDelete(Member member, Scrap scrap) {
        if (member.hasAuthority(scrap.getMember().getId())) {
            throw new ScrapForbiddenException(FORBIDDEN_SCRAP_DELETE);
        }
    }

    private void validateDuplicateScrap(Member member) {
        if (scrapRepository.existsByMember(member)) {
            throw new ScrapDuplicationException(ALREADY_STORY_SCRAP);
        }
    }
}
