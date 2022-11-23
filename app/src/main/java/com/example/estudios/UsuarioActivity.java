package com.example.estudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class UsuarioActivity extends AppCompatActivity {

     CheckBox jcbActivo;
     EditText jetIdentificacion, jetNombre, jetProfesion, jetEmpresa, jetSalario, jetExtras, jetGastos;
     ClsOpenHelper admin = new ClsOpenHelper(this, "banco.bd", null, 1);

     String identificacion, nombre, profesion, empresa, salario, extras, gastos;
     long respuesta;
     byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        getSupportActionBar().hide();

        jetIdentificacion = findViewById(R.id.etidentificacion);
        jetNombre = findViewById(R.id.etnombre);
        jetProfesion = findViewById(R.id.etprofesion);
        jetEmpresa = findViewById(R.id.etempresa);
        jetSalario = findViewById(R.id.etsalario);
        jetExtras = findViewById(R.id.etextras);
        jetGastos = findViewById(R.id.etgastos);

        jcbActivo = findViewById(R.id.cbactivo);

        sw = 0;
    }

    public void guardar(View view) {
        identificacion = jetIdentificacion.getText().toString();
        nombre = jetNombre.getText().toString();
        profesion = jetProfesion.getText().toString();
        empresa = jetEmpresa.getText().toString();
        salario = jetSalario.getText().toString();
        extras = jetExtras.getText().toString();
        gastos = jetGastos.getText().toString();

        if (identificacion.isEmpty() || nombre.isEmpty() || profesion.isEmpty() || empresa.isEmpty()
                || salario.isEmpty() || extras.isEmpty() || gastos.isEmpty()) {
            Toast.makeText(this, "Todos Los Campos Son Obligatorios", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {

            SQLiteDatabase fila = admin.getWritableDatabase();


            ContentValues registro = new ContentValues();
            registro.put("identificacion", identificacion);
            registro.put("nombre", nombre);
            registro.put("profesion", profesion);
            registro.put("empresa", empresa);
            registro.put("salario", Integer.parseInt(salario));
            registro.put("ingresoExtra", Integer.parseInt(extras));
            registro.put("gastos", Integer.parseInt(gastos));


            if (sw == 0) {

                respuesta = fila.insert("TblCliente", null, registro);
            } else {

                respuesta = fila.update("TblCliente", registro, "identificacion='" + identificacion + "'", null);
                sw = 0;
            }


            if (respuesta == 0) {

                Toast.makeText(this, "ERROR, El Usuario NO Se Guardo", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(this, "Guardando Regristro Exitosamente", Toast.LENGTH_LONG).show();
                limpiarCampos();
            }


            fila.close();
        }
    }

    public void buscar(View view) {
        identificacion = jetIdentificacion.getText().toString();

        if (identificacion.isEmpty()) {
            Toast.makeText(this, "La Identificación Es Requerida", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {
            SQLiteDatabase fila = admin.getReadableDatabase();
            Cursor dato = fila.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);

            if (dato.moveToNext()) {
                sw = 1;

                Toast.makeText(this, "Usuario Encontrado Existosamente En La Base De Datos", Toast.LENGTH_LONG).show();


                jetNombre.setText(dato.getString(1));
                jetProfesion.setText(dato.getString(2));
                jetEmpresa.setText(dato.getString(3));
                jetSalario.setText(dato.getString(4));
                jetExtras.setText(dato.getString(5));
                jetGastos.setText(dato.getString(6));


                String userActive = dato.getString(7);

                if (userActive.equals("si")) {
                    jcbActivo.setChecked(true);
                } else {
                    jcbActivo.setChecked(false);
                }
            } else {

                Toast.makeText(this, "El Cliente No Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                jetIdentificacion.requestFocus();
            }
        }
    }

    public void anular(View view) {
        if (sw == 0) {
            Toast.makeText(this, "Debe Primero Consultar El Registro", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {
            identificacion = jetIdentificacion.getText().toString();

            if (identificacion.isEmpty()) {
                Toast.makeText(this, "La Identificacion Es Obligatorios", Toast.LENGTH_LONG).show();
                jetIdentificacion.requestFocus();
            } else {

                SQLiteDatabase fila = admin.getWritableDatabase();


                ContentValues registro = new ContentValues();
                registro.put("activo", "no");




                respuesta = fila.update("TblCliente", registro, "identificacion='" + identificacion + "'", null);
                sw = 0;


                if (respuesta == 0) {
                    
                    Toast.makeText(this, "ERROR, El Usuario NO Se Anulo", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(this, "Anulando Regristro Exitosamente", Toast.LENGTH_LONG).show();
                    limpiarCampos();
                }


                fila.close();
            }
        }
    }

    public void regresar(View view) {
        Intent intentMenu = new Intent(this, MenuActivity.class);
        startActivity(intentMenu);
    }

    public void cancelar(View view) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        jetIdentificacion.setText("");
        jetNombre.setText("");
        jetProfesion.setText("");
        jetEmpresa.setText("");
        jetSalario.setText("");
        jetExtras.setText("");
        jetGastos.setText("");

        jcbActivo.setChecked(false);
        jetIdentificacion.requestFocus();
        sw = 0;
    }
}