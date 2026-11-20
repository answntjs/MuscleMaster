package cli;

import user.BodyPartProgress;
import user.Player;
import user.PlayerStorage;

import java.util.Scanner;

/**
 * 콘솔 기반 캐릭터 정보 메뉴를 제공하는 CLI 클래스입니다.
 */
public class CharacterMenu {
    private CharacterMenu() {
    }

    /**
     * 캐릭터 정보 메뉴를 실행하고 변경 사항을 기본 저장 파일에 저장합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param player 대상 {@link Player} (null 불가)
     */
    public static void show(Scanner scanner, Player player) {
        show(scanner, player, () -> PlayerStorage.save(player));
    }

    /**
     * 캐릭터 정보 메뉴를 실행하며, 이름 변경 시 저장 콜백을 호출합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param player 대상 {@link Player} (null 불가)
     * @param onSave 상태 변경 시 호출할 저장 콜백 (null 허용)
     */
    public static void show(Scanner scanner, Player player, Runnable onSave) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner는 null일 수 없습니다.");
        }
        if (player == null) {
            throw new IllegalArgumentException("플레이어는 null일 수 없습니다.");
        }

        while (true) {
            System.out.println("\n=== 캐릭터 정보 ===");
            System.out.println(player);
            System.out.println("\n1. 신체정보");
            System.out.println("2. 이름 바꾸기");
            System.out.println("3. 뒤로");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n=== 신체정보 ===");
                    for (BodyPartProgress part : player.getPartProgressMap().values()) {
                        System.out.println(part);
                    }
                }
                case "2" -> {
                    System.out.print("새 이름: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isBlank()) {
                        System.out.println("이름은 비어 있을 수 없습니다.");
                    } else {
                        player.setName(newName);
                        if (onSave != null) {
                            onSave.run();
                        }
                        System.out.println("이름이 변경되었습니다.");
                    }
                }
                case "3" -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }
}
