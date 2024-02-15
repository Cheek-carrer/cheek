package story.cheek.common.exception;

public class DuplicateFollowException extends BusinessException{
    public DuplicateFollowException(ErrorCode errorCode) {
        super(errorCode);
    }
}
