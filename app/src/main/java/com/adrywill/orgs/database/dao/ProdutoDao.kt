package com.adrywill.orgs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adrywill.orgs.model.Produto

@Dao
abstract class ProdutoDao {

    @Query("SELECT * FROM Produto")
    abstract fun buscaTodos() : List<Produto>

    @Insert
    abstract fun salva(vararg produto: Produto)
}