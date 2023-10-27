package com.adrywill.orgs.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.dialog.FormularioImagemDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    private var idProduto = 0L

    private var produto: Produto? = null

    private var url: String? = null

    private val layoutFormularioProduto by lazy {
        ActivityFormularioProdutoBinding.inflate(layoutInflater)
    }

    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    private var scope = CoroutineScope(Main)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuraBotaoSalvar()
        configuraClickImagem()
        setContentView(layoutFormularioProduto.root)
        if (intent.hasExtra(CHAVE_PRODUTO_ID)) {
            idProduto = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
            scope.launch {
                produtoDao.buscaProduto(idProduto).collect {
                    it?.let {
                        carregaProduto(it)
                    }
                }
                title = "Alterar Produto"
            }
        }
        title = "Cadastrar Produto"
    }

    private fun configuraClickImagem() {
        layoutFormularioProduto.activityFormularioProdutoImagem.setOnClickListener {
            FormularioImagemDialog(this)
                .mostra(url) { imagem ->
                    url = imagem
                    layoutFormularioProduto.activityFormularioProdutoImagem.tentaCarregarImagem(url)
                }
        }
    }

    private fun configuraBotaoSalvar() {
        val produtoDao = AppDatabase.instancia(this).produtoDao()
        layoutFormularioProduto.activityProdutoItemBotaoSalvar.setOnClickListener {
            val produto = criaProduto()
            scope.launch {
                produtoDao.salva(produto)
            }
            finish()
        }

    }

    private fun criaProduto(): Produto {
        with(layoutFormularioProduto) {
            val valorEmTexto = activityFormularioProdutoValor.text.toString()
            produto = Produto(
                id = idProduto,
                nome = activityFormularioProdutoNome.text.toString(),
                descricao = activityFormularioProdutoDescricao.text.toString(),
                valor = if (valorEmTexto.isBlank()) BigDecimal.ZERO else BigDecimal(valorEmTexto),
                imagem = url
            )
            return produto as Produto
        }
    }

    private fun carregaProduto(produto: Produto) {
        title = "Alterar Produto"
        with(layoutFormularioProduto) {
            with(produto) {
                activityFormularioProdutoNome.setText(nome)
                activityFormularioProdutoDescricao.setText(descricao)
                activityFormularioProdutoValor.setText(valor.toPlainString())
                url = imagem
                activityFormularioProdutoImagem.tentaCarregarImagem(url)
            }
        }
    }

}