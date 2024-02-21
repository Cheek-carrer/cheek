package story.cheek.common.exception;

public class ForbiddenHighlightException extends BusinessException {
    public ForbiddenHighlightException(ErrorCode errorCode) {
        super(errorCode);
    }
}
