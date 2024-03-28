package story.cheek.common.exception;

public class BlockedMemberException extends BusinessException {
    public BlockedMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
