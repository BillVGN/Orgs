package com.adrywill.orgs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrywill.orgs.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM Produto")
    fun buscaTodos(): Flow<List<Produto>>

    @Query("SELECT * FROM Produto WHERE usuarioId = :usuarioId")
    fun buscaTodosDoUsuario(usuarioId: String) : Flow<List<Produto>>

    @Query(
        "SELECT * FROM Produto WHERE usuarioId = :usuarioId ORDER BY " +
                "CASE WHEN :crescente = 1 THEN nome END ASC, " +
                "CASE WHEN :crescente = 0 THEN nome END DESC"
    )
    fun buscaOrdenadaPorNome(crescente: Boolean, usuarioId: String): Flow<List<Produto>>

    @Query(
        "SELECT * FROM Produto WHERE usuarioId = :usuarioId ORDER BY " +
                "CASE WHEN :crescente = 1 THEN descricao END ASC, " +
                "CASE WHEN :crescente = 0 THEN descricao END DESC"
    )
    fun buscaOrdenadaPorDescricao(crescente: Boolean, usuarioId: String): Flow<List<Produto>>

    @Query(
        "SELECT * FROM Produto WHERE usuarioId = :usuarioId ORDER BY " +
                "CASE WHEN :crescente = 1 THEN valor END ASC, " +
                "CASE WHEN :crescente = 0 THEN valor END DESC"
    )
    fun buscaOrdenadaPorValor(crescente: Boolean, usuarioId: String): Flow<List<Produto>>

    @Query("SELECT * FROM Produto WHERE id = :idProduto")
    fun buscaProduto(idProduto: Long): Flow<Produto?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salva(vararg produto: Produto)

    @Delete
    suspend fun remove(produto: Produto)

    @Query("DELETE FROM Produto WHERE id = :id")
    suspend fun removeProdutoPorId(id: Long)
}
