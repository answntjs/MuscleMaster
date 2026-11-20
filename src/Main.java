import cli.MainMenu;

/**
 * MuscleMaster 콘솔 애플리케이션의 진입점 클래스입니다.
 */
public class Main {
    /**
     * 기본 생성자입니다.
     */
    public Main() {
    }

    /**
     * 애플리케이션을 시작하여 CLI 메인 메뉴를 실행합니다.
     *
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        MainMenu.start();
    }
}
