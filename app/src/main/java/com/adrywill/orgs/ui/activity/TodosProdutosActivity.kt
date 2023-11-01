package com.adrywill.orgs.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityTodosProdutosBinding
import com.adrywill.orgs.extensions.vaiPara
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.recyclerview.adapter.CabecalhoAdapter
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TodosProdutosActivity : UsuarioBaseActivity() {
    private val layout by lazy {
        ActivityTodosProdutosBinding.inflate(layoutInflater)
    }

    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)
        val recyclerView = layout.todosProdutosRecyclerview
        lifecycleScope.launch {
            produtoDao.buscaTodos()
                .map { produtos ->
                    produtos
                        .sortedBy {
                            it.usuarioId
                        }.groupBy {
                            it.usuarioId
                        }.map { produtosUsuario ->
                            criaAdapterDeProdutosComCabecalho(produtosUsuario)
                        }.flatten()
                }
                .collect { adapter ->
                    recyclerView.adapter = ConcatAdapter(adapter)
                }
        }
    }

    private fun criaAdapterDeProdutosComCabecalho(produtosUsuario: Map.Entry<String?, List<Produto>>) =
        listOf(
            CabecalhoAdapter(this, listOf(produtosUsuario.key)),
            ListaProdutosAdapter(
                this,
                produtosUsuario.value,
                quandoClicaNoItemListener = { produtoClicado ->
                    vaiPara(DetalhesProduto::class.java) {
                        putExtra(CHAVE_PRODUTO_ID, produtoClicado.id)
                    }
                }
            )
        )

}
