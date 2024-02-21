package story.cheek.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED_REDIRECT_URI(400, "AU_001", "인증되지 않은 REDIRECT_URI입니다."),

    DUPLICATED_REPORT(409, "RE_001", "이미 신고한 유저입니다"),
  
    MEMBER_NOT_FOUND(400, "C_001", "Member를 찾을 수 없습니다."),

    ENTITY_NOT_FOUND(400, "C_002", "지정한 Entity를 찾을 수 없습니다."),

    QUESTION_NOT_FOUND(400, "C_003", "Question을 찾을 수 없습니다."),

    FORBIDDEN_QUESTION_UPDATE(401, "C_004", "질문을 수정할 권한이 없습니다."),

    STORY_NOT_FOUND(400, "C_006", "Story를 찾을 수 없습니다."),

    FORBIDDEN_STORY_CREATE(401, "C_005", "스토리를 생성할 권한이 없습니다."),

    FORBIDDEN_SCRAP_DELETE(401, "C_008", "스크랩을 삭제할 권한이 없습니다."),

    ALREADY_STORY_SCRAP(400, "C_006", "이미 해당 스토리를 스크랩했습니다."),

    SCRAP_NOT_FOUND(400, "C_007", "Scrap을 찾을 수 없습니다."),

    FAILED_IMAGE_UPLOAD(400, "C_009", "이미지 업로드에 실패했습니다."),
    INVALID_FILE_EXTENSION(400, "C_010", "유효하지 않은 파일 확장자입니다."),

    FOLLOW_NOT_FOUND(400, "C_011", "Follow를 찾을 수 없습니다."),

    FOLLOWING_NOT_FOUND(400, "C_012", "아무도 팔로잉 하지않고 있습니다."),

    FOLLOWER_NOT_FOUND(400, "C_013", "팔로워가 없습니다"),

    DUPLICATED_FOLLOW(400, "C_014", "이미 팔로우한 유저입니다"),

    FORBIDDEN_FOLLOW_DELETE(401, "C_015", "Follow를 삭제할 권한이 없습니다."),

    NOT_ADMIN(400, "C_016", "Admin 사용자가 아닙니다."),
  
    APPLICATION_NOT_FOUND(400, "C_017", "Application 을 찾을 수 없습니다"),

    APPROVAL_DUPLICATION(400, "C_018", "이미 Mentor 승인을 받은 유저입니다."),
  
    DUPLICATED_APPLICATION(400, "C_019", "이미 신청서를 제출하셨습니다."),
  
    FAILED_GENERATE_CODE(500, "S_001", "인증 코드 생성에 실패했습니다."),
    
    FAILED_SEND_MAIL(500, "S_002", "메일 전송에 실패했습니다."),
  
    COMPANY_DOMAIN_NOT_FOUND(400, "C_020", "회사 도메인을 찾을 수 없습니다."),
  
    MAIL_CODE_NOT_MATCH(400, "C_021", "메일 인증 코드가 일치하지 않습니다."),
  
    MAIL_REQUEST_NOT_FOUND(400, "C_022", "인증 코드를 먼저 발급해주세요."),

    DUPLICATED_STORY_LIKE(400, "C_023", "이미 좋아요한 스토리입니다."),
    STORY_LIKE_NOT_FOUND(400, "C_024", "해당 스토리를 좋아요하지 않았습니다."),
    HIGHLIGHT_NOT_FOUND(400, "C_025", "하이라이트를 찾을 수 없습니다."),
    FORBIDDEN_HIGHLIGHT_CREATE(400, "C_026", "하이라이트를 생성할 권한이 없습니다."),
    FORBIDDEN_HIGHLIGHT_DELETE(400, "C_027", "하이라이트를 삭제할 권한이 없습니다."),
    FORBIDDEN_STORY_ADD(403, "C_028", "해당 스토리를 하이라이트에 추가할 권한이 없습니다."),
    FORBIDDEN_HIGHLIGHT_STORY_CREATE(403, "C_029", "하이라이트에 스토리를 추가할 권한이 없습니다."),
    FORBIDDEN_HIGHLIGHT_STORY_DELETE(403, "C_030", "하이라이트를 삭제할 권한이 없습니다."),
    STORY_HIGHLIGHT_NOT_FOUND(400, "C_031", "해당 스토리가 하이라이트에 포함되어 있지 않습니다.");


    private final int status;
    private final String code;
    private final String message;
}
