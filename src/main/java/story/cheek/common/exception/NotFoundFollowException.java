package story.cheek.common.exception;

public class NotFoundFollowException extends BusinessException {
    public NotFoundFollowException(ErrorCode errorCode) {
        super(errorCode);
    }
}
