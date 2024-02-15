package story.cheek.common.exception;

public class ForbiddenFollowException extends BusinessException {
    public ForbiddenFollowException(ErrorCode errorCode) {
        super(errorCode);
    }
}
