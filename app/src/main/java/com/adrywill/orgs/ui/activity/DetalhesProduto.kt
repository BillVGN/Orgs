package com.adrywill.orgs.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.adrywill.orgs.R
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityDetalhesProdutoBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "DetalhesProduto"

class DetalhesProduto : AppCompatActivity() {

    private var idProduto = 0L

    private val layoutDetalhesProduto by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }

    private val produtoDao by lazy { AppDatabase.instancia(this).produtoDao() }

    private val scope = CoroutineScope(Main)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutDetalhesProduto.root)
        idProduto = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
        vinculaProduto(idProduto)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detalhes_produto_remover -> {
                scope.launch {
                    produtoDao.removeProdutoPorId(idProduto)
                }
                finish()
            }

            R.id.menu_detalhes_produto_editar -> {
                editaProduto()
            }

            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhes_produto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun editaProduto() {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
            .putExtra(CHAVE_PRODUTO_ID, idProduto)
        startActivity(intent)
    }

    override fun onResume() {
        vinculaProduto(idProduto)
        super.onResume()
    }

    private fun vinculaProduto(idProduto: Long) {
        with(layoutDetalhesProduto) {
            scope.launch {
                produtoDao.buscaProduto(idProduto).collect {
                    detalhesProdutoImagem.tentaCarregarImagem(it?.imagem)
                    detalhesProdutoNome.text = it?.nome
                    detalhesProdutoDescricao.text = it?.descricao
                    detalhesProdutoValor.text =
                        NumberFormat.getCurrencyInstance(Locale("pt", "br"))
                            .format(it?.valor)
                }
            }
        }
    }
}