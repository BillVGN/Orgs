package com.adrywill.orgs.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.adrywill.orgs.databinding.FormularioImagemBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem

class FormularioImagemDialog(private val context: Context) {

    private var url: String? = null

    fun mostra() {
        val binding = FormularioImagemBinding.inflate(LayoutInflater.from(context))
        binding.formularioImagemBotaoCarregar.setOnClickListener {
            url = binding.formularioImagemUrl.text.toString()
            binding.formularioImagemImageview.tentaCarregarImagem(url)
        }
        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton("Carregar") { _, _ ->
//                Precisa poder utilizar o binding de layout da activity
//                layoutFormularioProduto.activityFormularioProdutoImagem.tentaCarregarImagem(url)
            }
            .setNegativeButton("Cancelar") { _, _ -> }
            .show()
    }

}