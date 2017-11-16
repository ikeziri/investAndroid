package com.example.ikezi.test2;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.Color.YELLOW;

public class MainActivity extends Activity implements Download_data.download_complete {

    public ListView list;
    public ArrayList<Acao> acoes = new ArrayList<Acao>();
    public ListAdapter adapter;
    PieChart pieChart ;
    ArrayList<PieEntry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;


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

    public void grafico(){
        pieChart = (PieChart) findViewById(R.id.chart1);
        entries = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Acao acao: acoes){
            entries.add(new PieEntry(acao.getValorAtual().floatValue() * acao.getQuantidade(), acao.getNome()));
            total = total.add(acao.getValorAtual().multiply(new BigDecimal(acao.getQuantidade())));
        }

        pieDataSet = new PieDataSet(entries, "Ações");

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);


        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            if ( c != Color.rgb(179, 48, 80) && c != Color.rgb(64, 89, 128)) // rosa parecido com a primeira  do colorful e azul ruim de ler
                colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
//            if (c !=  Color.rgb(254, 247, 120)) // amarelo ruim de ler
                colors.add(c);


        colors.add(ColorTemplate.getHoloBlue());

        pieDataSet.setColors(colors);

        PieData data = new PieData(pieDataSet);
        data.setValueFormatter(new PercentFormatter(new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMAN))));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.rgb(0 , 100 , 0));

//        data.setValueTypeface(mTfLight);

        pieChart.setData(data);

        Description description = new Description();
        DecimalFormat df = new DecimalFormat("#,###.00");
        description.setText("Total: " +df.format(total));
        pieChart.setDescription(description);
        pieChart.animateY(3000);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
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
            grafico();
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}