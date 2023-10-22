package com.adrywill.orgs.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.adrywill.orgs.dao.ProdutosDao
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.dialog.FormularioImagemDialog
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    var url: String? = null

    private val layoutFormularioProduto by lazy {
        ActivityFormularioProdutoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuraBotaoSalvar()
        configuraClickImagem()
        setContentView(layoutFormularioProduto.root)
        title = "Cadastrar Produto"
    }

    private fun configuraClickImagem() {
        layoutFormularioProduto.activityFormularioProdutoImagem.setOnClickListener {
            FormularioImagemDialog(this)
                .mostra(url) {imagem ->
                    url = imagem
                    layoutFormularioProduto.activityFormularioProdutoImagem.tentaCarregarImagem(url)
                }
        }
    }

    private fun configuraBotaoSalvar() {
        val produtoDao = AppDatabase.instancia(this).produtoDao()
        layoutFormularioProduto.activityProdutoItemBotaoSalvar.setOnClickListener {
            produtoDao.salva(criaProduto())
            finish()
        }

    }

    private fun criaProduto(): Produto {
        with(layoutFormularioProduto) {
            val valorEmTexto = activityFormularioProdutoValor.text.toString()
            return Produto(
                nome = activityFormularioProdutoNome.text.toString(),
                descricao = activityFormularioProdutoDescricao.text.toString(),
                valor = if (valorEmTexto.isBlank()) BigDecimal.ZERO else BigDecimal(valorEmTexto),
                imagem = url
            )
        }
    }

}