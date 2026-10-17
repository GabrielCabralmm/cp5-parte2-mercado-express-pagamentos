package com.fiap.mercadoexpresssecurity.repository;

import com.fiap.mercadoexpresssecurity.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findAllByOrderByDataCompraDesc();

    List<Compra> findByStatus(Compra.Status status);
}
