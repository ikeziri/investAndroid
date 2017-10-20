package com.example.ikezi.test2;

/**
 * Created by ikezi on 19/10/2017.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigDecimal;

public class ListAdapter extends BaseAdapter {

    MainActivity main;

    ListAdapter(MainActivity main)
    {
        this.main = main;
    }

    @Override
    public int getCount() {
        return  main.acoes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolderItem {
        TextView nome;
        TextView quantidade;
        TextView valor;
        TextView valorAbertura;
        TextView valorAtual;
        TextView percentualDia;
        TextView percentualTotal;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolderItem holder = new ViewHolderItem();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell, null);

            holder.nome = (TextView) convertView.findViewById(R.id.nome);
            holder.quantidade = (TextView) convertView.findViewById(R.id.quantidade);
            holder.valor = (TextView) convertView.findViewById(R.id.valor);
//            holder.valorAbertura = (TextView) convertView.findViewById(R.id.valorAbertura);
            holder.valorAtual = (TextView) convertView.findViewById(R.id.valorAtual);
            holder.percentualDia = (TextView) convertView.findViewById(R.id.percentualDia);
            holder.percentualTotal = (TextView) convertView.findViewById(R.id.percentualTotal);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolderItem) convertView.getTag();
        }


        holder.nome.setText(this.main.acoes.get(position).getNome());
        holder.quantidade.setText(this.main.acoes.get(position).getQuantidade().toString());
        holder.valor.setText(this.main.acoes.get(position).getValor().toString());
//        holder.valorAbertura.setText(this.main.acoes.get(position).getValorAbertura().toString());
        holder.valorAtual.setText(this.main.acoes.get(position).getValorAtual().toString());
        holder.percentualDia.setText(this.main.acoes.get(position).getPercentualDia().toString());
        holder.percentualTotal.setText(this.main.acoes.get(position).getPercentualTotal().toString());

        if(this.main.acoes.get(position).getPercentualDia().compareTo(BigDecimal.ZERO)>0){
            holder.percentualDia.setTextColor(ColorStateList.valueOf(Color.rgb(0 , 100 , 0)));
        }else{
            holder.percentualDia.setTextColor(ColorStateList.valueOf(Color.RED));
        }
        if(this.main.acoes.get(position).getPercentualTotal().compareTo(BigDecimal.ZERO)>0){
            holder.percentualTotal.setTextColor(ColorStateList.valueOf(Color.rgb(0 , 100 , 0)));
        }else{
            holder.percentualTotal.setTextColor(ColorStateList.valueOf(Color.RED));
        }
        return convertView;
    }

}
