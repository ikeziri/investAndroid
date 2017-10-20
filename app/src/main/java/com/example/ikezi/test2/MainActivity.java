package com.example.ikezi.test2;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends Activity implements Download_data.download_complete {

    public ListView list;
    public ArrayList<Acao> acoes = new ArrayList<Acao>();
    public ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        adapter = new ListAdapter(this);
        list.setAdapter(adapter);

        Download_data download_data = new Download_data((Download_data.download_complete) this);
        download_data.download_data_from_link("http://invest-182620.appspot.com/rest/investimentoResource/listarAcoesConsolidada");

    }


    public void get_data(String data)
    {

        try {
            JSONArray data_array=new JSONArray(data);

            for (int i = 0 ; i < data_array.length() ; i++)
            {
                JSONObject obj=new JSONObject(data_array.get(i).toString());

                BigDecimal valorAbertura, valorAtual;
                if(obj.getString("valorAbertura").equals("null")){
                    valorAbertura = (BigDecimal.ZERO);
                }else{
                    valorAbertura = new BigDecimal((obj.getString("valorAbertura")));
                }

                if(obj.getString("valorAtual").equals("null")){
                    valorAtual = (BigDecimal.ZERO);
                }else{
                    valorAtual =( new BigDecimal((obj.getDouble("valorAtual"))));
                }

                Acao acao=new Acao(obj.getString("nome") , obj.getInt("quantidade"), new BigDecimal(obj.getString("valor")), valorAtual , valorAbertura );

                acoes.add(acao);

            }

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}