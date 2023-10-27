package com.adrywill.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adrywill.orgs.database.AppDatabase
import com.adrywill.orgs.databinding.ActivityFormularioCadastroUsuarioBinding
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
            val novoUsuario = criaUsuario()
            Log.i(TAG, "onCreate: $novoUsuario")
            lifecycleScope.launch {
                try {
                    usuarioDao.salva(novoUsuario)
                    finish()
                } catch (e: Exception) {
                    Log.e(TAG, "configuraBotaoCadastrar: ", e)
                    Toast.makeText(
                        this@FormularioCadastroUsuarioActivity,
                        "Falha ao cadastrar usuário",
                        Toast.LENGTH_LONG
                    ).show()
                }
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