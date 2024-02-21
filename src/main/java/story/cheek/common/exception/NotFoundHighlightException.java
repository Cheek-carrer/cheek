package story.cheek.common.exception;

public class NotFoundHighlightException extends BusinessException {
    public NotFoundHighlightException(ErrorCode errorCode) {
        super(errorCode);
    }
}
