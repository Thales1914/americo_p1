import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Apenas os managers que serão utilizados
        Hospede hospedeManager = new Hospede(null, null, 0, null, false);
        Funcionario funcionarioManager = new Funcionario(null, null, 0, null);

        int opcao;
        do {
            exibirMenu();
            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                switch (opcao) {
                    // --- HÓSPEDES ---
                    case 1: hospedeManager.inserir(scanner); break;
                    case 2: hospedeManager.listar(); break;
                    case 3: hospedeManager.consultar(scanner); break;
                    case 4: hospedeManager.atualizar(scanner); break;
                    case 5: hospedeManager.excluir(scanner); break;

                    // --- FUNCIONÁRIOS ---
                    case 6: funcionarioManager.inserir(scanner); break;
                    case 7: funcionarioManager.listar(); break;
                    case 8: funcionarioManager.consultar(scanner); break;
                    case 9: funcionarioManager.atualizar(scanner); break;
                    case 10: funcionarioManager.excluir(scanner); break;

                    // --- SAIR ---
                    case 0:
                        System.out.println("Saindo do sistema... Obrigado!");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Erro: Por favor, digite um número para a opção.");
                scanner.nextLine();
                opcao = -1; // Reseta a opção para continuar no loop
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n---------- MENU PRINCIPAL - HOTEL ----------");
        System.out.println("--- HÓSPEDES ---");
        System.out.println(" 1. Inserir | 2. Listar | 3. Consultar | 4. Atualizar | 5. Excluir");
        System.out.println("--- FUNCIONÁRIOS ---");
        System.out.println(" 6. Inserir | 7. Listar | 8. Consultar | 9. Atualizar | 10. Excluir");
        System.out.println("----------------------------------------------------");
        System.out.println(" 0. Sair");
        System.out.print("Escolha uma opção: ");
    }
}