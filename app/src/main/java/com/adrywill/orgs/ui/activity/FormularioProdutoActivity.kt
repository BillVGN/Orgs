package com.adrywill.orgs.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.adrywill.orgs.R
import com.adrywill.orgs.dao.ProdutosDao
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.model.Produtos
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    private val layoutFormularioProduto by lazy {
        ActivityFormularioProdutoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuraBotaoSalvar()
        setContentView(layoutFormularioProduto.root)
    }

    private fun configuraBotaoSalvar() {
        val dao = ProdutosDao()
        layoutFormularioProduto.activityProdutoItemBotaoSalvar.setOnClickListener {
            dao.adiciona(criaProduto())
            finish()
        }

    }

    private fun criaProduto(): Produtos {
        val valorEmTexto = layoutFormularioProduto.activityFormularioProdutoValor.text.toString()
        return Produtos(
            layoutFormularioProduto.activityFormularioProdutoNome.text.toString(),
            layoutFormularioProduto.activityFormularioProdutoDescricao.text.toString(),
            if (valorEmTexto.isBlank()) BigDecimal.ZERO else BigDecimal(valorEmTexto)
        )
    }

}