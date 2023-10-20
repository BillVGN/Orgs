package com.adrywill.orgs.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adrywill.orgs.databinding.ProdutoItemBinding
import com.adrywill.orgs.model.Produtos
import java.text.NumberFormat
import java.util.Locale

class ListaProdutosAdapter(
    private val context: Context,
    produtos: List<Produtos>
) : RecyclerView.Adapter<ListaProdutosAdapter.ProdutoItemHolder>() {

    private val produtos = produtos.toMutableList()

    class ProdutoItemHolder(view: View, binding: ProdutoItemBinding) : RecyclerView.ViewHolder(view) {
        private var layoutBinding = binding
        fun vincula(produto: Produtos) {
            layoutBinding.produtoItemNome.text = produto.nome
            layoutBinding.produtoItemDescricao.text = produto.descricao
            layoutBinding.produtoItemValor.text =
                NumberFormat.getCurrencyInstance(Locale("pt","br")).format(produto.valor)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoItemHolder {
        val layoutProdutoItem = ProdutoItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProdutoItemHolder(layoutProdutoItem.root, layoutProdutoItem)
    }

    override fun getItemCount(): Int = produtos.size

    override fun onBindViewHolder(holder: ProdutoItemHolder, position: Int) {
        val produto = produtos[position]
        holder.vincula(produto)
    }

    fun atualiza(produtos: List<Produtos>) {
        this.produtos.clear()
        this.produtos.addAll(produtos)
        notifyDataSetChanged()
    }

}
