import java.util.Scanner;

public abstract class Pessoa {
    protected String cpf;
    protected String nome;
    protected int idade;

    public Pessoa(String cpf, String nome, int idade) {
        this.cpf = cpf;
        this.nome = nome;
        this.idade = idade;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public abstract void inserir(Scanner scanner);
    public abstract void listar();
    public abstract void consultar(Scanner scanner);
    public abstract void atualizar(Scanner scanner);
    public abstract void excluir(Scanner scanner);

    public abstract void mostrar();
    @Override
    public abstract String toString();
}