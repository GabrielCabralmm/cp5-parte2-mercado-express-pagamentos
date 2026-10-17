package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.model.Compra;
import com.fiap.mercadoexpresssecurity.model.Mercado;
import com.fiap.mercadoexpresssecurity.repository.CompraRepository;
import com.fiap.mercadoexpresssecurity.repository.MercadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard da gerência do Mercado Express (CP5 Parte 2): mostra as compras
 * realizadas e gráficos de vendas, lucro e estoque, conforme exigido no
 * enunciado. Acesso restrito a ROLE_ADMIN (ver SecurityConfig, "/admin/**").
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CompraRepository compraRepository;
    private final MercadoRepository mercadoRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        List<Compra> compras = compraRepository.findAllByOrderByDataCompraDesc();
        List<Mercado> produtos = mercadoRepository.findAll();

        List<Compra> confirmadas = compras.stream()
                .filter(c -> c.getStatus() == Compra.Status.CONFIRMADA)
                .toList();

        // Vendas por produto (soma do valor total das compras confirmadas)
        Map<String, Double> vendasPorProduto = new LinkedHashMap<>();
        // Lucro por produto ((preco - custo) * quantidade, compras confirmadas)
        Map<String, Double> lucroPorProduto = new LinkedHashMap<>();

        for (Compra compra : confirmadas) {
            String nome = compra.getProduto().getNome();
            vendasPorProduto.merge(nome, compra.getValorTotal(), Double::sum);

            double custo = compra.getProduto().getCusto() != null ? compra.getProduto().getCusto() : 0.0;
            double lucro = (compra.getProduto().getPreco() - custo) * compra.getQuantidade();
            lucroPorProduto.merge(nome, lucro, Double::sum);
        }

        // Estoque atual de todos os produtos
        Map<String, Integer> estoquePorProduto = new LinkedHashMap<>();
        for (Mercado produto : produtos) {
            estoquePorProduto.put(produto.getNome(), produto.getEstoque());
        }

        double totalVendido = vendasPorProduto.values().stream().mapToDouble(Double::doubleValue).sum();
        double lucroTotal = lucroPorProduto.values().stream().mapToDouble(Double::doubleValue).sum();

        model.addAttribute("compras", compras);
        model.addAttribute("totalCompras", compras.size());
        model.addAttribute("totalConfirmadas", confirmadas.size());
        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("lucroTotal", lucroTotal);

        model.addAttribute("labelsVendas", vendasPorProduto.keySet());
        model.addAttribute("valoresVendas", vendasPorProduto.values());
        model.addAttribute("labelsLucro", lucroPorProduto.keySet());
        model.addAttribute("valoresLucro", lucroPorProduto.values());
        model.addAttribute("labelsEstoque", estoquePorProduto.keySet());
        model.addAttribute("valoresEstoque", estoquePorProduto.values());

        return "dashboard";
    }
}
