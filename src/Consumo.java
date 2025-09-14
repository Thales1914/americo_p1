import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Consumo {
    private int idConsumo;
    private Reserva reserva;
    private Produto produto;
    private double quantidade;
    private static final String FILE_NAME = "consumos.txt";

    public Consumo(int idConsumo, Reserva reserva, Produto produto, double quantidade) {
        this.idConsumo = idConsumo;
        this.reserva = reserva;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public int getIdConsumo() { return idConsumo; }
    public void setIdConsumo(int idConsumo) { this.idConsumo = idConsumo; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    @Override
    public String toString() {
        return String.format("%d;%d;%d;%.2f",
                        this.idConsumo, this.reserva.getIdReserva(), this.produto.getIdProduto(), this.quantidade)
                .replace(",", ".");
    }

    public void mostrar() {
        System.out.println("--- DADOS DO CONSUMO ---");
        System.out.println("ID Consumo: " + this.idConsumo);
        System.out.println("Reserva Associada (ID): " + (this.reserva != null ? this.reserva.getIdReserva() : "N/A"));
        System.out.println("Produto: " + (this.produto != null ? this.produto.getDescProduto() : "N/A"));
        System.out.println("Quantidade: " + this.quantidade);
        System.out.println("------------------------");
    }

    public void inserir(Scanner scanner) {
        try {
            Reserva reservaManager = new Reserva(0,0,null,null,false,false,null,null);
            Produto produtoManager = new Produto(0,null,0);

            System.out.print("Digite o ID do novo consumo: ");
            int idConsumo = scanner.nextInt(); scanner.nextLine();
            if (findById(idConsumo) != null) {
                System.err.println("Erro: Já existe um consumo com este ID.");
                return;
            }

            System.out.print("Digite o ID da Reserva associada: ");
            int idReserva = scanner.nextInt(); scanner.nextLine();
            Reserva reservaEncontrada = reservaManager.findById(idReserva);
            if (reservaEncontrada == null) {
                System.err.println("Erro: Reserva com ID " + idReserva + " não encontrada.");
                return;
            }

            System.out.print("Digite o ID do Produto consumido: ");
            int idProduto = scanner.nextInt(); scanner.nextLine();
            Produto produtoEncontrado = produtoManager.findById(idProduto);
            if (produtoEncontrado == null) {
                System.err.println("Erro: Produto com ID " + idProduto + " não encontrado.");
                return;
            }

            System.out.print("Digite a quantidade: ");
            double quantidade = scanner.nextDouble(); scanner.nextLine();

            Consumo novoConsumo = new Consumo(idConsumo, reservaEncontrada, produtoEncontrado, quantidade);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(novoConsumo.toString());
                writer.newLine();
                System.out.println("Consumo registrado com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao salvar o consumo: " + e.getMessage());
            }

        } catch (InputMismatchException e) {
            System.err.println("Erro de entrada de dados. Verifique os tipos.");
            scanner.nextLine();
        }
    }

    public void listar() {
        List<Consumo> consumos = findAll();
        if (consumos.isEmpty()) {
            System.out.println("Nenhum consumo registrado.");
        } else {
            consumos.forEach(Consumo::mostrar);
        }
    }

    public void consultar(Scanner scanner) {
        System.out.print("Digite o ID do Consumo para consulta: ");
        int id = scanner.nextInt(); scanner.nextLine();
        Consumo c = findById(id);
        if (c != null) {
            c.mostrar();
        } else {
            System.out.println("Consumo com ID " + id + " não encontrado.");
        }
    }

    public void atualizar(Scanner scanner) {
        System.out.print("Digite o ID do consumo para atualizar: ");
        int id = scanner.nextInt(); scanner.nextLine();
        List<Consumo> consumos = findAll();
        Consumo consumoParaAtualizar = consumos.stream().filter(c -> c.getIdConsumo() == id).findFirst().orElse(null);

        if (consumoParaAtualizar != null) {
            try {
                System.out.print("Nova quantidade: ");
                consumoParaAtualizar.setQuantidade(scanner.nextDouble());
                scanner.nextLine();
                saveAll(consumos);
                System.out.println("Consumo atualizado com sucesso!");
            } catch (InputMismatchException e) {
                System.err.println("Erro de entrada. Quantidade deve ser um número.");
                scanner.nextLine();
            }
        } else {
            System.out.println("Consumo não encontrado.");
        }
    }

    public void excluir(Scanner scanner) {
        System.out.print("Digite o ID do consumo para excluir: ");
        int id = scanner.nextInt(); scanner.nextLine();
        List<Consumo> consumos = findAll();
        boolean removed = consumos.removeIf(c -> c.getIdConsumo() == id);

        if (removed) {
            saveAll(consumos);
            System.out.println("Consumo excluído com sucesso!");
        } else {
            System.out.println("Consumo com ID " + id + " não encontrado.");
        }
    }

    private void saveAll(List<Consumo> consumos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Consumo c : consumos) {
                writer.write(c.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a lista de consumos: " + e.getMessage());
        }
    }

    public List<Consumo> findAll() {
        List<Consumo> consumos = new ArrayList<>();
        Reserva reservaManager = new Reserva(0,0,null,null,false,false,null,null);
        Produto produtoManager = new Produto(0,null,0);

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    int idConsumo = Integer.parseInt(parts[0]);
                    int idReserva = Integer.parseInt(parts[1]);
                    int idProduto = Integer.parseInt(parts[2]);
                    double quantidade = Double.parseDouble(parts[3]);

                    Reserva reserva = reservaManager.findById(idReserva);
                    Produto produto = produtoManager.findById(idProduto);

                    if (reserva != null && produto != null) {
                        consumos.add(new Consumo(idConsumo, reserva, produto, quantidade));
                    } else {
                        System.err.println("Aviso: Consumo ID " + idConsumo + " ignorado por ter Reserva ou Produto inválido/excluído.");
                    }
                }
            }
        } catch (IOException e) { /* Arquivo pode não existir */ }
        return consumos;
    }

    public Consumo findById(int id) {
        return findAll().stream().filter(c -> c.getIdConsumo() == id).findFirst().orElse(null);
    }
}