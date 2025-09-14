import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Reserva {
    private int idReserva;
    private double valor;
    private String dataEntrada;
    private String dataSaida;
    private boolean checkin;
    private boolean checkout;
    private Quarto quarto;
    private Hospede hospede;
    private static final String FILE_NAME = "reservas.txt";

    public Reserva(int idReserva, double valor, String dataEntrada, String dataSaida, boolean checkin, boolean checkout, Quarto quarto, Hospede hospede) {
        this.idReserva = idReserva;
        this.valor = valor;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.checkin = checkin;
        this.checkout = checkout;
        this.quarto = quarto;
        this.hospede = hospede;
    }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(String dataEntrada) { this.dataEntrada = dataEntrada; }
    public String getDataSaida() { return dataSaida; }
    public void setDataSaida(String dataSaida) { this.dataSaida = dataSaida; }
    public boolean isCheckin() { return checkin; }
    public void setCheckin(boolean checkin) { this.checkin = checkin; }
    public boolean isCheckout() { return checkout; }
    public void setCheckout(boolean checkout) { this.checkout = checkout; }
    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }
    public Hospede getHospede() { return hospede; }
    public void setHospede(Hospede hospede) { this.hospede = hospede; }

    @Override
    public String toString() {
        return String.format("%d;%.2f;%s;%s;%b;%b;%d;%s",
                        this.idReserva, this.valor, this.dataEntrada, this.dataSaida,
                        this.checkin, this.checkout, this.quarto.getIdQuarto(), this.hospede.getCpf())
                .replace(",", ".");
    }

    public void mostrar() {
        System.out.println("--- DADOS DA RESERVA ---");
        System.out.println("ID Reserva: " + this.idReserva);
        System.out.println("Hóspede: " + (this.hospede != null ? this.hospede.getNome() + " (CPF: " + this.hospede.getCpf() + ")" : "N/A"));
        System.out.println("Quarto: " + (this.quarto != null ? this.quarto.getIdQuarto() + " - " + this.quarto.getDescQuarto() : "N/A"));
        System.out.println("Data Entrada: " + this.dataEntrada + " | Data Saída: " + this.dataSaida);
        System.out.println("Valor Total: R$ " + String.format("%.2f", this.valor));
        System.out.println("Check-in Realizado: " + (this.checkin ? "Sim" : "Não"));
        System.out.println("Check-out Realizado: " + (this.checkout ? "Sim" : "Não"));
        System.out.println("------------------------");
    }

    public void inserir(Scanner scanner) {
        try {
            Hospede hospedeManager = new Hospede(null,null,0,null,false);
            Quarto quartoManager = new Quarto(0,null);

            System.out.print("Digite o ID da nova reserva: ");
            int idReserva = scanner.nextInt(); scanner.nextLine();
            if (findById(idReserva) != null) {
                System.err.println("Erro: Já existe uma reserva com este ID.");
                return;
            }

            System.out.print("Digite o CPF do Hóspede: ");
            String cpfHospede = scanner.nextLine();
            Hospede hospedeEncontrado = hospedeManager.findByCpf(cpfHospede);
            if (hospedeEncontrado == null) {
                System.err.println("Erro: Hóspede com CPF " + cpfHospede + " não encontrado.");
                return;
            }

            System.out.print("Digite o ID do Quarto: ");
            int idQuarto = scanner.nextInt(); scanner.nextLine();
            Quarto quartoEncontrado = quartoManager.findById(idQuarto);
            if (quartoEncontrado == null) {
                System.err.println("Erro: Quarto com ID " + idQuarto + " não encontrado.");
                return;
            }

            System.out.print("Digite a Data de Entrada (dd/mm/aaaa): ");
            String dataEntrada = scanner.nextLine();
            System.out.print("Digite a Data de Saída (dd/mm/aaaa): ");
            String dataSaida = scanner.nextLine();
            System.out.print("Digite o valor total da reserva: ");
            double valor = scanner.nextDouble(); scanner.nextLine();
            System.out.print("Check-in já realizado (true/false)? ");
            boolean checkin = scanner.nextBoolean(); scanner.nextLine();
            System.out.print("Check-out já realizado (true/false)? ");
            boolean checkout = scanner.nextBoolean(); scanner.nextLine();

            Reserva novaReserva = new Reserva(idReserva, valor, dataEntrada, dataSaida, checkin, checkout, quartoEncontrado, hospedeEncontrado);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(novaReserva.toString());
                writer.newLine();
                System.out.println("Reserva inserida com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao salvar a reserva: " + e.getMessage());
            }

        } catch (InputMismatchException e) {
            System.err.println("Erro de entrada. Verifique os tipos de dados inseridos.");
            scanner.nextLine();
        }
    }

    public void listar() {
        List<Reserva> reservas = findAll();
        if (reservas.isEmpty()) {
            System.out.println("Nenhuma reserva cadastrada.");
        } else {
            reservas.forEach(Reserva::mostrar);
        }
    }

    public void consultar(Scanner scanner) {
        System.out.print("Digite o ID da Reserva para consulta: ");
        int id = scanner.nextInt(); scanner.nextLine();
        Reserva r = findById(id);
        if (r != null) {
            r.mostrar();
        } else {
            System.out.println("Reserva com ID " + id + " não encontrada.");
        }
    }

    public void atualizar(Scanner scanner) {
        System.out.print("Digite o ID da reserva a ser atualizada: ");
        int id = scanner.nextInt(); scanner.nextLine();
        List<Reserva> reservas = findAll();
        Reserva reservaParaAtualizar = reservas.stream().filter(r -> r.getIdReserva() == id).findFirst().orElse(null);

        if (reservaParaAtualizar != null) {
            try {
                System.out.println("Reserva encontrada. Insira os novos dados:");

                System.out.print("Check-in realizado (true/false)? ");
                reservaParaAtualizar.setCheckin(scanner.nextBoolean()); scanner.nextLine();
                System.out.print("Check-out realizado (true/false)? ");
                reservaParaAtualizar.setCheckout(scanner.nextBoolean()); scanner.nextLine();

                saveAll(reservas);
                System.out.println("Reserva atualizada com sucesso!");
            } catch (InputMismatchException e) {
                System.err.println("Erro de entrada de dados.");
                scanner.nextLine();
            }
        } else {
            System.out.println("Reserva com ID " + id + " não encontrada.");
        }
    }

    public void excluir(Scanner scanner) {
        System.out.print("Digite o ID da reserva a ser excluída: ");
        int id = scanner.nextInt(); scanner.nextLine();
        List<Reserva> reservas = findAll();
        boolean removed = reservas.removeIf(r -> r.getIdReserva() == id);

        if (removed) {
            saveAll(reservas);
            System.out.println("Reserva excluída com sucesso!");
        } else {
            System.out.println("Reserva com ID " + id + " não encontrada.");
        }
    }

    private void saveAll(List<Reserva> reservas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Reserva r : reservas) {
                writer.write(r.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a lista de reservas: " + e.getMessage());
        }
    }

    public List<Reserva> findAll() {
        List<Reserva> reservas = new ArrayList<>();
        Hospede hospedeManager = new Hospede(null, null, 0, null, false);
        Quarto quartoManager = new Quarto(0, null);

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 8) {
                    int idReserva = Integer.parseInt(parts[0]);
                    double valor = Double.parseDouble(parts[1]);
                    String dataEntrada = parts[2];
                    String dataSaida = parts[3];
                    boolean checkin = Boolean.parseBoolean(parts[4]);
                    boolean checkout = Boolean.parseBoolean(parts[5]);
                    int idQuarto = Integer.parseInt(parts[6]);
                    String cpfHospede = parts[7];

                    Quarto quarto = quartoManager.findById(idQuarto);
                    Hospede hospede = hospedeManager.findByCpf(cpfHospede);

                    if (quarto != null && hospede != null) {
                        reservas.add(new Reserva(idReserva, valor, dataEntrada, dataSaida, checkin, checkout, quarto, hospede));
                    } else {
                        System.err.println("Aviso: Reserva ID " + idReserva + " ignorada por ter Hóspede ou Quarto inválido/excluído.");
                    }
                }
            }
        } catch (IOException e) { /* Arquivo pode não existir */ }
        return reservas;
    }

    public Reserva findById(int id) {
        return findAll().stream().filter(r -> r.getIdReserva() == id).findFirst().orElse(null);
    }
}