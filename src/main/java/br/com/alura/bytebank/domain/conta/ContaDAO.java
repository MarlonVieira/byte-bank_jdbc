package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO);
        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)"+
                "VALUES(?,?,?,?,?)";

        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, conta.getSaldo());
            preparedStatement.setString(3,dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4,dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5,dadosDaConta.dadosCliente().email());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Conta buscarPorNumero(Integer numeroConta) {
        String sql = "SELECT numero, saldo, cliente_nome, cliente_cpf, cliente_email FROM conta WHERE numero = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, numeroConta);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Integer numero = resultSet.getInt("numero");
                BigDecimal saldo = resultSet.getBigDecimal("saldo");
                String nome = resultSet.getString("cliente_nome");
                String cpf = resultSet.getString("cliente_cpf");
                String email = resultSet.getString("cliente_email");

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);
                Conta conta = new Conta(numero, cliente, saldo);
                return conta;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta por número", e);
        }
        return null;
    }


    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT numero, saldo, cliente_nome, cliente_cpf, cliente_email FROM conta";

        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt("numero");
                BigDecimal saldo = resultSet.getBigDecimal("saldo");
                String nome = resultSet.getString("cliente_nome");
                String cpf = resultSet.getString("cliente_cpf");
                String email = resultSet.getString("cliente_email");

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);
                contas.add(new Conta(numero, cliente, saldo));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }
}
