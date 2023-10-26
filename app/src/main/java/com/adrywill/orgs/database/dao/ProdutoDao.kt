package com.adrywill.orgs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrywill.orgs.model.Produto

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM Produto")
    fun buscaTodos(): List<Produto>

    @Query(
        "SELECT * FROM Produto ORDER BY " +
                "CASE WHEN :crescente = 1 THEN nome END ASC, " +
                "CASE WHEN :crescente = 0 THEN nome END DESC"
    )
    fun buscaOrdenadaPorNome(crescente: Boolean): List<Produto>

    @Query(
        "SELECT * FROM Produto ORDER BY " +
                "CASE WHEN :crescente = 1 THEN descricao END ASC, " +
                "CASE WHEN :crescente = 0 THEN descricao END DESC"
    )
    fun buscaOrdenadaPorDescricao(crescente: Boolean): List<Produto>

    @Query(
        "SELECT * FROM Produto ORDER BY " +
                "CASE WHEN :crescente = 1 THEN valor END ASC, " +
                "CASE WHEN :crescente = 0 THEN valor END DESC"
    )
    fun buscaOrdenadaPorValor(crescente: Boolean): List<Produto>

    @Query("SELECT * FROM Produto WHERE id = :idProduto")
    fun buscaProduto(idProduto: Long): Produto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg produto: Produto)

    @Delete
    fun remove(produto: Produto)

    @Query("DELETE FROM Produto WHERE id = :id")
    fun removeProdutoPorId(id: Long)
}
