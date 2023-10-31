package com.adrywill.orgs.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Entity()
@Parcelize
data class Produto(
    @PrimaryKey(true)
    val id: Long = 0L,
    val nome: String,
    val descricao: String,
    @ColumnInfo(typeAffinity = ColumnInfo.REAL)
    val valor: BigDecimal,
    val imagem: String? = null,
    val usuarioId: String? = null
) : Parcelable
