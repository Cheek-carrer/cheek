package story.cheek.common.exception;

public class NotFoundLikeException extends BusinessException {
    public NotFoundLikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
