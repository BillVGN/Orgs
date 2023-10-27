package com.adrywill.orgs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.adrywill.orgs.model.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun salva(usuario: Usuario)
}