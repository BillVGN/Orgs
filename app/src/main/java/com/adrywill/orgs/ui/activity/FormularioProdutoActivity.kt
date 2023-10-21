package com.adrywill.orgs.ui.activity

import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Images
import androidx.appcompat.app.AlertDialog
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.adrywill.orgs.R
import com.adrywill.orgs.dao.ProdutosDao
import com.adrywill.orgs.databinding.ActivityFormularioProdutoBinding
import com.adrywill.orgs.databinding.FormularioImagemBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import com.adrywill.orgs.ui.dialog.FormularioImagemDialog
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    var url: String? = null

    private val layoutFormularioProduto by lazy {
        ActivityFormularioProdutoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuraBotaoSalvar()
        configuraClickImagem()
        setContentView(layoutFormularioProduto.root)
    }

    private fun configuraClickImagem() {
        layoutFormularioProduto.activityFormularioProdutoImagem.setOnClickListener {
            FormularioImagemDialog(this).mostra()
        }
    }

    private fun configuraBotaoSalvar() {
        val dao = ProdutosDao()
        layoutFormularioProduto.activityProdutoItemBotaoSalvar.setOnClickListener {
            dao.adiciona(criaProduto())
            finish()
        }

    }

    private fun criaProduto(): Produto {
        val valorEmTexto = layoutFormularioProduto.activityFormularioProdutoValor.text.toString()
        return Produto(
            layoutFormularioProduto.activityFormularioProdutoNome.text.toString(),
            layoutFormularioProduto.activityFormularioProdutoDescricao.text.toString(),
            if (valorEmTexto.isBlank()) BigDecimal.ZERO else BigDecimal(valorEmTexto),
            url
        )
    }

}