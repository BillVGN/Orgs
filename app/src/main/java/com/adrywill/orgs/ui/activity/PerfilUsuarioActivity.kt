package com.adrywill.orgs.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.R
import com.adrywill.orgs.databinding.ActivityPerfilUsuarioBinding
import com.adrywill.orgs.extensions.toast
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class PerfilUsuarioActivity : UsuarioBaseActivity() {

    private val layout by lazy {
        ActivityPerfilUsuarioBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)
        configuraBotaoSairDoApp()
        vinculaViews()
    }

    private fun vinculaViews() {
        lifecycleScope.launch {
            usuario.filterNotNull().collect {usuarioLogado ->
                layout.perfilUsuarioId.text = usuarioLogado.id
                layout.perfilUsuarioNome.text = usuarioLogado.nome
            }
        }
    }

    private fun configuraBotaoSairDoApp() {
        layout.perfilUsuarioSairDoApp.setOnClickListener {
            lifecycleScope.launch {
                deslogaUsuario()
            }
        }
    }
}