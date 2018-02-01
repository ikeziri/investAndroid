package com.example.ikezi.test2;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
    private int quantidadeRequisicao = 0;
    private int quantidadeResolvida = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        adapter = new ListAdapter(this);
        list.setAdapter(adapter);

        new FetchData().execute(Links.ListarAtivos.getValor());

//        Download_data download_data = new Download_data((Download_data.download_complete) this);
//        download_data.download_data_from_link("http://invest-182620.appspot.com/rest/investimentoResource/listarAcoesConsolidada");

    }

    public void grafico(){
        pieChart = (PieChart) findViewById(R.id.chart1);
        entries = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Acao acao: acoes){
//            Log.i("graficoAcao",acao.toString());
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

                Acao acao=new Acao(obj.getString("nome") , obj.getInt("quantidade"), new BigDecimal(obj.getString("valor")) );

                quantidadeRequisicao++;
                new FetchData().execute(Links.ConsultarAtivo.getValor(),acao.getNome());

                acoes.add(acao);

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private class FetchData extends AsyncTask<String, Void, String> {

        private String acao = new String();
        @Override
        protected String doInBackground(String... params) {
            acao = params[0];
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = null;


                if(Links.ListarAtivos.getValor().equals(acao)){
                    url = new URL(acao);
                }else if (Links.ConsultarAtivo.getValor().equals(acao)){
                    url = new URL(acao+params[1]);
                }




                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
//                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.i("json", s);
            if(Links.ListarAtivos.getValor().equals(acao)){
                get_data(s);
            }else if (Links.ConsultarAtivo.getValor().equals(acao)){
                atualizaAtivo(s);
            }

        }
    }

    private void atualizaAtivo(String jsonData) {
        quantidadeResolvida++;

        JSONObject obj = null;
        BigDecimal valorDiferenca = BigDecimal.ZERO, valorAtual = BigDecimal.ZERO;
        String nomeAcao = "";
        try {
            obj=new JSONObject(jsonData);
            valorDiferenca = new BigDecimal(obj.getString("c"));
            valorAtual = new BigDecimal(obj.getDouble("l"));
            nomeAcao = obj.getString("t");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("acao",nomeAcao + ": " + valorAtual.toString() + " - " + valorDiferenca.toString());
        for (Iterator<Acao> iterator = acoes.iterator(); iterator.hasNext();) {
            Acao acao = (Acao) iterator.next();
            if(nomeAcao.equals(acao.getNome())){
                Acao temp = new Acao(nomeAcao , acao.getQuantidade(), acao.getValor(), valorAtual , valorAtual.subtract(valorDiferenca) );
                acao.setValorAbertura(temp.getValorAbertura());
                acao.setValorAtual(temp.getValorAtual());
                acao.setPercentualDia(temp.getPercentualDia());
                acao.setPercentualTotal(temp.getPercentualTotal());
//                Log.i("acaoPos", acao.toString());
            }
        }

        if(quantidadeRequisicao == quantidadeResolvida){
            grafico();
            adapter.notifyDataSetChanged();
        }
    }

    private static enum Links {
        ListarAtivos("http://invest-182620.appspot.com/rest/investimentoResource/listarAcoesConsolidada"),
        ConsultarAtivo("https://finance.google.com/finance?output=json&q=BVMF:");

        private final String valor;
        Links(String valorOpcao){
            valor = valorOpcao;
        }
        public String getValor(){
            return valor;
        }
    }
}