package com.adrywill.orgs.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adrywill.orgs.databinding.ProdutoItemBinding
import com.adrywill.orgs.extensions.tentaCarregarImagem
import com.adrywill.orgs.model.Produto
import java.text.NumberFormat
import java.util.Locale

class ListaProdutosAdapter(
    private val context: Context,
    produtos: List<Produto> = emptyList(),
    var quandoClicaNoItemListener: (produto: Produto) -> Unit = {},
    var quandoPressionaItemListener: (produto: Produto, view: View) -> Unit = { produto: Produto, view: View -> }
) : RecyclerView.Adapter<ListaProdutosAdapter.ProdutoItemHolder>() {

    private val produtos = produtos.toMutableList()

    inner class ProdutoItemHolder(view: View, binding: ProdutoItemBinding) : RecyclerView.ViewHolder(view) {
        private var layoutBinding = binding

        private lateinit var produto: Produto

        init {
            itemView.setOnLongClickListener{
                if (::produto.isInitialized) {
                    quandoPressionaItemListener(produto, it)
                }
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener {
                if (::produto.isInitialized) {
                    quandoClicaNoItemListener(produto)
                }
            }
        }

        fun vincula(produto: Produto) {
            this.produto = produto
            layoutBinding.produtoItemNome.text = produto.nome
            layoutBinding.produtoItemDescricao.text = produto.descricao
            layoutBinding.produtoItemValor.text =
                NumberFormat.getCurrencyInstance(Locale("pt","br")).format(produto.valor)
            if (produto.imagem != null) {
                layoutBinding.produtoItemImagem.visibility = View.VISIBLE
                layoutBinding.produtoItemImagem.tentaCarregarImagem(produto.imagem)
            } else layoutBinding.produtoItemImagem.visibility = View.GONE
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

    fun atualiza(produtos: List<Produto>) {
        this.produtos.clear()
        this.produtos.addAll(produtos)
        notifyDataSetChanged()
    }

}
