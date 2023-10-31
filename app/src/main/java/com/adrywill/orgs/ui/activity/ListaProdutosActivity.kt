package com.adrywill.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.R
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityListaProdutosBinding
import com.adrywill.orgs.extensions.vaiPara
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private const val TAG = "ListaProdutosActivity"

class ListaProdutosActivity : UsuarioBaseActivity() {

    private val adapter = ListaProdutosAdapter(context = this)
    private val layout by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }
    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    private var ordenacaoLista: Int = R.id.menu_lista_produtos_nenhuma

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(this.layout.root)
        configuraRecyclerView()
        configuraFAB()
        lifecycleScope.launch {
            launch {
                usuario.filterNotNull().collect {
                    buscaProdutosUsuario(it.id)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lista_produtos_perfil_usuario -> {
                vaiPara(PerfilUsuarioActivity::class.java)
            }

            else -> {
                if (item.itemId != R.id.menu_lista_produtos_ordenacao) {
                    item.isChecked = true
                    lifecycleScope.launch {
                        usuario.value?.let { usuario ->
                            buscaListaOrdenada(
                                item.itemId,
                                usuario.id
                            ).collect {
                                adapter.atualiza(it)
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buscaListaOrdenada(idMenuItem: Int, usuarioId: String): Flow<List<Produto>> {
        ordenacaoLista = idMenuItem
        val flowProdutos = with(produtoDao) {
            when (idMenuItem) {
                R.id.menu_lista_produtos_nome_asc -> {
                    buscaOrdenadaPorNome(true, usuarioId)
                }

                R.id.menu_lista_produtos_nome_desc -> {
                    buscaOrdenadaPorNome(false, usuarioId)
                }

                R.id.menu_lista_produtos_descricao_asc -> {
                    buscaOrdenadaPorDescricao(true, usuarioId)
                }

                R.id.menu_lista_produtos_descricao_desc -> {
                    buscaOrdenadaPorDescricao(false, usuarioId)
                }

                R.id.menu_lista_produtos_valor_asc -> {
                    buscaOrdenadaPorValor(true, usuarioId)
                }

                R.id.menu_lista_produtos_valor_desc -> {
                    buscaOrdenadaPorValor(false, usuarioId)
                }

                else -> {
                    buscaTodosDoUsuario(usuarioId)
                }
            }
        }
        return flowProdutos
    }

    private fun configuraFAB() {
        layout.activityListaProdutosBotaoNovoProduto.setOnClickListener {
            vaiPara(FormularioProdutoActivity::class.java) {
                putExtra(CHAVE_PRODUTO_ID, 0L)
            }
        }
    }

    private fun configuraRecyclerView() {
        layout.activityListaProdutosRecyclerView.adapter = adapter
        adapter.quandoClicaNoItemListener = { produto ->
            vaiPara(DetalhesProduto::class.java) {
                putExtra(CHAVE_PRODUTO_ID, produto.id)
            }
        }
        adapter.quandoPressionaItemListener = { produto: Produto, view: View ->
            PopupMenu(this, view).apply {
                setOnMenuItemClickListener { menuItem ->
                    return@setOnMenuItemClickListener when (menuItem.itemId) {
                        R.id.menu_detalhes_produto_editar -> {
                            vaiPara(FormularioProdutoActivity::class.java) {
                                putExtra(CHAVE_PRODUTO_ID, produto.id)
                            }
                            true
                        }

                        R.id.menu_detalhes_produto_remover -> {
                            val produtoDao =
                                AppDatabase.instancia(this@ListaProdutosActivity).produtoDao()
                            lifecycleScope.launch {
                                produtoDao.remove(produto)
                                usuario.value?.let { usuario ->
                                    buscaProdutosUsuario(usuario.id)
                                }
                            }
                            true
                        }

                        else -> false
                    }
                }
                inflate(R.menu.menu_detalhes_produto)
                show()
            }
        }
    }

    private suspend fun buscaProdutosUsuario(usuarioId: String) {
        buscaListaOrdenada(ordenacaoLista, usuarioId).collect {
            adapter.atualiza(it)
        }
    }

}