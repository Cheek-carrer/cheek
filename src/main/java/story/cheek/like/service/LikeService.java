package story.cheek.like.service;

import static story.cheek.common.exception.ErrorCode.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.exception.DuplicateLikeException;
import story.cheek.common.exception.NotFoundLikeException;
import story.cheek.common.exception.NotFoundStoryException;
import story.cheek.like.domain.Like;
import story.cheek.like.repository.LikeRepository;
import story.cheek.member.domain.Member;
import story.cheek.story.domain.Story;
import story.cheek.story.repository.StoryRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final StoryRepository storyRepository;

    @Transactional
    public Long likeStory(Member member, Long storyId) {
        Story story = findStory(storyId);
        validateDuplicateLike(member);
        story.like();
        Like like = Like.createLike(member, story);

        return likeRepository.save(like).getId();
    }

    @Transactional
    public void cancelStoryLike(Member member, Long storyId) {
        Story story = findStory(storyId);
        Like like = findLikeByMemberAndStory(member, story);
        story.dislike();
        likeRepository.delete(like);
    }

    private Like findLikeByMemberAndStory(Member member, Story story) {
        Like like = likeRepository.findByMemberAndStory(member, story)
                .orElseThrow(() -> new NotFoundLikeException(STORY_LIKE_NOT_FOUND));
        return like;
    }

    private Story findStory(Long storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundStoryException(STORY_NOT_FOUND));
    }

    private void validateDuplicateLike(Member member) {
        if (likeRepository.existsByMember(member)) {
            throw new DuplicateLikeException(DUPLICATED_STORY_LIKE);
        }
    }
}
