package com.adrywill.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.adrywill.orgs.R
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityListaProdutosBinding
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
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
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        val scope = MainScope()
        scope.launch {
            delay(2000)
            atualizaListaOrdenada(ordenacaoLista)
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos_ordenacao, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        atualizaListaOrdenada(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun atualizaListaOrdenada(idMenuItem: Int) {
        with(produtoDao) {
            when (idMenuItem) {

                R.id.menu_lista_produtos_ordenacao -> {
                    // Não faz nada porque se trata do botão raiz que mostra os demais
                }

                R.id.menu_lista_produtos_ordenacao_nome_asc -> {
                    adapter.atualiza(buscaOrdenadaPorNome(true))
                }

                R.id.menu_lista_produtos_ordenacao_nome_desc -> {
                    adapter.atualiza(buscaOrdenadaPorNome(false))
                }

                R.id.menu_lista_produtos_ordenacao_descricao_asc -> {
                    adapter.atualiza(buscaOrdenadaPorDescricao(true))
                }

                R.id.menu_lista_produtos_ordenacao_descricao_desc -> {
                    adapter.atualiza(buscaOrdenadaPorDescricao(false))
                }

                R.id.menu_lista_produtos_ordenacao_valor_asc -> {
                    adapter.atualiza(buscaOrdenadaPorValor(true))
                }

                R.id.menu_lista_produtos_ordenacao_valor_desc -> {
                    adapter.atualiza(buscaOrdenadaPorValor(false))
                }

                R.id.menu_lista_produtos_ordenacao_nenhuma -> {
                    adapter.atualiza(buscaTodos())
                }

                else -> {
                    adapter.atualiza(buscaTodos())
                }
            }
        }
        ordenacaoLista = idMenuItem
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
                setOnMenuItemClickListener {
                    return@setOnMenuItemClickListener when (it.itemId) {
                        R.id.menu_detalhes_produto_editar -> {
                            vaiParaFormularioProduto(produto.id)
                            true
                        }

                        R.id.menu_detalhes_produto_remover -> {
                            val produtoDao =
                                AppDatabase.instancia(this@ListaProdutosActivity).produtoDao()
                            produtoDao.remove(produto)
                            atualizaListaOrdenada(ordenacaoLista)
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