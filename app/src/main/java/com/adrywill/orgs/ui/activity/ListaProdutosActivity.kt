package com.adrywill.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.R
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityListaProdutosBinding
import com.adrywill.orgs.extensions.vaiPara
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.preferences.dataStore
import com.adrywill.orgs.preferences.usuarioLogadoPreferences
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val TAG = "ListaProdutosActivity"

class ListaProdutosActivity : AppCompatActivity() {

    private val adapter = ListaProdutosAdapter(context = this)
    private val layout by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }
    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }
    private val usuarioDao by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    private var ordenacaoLista: Int = R.id.menu_lista_produtos_nenhuma

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(this.layout.root)
        configuraRecyclerView()
        configuraFAB()
        lifecycleScope.launch {
            launch {
                verificaUsuarioLogado()
            }
        }
        super.onCreate(savedInstanceState)
    }

    private suspend fun verificaUsuarioLogado() {
        dataStore.data.collect { preferences ->
            preferences[usuarioLogadoPreferences]?.let { usuarioId ->
                buscaUsuario(usuarioId)
            } ?: vaiParaLogin()
        }
    }

    private fun buscaUsuario(usuarioId: String) {
        lifecycleScope.launch {
            usuarioDao.buscaPorId(usuarioId).firstOrNull()?.let {
                launch {
                    buscaProdutosUsuario()
                }
            }
        }
    }

    private fun vaiParaLogin() {
        vaiPara(LoginActivity::class.java)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lista_produtos_sair_do_app -> {
                lifecycleScope.launch {
                    deslogaUsuario()
                }
            }

            else -> {
                if (item.itemId != R.id.menu_lista_produtos_ordenacao) {
                    item.isChecked = true
                    lifecycleScope.launch {
                        buscaListaOrdenada(item.itemId).collect {
                            adapter.atualiza(it)
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun deslogaUsuario() {
        dataStore.edit { preferences ->
            preferences.remove(usuarioLogadoPreferences)
        }
    }

    private fun buscaListaOrdenada(idMenuItem: Int): Flow<List<Produto>> {
        ordenacaoLista = idMenuItem
        val flowProdutos = with(produtoDao) {
            when (idMenuItem) {
                R.id.menu_lista_produtos_nome_asc -> {
                    buscaOrdenadaPorNome(true)
                }

                R.id.menu_lista_produtos_nome_desc -> {
                    buscaOrdenadaPorNome(false)
                }

                R.id.menu_lista_produtos_descricao_asc -> {
                    buscaOrdenadaPorDescricao(true)
                }

                R.id.menu_lista_produtos_descricao_desc -> {
                    buscaOrdenadaPorDescricao(false)
                }

                R.id.menu_lista_produtos_valor_asc -> {
                    buscaOrdenadaPorValor(true)
                }

                R.id.menu_lista_produtos_valor_desc -> {
                    buscaOrdenadaPorValor(false)
                }

                else -> {
                    buscaTodos()
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
                                buscaProdutosUsuario()
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

    private suspend fun buscaProdutosUsuario() {
        buscaListaOrdenada(ordenacaoLista).collect {
            adapter.atualiza(it)
        }
    }

}