import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Funcionario extends Pessoa {
    private String funcao;
    private static final String FILE_NAME = "funcionarios.txt";

    public Funcionario(String cpf, String nome, int idade, String funcao) {
        super(cpf, nome, idade);
        this.funcao = funcao;
    }

    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }

    @Override
    public String toString() {
        return String.format("%s;%s;%d;%s", getCpf(), getNome(), getIdade(), getFuncao());
    }

    @Override
    public void mostrar() {
        System.out.println("--- DADOS DO FUNCIONÁRIO ---");
        System.out.println("CPF: " + getCpf());
        System.out.println("Nome: " + getNome());
        System.out.println("Idade: " + getIdade());
        System.out.println("Função: " + getFuncao());
        System.out.println("----------------------------");
    }

    @Override
    public void inserir(Scanner scanner) {
        try {
            System.out.print("Digite o CPF do funcionário: ");
            String cpf = scanner.nextLine();
            if (findByCpf(cpf) != null) {
                System.err.println("Erro: Já existe um funcionário com este CPF.");
                return;
            }
            System.out.print("Digite o nome: ");
            String nome = scanner.nextLine();
            System.out.print("Digite a idade: ");
            int idade = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Digite a função: ");
            String funcao = scanner.nextLine();

            Funcionario novoFuncionario = new Funcionario(cpf, nome, idade, funcao);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(novoFuncionario.toString());
                writer.newLine();
                System.out.println("Funcionário inserido com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao salvar funcionário: " + e.getMessage());
            }
        } catch (InputMismatchException e) {
            System.err.println("Erro de entrada. Idade deve ser um número.");
            scanner.nextLine();
        }
    }

    @Override
    public void listar() {
        List<Funcionario> funcionarios = findAll();
        if (funcionarios.isEmpty()) {
            System.out.println("Nenhum funcionário cadastrado.");
        } else {
            funcionarios.forEach(Funcionario::mostrar);
        }
    }

    @Override
    public void consultar(Scanner scanner) {
        System.out.print("Digite o CPF do Funcionário a ser consultado: ");
        String cpf = scanner.nextLine();
        Funcionario f = findByCpf(cpf);
        if (f != null) {
            f.mostrar();
        } else {
            System.out.println("Funcionário com CPF " + cpf + " não encontrado.");
        }
    }

    @Override
    public void atualizar(Scanner scanner) {
        System.out.print("Digite o CPF do funcionário a ser atualizado: ");
        String cpf = scanner.nextLine();
        List<Funcionario> funcionarios = findAll();
        Funcionario funcParaAtualizar = null;
        for (Funcionario f : funcionarios) {
            if (f.getCpf().equals(cpf)) {
                funcParaAtualizar = f;
                break;
            }
        }

        if (funcParaAtualizar != null) {
            try {
                System.out.println("Funcionário encontrado. Insira os novos dados:");
                System.out.print("Novo nome: ");
                funcParaAtualizar.setNome(scanner.nextLine());
                System.out.print("Nova idade: ");
                funcParaAtualizar.setIdade(scanner.nextInt());
                scanner.nextLine();
                System.out.print("Nova função: ");
                funcParaAtualizar.setFuncao(scanner.nextLine());

                saveAll(funcionarios);
                System.out.println("Funcionário atualizado com sucesso!");
            } catch (InputMismatchException e) {
                System.err.println("Erro de entrada. Idade deve ser um número.");
                scanner.nextLine();
            }
        } else {
            System.out.println("Funcionário com CPF " + cpf + " não encontrado.");
        }
    }

    @Override
    public void excluir(Scanner scanner) {
        System.out.print("Digite o CPF do funcionário a ser excluído: ");
        String cpf = scanner.nextLine();
        List<Funcionario> funcionarios = findAll();
        boolean removed = funcionarios.removeIf(f -> f.getCpf().equals(cpf));

        if (removed) {
            saveAll(funcionarios);
            System.out.println("Funcionário excluído com sucesso!");
        } else {
            System.out.println("Funcionário com CPF " + cpf + " não encontrado.");
        }
    }

    private void saveAll(List<Funcionario> funcionarios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Funcionario f : funcionarios) {
                writer.write(f.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a lista de funcionários: " + e.getMessage());
        }
    }

    public List<Funcionario> findAll() {
        List<Funcionario> funcionarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    funcionarios.add(new Funcionario(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
                }
            }
        } catch (IOException e) { /* Arquivo pode não existir */ }
        return funcionarios;
    }

    public Funcionario findByCpf(String cpf) {
        return findAll().stream().filter(f -> f.getCpf().equals(cpf)).findFirst().orElse(null);
    }
}