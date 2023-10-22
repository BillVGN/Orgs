package com.adrywill.orgs.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.adrywill.orgs.databinding.FormularioImagemBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem

class FormularioImagemDialog(private val context: Context) {

    private var url: String = ""

    fun mostra(
        urlPadrao: String? = null,
        quandoImagemCarregada: (imagem: String) -> Unit
    ) {
        FormularioImagemBinding.inflate(LayoutInflater.from(context)).apply {
            urlPadrao?.let {
                formularioImagemImageview.tentaCarregarImagem(it)
                formularioImagemUrl.setText(it)
            }
            formularioImagemBotaoCarregar.setOnClickListener {
                url = formularioImagemUrl.text.toString()
                formularioImagemImageview.tentaCarregarImagem(url)
            }
            AlertDialog.Builder(context)
                .setView(root)
                .setPositiveButton("Carregar") { _, _ ->
                    quandoImagemCarregada(url)
                }
                .setNegativeButton("Cancelar") { _, _ -> }
                .show()
        }

    }

}