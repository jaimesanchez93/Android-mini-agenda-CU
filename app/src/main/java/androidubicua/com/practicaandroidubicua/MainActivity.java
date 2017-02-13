package androidubicua.com.practicaandroidubicua;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends ListActivity {

    private EditText textoNumero;
    private Button botonAgregar;
    private ArrayList<String> numeros;
    private boolean primera_vez;
    private static final String CODE_VEZ="estadoVez";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         preferences=getSharedPreferences("DatosGuardar",this.MODE_PRIVATE);
        primera_vez=preferences.getBoolean("estadoVez", true);

        textoNumero= (EditText) findViewById(R.id.texto);
        botonAgregar= (Button) findViewById(R.id.botonAgregar);
        String[] datos= new String[]{"hola","adios"};

        if(primera_vez==true){
            numeros= new ArrayList<String>();
            ListAdapter lA= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,numeros);
            setListAdapter(lA);
        }
        else{
            InputStream inputStream= null;
            try {
                inputStream = openFileInput("listacontactos.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bReader= null;
            try {
                bReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            int cont=0;
            String linea;
            numeros= new ArrayList<String>();
            try {
                while((linea=bReader.readLine())!=null){

                    numeros.add(cont++,linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            ListAdapter lA= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,numeros);
            setListAdapter(lA);

        }

        //  numeros.add("Lista vacía");

       // ListView lista= (ListView) findViewById(R.id.lista);
 //    SimpleAdapter sA=new SimpleAdapter(this,android.R.layout.simple_list_item_1,R.id.lista,new String[]{"nombre"},)


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CODE_VEZ,true);
        preferences=getSharedPreferences("DatosGuardar",this.MODE_PRIVATE);
        preferences.edit().putBoolean("estadoVez",false).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickAgregarNumero(View view) {
         String num= textoNumero.getText()+"";
        if(num.equals("")){
            textoNumero.setError("El numero está vacío");
        }
       else if(num.length()==9){
            numeros.remove("Lista vacía");
            numeros.add(num);
            ListAdapter lA = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, numeros);
            setListAdapter(lA);
            Toast.makeText(this, "Se ha agregado un nuevo numero", Toast.LENGTH_SHORT).show();
            textoNumero.setText("");

        }
        else {
           textoNumero.setError("El número debe ser de 9 dígitos");
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String n= l.getItemAtPosition(position)+"";

        Intent i= new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+n));
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            FileOutputStream outputStream= openFileOutput("listacontactos.txt",this.MODE_PRIVATE);
            BufferedWriter bWriter= new BufferedWriter(new OutputStreamWriter(outputStream));
            int contador=0;
            while (contador<numeros.size()){
                try {
                    bWriter.write(numeros.get(contador));
                    bWriter.newLine();
                    contador++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bWriter.flush();
                bWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
