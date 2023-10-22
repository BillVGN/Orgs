package com.adrywill.orgs.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.adrywill.orgs.R
import com.adrywill.orgs.dao.ProdutosDao
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.databinding.ActivityListaProdutosBinding
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.math.BigDecimal

class ListaProdutosActivity : AppCompatActivity() {

    private val dao = ProdutosDao()
    private val adapter = ListaProdutosAdapter(context = this, produtos = dao.buscaTodos())
    private val layout by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(this.layout.root)
        configuraRecyclerView()
        configuraFAB()
        val produtoDao = AppDatabase.instancia(this).produtoDao()
        adapter.atualiza(produtoDao.buscaTodos())
    }

    override fun onResume() {
        super.onResume()
        val produtoDao = AppDatabase.instancia(this).produtoDao()
        adapter.atualiza(produtoDao.buscaTodos())
    }

    private fun configuraFAB() {
        layout.activityListaProdutosBotaoNovoProduto.setOnClickListener {
            vaiParaFormularioProduto()
        }
    }

    private fun vaiParaFormularioProduto() {
        Intent(this, FormularioProdutoActivity::class.java).also { startActivity(it) }
    }

    private fun configuraRecyclerView() {
        layout.activityListaProdutosRecyclerView.adapter = adapter
        adapter.quandoClicaNoItemListener = {
            Intent(this, DetalhesProduto::class.java).apply {
                putExtra("produto", it)
            }.also { startActivity(it) }
        }
    }

}