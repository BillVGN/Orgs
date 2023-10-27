package com.adrywill.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.R
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityListaProdutosBinding
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.flow.Flow
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
    private var ordenacaoLista: Int = R.id.menu_lista_produtos_ordenacao_nenhuma

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(this.layout.root)
        configuraRecyclerView()
        configuraFAB()
        lifecycleScope.launch {
            buscaListaOrdenada(ordenacaoLista).collect{
                adapter.atualiza(it)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos_ordenacao, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        if (item.itemId != R.id.menu_lista_produtos_ordenacao) {
            lifecycleScope.launch {
                buscaListaOrdenada(item.itemId).collect{
                    adapter.atualiza(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun buscaListaOrdenada(idMenuItem: Int): Flow<List<Produto>> {
        ordenacaoLista = idMenuItem
        val flowProdutos = with(produtoDao) {
            when (idMenuItem) {
                R.id.menu_lista_produtos_ordenacao_nome_asc -> {
                    buscaOrdenadaPorNome(true)
                }

                R.id.menu_lista_produtos_ordenacao_nome_desc -> {
                    buscaOrdenadaPorNome(false)
                }

                R.id.menu_lista_produtos_ordenacao_descricao_asc -> {
                    buscaOrdenadaPorDescricao(true)
                }

                R.id.menu_lista_produtos_ordenacao_descricao_desc -> {
                    buscaOrdenadaPorDescricao(false)
                }

                R.id.menu_lista_produtos_ordenacao_valor_asc -> {
                    buscaOrdenadaPorValor(true)
                }

                R.id.menu_lista_produtos_ordenacao_valor_desc -> {
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
            vaiParaFormularioProduto()
        }
    }

    private fun vaiParaFormularioProduto(idProduto: Long = 0L) {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
        intent.putExtra(CHAVE_PRODUTO_ID, idProduto)
        startActivity(intent)
    }

    private fun vaiParaDetalhesProduto(produto: Produto) {
        val intent = Intent(this, DetalhesProduto::class.java)
        intent.putExtra(CHAVE_PRODUTO_ID, produto.id)
        startActivity(intent)
    }

    private fun configuraRecyclerView() {
        layout.activityListaProdutosRecyclerView.adapter = adapter
        adapter.quandoClicaNoItemListener = {
            vaiParaDetalhesProduto(produto = it)
        }
        adapter.quandoPressionaItemListener = { produto: Produto, view: View ->
            PopupMenu(this, view).apply {
                setOnMenuItemClickListener { menuItem ->
                    return@setOnMenuItemClickListener when (menuItem.itemId) {
                        R.id.menu_detalhes_produto_editar -> {
                            vaiParaFormularioProduto(produto.id)
                            true
                        }

                        R.id.menu_detalhes_produto_remover -> {
                            val produtoDao =
                                AppDatabase.instancia(this@ListaProdutosActivity).produtoDao()
                            lifecycleScope.launch {
                                produtoDao.remove(produto)
                                buscaListaOrdenada(ordenacaoLista).collect {
                                    adapter.atualiza(it)
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

}