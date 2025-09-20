import java.io.*;
import java.util.*;

public class Main {
    private static Map<Integer, ProcessInfo> processes = new HashMap<>();

    static class ProcessInfo {
        Process process;
        String name;

        ProcessInfo(Process process, String name) {
            this.process = process;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Управление процессами ===");

            // Запуск процессов
            for (int i = 0; i < 3; i++) {
                System.out.print("Введите имя процесса " + (i+1) + ": ");
                String name = scanner.nextLine();
                if (!startProcess(name)) {
                    System.out.println("Не удалось запустить: " + name);
                }
            }

            // Управление процессами
            manageProcesses(scanner);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            scanner.close();
            cleanup();
        }
    }

    private static boolean startProcess(String name) {
        try {
            Process process = Runtime.getRuntime().exec(name);
            int pid = getPid(process);
            processes.put(pid, new ProcessInfo(process, name));
            System.out.println("✓ Запущен: " + name + " (PID: " + pid + ")");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static int getPid(Process process) {
        try {
            return (int) process.getClass().getMethod("pid").invoke(process);
        } catch (Exception e) {
            return process.hashCode();
        }
    }

    private static void manageProcesses(Scanner scanner) {
        while (!processes.isEmpty()) {
            System.out.println("\nАктивные процессы: " + processes.keySet());
            System.out.print("PID для завершения или 'выход': ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("выход")) break;

            try {
                int pid = Integer.parseInt(input);
                ProcessInfo info = processes.get(pid);

                if (info != null && confirmTermination(scanner)) {
                    info.process.destroy();
                    processes.remove(pid);
                    System.out.println("✓ Процесс " + pid + " завершен");
                }
            } catch (NumberFormatException e) {
                System.out.println("✗ Неверный PID");
            }
        }
    }

    private static boolean confirmTermination(Scanner scanner) {
        System.out.print("Завершить процесс? (д/н): ");
        return scanner.nextLine().trim().equalsIgnoreCase("д");
    }

    private static void cleanup() {
        processes.values().forEach(info -> info.process.destroy());
        System.out.println("\nВсе процессы завершены");
    }
}
