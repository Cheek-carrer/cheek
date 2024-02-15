package story.cheek.common.exception;

public class DuplicateLikeException extends BusinessException {
    public DuplicateLikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
