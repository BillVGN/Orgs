package com.adrywill.orgs.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.preferences.dataStore
import com.adrywill.orgs.preferences.usuarioLogadoPreferences
import com.adrywill.orgs.ui.dialog.FormularioImagemDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal

private const val TAG = "FormularioProduto"

class FormularioProdutoActivity : UsuarioBaseActivity() {

    private var idProduto = 0L

    private var produto: Produto? = null

    private var url: String? = null

    private val layoutFormularioProduto by lazy {
        ActivityFormularioProdutoBinding.inflate(layoutInflater)
    }

    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    private val usuarioDao by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuraBotaoSalvar()
        configuraClickImagem()
        setContentView(layoutFormularioProduto.root)
        if (intent.hasExtra(CHAVE_PRODUTO_ID)) {
            idProduto = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
            lifecycleScope.launch {
                produtoDao.buscaProduto(idProduto).collect {
                    it?.let {
                        carregaProduto(it)
                    }
                }
                title = "Alterar Produto"
            }
        }
        lifecycleScope.launch {
            usuario.filterNotNull().collect {
                Log.i(TAG, "onCreate: $it")
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
            lifecycleScope.launch {
                usuario.value?.let {usuario ->
                    val produto = criaProduto(usuario.id)
                    produtoDao.salva(produto)
                    finish()
                }
            }
        }

    }

    private fun criaProduto(usuarioId: String): Produto {
        with(layoutFormularioProduto) {
            val valorEmTexto = activityFormularioProdutoValor.text.toString()
            produto = Produto(
                id = idProduto,
                nome = activityFormularioProdutoNome.text.toString(),
                descricao = activityFormularioProdutoDescricao.text.toString(),
                valor = if (valorEmTexto.isBlank()) BigDecimal.ZERO else BigDecimal(valorEmTexto),
                imagem = url,
                usuarioId = usuarioId
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