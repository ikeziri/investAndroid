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

        list = findViewById(R.id.list);
        adapter = new ListAdapter(this);
        list.setAdapter(adapter);

        new FetchData().execute(Links.ListarAtivos.getValor());

//        Download_data download_data = new Download_data((Download_data.download_complete) this);
//        download_data.download_data_from_link("http://invest-182620.appspot.com/rest/investimentoResource/listarAcoesConsolidada");

    }

    public void grafico(){
        pieChart = findViewById(R.id.chart1);
        entries = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Acao acao: acoes){
//            Log.i("graficoAcao",acao.toString());
            if(acao.getValorAtual() == null){
                acao.setValorAtual(BigDecimal.ZERO);
            }
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

                Acao acao=new Acao(obj.getString("nome") , obj.getString("data") , obj.getInt("quantidade"), new BigDecimal(obj.getString("valor")) , new BigDecimal(obj.getString("custo")) );
                acao.setValor(
                        acao.getValor().multiply(new BigDecimal(acao.getQuantidade())).add(acao.getCusto())
                                .divide(new BigDecimal(acao.getQuantidade()) , 2, BigDecimal.ROUND_HALF_EVEN));
                Log.i("acao", acao.toString());
                adicionaAcao(acao);
                //break;

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void adicionaAcao(Acao novaAcao) {
        boolean flag = false;
        for (Iterator<Acao> iterator = acoes.iterator(); iterator.hasNext();) {
            Acao acao = iterator.next();
            if(novaAcao.getNome().equals(acao.getNome())){
                flag = true;
                if (acao.getQuantidade() + novaAcao.getQuantidade() == 0){
                    acoes.remove(acao);
                }else{
//                    somente atualiza valor PM se foi compra
                    if (novaAcao.getQuantidade() > 0){
                        final BigDecimal valorAcao = acao.getValor().multiply(new BigDecimal(acao.getQuantidade()));
                        final BigDecimal valorAcaoNova = novaAcao.getValor().multiply(new BigDecimal(novaAcao.getQuantidade())).add(novaAcao.getCusto());
                        final Integer quantidadeNova = acao.getQuantidade() + novaAcao.getQuantidade();
                        acao.setValor(
                                valorAcao
                                        .add(valorAcaoNova)
                                        .divide(new BigDecimal(quantidadeNova) , 2, BigDecimal.ROUND_HALF_EVEN));
                    }
                    acao.setQuantidade(acao.getQuantidade() + novaAcao.getQuantidade());
                    acao.setCusto(acao.getCusto().add(novaAcao.getCusto()));
                }
            }
        }

        if (!flag) {
            quantidadeRequisicao++;
 //           new FetchData().execute(Links.ConsultarAtivoUol.getValor(),novaAcao.getNome());/**/
            // new FetchData().execute(Links.ConsultarAtivo.getValor(),novaAcao.getNome());
//            new FetchData().execute(Links.ConsultarAtivoHgBrasil.getValor(),novaAcao.getNome());\
            new FetchData().execute(Links.ConsultarAtivoYahoo.getValor(),novaAcao.getNome());

            acoes.add(novaAcao);

        }

    }

    private class FetchData extends AsyncTask<String, Void, String> {

        private String acao = "";
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

                String link = "";
                if(Links.ListarAtivos.getValor().equals(acao)){
                    link = (acao);
                }else if (Links.ConsultarAtivo.getValor().equals(acao)){
                    link = (acao+params[1]);
                }else if (Links.ConsultarAtivoUol.getValor().equals(acao)){
                    link = (acao+params[1]+".SA");
                }else if (Links.ConsultarAtivoHgBrasil.getValor().equals(acao)) {
                    link = (acao + params[1]);
                }else if (Links.ConsultarAtivoYahoo.getValor().equals(acao)){
                    link = (acao+params[1]+".SA&range=1d&interval=5m&indicators=close&includeTimestamps=false&includePrePost=false&corsDomain=finance.yahoo.com&.tsrc=finance");
                }

                Log.i("link", link);
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(link);



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
                    //Log.i("line-> ", line );

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
            if (s != null ) {
                Log.i("json", s);
                if (Links.ListarAtivos.getValor().equals(acao)) {
                    get_data(s);
                } else if (Links.ConsultarAtivo.getValor().equals(acao)) {
                    atualizaAtivo(s);
                } else if (Links.ConsultarAtivoUol.getValor().equals(acao)) {
                    atualizaAtivoUol(s);
                } else if (Links.ConsultarAtivoHgBrasil.getValor().equals(acao)) {
                    atualizaAtivoHgBrasil(s);
                } else if (Links.ConsultarAtivoYahoo.getValor().equals(acao)) {
                    atualizaAtivoYahoo(s);
                }
            }else{
                quantidadeResolvida++;
                if (quantidadeRequisicao == quantidadeResolvida) {
                    grafico();
                    adapter.notifyDataSetChanged();
                }
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
        atualizaGrafico(valorDiferenca, valorAtual, nomeAcao);
    }

    private void atualizaAtivoUol(String jsonData) {
        quantidadeResolvida++;

        JSONObject obj = null;
        BigDecimal valorDiferenca = BigDecimal.ZERO, valorAtual = BigDecimal.ZERO;
        String nomeAcao = "";
        try {
            obj=new JSONObject(jsonData);
            valorDiferenca = new BigDecimal(obj.getString("change").replace(',', '.'));
            valorAtual = new BigDecimal(obj.getString("price").replace(',', '.'));
            nomeAcao = obj.getString("code").substring(0 , 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("jsonData", jsonData);
//        Log.i("acao",nomeAcao + ": " + valorAtual.toString() + " - " + valorDiferenca.toString());
        atualizaGrafico(valorDiferenca, valorAtual, nomeAcao);
    }

    private void atualizaAtivoHgBrasil(String jsonData) {
        quantidadeResolvida++;

        JSONObject obj = null, json = null;
        BigDecimal valorDiferenca = BigDecimal.ZERO, percentual = BigDecimal.ZERO , valorAtual = BigDecimal.ZERO;
        String nomeAcao = "";
        try {
            json=new JSONObject(jsonData);

            nomeAcao = (json.getJSONObject("results")).names().getString(0);
            obj =(json.getJSONObject("results")).getJSONObject(nomeAcao);

            percentual = new BigDecimal(obj.getString("change_percent"));
            valorAtual = new BigDecimal(obj.getString("price"));
            valorDiferenca =  valorAtual.multiply(percentual.divide(new BigDecimal(100)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("jsonData", jsonData);
        Log.i("jsonData", obj.toString());
//        Log.i("acao",nomeAcao + ": " + valorAtual.toString() + " - " + valorDiferenca.toString());
        atualizaGrafico(valorDiferenca, valorAtual, nomeAcao);
    }


    private void atualizaAtivoYahoo(String jsonData) {
        quantidadeResolvida++;

        JSONObject obj = null, json = null;
        BigDecimal valorDiferenca = BigDecimal.ZERO, percentual = BigDecimal.ZERO , valorAtual = BigDecimal.ZERO;
        String nomeAcao = "";
        try {
            obj=(new JSONObject(jsonData)).getJSONObject("spark").getJSONArray("result").getJSONObject(0).getJSONArray("response").getJSONObject(0).getJSONObject("meta");
           // Log.i("obj -> ", obj.toString());
            nomeAcao = (obj.getString("symbol")).split("\\.")[0];
            valorAtual = new BigDecimal(obj.getString("regularMarketPrice"));
            valorDiferenca =  valorAtual.subtract(new BigDecimal(obj.getString("previousClose")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
       // Log.i("jsonData", jsonData);

//        Log.i("acao",nomeAcao + ": " + valorAtual.toString() + " - " + valorDiferenca.toString());
        atualizaGrafico(valorDiferenca, valorAtual, nomeAcao);
    }

    private void atualizaGrafico(BigDecimal valorDiferenca, BigDecimal valorAtual, String nomeAcao) {
        for (Iterator<Acao> iterator = acoes.iterator(); iterator.hasNext(); ) {
            Acao acao = iterator.next();
            if (nomeAcao.equals(acao.getNome())) {
                Acao temp = new Acao(nomeAcao, acao.getQuantidade(), acao.getValor(), valorAtual, valorAtual.subtract(valorDiferenca));
                acao.setValorAbertura(temp.getValorAbertura());
                acao.setValorAtual(temp.getValorAtual());
                acao.setPercentualDia(temp.getPercentualDia());
                acao.setPercentualTotal(temp.getPercentualTotal());
//                Log.i("acaoPos", acao.toString());
            }
        }

        if (quantidadeRequisicao == quantidadeResolvida) {
            grafico();
            adapter.notifyDataSetChanged();
        }
    }

    private enum Links {
//        ListarAtivos("http://invest-182620.appspot.com/rest/investimentoResource/listarAcoesConsolidada"),
        ListarAtivos("https://api.mlab.com/api/1/databases/invest/collections/acoes?apiKey=Wtax8CjOW6j5Bo5kBVTirXR4a4qxLDFh&s={\"nome\":%201,%20\"data\":%201}"),
        ConsultarAtivoUol("http://cotacoes.economia.uol.com.br/snapQuote.html?code="),
        ConsultarAtivo("https://finance.google.com/finance?output=json&q=BVMF:"),
        ConsultarAtivoHgBrasil("https://api.hgbrasil.com/finance/stock_price?key=9e75094e&symbol="),
        ConsultarAtivoYahoo("https://query1.finance.yahoo.com/v7/finance/spark?symbols=")
        ;

        private final String valor;
        Links(String valorOpcao){
            valor = valorOpcao;
        }
        public String getValor(){
            return valor;
        }
    }
}