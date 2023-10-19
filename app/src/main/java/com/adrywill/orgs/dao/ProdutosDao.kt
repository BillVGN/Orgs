package com.adrywill.orgs.dao

import com.adrywill.orgs.model.Produtos
import java.math.BigDecimal

class ProdutosDao {

    fun adiciona(produto: Produtos) {
        produtos.add(produto)
    }

    fun buscaTodos(): List<Produtos> {
        return produtos.toList()
    }

    companion object {
        private val produtos = mutableListOf<Produtos>(
            Produtos(
                "Salada de Frutas",
                "Laranja, maçãs e uvas",
                BigDecimal("19.83")
            )
        )
    }
}