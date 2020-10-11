package com.ugb.calculadora;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar_productos extends AppCompatActivity {

    String resp, accion, id, rev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_productos);

        try {
            FloatingActionButton btnMostrarProducto = findViewById(R.id.btnMostrarProducto);
            btnMostrarProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarAmigos();
                }
            });
            Button btnGuardarProducto = findViewById(R.id.btnGuardarProducto);
            btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarAmigo();
                }
            });
            mostrarDatosProducto();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al agregar amigos: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    void mostrarDatosProducto(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                JSONObject dataAmigo = new JSONObject(recibirParametros.getString("dataAmigo")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtCodigoProducto);
                tempVal.setText(dataAmigo.getString("codigo"));

                tempVal = (TextView)findViewById(R.id.txtNombreProducto);
                tempVal.setText(dataAmigo.getString("nombre"));

                tempVal = (TextView)findViewById(R.id.txtMarca);
                tempVal.setText(dataAmigo.getString("marca"));

                tempVal = (TextView)findViewById(R.id.txtPrecio);
                tempVal.setText(dataAmigo.getString("precio"));


                id = dataAmigo.getString("_id");
                rev = dataAmigo.getString("_rev");
            }
        }catch (Exception ex){
            ///
        }
    }
    private void mostrarAmigos(){
        Intent mostrarAmigos = new Intent(agregar_productos.this, MainActivity.class);
        startActivity(mostrarAmigos);
    }
    private void guardarAmigo(){
        TextView tempVal = findViewById(R.id.txtCodigoProducto);
        String codigo = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtNombreProducto);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtMarca);
        String marca = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtPrecio);
        String precio = tempVal.getText().toString();



        try {
            JSONObject datosAmigo = new JSONObject();
            if (accion.equals("modificar")){
                datosAmigo.put("_id",id);
                datosAmigo.put("_rev",rev);
            }
            datosAmigo.put("codigo", codigo);
            datosAmigo.put("nombre", nombre);
            datosAmigo.put("marca", marca);
            datosAmigo.put("precio", precio);


            enviarDatosProducto objGuardarProducto = new enviarDatosProducto();
            objGuardarProducto.execute(datosAmigo.toString());
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private class enviarDatosProducto extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL("http://192.168.1.7:5984/db_agenda/");
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonDatos);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();

                String inputLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=reader.readLine())!= null){
                    stringBuffer.append(inputLine+"\n");
                }
                if(stringBuffer.length()==0){
                    return null;
                }
                jsonResponse = stringBuffer.toString();
                return jsonResponse;
            }catch (Exception ex){
                //
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("ok")){
                    Toast.makeText(getApplicationContext(), "Datos de producto guardado con exito", Toast.LENGTH_SHORT).show();
                    mostrarAmigos();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al intentar guardar producto", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al guardar producto: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }



}
