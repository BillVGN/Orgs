package com.adrywill.orgs.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrywill.orgs.databinding.ActivityDetalhesProdutoBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import java.text.NumberFormat
import java.util.Locale

class DetalhesProduto : AppCompatActivity() {

    private val layoutDetalhesProduto by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutDetalhesProduto.root)
        val produtoRecebido: Produto? = intent.extras?.getParcelable("produto")
        vinculaProduto(produtoRecebido)
    }

    private fun vinculaProduto(produto: Produto?) {
        with(layoutDetalhesProduto) {
            detalhesProdutoImagem.tentaCarregarImagem(produto?.imagem)
            detalhesProdutoNome.text = produto?.nome
            detalhesProdutoDescricao.text = produto?.descricao
            detalhesProdutoValor.text =
                NumberFormat.getCurrencyInstance(Locale("pt","br"))
                    .format(produto?.valor)
        }
    }
}