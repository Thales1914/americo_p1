import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Hospede extends Pessoa {
    private String rg;
    private boolean fidelidade;
    private static final String FILE_NAME = "hospedes.txt";

    public Hospede(String cpf, String nome, int idade, String rg, boolean fidelidade) {
        super(cpf, nome, idade);
        this.rg = rg;
        this.fidelidade = fidelidade;
    }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public boolean isFidelidade() { return fidelidade; }
    public void setFidelidade(boolean fidelidade) { this.fidelidade = fidelidade; }

    @Override
    public String toString() {
        return String.format("%s;%s;%d;%s;%b", getCpf(), getNome(), getIdade(), getRg(), isFidelidade());
    }

    @Override
    public void mostrar() {
        System.out.println("--- DADOS DO HÓSPEDE ---");
        System.out.println("CPF: " + getCpf());
        System.out.println("Nome: " + getNome());
        System.out.println("Idade: " + getIdade());
        System.out.println("RG: " + getRg());
        System.out.println("Programa de Fidelidade: " + (isFidelidade() ? "Ativo" : "Inativo"));
        System.out.println("------------------------");
    }

    @Override
    public void inserir(Scanner scanner) {
        try {
            System.out.print("Digite o CPF do hóspede: ");
            String cpf = scanner.nextLine();
            if (findByCpf(cpf) != null) {
                System.err.println("Erro: Já existe um hóspede com este CPF.");
                return;
            }
            System.out.print("Digite o nome: ");
            String nome = scanner.nextLine();
            System.out.print("Digite a idade: ");
            int idade = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Digite o RG: ");
            String rg = scanner.nextLine();
            System.out.print("É cliente fidelidade (true/false)? ");
            boolean fidelidade = scanner.nextBoolean();
            scanner.nextLine();

            Hospede novoHospede = new Hospede(cpf, nome, idade, rg, fidelidade);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(novoHospede.toString());
                writer.newLine();
                System.out.println("Hóspede inserido com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao salvar hóspede: " + e.getMessage());
            }
        } catch (InputMismatchException e) {
            System.err.println("Erro de entrada. Verifique os tipos de dados.");
            scanner.nextLine();
        }
    }

    @Override
    public void listar() {
        List<Hospede> hospedes = findAll();
        if (hospedes.isEmpty()) {
            System.out.println("Nenhum hóspede cadastrado.");
        } else {
            hospedes.forEach(Hospede::mostrar);
        }
    }

    @Override
    public void consultar(Scanner scanner) {
        System.out.print("Digite o CPF do Hóspede a ser consultado: ");
        String cpf = scanner.nextLine();
        Hospede h = findByCpf(cpf);
        if (h != null) {
            h.mostrar();
        } else {
            System.out.println("Hóspede com CPF " + cpf + " não encontrado.");
        }
    }

    @Override
    public void atualizar(Scanner scanner) {
        System.out.print("Digite o CPF do hóspede a ser atualizado: ");
        String cpf = scanner.nextLine();
        List<Hospede> hospedes = findAll();
        Hospede hospedeParaAtualizar = null;
        for (Hospede h : hospedes) {
            if (h.getCpf().equals(cpf)) {
                hospedeParaAtualizar = h;
                break;
            }
        }

        if (hospedeParaAtualizar != null) {
            try {
                System.out.println("Hóspede encontrado. Insira os novos dados:");
                System.out.print("Novo nome: ");
                hospedeParaAtualizar.setNome(scanner.nextLine());
                System.out.print("Nova idade: ");
                hospedeParaAtualizar.setIdade(scanner.nextInt());
                scanner.nextLine();
                System.out.print("Novo RG: ");
                hospedeParaAtualizar.setRg(scanner.nextLine());
                System.out.print("É cliente fidelidade (true/false)? ");
                hospedeParaAtualizar.setFidelidade(scanner.nextBoolean());
                scanner.nextLine();

                saveAll(hospedes);
                System.out.println("Hóspede atualizado com sucesso!");
            } catch (InputMismatchException e) {
                System.err.println("Erro de entrada. Verifique os tipos de dados.");
                scanner.nextLine();
            }
        } else {
            System.out.println("Hóspede com CPF " + cpf + " não encontrado.");
        }
    }

    @Override
    public void excluir(Scanner scanner) {
        System.out.print("Digite o CPF do hóspede a ser excluído: ");
        String cpf = scanner.nextLine();
        List<Hospede> hospedes = findAll();
        boolean removed = hospedes.removeIf(h -> h.getCpf().equals(cpf));

        if (removed) {
            saveAll(hospedes);
            System.out.println("Hóspede excluído com sucesso!");
        } else {
            System.out.println("Hóspede com CPF " + cpf + " não encontrado.");
        }
    }

    private void saveAll(List<Hospede> hospedes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Hospede h : hospedes) {
                writer.write(h.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a lista de hóspedes: " + e.getMessage());
        }
    }

    public List<Hospede> findAll() {
        List<Hospede> hospedes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    hospedes.add(new Hospede(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], Boolean.parseBoolean(parts[4])));
                }
            }
        } catch (IOException e) { /* Arquivo pode não existir */ }
        return hospedes;
    }

    public Hospede findByCpf(String cpf) {
        return findAll().stream().filter(h -> h.getCpf().equals(cpf)).findFirst().orElse(null);
    }
}