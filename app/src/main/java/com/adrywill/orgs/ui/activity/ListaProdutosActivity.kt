package com.adrywill.orgs.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.room.Room
import com.adrywill.orgs.R
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.database.dao.ProdutoDao
import com.adrywill.orgs.databinding.ActivityListaProdutosBinding
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter

private const val TAG = "ListaProdutosActivity"

class ListaProdutosActivity : AppCompatActivity() {

    private val adapter = ListaProdutosAdapter(context = this)
    private val layout by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(this.layout.root)
        configuraRecyclerView()
        configuraFAB()
    }

    override fun onResume() {
        super.onResume()
        val produtoDao = AppDatabase.instancia(this).produtoDao()
        atualizaLista(produtoDao)
    }

    private fun configuraFAB() {
        layout.activityListaProdutosBotaoNovoProduto.setOnClickListener {
            vaiParaFormularioProduto()
        }
    }

    private fun atualizaLista(produtoDao: ProdutoDao) {
        adapter.atualiza(produtoDao.buscaTodos())
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
                    return@setOnMenuItemClickListener when(it.itemId) {
                        R.id.menu_detalhes_produto_editar -> {
                            vaiParaFormularioProduto(produto.id)
                            true
                        }
                        R.id.menu_detalhes_produto_remover -> {
                            val produtoDao = AppDatabase.instancia(this@ListaProdutosActivity).produtoDao()
                            produtoDao.remove(produto)
                            atualizaLista(produtoDao)
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