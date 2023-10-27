package com.adrywill.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adrywill.orgs.databinding.ActivityLoginBinding
import com.adrywill.orgs.extensions.vaiPara

class LoginActivity : AppCompatActivity() {

    private val layout by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)
        configuraBotaoCadastrar()
        configuraBotaoEntrar()
    }

    private fun configuraBotaoEntrar() {
        layout.activityLoginBotaoEntrar.setOnClickListener {
            val usuario = layout.activityLoginUsuario.text.toString()
            val senha = layout.activityLoginSenha.text.toString()
            Log.i("LoginActivity", "onCreate: $usuario - $senha")
            vaiPara(ListaProdutosActivity::class.java)
        }
    }

    private fun configuraBotaoCadastrar() {
        layout.activityLoginBotaoCadastrar.setOnClickListener {
            vaiPara(FormularioCadastroUsuarioActivity::class.java)
        }
    }
}