import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Produto {
    private int idProduto;
    private String descProduto;
    private double valor;
    private static final String FILE_NAME = "produtos.txt";

    public Produto(int idProduto, String descProduto, double valor) {
        this.idProduto = idProduto;
        this.descProduto = descProduto;
        this.valor = valor;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }
    public String getDescProduto() { return descProduto; }
    public void setDescProduto(String descProduto) { this.descProduto = descProduto; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    @Override
    public String toString() {
        return String.format("%d;%s;%.2f", this.idProduto, this.descProduto, this.valor).replace(",", ".");
    }

    public void mostrar() {
        System.out.println("ID: " + this.idProduto + " | Descrição: " + this.descProduto + " | Valor: R$ " + String.format("%.2f", this.valor));
    }

    public void inserir(Scanner scanner) {
        try {
            System.out.print("Digite o ID do novo produto: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            if (findById(id) != null) {
                System.err.println("Erro: Já existe um produto com este ID.");
                return;
            }
            System.out.print("Digite a descrição: ");
            String desc = scanner.nextLine();
            System.out.print("Digite o valor: ");
            double val = scanner.nextDouble();
            scanner.nextLine();

            Produto p = new Produto(id, desc, val);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(p.toString());
                writer.newLine();
                System.out.println("Produto inserido com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao salvar produto.");
            }
        } catch (InputMismatchException e) {
            System.err.println("Erro de entrada de dados. ID deve ser número e valor deve ser numérico.");
            scanner.nextLine();
        }
    }

    public void listar() {
        List<Produto> produtos = findAll();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            produtos.forEach(Produto::mostrar);
        }
    }

    public void consultar(Scanner scanner) {
        System.out.print("Digite o ID do Produto para consulta: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Produto p = findById(id);
        if (p != null) {
            p.mostrar();
        } else {
            System.out.println("Produto com ID " + id + " não encontrado.");
        }
    }

    public void atualizar(Scanner scanner) {
        System.out.print("Digite o ID do produto para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        List<Produto> produtos = findAll();
        Produto produtoParaAtualizar = null;
        for (Produto p : produtos) {
            if (p.getIdProduto() == id) {
                produtoParaAtualizar = p;
                break;
            }
        }

        if (produtoParaAtualizar != null) {
            try {
                System.out.print("Nova descrição: ");
                produtoParaAtualizar.setDescProduto(scanner.nextLine());
                System.out.print("Novo valor: ");
                produtoParaAtualizar.setValor(scanner.nextDouble());
                scanner.nextLine();
                saveAll(produtos);
                System.out.println("Produto atualizado com sucesso!");
            } catch (InputMismatchException e) {
                System.err.println("Erro de entrada de dados. Valor deve ser numérico.");
                scanner.nextLine();
            }
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    public void excluir(Scanner scanner) {
        System.out.print("Digite o ID do produto para excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        List<Produto> produtos = findAll();
        boolean removed = produtos.removeIf(p -> p.getIdProduto() == id);

        if (removed) {
            saveAll(produtos);
            System.out.println("Produto excluído com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private void saveAll(List<Produto> produtos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Produto p : produtos) {
                writer.write(p.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a lista de produtos: " + e.getMessage());
        }
    }

    public List<Produto> findAll() {
        List<Produto> produtos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    produtos.add(new Produto(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2])));
                }
            }
        } catch (IOException e) { /* Arquivo pode não existir */ }
        return produtos;
    }

    public Produto findById(int id) {
        return findAll().stream().filter(p -> p.getIdProduto() == id).findFirst().orElse(null);
    }
}