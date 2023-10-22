package com.adrywill.orgs.extensions

import android.widget.ImageView
import coil.load
import com.adrywill.orgs.R

fun ImageView.tentaCarregarImagem(url: String? = null) {
    load(url)
    {
        error(R.drawable.erro)
        fallback(R.drawable.imagem_padrao)
        placeholder(R.drawable.carregando_outline_downloading_24)
    }
}