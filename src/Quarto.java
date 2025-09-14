import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Quarto {
    private int idQuarto;
    private String descQuarto;
    private static final String FILE_NAME = "quartos.txt";

    public Quarto(int idQuarto, String descQuarto) {
        this.idQuarto = idQuarto;
        this.descQuarto = descQuarto;
    }

    public int getIdQuarto() { return idQuarto; }
    public void setIdQuarto(int idQuarto) { this.idQuarto = idQuarto; }
    public String getDescQuarto() { return descQuarto; }
    public void setDescQuarto(String descQuarto) { this.descQuarto = descQuarto; }

    @Override
    public String toString() {
        return String.format("%d;%s", this.idQuarto, this.descQuarto);
    }

    public void mostrar() {
        System.out.println("ID: " + this.idQuarto + " | Descrição: " + this.descQuarto);
    }

    public void inserir(Scanner scanner) {
        try {
            System.out.print("Digite o ID do novo quarto: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            if (findById(id) != null) {
                System.err.println("Erro: Já existe um quarto com este ID.");
                return;
            }
            System.out.print("Digite a descrição (Ex: Luxo, Padrão): ");
            String desc = scanner.nextLine();

            Quarto q = new Quarto(id, desc);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(q.toString());
                writer.newLine();
                System.out.println("Quarto inserido com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao salvar quarto.");
            }
        } catch (InputMismatchException e) {
            System.err.println("Erro de entrada de dados. ID deve ser um número.");
            scanner.nextLine();
        }
    }

    public void listar() {
        List<Quarto> quartos = findAll();
        if (quartos.isEmpty()) {
            System.out.println("Nenhum quarto cadastrado.");
        } else {
            quartos.forEach(Quarto::mostrar);
        }
    }

    public void consultar(Scanner scanner) {
        System.out.print("Digite o ID do Quarto para consulta: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Quarto q = findById(id);
        if (q != null) {
            q.mostrar();
        } else {
            System.out.println("Quarto com ID " + id + " não encontrado.");
        }
    }

    public void atualizar(Scanner scanner) {
        System.out.print("Digite o ID do quarto para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        List<Quarto> quartos = findAll();
        Quarto quartoParaAtualizar = null;
        for (Quarto q : quartos) {
            if (q.getIdQuarto() == id) {
                quartoParaAtualizar = q;
                break;
            }
        }

        if (quartoParaAtualizar != null) {
            System.out.print("Nova descrição: ");
            quartoParaAtualizar.setDescQuarto(scanner.nextLine());
            saveAll(quartos);
            System.out.println("Quarto atualizado com sucesso!");
        } else {
            System.out.println("Quarto não encontrado.");
        }
    }

    public void excluir(Scanner scanner) {
        System.out.print("Digite o ID do quarto para excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        List<Quarto> quartos = findAll();
        boolean removed = quartos.removeIf(q -> q.getIdQuarto() == id);

        if (removed) {
            saveAll(quartos);
            System.out.println("Quarto excluído com sucesso!");
        } else {
            System.out.println("Quarto não encontrado.");
        }
    }

    private void saveAll(List<Quarto> quartos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Quarto q : quartos) {
                writer.write(q.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a lista de quartos: " + e.getMessage());
        }
    }

    public List<Quarto> findAll() {
        List<Quarto> quartos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    quartos.add(new Quarto(Integer.parseInt(parts[0]), parts[1]));
                }
            }
        } catch (IOException e) { /* Arquivo pode não existir */ }
        return quartos;
    }

    public Quarto findById(int id) {
        return findAll().stream().filter(q -> q.getIdQuarto() == id).findFirst().orElse(null);
    }
}