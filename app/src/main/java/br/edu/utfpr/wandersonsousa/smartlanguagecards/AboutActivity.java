package br.edu.utfpr.wandersonsousa.smartlanguagecards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.sobre));
    }

    public void abrirSiteAutoria(View view){
        abrirSite(getString(R.string.github));
    }

    private void abrirSite(String endereco){

        Intent intentAbertura = new Intent(Intent.ACTION_VIEW);

        intentAbertura.setData(Uri.parse(endereco));

        if (intentAbertura.resolveActivity(getPackageManager()) != null){
            startActivity(intentAbertura);
        }else{
            Toast.makeText(this,
                    R.string.nenhum_aplicativo_para_abrir_paginas_web,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void enviarEmailAutor(View view){
        enviarEmail(new String[]{getString(R.string.send_email)}, getString(R.string.contato_pelo_aplicativo));
    }

    private void enviarEmail(String[] enderecos, String assunto){

        Intent intentAbertura = new Intent(Intent.ACTION_SENDTO);

        intentAbertura.setData(Uri.parse(getString(R.string.mailto)));
        intentAbertura.putExtra(Intent.EXTRA_EMAIL, enderecos);
        intentAbertura.putExtra(Intent.EXTRA_SUBJECT, assunto);

        if (intentAbertura.resolveActivity(getPackageManager()) != null){
            startActivity(intentAbertura);
        }else{
            Toast.makeText(this,
                    R.string.nenhum_aplicativo_para_enviar_um_e_mail,
                    Toast.LENGTH_LONG).show();
        }
    }

}