package com.adrywill.orgs.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.database.dao.ProdutoDao
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.model.Usuario
import com.adrywill.orgs.ui.dialog.FormularioImagemDialog
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.math.BigDecimal

private const val TAG = "FormularioProduto"

class FormularioProdutoActivity : UsuarioBaseActivity() {

    private var idProduto = 0L

    private var produto: Produto? = null

    private var url: String? = null

    private val campoUsuarioId by lazy {
        layoutFormularioProduto.formularioProdutoUsuarioId
    }

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
                produtoDao.buscaProduto(idProduto).collect { produto ->
                    produto?.let { produtoEncontrado ->
                        campoUsuarioId.visibility = if (produtoEncontrado.salvoSemUsuario()) {
                            configuraCampoUsuario()
                            VISIBLE
                        } else {
                            GONE
                        }
                        carregaProduto(produtoEncontrado)
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
    }

    private fun configuraCampoUsuario() {
        lifecycleScope.launch {
            usuarios().map { usuarios ->
                usuarios.map { it.id }
            }.collect { usuarios ->
                configuraAutoCompleteTextView(usuarios)
            }
        }
    }

    private fun configuraAutoCompleteTextView(usuarios: List<String>) {
        val adapter = ArrayAdapter(
            this@FormularioProdutoActivity,
            support_simple_spinner_dropdown_item,
            usuarios
        )
        campoUsuarioId.setAdapter(adapter)
        campoUsuarioId.setOnFocusChangeListener { _, focado ->
            if(!focado) {
                usuarioExistenteValido(usuarios)
            }
        }
    }

    private fun usuarioExistenteValido(usuarios: List<String>): Boolean {
        val usuarioId = campoUsuarioId.text.toString()
        if(!usuarios.contains(usuarioId)) {
            campoUsuarioId.error = "Usuário Inexistente!"
            return false
        }
        return true
    }

    private fun configuraClickImagem() {
        layoutFormularioProduto.formularioProdutoImagem.setOnClickListener {
            FormularioImagemDialog(this)
                .mostra(url) { imagem ->
                    url = imagem
                    layoutFormularioProduto.formularioProdutoImagem.tentaCarregarImagem(url)
                }
        }
    }

    private fun configuraBotaoSalvar() {
        val produtoDao = AppDatabase.instancia(this).produtoDao()
        layoutFormularioProduto.formularioProdutoBotaoSalvar.setOnClickListener {
            lifecycleScope.launch {
                tentaSalvarProduto()
            }
        }

    }

    private suspend fun tentaSalvarProduto() {
        usuario.value?.let { usuario ->
            try {
                val usuarioId = defineUsuarioId(usuario)
                val produto = criaProduto(usuarioId)
                produtoDao.salva(produto)
                finish()
            } catch (e: RuntimeException) {
                Log.e(TAG, "configuraBotaoSalvar: ", e)
            }
        }
    }

    private suspend fun defineUsuarioId(usuario: Usuario): String = produtoDao
        .buscaProduto(idProduto)
        .first()?.let {produtoEncontrado ->
            if (produtoEncontrado.usuarioId.isNullOrBlank()) {
                val usuarios = usuarios()
                    .map { usuariosEncontrados ->
                        usuariosEncontrados.map { it.id }
                    }.first()
                if (usuarioExistenteValido(usuarios)) {
                    return campoUsuarioId.text.toString()
                } else {
                    throw RuntimeException("Tentou salvar produto com usuário inexistente")
                }
            }
            null
        } ?: usuario.id

    private fun criaProduto(usuarioId: String): Produto {
        with(layoutFormularioProduto) {
            val valorEmTexto = formularioProdutoValor.text.toString()
            produto = Produto(
                id = idProduto,
                nome = formularioProdutoNome.text.toString(),
                descricao = formularioProdutoDescricao.text.toString(),
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
                formularioProdutoNome.setText(nome)
                formularioProdutoDescricao.setText(descricao)
                formularioProdutoValor.setText(valor.toPlainString())
                url = imagem
                formularioProdutoImagem.tentaCarregarImagem(url)
            }
        }
    }

}