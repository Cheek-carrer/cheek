package story.cheek.common.constant;

public enum SortType {
    FOLLOWING("FOLLOWING", "내가 팔로우 중인 유저 검색"),
    FOLLOWER("FOLLOWER", "나를 팔로우 중인 유저 검색"),
    LIKE("LIKE", "좋아요순"),
    LATEST("LATEST", "최신순");

    private String title;

    private String value;

    private SortType(String title, String value) {
        this.title = title;
        this.value = value;
    }
}
