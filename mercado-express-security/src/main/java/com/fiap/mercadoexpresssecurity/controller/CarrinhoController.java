package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.dto.CarrinhoItemView;
import com.fiap.mercadoexpresssecurity.model.Compra;
import com.fiap.mercadoexpresssecurity.model.Mercado;
import com.fiap.mercadoexpresssecurity.model.Usuario;
import com.fiap.mercadoexpresssecurity.repository.CompraRepository;
import com.fiap.mercadoexpresssecurity.repository.MercadoRepository;
import com.fiap.mercadoexpresssecurity.repository.UsuarioRepository;
import com.fiap.mercadoexpresssecurity.service.CompraService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluxo de carrinho de compras (CP5 Parte 2): o usuário logado adiciona
 * produtos do catálogo ao carrinho (guardado na sessão), confere o resumo, e
 * ao finalizar, cada item vira uma Compra publicada na fila.pedidos.compra
 * (ver CompraService), processada de forma assíncrona pelo microsserviço
 * banco-tranquilo-pagamentos.
 */
@Controller
@RequiredArgsConstructor
public class CarrinhoController {

    private static final String SESSION_CARRINHO = "carrinho";

    private final MercadoRepository mercadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final CompraService compraService;

    @GetMapping("/carrinho/adicionar/{produtoId}")
    public String adicionar(@PathVariable Long produtoId, HttpSession session) {
        Map<Long, Integer> carrinho = obterCarrinho(session);
        carrinho.merge(produtoId, 1, Integer::sum);
        session.setAttribute(SESSION_CARRINHO, carrinho);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover/{produtoId}")
    public String remover(@PathVariable Long produtoId, HttpSession session) {
        Map<Long, Integer> carrinho = obterCarrinho(session);
        carrinho.remove(produtoId);
        session.setAttribute(SESSION_CARRINHO, carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String verCarrinho(HttpSession session, Model model) {
        Map<Long, Integer> carrinho = obterCarrinho(session);

        List<CarrinhoItemView> itens = new ArrayList<>();
        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : carrinho.entrySet()) {
            Mercado produto = mercadoRepository.findById(entry.getKey()).orElse(null);
            if (produto == null) {
                continue;
            }
            double subtotal = produto.getPreco() * entry.getValue();
            itens.add(new CarrinhoItemView(produto, entry.getValue(), subtotal));
            total += subtotal;
        }

        model.addAttribute("itens", itens);
        model.addAttribute("total", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/finalizar")
    public String finalizar(HttpSession session, Authentication authentication, RedirectAttributes redirectAttributes) {
        Map<Long, Integer> carrinho = obterCarrinho(session);

        if (carrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Seu carrinho esta vazio.");
            return "redirect:/carrinho";
        }

        Usuario comprador = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        String nomeComprador = comprador != null ? comprador.getNome() : authentication.getName();
        String emailComprador = authentication.getName();

        for (Map.Entry<Long, Integer> entry : carrinho.entrySet()) {
            compraService.finalizarCompra(entry.getKey(), entry.getValue(), nomeComprador, emailComprador);
        }

        session.removeAttribute(SESSION_CARRINHO);

        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Compra enviada para processamento! Assim que o Banco Tranquilo confirmar o pagamento, " +
                        "voce recebera um e-mail e podera acompanhar o status abaixo.");
        return "redirect:/minhas-compras";
    }

    @GetMapping("/minhas-compras")
    public String minhasCompras(Authentication authentication, Model model) {
        String email = authentication.getName();
        List<Compra> compras = compraRepository.findAllByOrderByDataCompraDesc().stream()
                .filter(c -> c.getEmailComprador().equalsIgnoreCase(email))
                .toList();
        model.addAttribute("compras", compras);
        return "minhas-compras";
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> obterCarrinho(HttpSession session) {
        Map<Long, Integer> carrinho = (Map<Long, Integer>) session.getAttribute(SESSION_CARRINHO);
        if (carrinho == null) {
            carrinho = new LinkedHashMap<>();
            session.setAttribute(SESSION_CARRINHO, carrinho);
        }
        return carrinho;
    }
}
