package com.adrywill.orgs.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.adrywill.orgs.R
import com.adrywill.orgs.dao.ProdutosDao
import com.adrywill.orgs.model.Produtos
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity(R.layout.activity_formulario_produto) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val botaoSalvar = findViewById<Button>(R.id.salvar)
        botaoSalvar.setOnClickListener {
            val campoNome = findViewById<EditText>(R.id.editNome)
            val nome = campoNome.text.toString()
            val campoDescricao = findViewById<EditText>(R.id.editDescricao)
            val descricao = campoDescricao.text.toString()
            val campoValor = findViewById<EditText>(R.id.editValor)
            val valorEmTexto = campoValor.text.toString()
            val valor = if (valorEmTexto.isBlank()) BigDecimal.ZERO else BigDecimal(valorEmTexto)
            val produtoNovo = Produtos(nome, descricao, valor)

            Log.d("FormularioProduto", "onCreate: $produtoNovo")
            val dao = ProdutosDao()
            dao.adiciona(produtoNovo)
            Log.d("FormularioProduto", "onCreate: ${dao.buscaTodos()}")
            finish()
        }
    }

}