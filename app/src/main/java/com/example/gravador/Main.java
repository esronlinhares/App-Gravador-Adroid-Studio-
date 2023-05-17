package com.example.gravador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Main extends AppCompatActivity {

    //Criando os atributos necessarios no programa
    Button iniciar, parar, repro;
    TextView save;
    static int microPermissionCode = 200;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Metodo usado para ligar os atributos aos objetos no designer
        iniciarComponentes();

        // Condição para verificar a presença do microfone e
        // para solicitar a permissão de uso do microfone ao usuario
        if (isMicroPresent()){
            getMicroPermi();
        }

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // O try será responsavel por tentar executar o código
                // caso não dê executará o catch
                try {
                    // Fazendo a instancia da classe
                    mediaRecorder = new MediaRecorder();

                    // Configurações da parte da gravação como: tipo de arquivo,
                    // local para salvar, responsavel por gravar e tipo de decodificação
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Configura a fonte de áudio como o microfone do dispositivo
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Configura o formato de saída do arquivo de áudio
                    mediaRecorder.setOutputFile(getRecordFilePath());
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // Configura o codificador de áudio para AMR-NB

                    // Preparação e inicio da gravação
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                    // Mensagem em formato de pop-up rapido
                    Toast.makeText(Main.this, "Gravando...", Toast.LENGTH_LONG).show();

                    // Mostrará onde o arquivo foi salvo
                    save.setText("Salvo em: " + getRecordFilePath());
                }
                catch (Exception e){
                    // Responsavel por mostrar o erro no logcat caso de algo errado no try
                    e.printStackTrace();
                }
            }
        });

        parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Metodo chamado para parar a gravação
                mediaRecorder.stop();
                // Metodo que finalizará a gravação
                mediaRecorder.release();
                mediaRecorder = null;

                Toast.makeText(Main.this, "Gravação finalizada", Toast.LENGTH_LONG).show();
            }
        });

        repro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Novamente uma instancia só que de outra classe, que será
                    // responsavél por reproduzir o audio.
                    mediaPlayer = new MediaPlayer();
                    // Usará o mesmo metodo que foi usado para salvar o arquivo
                    mediaPlayer.setDataSource(getRecordFilePath());
                    // Da mesma forma da gravação na reprodução tem que se
                    // preparar antes de iniciar
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(Main.this, "Reproduzindo", Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void iniciarComponentes() {
        iniciar = findViewById(R.id.iniciar);
        parar = findViewById(R.id.parar);
        repro = findViewById(R.id.repro);
        save = findViewById(R.id.save);
    }

    // Verificação se é possivel gravar com o microfone
    boolean isMicroPresent(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        else {
            return false;
        }
    }

    // Requisição de uso do microfone, e que contem uma condição que só irá
    // solicitar caso o usuario não tenha aceitado
    void getMicroPermi(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO}, microPermissionCode);
        }
    }

    // Metodo responsavel por definir o diretorio onde será salvo o audio
    String getRecordFilePath(){
        ContextWrapper contextcWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextcWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "arquivoMusica" + ".mp3");

        return file.getPath();
    }
}