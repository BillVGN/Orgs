package com.adrywill.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityFormularioCadastroUsuarioBinding
import com.adrywill.orgs.extensions.toast
import com.adrywill.orgs.model.Usuario
import kotlinx.coroutines.launch

private const val TAG = "CadastroUsuario"

class FormularioCadastroUsuarioActivity : AppCompatActivity() {

    private val layout by lazy {
        ActivityFormularioCadastroUsuarioBinding.inflate(layoutInflater)
    }

    private val usuarioDao by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)
        configuraBotaoCadastrar()
    }

    private fun configuraBotaoCadastrar() {
        layout.activityFormularioCadastroBotaoCadastrar.setOnClickListener {
            val usuario = criaUsuario()
            cadastra(usuario)
        }
    }

    private fun cadastra(usuario: Usuario) {
        lifecycleScope.launch {
            try {
                usuarioDao.salva(usuario)
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "configuraBotaoCadastrar: ", e)
                toast("Falha ao cadastrar usuário")
            }
        }
    }

    private fun criaUsuario(): Usuario {
        with(layout) {
            val usuario = activityFormularioCadastroUsuario.text.toString()
            val nome = activityFormularioCadastroNome.text.toString()
            val senha = activityFormularioCadastroSenha.text.toString()
            return Usuario(usuario, nome, senha)
        }
    }
}