package com.adrywill.orgs.dao

import com.adrywill.orgs.model.Produtos

class ProdutosDao {

    fun adiciona(produto: Produtos) {
        produtos.add(produto)
    }

    fun buscaTodos() : List<Produtos> {
        return produtos.toList()
    }

    companion object {
        private val produtos = mutableListOf<Produtos>()
    }
}