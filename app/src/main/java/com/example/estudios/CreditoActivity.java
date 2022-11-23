package com.example.estudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreditoActivity extends AppCompatActivity {
    EditText jetCodigoPrestamo, jetIdentificacion;
    TextView jtvNombre, jtvProfesion, jtvSalario, jtvIngresoExtra, jtvGastos, jtvValorPrestamo;

    ClsOpenHelper admin = new ClsOpenHelper(this, "banco.bd", null, 1);

    String identificacion, codigoPrestamo, valorPrestamo;
    long respuestaDB;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito);
        getSupportActionBar().hide();

        jetCodigoPrestamo = findViewById(R.id.etcodigoPrestamo);
        jetIdentificacion = findViewById(R.id.etIdentificacion);

        jtvNombre = findViewById(R.id.tvNombre);
        jtvProfesion = findViewById(R.id.tvProfesion);
        jtvSalario = findViewById(R.id.tvSalario);
        jtvIngresoExtra = findViewById(R.id.tvIngresoExtra);
        jtvGastos = findViewById(R.id.tvGastos);
        jtvValorPrestamo = findViewById(R.id.tvValorPrestamo);
    }

    public boolean buscarUsuario(View view) {
        boolean isUserExist = false;
        identificacion = jetIdentificacion.getText().toString();

        if (identificacion.isEmpty()) {
            Toast.makeText(this, "La Identificación Del Usuario Es Requerida", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {

            try {

                SQLiteDatabase fila = admin.getReadableDatabase();
                Cursor dato = fila.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);

                if (dato.moveToNext()) {

                    String userActive = dato.getString(7);

                    if (userActive.equalsIgnoreCase("si")) {

                        jtvNombre.setText(dato.getString(1));
                        jtvProfesion.setText(dato.getString(2));
                        jtvSalario.setText(dato.getString(4));
                        jtvIngresoExtra.setText(dato.getString(5));
                        jtvGastos.setText(dato.getString(6));
                        isUserExist = true;
                    } else {
                        limpiarCampos();
                        Toast.makeText(this, "El Usuario Existe, Pero, Esta INACTIVO, Busca Un Usuario ACTIVO", Toast.LENGTH_LONG).show();
                    }


                    fila.close();
                } else {

                    Toast.makeText(this, "El Usuario No Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                    limpiarCampos();
                }
            } catch (Exception e) {
                System.out.println("Exception Result " + e);
            }
        }

        return isUserExist;
    }

    public void calcularPrestamo(View view) {
        String salario, ingresoExtra, gastos;


        salario = jtvSalario.getText().toString();
        ingresoExtra = jtvIngresoExtra.getText().toString();
        gastos = jtvGastos.getText().toString();

        if (salario.isEmpty() || ingresoExtra.isEmpty() || gastos.isEmpty()) {
            Toast.makeText(this, "La Idetificación Del Usuario Es Requerida", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {
            int salarioC, ingresoExtraC, gastosC, prestamoBruto, prestamoNeto;


            salarioC = Integer.parseInt(salario);
            ingresoExtraC = Integer.parseInt(ingresoExtra);
            gastosC = Integer.parseInt(gastos);


            prestamoBruto = (salarioC + ingresoExtraC) - gastosC;
            prestamoNeto = prestamoBruto * 10;

            jtvValorPrestamo.setText(String.valueOf(prestamoNeto));
        }
    }

    private boolean buscarCreditoExistente(String codigoPrestamo) {
        boolean creditResult = false;


        try {

            SQLiteDatabase fila = admin.getReadableDatabase();
            Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

            if (!dato.moveToNext()) {

                creditResult = true;
            }


            fila.close();
        } catch (Exception e) {
            System.out.println("Exception Result " + e);
        }

        return creditResult;
    }

    public void guardarCredito(View view) {
        identificacion = jetIdentificacion.getText().toString();
        codigoPrestamo = jetCodigoPrestamo.getText().toString();
        valorPrestamo = jtvValorPrestamo.getText().toString();

        if (identificacion.isEmpty() || codigoPrestamo.isEmpty() || valorPrestamo.isEmpty()) {
            Toast.makeText(this, "Todos Los Campos Son Requeridos", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {

            boolean isCreditExist = buscarCreditoExistente(codigoPrestamo);

            if (isCreditExist) {

                boolean isUserExist = buscarUsuario(view);

                if (isUserExist) {

                    try {

                        SQLiteDatabase fila = admin.getWritableDatabase();


                        ContentValues registro = new ContentValues();

                        registro.put("codigoCredito", codigoPrestamo);
                        registro.put("identificacion", identificacion);
                        registro.put("valorPrestamo", Integer.parseInt(valorPrestamo));


                        respuestaDB = fila.insert("TblCredito", null, registro);



                        if (respuestaDB == (-1)) {

                            Toast.makeText(this, "ERROR, El Credito NO Se Registro Exitosamente", Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(this, "El Credito Se Regristro Exitosamente", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        }


                        fila.close();
                    } catch (Exception e) {
                        System.out.println("Exception Result " + e);
                    }
                }
            } else {
                try {

                    SQLiteDatabase fila = admin.getReadableDatabase();
                    Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

                    if (dato.moveToNext()) {
                        String creditActive = dato.getString(3);
                        System.out.println(creditActive);

                        if (creditActive.equalsIgnoreCase("si")) {
                            Toast.makeText(this, "El Credito Ya Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        } else {
                            Toast.makeText(this, "El Credito Ya Esta Registrado Y Además, Esta INACTIVO, Busca Un Credito ACTIVO", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        }
                    }


                    fila.close();
                } catch (Exception e) {
                    System.out.println("Exception Result 2 " + e);
                }
            }
        }
    }

    public void consultarCredito(View view) {
        codigoPrestamo = jetCodigoPrestamo.getText().toString();

        if (codigoPrestamo.isEmpty()) {
            Toast.makeText(this, "El Codigo Del Credito Es Requerido", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {

            boolean isCreditExist = buscarCreditoExistente(codigoPrestamo);

            if (isCreditExist) {

                Toast.makeText(this, "El Credito NO Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
            } else {

                try {

                    SQLiteDatabase fila = admin.getReadableDatabase();
                    Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

                    if (dato.moveToNext()) {
                        String creditActive = dato.getString(3);

                        if (creditActive.equalsIgnoreCase("si")) {
                            dato = fila.rawQuery("select TblCredito.valorPrestamo, TblCliente.identificacion, TblCliente.nombre, " +
                                    "TblCliente.profesion, TblCliente.salario, TblCliente.ingresoExtra, TblCliente.gastos " +
                                    "from TblCredito inner join TblCliente on TblCredito.identificacion = TblCliente.identificacion " +
                                    "where TblCredito.codigoCredito='" + codigoPrestamo + "'", null);

                            if (dato.moveToNext()) {

                                jetIdentificacion.setText(dato.getString(1));
                                jtvNombre.setText(dato.getString(2));
                                jtvProfesion.setText(dato.getString(3));
                                jtvSalario.setText(dato.getString(4));
                                jtvIngresoExtra.setText(dato.getString(5));
                                jtvGastos.setText(dato.getString(6));
                                jtvValorPrestamo.setText(dato.getString(0));
                            }


                            fila.close();
                        } else {
                            Toast.makeText(this, "El Credito Existe, Pero, Esta INACTIVO, Busca Un Credito ACTIVO", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        }
                    } else {
                        Toast.makeText(this, "El Credito NO Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    System.out.println("Exception Result " + e);
                }
            }
        }
    }

    public void anularCredito(View view) {
        codigoPrestamo = jetCodigoPrestamo.getText().toString();

        if (codigoPrestamo.isEmpty()) {
            Toast.makeText(this, "El Codigo Del Credito Es Requerido", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {

            boolean isCreditExist = buscarCreditoExistente(codigoPrestamo);

            if (isCreditExist) {

                Toast.makeText(this, "El Credito NO Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
            } else {

                try {

                    SQLiteDatabase fila = admin.getReadableDatabase();
                    Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

                    if (dato.moveToNext()) {

                        String creditActive = dato.getString(3);

                        if (creditActive.equalsIgnoreCase("si")) {

                            fila = admin.getWritableDatabase();


                            ContentValues registro = new ContentValues();
                            registro.put("activo", "no");


                            respuestaDB = fila.update("TblCredito", registro, "codigoCredito='" + codigoPrestamo + "'", null);


                            if (respuestaDB == (-1)) {

                                Toast.makeText(this, "ERROR, El Credito NO Se Anuló Exitosamente", Toast.LENGTH_LONG).show();
                            } else {

                                Toast.makeText(this, "El Credito Se Anuló Exitosamente", Toast.LENGTH_LONG).show();
                                limpiarCampos();
                            }
                        } else {
                            Toast.makeText(this, "El Credito Existe, Pero, Esta INACTIVO, Busca Un Credito ACTIVO.", Toast.LENGTH_LONG).show();
                        }
                    }


                    fila.close();
                } catch (Exception e) {
                    System.out.println("Exception Result " + e);
                }
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

    public void limpiarCampos() {
        jetCodigoPrestamo.setText("");
        jetIdentificacion.setText("");
        jtvNombre.setText("");
        jtvProfesion.setText("");
        jtvSalario.setText("");
        jtvIngresoExtra.setText("");
        jtvGastos.setText("");
        jtvValorPrestamo.setText("");

        jetIdentificacion.requestFocus();
    }
}